package com.evranger.soulevspy.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.evranger.soulevspy.R;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-09-28.
 */
public class ClientSharedPreferences {

    // SharedPreferences name
    public static final String SHARED_PREFERENCES_NAME = "SoulEvSpySharedPreferences";

    // preferences.xml keys
    private final String PREF_CAR_MODEL;
    private final String PREF_UNITS_DISTANCE;
    private final String PREF_UNITS_ENERGY_CONSUMPTION;
    private final String PREF_UNITS_TEMPERATURE;
    private final String PREF_UNITS_PRESSURE;
    private final String PREF_BLUETOOTH_DEVICE;
    private final String PREF_AUTO_RECONNECT;
    private final String PREF_SCAN_INTERVAL;
    private final String PREF_UPLOAD_TO_CLOUD;
    private final String PREF_SAVE_IN_DOWNLOADS;

    // Default values
    public final String DEFAULT_CAR_MODEL;
    public final String DEFAULT_UNITS_DISTANCE;
    public final String DEFAULT_UNITS_ENERGY_CONSUMPTION;
    public final String DEFAULT_UNITS_TEMPERATURE;
    public final String DEFAULT_UNITS_PRESSURE;
    public final String DEFAULT_BLUETOOTH_DEVICE;
    public final boolean DEFAULT_AUTO_RECONNECT;
    public final float DEFAULT_SCAN_INTERVAL;
    public final boolean DEFAULT_UPLOAD_TO_CLOUD;
    public final boolean DEFAULT_SAVE_IN_DOWNLOADS;

    final private Context mContext;
    final private SharedPreferences sharedPreferences;

    public ClientSharedPreferences(Context context) {
        // Load preference keys from XML
        PREF_CAR_MODEL = context.getString(R.string.key_list_car_model);
        PREF_UNITS_DISTANCE = context.getString(R.string.key_list_units_distance);
        PREF_UNITS_ENERGY_CONSUMPTION = context.getString(R.string.key_list_units_energy_consumption);
        PREF_UNITS_TEMPERATURE = context.getString(R.string.key_list_units_temperature);
        PREF_UNITS_PRESSURE = context.getString(R.string.key_list_units_pressure);
        PREF_BLUETOOTH_DEVICE = context.getString(R.string.key_list_bluetooth_device);
        PREF_AUTO_RECONNECT = context.getString(R.string.key_check_auto_reconnect);
        PREF_SCAN_INTERVAL = context.getString(R.string.key_edit_scan_interval);
        PREF_UPLOAD_TO_CLOUD = context.getString(R.string.key_check_storage_upload_to_cloud);
        PREF_SAVE_IN_DOWNLOADS = context.getString(R.string.key_check_storage_save_in_downloads_dir);

        // Load default values
        DEFAULT_CAR_MODEL = context.getString(R.string.list_car_model_SoulEV2015);
        DEFAULT_UNITS_DISTANCE = context.getString(R.string.list_distance_km);
        DEFAULT_UNITS_ENERGY_CONSUMPTION = context.getString(R.string.list_energy_consumption_kwh_100km);
        DEFAULT_UNITS_TEMPERATURE = context.getString(R.string.list_temperature_c);
        DEFAULT_UNITS_PRESSURE = context.getString(R.string.list_pressure_psi);
        DEFAULT_BLUETOOTH_DEVICE = "";
        DEFAULT_AUTO_RECONNECT = false;
        DEFAULT_SCAN_INTERVAL = Float.valueOf(context.getString(R.string.pref_default_scan_interval));
        DEFAULT_UPLOAD_TO_CLOUD = false;
        DEFAULT_SAVE_IN_DOWNLOADS = false;

        // Create the SharedPreferences object
        mContext = context;
        sharedPreferences = context.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );
    }

    public String getCarModelStringValue() {
        return sharedPreferences.getString(PREF_CAR_MODEL, DEFAULT_CAR_MODEL);
    }

    public String getUnitsDistanceStringValue() {
        return sharedPreferences.getString(PREF_UNITS_DISTANCE, DEFAULT_UNITS_DISTANCE);
    }

    public String getUnitsEnergyConsumptionStringValue() {
        return sharedPreferences.getString(PREF_UNITS_ENERGY_CONSUMPTION, DEFAULT_UNITS_ENERGY_CONSUMPTION);
    }

    public String getUnitsTemperatureStringValue() {
        return sharedPreferences.getString(PREF_UNITS_TEMPERATURE, DEFAULT_UNITS_TEMPERATURE);
    }

    public String getUnitsPressureStringValue() {
        return sharedPreferences.getString(PREF_UNITS_PRESSURE, DEFAULT_UNITS_PRESSURE);
    }

    public String getBluetoothDeviceStringValue() {
        return sharedPreferences.getString(PREF_BLUETOOTH_DEVICE, DEFAULT_BLUETOOTH_DEVICE);
    }

    public boolean getAutoReconnectBooleanValue() {
        return sharedPreferences.getBoolean(PREF_AUTO_RECONNECT, DEFAULT_AUTO_RECONNECT);
    }

    public float getScanIntervalFloatValue() {
        try {
            String str = sharedPreferences.getString(PREF_SCAN_INTERVAL, Float.toString(DEFAULT_SCAN_INTERVAL));
            float f = Float.valueOf(str);
            return f;
        } catch (Exception e) {
            String what = e.toString();
            return DEFAULT_SCAN_INTERVAL;
        }
    }

    public Context getContext() {
        return mContext;
    }
}