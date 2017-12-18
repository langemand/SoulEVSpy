package org.hexpresso.soulevspy.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;

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

import org.hexpresso.elm327.log.CommLog;
import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.fragment.AdvisoryFragment;
import org.hexpresso.soulevspy.fragment.BatteryFragment;
import org.hexpresso.soulevspy.fragment.CarFragment;
import org.hexpresso.soulevspy.fragment.DashboardFragment;
import org.hexpresso.soulevspy.fragment.GpsFragment;
import org.hexpresso.soulevspy.fragment.LdcFragment;
import org.hexpresso.soulevspy.fragment.TireFragment;
import org.hexpresso.soulevspy.io.OBD2Device;
import org.hexpresso.soulevspy.io.Position;
import org.hexpresso.soulevspy.util.ClientSharedPreferences;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener {

    // Navigation Drawer items constants
    private enum NavigationDrawerItem {
        Invalid,
        Bluetooth,
        Car,
        Advisor,
        Ldc,
        Gps,
        Dashboard,
        Battery,
        Tires,
        DtcCodes,
        Settings,
        HelpFeedback
    }
    private Position mPosition;
    private OBD2Device mDevice;
    private ClientSharedPreferences mSharedPreferences;
    private Drawer mDrawer;


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_LOCATION = 2;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /**
     * Checks if the app has permission to gps location
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyLocationPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    REQUEST_LOCATION);
        }
    }



    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);
        verifyLocationPermissions(this);

        // Preferences
        mSharedPreferences = new ClientSharedPreferences(this);

        CurrentValuesSingleton.getInstance().setPreferences(mSharedPreferences);

        // Listen to GPS location updates
        mPosition = new Position(getBaseContext());

        // Bluetooth OBD2 Device
        mDevice = new OBD2Device(mSharedPreferences);

        // Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ClientSharedPreferences prefs = new ClientSharedPreferences(getApplicationContext());

        // Navigation Drawer
        mDrawer = new DrawerBuilder(this)
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withHeader(R.layout.nav_header)
                .addDrawerItems(
                        new SwitchDrawerItem().withIdentifier(NavigationDrawerItem.Bluetooth.ordinal()).withName(R.string.action_bluetooth).withIcon(GoogleMaterial.Icon.gmd_bluetooth).withChecked(false).withSelectable(false).withOnCheckedChangeListener(mOnCheckedBluetoothDevice),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Car.ordinal()).withName(R.string.action_car_information).withIcon(FontAwesome.Icon.faw_car),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Advisor.ordinal()).withName(R.string.action_advisor).withIcon(FontAwesome.Icon.faw_list),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Ldc.ordinal()).withName(R.string.action_ldc).withIcon(FontAwesome.Icon.faw_battery_4),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Gps.ordinal()).withName("GPS information").withIcon(FontAwesome.Icon.faw_clock_o),
//                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Dashboard.ordinal()).withName(R.string.action_dashboard).withIcon(FontAwesome.Icon.faw_dashboard).withEnabled(false),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Battery.ordinal()).withName(R.string.action_battery).withIcon(FontAwesome.Icon.faw_battery_three_quarters),
//                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.DtcCodes.ordinal()).withName(R.string.action_dtc).withIcon(FontAwesome.Icon.faw_stethoscope).withEnabled(false),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Tires.ordinal()).withName(R.string.action_tires).withIcon(FontAwesome.Icon.faw_circle_o_notch),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withIdentifier(NavigationDrawerItem.Settings.ordinal()).withName(R.string.action_settings).withSelectable(false).withIcon(GoogleMaterial.Icon.gmd_settings),
                        new SecondaryDrawerItem().withIdentifier(NavigationDrawerItem.HelpFeedback.ordinal()).withName(R.string.action_help).withIcon(GoogleMaterial.Icon.gmd_help).withEnabled(false)
                )
                .withOnDrawerItemClickListener(this)
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 2
            mDrawer.setSelection(NavigationDrawerItem.Battery.ordinal(), true);
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
                switch (item) {
                    case Bluetooth:
                        // Do nothing
                        break;
                    case Gps:
                        fragment = new GpsFragment();
                        break;
                    case Advisor:
                        fragment = new AdvisoryFragment();
                        break;
                    case Ldc:
                        fragment = new LdcFragment();
                        break;
                    case Car:
                        fragment = new CarFragment();
                        Bundle args = new Bundle();
                        Object objVin = CurrentValuesSingleton.getInstance().get("VIN");
                        if (objVin != null) {
                            args.putString("VIN", objVin.toString());
                        }
                        fragment.setArguments(args);
                        break;
                    case Dashboard:
                        fragment = new DashboardFragment();
                        break;
                    case Battery:
                        fragment = new BatteryFragment();
//                        Bundle batteryArgs = new Bundle();
//                        Object objSoc = CurrentValuesSingleton.getInstance().get("TrueSOC");
//                        if (objSoc != null) {
//                            Double trueSOC = new Double((Double)objSoc);
//                            if (trueSOC != null) {
//                                batteryArgs.putDouble("TrueSOC", trueSOC);
//                            }
//                        }
//                        batteryArgs.putDouble("SOC", 0.0);
//                        fragment.setArguments(batteryArgs);
                        break;
                    case Tires:
                        fragment = new TireFragment();
                        break;
                    case Settings:
                        intent = new Intent(MainActivity.this, SettingsActivity.class);
                        break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                // Do nothing
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
        boolean success = false;
        if (connect) {
            success = mDevice.connect();
        }
        else {
            success = mDevice.disconnect();
        }

        return success;
    }

    /**
     *
     */
    public void onDestroy() {
        super.onDestroy();
        bluetoothDeviceConnect(false);
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
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}