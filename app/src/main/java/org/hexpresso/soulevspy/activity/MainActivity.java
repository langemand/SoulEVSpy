package org.hexpresso.soulevspy.activity;

import android.content.Intent;
import android.os.Bundle;
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

import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.fragment.BatteryFragment;
import org.hexpresso.soulevspy.fragment.CarFragment;
import org.hexpresso.soulevspy.fragment.DashboardFragment;
import org.hexpresso.soulevspy.io.OBD2Device;
import org.hexpresso.soulevspy.util.ClientSharedPreferences;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener {

    // Navigation Drawer items constants
    private enum NavigationDrawerItem {
        Invalid,
        Bluetooth,
        Car,
        Dashboard,
        Battery,
        DtcCodes,
        Settings,
        HelpFeedback
    }
    private OBD2Device mDevice;
    private ClientSharedPreferences mSharedPreferences;
    private Drawer mDrawer;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

        // Preferences
        mSharedPreferences = new ClientSharedPreferences(this);

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
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Dashboard.ordinal()).withName(R.string.action_dashboard).withIcon(FontAwesome.Icon.faw_dashboard).withEnabled(false),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.Battery.ordinal()).withName(R.string.action_battery).withIcon(FontAwesome.Icon.faw_battery_three_quarters),
                        new PrimaryDrawerItem().withIdentifier(NavigationDrawerItem.DtcCodes.ordinal()).withName(R.string.action_dtc).withIcon(FontAwesome.Icon.faw_stethoscope).withEnabled(false),
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

        if (drawerItem != null) {
            Intent intent = null;
            Fragment fragment = null;
            try {
                NavigationDrawerItem item = NavigationDrawerItem.values()[drawerItem.getIdentifier()];
                switch (item) {
                    case Bluetooth:
                        // Do nothing
                        break;
                    case Car:
                        fragment = new CarFragment();
                        Bundle args = new Bundle();
                        args.putString("VIN", mDevice.mVehicleIdentifierNumberCommand.getValue());
                        fragment.setArguments(args);
                        break;
                    case Dashboard:
                        fragment = new DashboardFragment();
                        break;
                    case Battery:
                        fragment = new BatteryFragment();
                        Bundle batteryArgs = new Bundle();
                        batteryArgs.putDouble("SOC", mDevice.mBatteryManagementSystemCommand.getStateOfCharge());
                        fragment.setArguments(batteryArgs);
                        break;
                    case Settings:
                        intent = new Intent(MainActivity.this, SettingsActivity.class);
                        break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                // Do nothing
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
}