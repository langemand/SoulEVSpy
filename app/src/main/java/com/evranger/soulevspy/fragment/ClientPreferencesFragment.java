package com.evranger.soulevspy.fragment;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.evranger.soulevspy.util.ClientSharedPreferences;

import com.evranger.soulevspy.R;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-09-28.
 */
public class ClientPreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ClientSharedPreferences mSharedPreferences;
    private WebView mWebview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = new ClientSharedPreferences(getActivity());

        // Set the shared preferences name
        getPreferenceManager().setSharedPreferencesName(ClientSharedPreferences.SHARED_PREFERENCES_NAME);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // Load Bluetooth devices list
        loadBluetoothDevices();

        // Set the summaries
        setApplicationVersion();
        onSharedPreferenceChanged(null, "");
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        //onSharedPreferenceChanged(null, "");
    }

    @Override
    public boolean onPreferenceTreeClick (PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals(getString(R.string.key_open_source_licenses)))
        {
            displayOpenSourceLicensesDialog();
            return true;
        }

        if (preference.getKey().equals(getString(R.string.key_privacy_policy)))
        {
            displayPrivacyPolicy();
            return true;
        }

        if (preference.getKey().equals(getString(R.string.key_application_version)))
        {
            displayContributors();
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Updating all preferences summary...

        ListPreference listPref = (ListPreference) findPreference(getString(R.string.key_list_car_model));
        setListPreferenceSummary(listPref, mSharedPreferences.getCarModelStringValue());

        listPref = (ListPreference) findPreference(getString(R.string.key_list_units_distance));
        setListPreferenceSummary(listPref, mSharedPreferences.getUnitsDistanceStringValue());

        listPref = (ListPreference) findPreference(getString(R.string.key_list_units_energy_consumption));
        setListPreferenceSummary(listPref, mSharedPreferences.getUnitsEnergyConsumptionStringValue());

        listPref = (ListPreference) findPreference(getString(R.string.key_list_units_temperature));
        setListPreferenceSummary(listPref, mSharedPreferences.getUnitsTemperatureStringValue());

        listPref = (ListPreference) findPreference(getString(R.string.key_list_units_pressure));
        setListPreferenceSummary(listPref, mSharedPreferences.getUnitsPressureStringValue());

        listPref = (ListPreference) findPreference(getString(R.string.key_list_bluetooth_device));
        String btSummary = getString(R.string.pref_bluetooth_device_summary);
        String btAddress = mSharedPreferences.getBluetoothDeviceStringValue();
        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
        if (!mSharedPreferences.DEFAULT_BLUETOOTH_DEVICE.equals(btAddress) && (bta != null))
        {
            if (bta.isEnabled()) {
                // Set the bluetooth adapter name as summary
                try {
                    BluetoothDevice device = bta.getRemoteDevice(btAddress);
                    btSummary = device.getName();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    // Do nothing, we will set the default summary instead
                }
            } else {
                btSummary = getString(R.string.enable_bluetooth_on_phone);
            }
        }

        listPref.setSummary(btSummary);

        CheckBoxPreference checkBoxPref = (CheckBoxPreference) findPreference(getString(R.string.key_check_auto_reconnect));
        setCheckBoxPreferenceSummary(checkBoxPref, mSharedPreferences.getAutoReconnectBooleanValue());

        EditTextPreference editTextPref = (EditTextPreference) findPreference(getString(R.string.key_edit_scan_interval));
        setEditTextPreferenceSummary(editTextPref, mSharedPreferences.getScanIntervalFloatValue());

        // Force refresh
        //getActivity().onContentChanged();
    }

    private void loadBluetoothDevices() {
        // Get paired devices and populate preference list
        ListPreference listBtDevices = (ListPreference) findPreference(getString(R.string.key_list_bluetooth_device));
        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
        if (bta == null || !bta.isEnabled()) {
            // The device do not support Bluetooth
            listBtDevices.setEnabled(false);
        }
        else {
            Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            ArrayList<CharSequence> pairedDeviceStrings = new ArrayList<>();
            ArrayList<CharSequence> pairedDevicesValues = new ArrayList<>();
            boolean hasPairedDevices = !pairedDevices.isEmpty();
            if (hasPairedDevices) {
                for (BluetoothDevice device : pairedDevices) {
                    pairedDeviceStrings.add(device.getName() + "\n" + device.getAddress());
                    pairedDevicesValues.add(device.getAddress());
                }
                // Set the values in the list
                listBtDevices.setEntries(pairedDeviceStrings.toArray(new CharSequence[1]));
                listBtDevices.setEntryValues(pairedDevicesValues.toArray(new CharSequence[1]));
            } else {
                listBtDevices.setEntries(new CharSequence[0]);
                listBtDevices.setEntryValues(new CharSequence[0]);
            }
            listBtDevices.setEnabled(hasPairedDevices);
        }
    }

    /**
     * Set the application version in the About preference ("x.y.z (build)")
     */
    private void setApplicationVersion() {
        Preference pref = findPreference(getString(R.string.key_application_version));
        String version = "";
        try {
            String packageName = getActivity().getPackageName();
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(packageName, 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // Do nothing
            e.printStackTrace();
        }

        pref.setSummary(String.format(getString(R.string.pref_version_summary), version));
    }

    private void setListPreferenceSummary(ListPreference pref, String value) {
        final int index = pref.findIndexOfValue(value);
        if (index >= 0) {
            final String summary = (String) pref.getEntries()[index];
            pref.setSummary(summary);
        }
    }

    private void setCheckBoxPreferenceSummary(CheckBoxPreference pref, Boolean value) {
        pref.setChecked(value);
    }

    private void setEditTextPreferenceSummary(EditTextPreference pref, Float value) {
        pref.setSummary(String.format("%.1f", value));
    }

    private void displayOpenSourceLicensesDialog() {
        Context c = getActivity();

        // Prepare the view
        WebView view = (WebView) LayoutInflater.from(c).inflate(R.layout.dialog_licenses, null);
        view.loadUrl("file:///android_asset/open_source_licenses.html");

        // Show the dialog
        AlertDialog.Builder ab = new AlertDialog.Builder(c, R.style.Theme_AppCompat_Light_Dialog_Alert);
        ab.setTitle(R.string.pref_licenses);
        ab.setView(view)
        .setPositiveButton(android.R.string.ok, null)
        .show();
    }

    private void displayPrivacyPolicy() {
        final Context context = getActivity();
        mWebview  = new WebView(context);

        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        mWebview.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });

        mWebview.loadUrl("https://evranger.com/soulevspy-privacy-policy.html");

        // Show the dialog
        AlertDialog.Builder ab = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        ab.setTitle(R.string.pref_privacy_policy);
        ab.setView(mWebview)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void displayContributors() {
        Context c = getActivity();

        // Prepare the view
        WebView view = (WebView) LayoutInflater.from(c).inflate(R.layout.dialog_contributors, null);
        view.loadUrl("file:///android_asset/contributors.html");

        // Show the dialog
        AlertDialog.Builder ab = new AlertDialog.Builder(c, R.style.Theme_AppCompat_Light_Dialog_Alert);
        ab.setTitle(R.string.pref_about);
        ab.setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
