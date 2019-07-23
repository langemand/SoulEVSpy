package com.evranger.soulevspy;


import android.test.AndroidTestCase;

import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import com.evranger.soulevspy.R;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class BaseUnitConversionTest extends AndroidTestCase {
    String mCarModelStringValue = null;
    protected ClientSharedPreferences prefs;

    protected BaseUnitConversionTest(String carModelStringValue) {
        mCarModelStringValue = carModelStringValue;
    }

    public void setUp() throws Exception {
        System.setProperty("org.mockito.android.target", getContext().getCacheDir().getPath());
        prefs = mock(ClientSharedPreferences.class);

        when(prefs.getContext()).thenReturn(getContext());
        when(prefs.getCarModelStringValue()).thenReturn(mCarModelStringValue);
        when(prefs.getUnitsTemperatureStringValue()).thenReturn(getContext().getString(R.string.list_temperature_value_c));
        when(prefs.getUnitsDistanceStringValue()).thenReturn(getContext().getString(R.string.list_distance_value_km));
        when(prefs.getUnitsEnergyConsumptionStringValue()).thenReturn(getContext().getString(R.string.list_energy_consumption_value_kwh_100km));

        CurrentValuesSingleton.reset().setPreferences(prefs);
    }

}
