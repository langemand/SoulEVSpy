package com.evranger.soulevspy.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.evranger.soulevspy.advisor.ChargeStations;
import com.evranger.soulevspy.advisor.EnergyWatcher;
import com.evranger.soulevspy.car_model.ModelSpecificCommands;
import com.evranger.soulevspy.fragment.BatteryCellmapFragment;
import com.evranger.soulevspy.fragment.EnergyFragment;
import com.evranger.soulevspy.io.Position;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import com.evranger.elm327.log.CommLog;
import com.evranger.soulevspy.R;

import com.evranger.soulevspy.fragment.ChargerLocationsFragment;
import com.evranger.soulevspy.fragment.BatteryFragment;
import com.evranger.soulevspy.fragment.CarFragment;
import com.evranger.soulevspy.fragment.DashboardFragment;
import com.evranger.soulevspy.fragment.GpsFragment;
import com.evranger.soulevspy.fragment.LdcFragment;
import com.evranger.soulevspy.fragment.ObcFragment;
import com.evranger.soulevspy.fragment.TireFragment;
import com.evranger.soulevspy.fragment.VmcuFragment;
import com.evranger.soulevspy.io.OBD2Device;
import com.evranger.soulevspy.io.ReplayLoop;
import com.evranger.soulevspy.util.BatteryStats;
import com.evranger.soulevspy.util.ClientSharedPreferences;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener {

    // Navigation Drawer items constants
    private enum NavigationDrawerItem {
        Invalid,
        Bluetooth,
        Car,
        VehicleMotorControlUnit,
        OnBoarCharger,
        ChargerLocations,
        Energy,
        Battery,
        BatteryCellmap,
        Ldc,
        Tires,
        Gps,
        Dashboard,
        DtcCodes,
        Settings,
        Replay,
        Demo,
        HelpFeedback
//        ,Debug
    }
    private Position mPosition;
    private OBD2Device mDevice;
    private ClientSharedPreferences mSharedPreferences;
    private Drawer mDrawer;
    private BackButtonDialog backButtonDialog = null;
    private SwitchDrawerItem bluetoothEnable = null;
    private ReplayLoop mReplayLoop;


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final int REQUEST_LOCATION = 2;
    public static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    public static final int RC_SIGN_IN = 3;
    public static final int RC_CHOOSE_FILE = 123;
    private int mRequested = 0;
    private ChargeStations mChargeStations = null;
    private BatteryStats mBatteryStats = null;
    private PowerConnectionReceiver mPowerConnectionReceiver = null;
    private EnergyWatcher mEnergyWatcher = null;
    private FirebaseAnalytics mFirebaseAnalytics;
    private ModelSpecificCommands mModelSpecificCommands;


    private boolean isPhoneCharging() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getBaseContext().registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        return isCharging;
    }

    synchronized public void wantScreenOn() {
        if (isPhoneCharging()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     */
    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            mRequested = REQUEST_EXTERNAL_STORAGE;
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /**
     * Checks if the app has permission to gps location
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public boolean verifyLocationPermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            mRequested = REQUEST_LOCATION;
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    REQUEST_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Preferences
        mSharedPreferences = new ClientSharedPreferences(this);
        CurrentValuesSingleton.getInstance().setPreferences(mSharedPreferences);

        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.activity_main);

        warningDialog(R.string.dialog_lite_splash_title, R.string.dialog_lite_splash_message);

        verifyLocationPermissions();

        // Listen to GPS location updates
        mPosition = new Position(this);

        // Model specific loop commands
        mModelSpecificCommands = new ModelSpecificCommands(mSharedPreferences);

        // ChargeStations
        mChargeStations = new ChargeStations(getBaseContext(),mModelSpecificCommands.hasChademo(), mModelSpecificCommands.hasCCS(), mModelSpecificCommands.getFullRange());

        // Bluetooth OBD2 Device
        mDevice = new OBD2Device(mSharedPreferences, mModelSpecificCommands.getLoopCommands());

        // Calculations based on CurrentValuesSingleton (determines original nominal battery capacity)
        mBatteryStats = new BatteryStats();

        // Monitor phone charging state
        mPowerConnectionReceiver = new PowerConnectionReceiver();
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        ifilter.addAction(Intent.ACTION_POWER_CONNECTED);
        ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        Intent batteryStatus = getBaseContext().registerReceiver(mPowerConnectionReceiver, ifilter);

        // EnergyWatcher
        // TODO: Fix the energywatcher to be useful - e.g. do not run backwards while charging...
        //  mEnergyWatcher = new EnergyWatcher();

        // Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bluetoothEnable = new SwitchDrawerItem().withIdentifier(NavigationDrawerItem.Bluetooth.ordinal()).withName(R.string.action_bluetooth).withIcon(GoogleMaterial.Icon.gmd_bluetooth).withChecked(false).withSelectable(false).withOnCheckedChangeListener(mOnCheckedBluetoothDevice);
        // Navigation Drawer
        mDrawer = new DrawerBuilder(this)
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withHeader(R.layout.nav_header)
                .addDrawerItems(
                        bluetoothEnable,
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.ChargerLocations.ordinal()).withName(R.string.action_charger_locations).withIcon(FontAwesome.Icon.faw_map),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Energy.ordinal()).withName(R.string.action_energy).withIcon(FontAwesome.Icon.faw_list),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.BatteryCellmap.ordinal()).withName(R.string.action_battery_cellmap).withIcon(FontAwesome.Icon.faw_table),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Battery.ordinal()).withName(R.string.action_battery).withIcon(FontAwesome.Icon.faw_battery_three_quarters),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Car.ordinal()).withName(R.string.action_car_information).withIcon(FontAwesome.Icon.faw_car),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.VehicleMotorControlUnit.ordinal()).withName(R.string.action_vmcu_information).withIcon(FontAwesome.Icon.faw_dot_circle_o),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.OnBoarCharger.ordinal()).withName(R.string.action_obc_information).withIcon(FontAwesome.Icon.faw_plug),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Ldc.ordinal()).withName(R.string.action_ldc).withIcon(FontAwesome.Icon.faw_battery_4),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Tires.ordinal()).withName(R.string.action_tires).withIcon(FontAwesome.Icon.faw_circle_o_notch),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Gps.ordinal()).withName(R.string.action_gps).withIcon(FontAwesome.Icon.faw_clock_o),
//                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Dashboard.ordinal()).withName(R.string.action_dashboard).withIcon(FontAwesome.Icon.faw_dashboard).withEnabled(false),
//                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.DtcCodes.ordinal()).withName(R.string.action_dtc).withIcon(FontAwesome.Icon.faw_stethoscope).withEnabled(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withIdentifier(NavigationDrawerItem.Settings.ordinal()).withName(R.string.action_settings).withSelectable(false).withIcon(GoogleMaterial.Icon.gmd_settings),
//                        new SecondaryDrawerItem().withIdentifier(NavigationDrawerItem.Replay.ordinal()).withName(R.string.action_replay).withSelectable(!mDevice.isConnected()).withIcon(GoogleMaterial.Icon.gmd_replay),
                        new SecondaryDrawerItem().withIdentifier(NavigationDrawerItem.Demo.ordinal()).withName(R.string.action_demo).withSelectable(!mDevice.isConnected()).withIcon(GoogleMaterial.Icon.gmd_replay)
//                        , new SecondaryDrawerItem().withIdentifier(NavigationDrawerItem.Debug.ordinal()).withName("Debug").withIcon(GoogleMaterial.Icon.gmd_assessment)

//                        new SecondaryDrawerItem().withIdentifier(NavigationDrawerItem.HelpFeedback.ordinal()).withName(R.string.action_help).withIcon(GoogleMaterial.Icon.gmd_help).withEnabled(false)
                )
                .withOnDrawerItemClickListener(this)
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        // Do not display empty fragments
        if (!mModelSpecificCommands.hasLdcData()) {
            mDrawer.removeItem(NavigationDrawerItem.Ldc.ordinal());
        }

        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 2
            mDrawer.setSelection(NavigationDrawerItem.ChargerLocations.ordinal(), true);
        }
    }

    /**
     *
     */
    private OnCheckedChangeListener mOnCheckedBluetoothDevice = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if( !bluetoothDeviceConnect(isChecked) )
            {
                buttonView.setChecked(false);
            }
        }
    };

    /**
     *
     * @param view
     * @param position
     * @param drawerItem
     * @return
     */
    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        //check if the drawerItem is set.
        //there are different reasons for the drawerItem to be null
        //--> click on the header
        //--> click on the footer
        //those items don't contain a drawerItem

        CommLog.getInstance().flush();
        if (drawerItem != null) {
            Intent intent = null;
            Fragment fragment = null;
            try {
                NavigationDrawerItem item = NavigationDrawerItem.values()[drawerItem.getIdentifier()];
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                switch (item) {
                    case Bluetooth:
                        // Do nothing
                        mPosition.listen(true);
                        break;
                    case Gps:
                        fragment = new GpsFragment();
                        break;
                    case ChargerLocations:
                        fragment = new ChargerLocationsFragment();
                        wantScreenOn();
                        break;
                    case Energy:
                        fragment = new EnergyFragment();
                        wantScreenOn();
                        break;
                    case Ldc:
                        fragment = new LdcFragment();
                        break;
                    case Car:
                        fragment = new CarFragment();
                        break;
                    case VehicleMotorControlUnit:
                        fragment = new VmcuFragment();
                        break;
                    case OnBoarCharger:
                        fragment = new ObcFragment();
                        break;
                    case Dashboard:
                        fragment = new DashboardFragment();
                        break;
                    case Battery:
                        fragment = new BatteryFragment();
                        break;
                    case BatteryCellmap:
                        fragment = new BatteryCellmapFragment();
                        break;
                    case Tires:
                        fragment = new TireFragment();
                        break;
                    case Settings:
                        intent = new Intent(MainActivity.this, SettingsActivity.class);
                        break;
                    case Replay:
                        if (!bluetoothEnable.isChecked()) {
                            Intent fileintent = new Intent()
                                    .setType("*/*")
                                    .setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(fileintent, "Select a file"), RC_CHOOSE_FILE);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), R.string.info_disconnect_bluetooth_to_start_replay, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    case Demo:
                        if (!bluetoothEnable.isChecked()) {
                            try {
                                InputStream is = getAssets().open("SoulData.demo.csv");
                                mPosition.listen(false);
                                if (mReplayLoop != null) {
                                    mReplayLoop.stop();
                                }
                                mReplayLoop = new ReplayLoop(is);
                            } catch (Exception ex) {
                                logEventException(ex);
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), R.string.info_disconnect_bluetooth_to_start_replay, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
//                    case Debug:
//                        fragment = new DebugFragment();
//                        break;

                }
            } catch (ArrayIndexOutOfBoundsException e) {
                logEventException(e);
                e.printStackTrace();
            }

            if (intent != null) {
                MainActivity.this.startActivity(intent);
            }

            if (fragment != null) {
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            }
        }

        return false;
    }

    /**
     *
     * @param connect
     * @return
     */
    private boolean bluetoothDeviceConnect(boolean connect){
        if (mReplayLoop != null) {
            mReplayLoop.stop();
            mPosition.listen(true);

        }
        boolean success = false;
        if (connect) {
            setDataFile();
            success = mDevice.connect();
        } else {
            success = mDevice.disconnect();
        }

        return success;
    }

    private void setDataFile() {
        CurrentValuesSingleton.getInstance().setDataFile(null);
    }

    /**
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = mDrawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else if (backButtonDialog == null || backButtonDialog.getChoice() == 0) {
            backButtonDialog = new BackButtonDialog();
            backButtonDialog.show(getFragmentManager(), "Terminate");
        } else if (backButtonDialog.getChoice() == 1) { // Terminate
            super.onBackPressed();
            finish();
//            finishAffinity();
            System.exit(0);
        } else if (backButtonDialog.getChoice() == 2) { // Continue in background
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
            backButtonDialog = null;
        } else if (backButtonDialog.getChoice() == 3) { // Cancel
            backButtonDialog = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     *
     */
    public void onDestroy() {
        getBaseContext().unregisterReceiver(mPowerConnectionReceiver);
        super.onDestroy();
        mPosition.listen(false);
        bluetoothDeviceConnect(false);
        if (mChargeStations != null) mChargeStations.onDestroy();
        if (mEnergyWatcher != null) mEnergyWatcher.finalize();
        String fullpath = CurrentValuesSingleton.getInstance().closeLog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        CommLog.getInstance().flush();
        if (!mDevice.isConnected()) {
            mPosition.listen(false);
        }
        if (mReplayLoop != null) {
            mReplayLoop.stop();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPosition.listen(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_CHOOSE_FILE && resultCode==RESULT_OK) {
            mPosition.listen(false);
            Uri selectedFile = data.getData(); //The uri with the location of the file
            if (mReplayLoop != null) {
                mReplayLoop.stop();
            }
            mReplayLoop = new ReplayLoop(selectedFile, this);
        }
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        int i = 0;
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == 0){
                mPosition.updateIfListening();
            }
        }
    }

    public void logEventException(Exception e) {
        Bundle params = new Bundle();
        params.putString("exception_type", e.getClass().getSimpleName());
        params.putString("exception_message", e.getMessage());
        params.putString("stack_trace", e.getStackTrace().toString());
        mFirebaseAnalytics.logEvent("handled_exception", params);
    }

    public void warningDialog(int titleId, int messageId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mSharedPreferences.getContext());

        // set title
        alertDialogBuilder.setTitle(titleId);

        // set dialog message
        alertDialogBuilder
                .setMessage(messageId)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_Ok,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void zipAndUpload(String fullpath) {
        // For test
        if (fullpath == null) {
            try {
                File tstFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "test.txt");
                String str = "Hello World";
//                String path = tstFile.getAbsolutePath();
//                new File file = File(path);
                tstFile.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(tstFile);
                byte[] strToBytes = str.getBytes();
                outputStream.write(strToBytes);

                outputStream.close();
                fullpath = tstFile.getAbsolutePath();
            } catch (IOException ex) {
                int i = 0;
            }
        }
//        if (fullpath != null) {
//            File zipFile = new File(mSharedPreferences.getContext().getCacheDir(), "temp.zip"); // Note: If device is running low on mem, file may be deleted!
            File zipFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "test.zip");
            try {
                String abspath = zipFile.getAbsolutePath();
                zip(fullpath, abspath);
                // Upload Zipped File to cloud
                upload(abspath);
            } catch (Exception ex) {
                // ?
            }
//        }
    }

    public void zip(String sourceFile, String zipFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();
    }

    public void upload(String path) {
        // Create a storage reference from our app
        //Firebase
        FirebaseStorage storage;
        StorageReference storageReference;
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Uri filePath = Uri.fromFile(new File(path));

// Create a reference to 'data/temp.zip'
        StorageReference tempDataRef = storageRef.child("data/temp.zip");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageTask task = tempDataRef.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
int i = 0;
    }

    void authenticate() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
//                new AuthUI.IdpConfig.FacebookBuilder().build(),
//                new AuthUI.IdpConfig.TwitterBuilder().build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

    }
}