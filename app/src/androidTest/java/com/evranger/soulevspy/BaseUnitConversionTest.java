package com.evranger.soulevspy;


import android.content.Context;

import androidx.test.InstrumentationRegistry;

import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import org.junit.Before;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class BaseUnitConversionTest {
    String mCarModelStringValue = null;
    protected ClientSharedPreferences prefs;
    protected ClientSharedPreferences appPrefs;

    protected BaseUnitConversionTest(String carModelStringValue) {
        mCarModelStringValue = carModelStringValue;
    }

    @Before
    public void setUp() throws Exception {
        appPrefs = new ClientSharedPreferences(InstrumentationRegistry.getTargetContext());

        Context context = InstrumentationRegistry.getTargetContext();
        System.setProperty("org.mockito.android.target", context.getCacheDir().getPath());
        prefs = mock(ClientSharedPreferences.class);

        when(prefs.getContext()).thenReturn(context);
        when(prefs.getCarModelStringValue()).thenReturn(mCarModelStringValue);
        when(prefs.getUnitsPressureStringValue()).thenReturn(context.getString(R.string.list_pressure_value_psi));
        when(prefs.getUnitsTemperatureStringValue()).thenReturn(context.getString(R.string.list_temperature_value_c));
        when(prefs.getUnitsDistanceStringValue()).thenReturn(context.getString(R.string.list_distance_value_km));
        when(prefs.getUnitsEnergyConsumptionStringValue()).thenReturn(context.getString(R.string.list_energy_consumption_value_kwh_100km));

        CurrentValuesSingleton.reset().setPreferences(prefs);
    }

    Context getContext() {
        return appPrefs.getContext();
    }

}
