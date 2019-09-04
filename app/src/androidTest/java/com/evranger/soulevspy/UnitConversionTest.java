package com.evranger.soulevspy;

import androidx.test.InstrumentationRegistry;

import com.evranger.soulevspy.util.ClientSharedPreferences;
import com.evranger.soulevspy.util.Unit;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static androidx.test.InstrumentationRegistry.getContext;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UnitConversionTest extends BaseUnitConversionTest {

    public UnitConversionTest() {
        super("IoniqEV");
    }

    @Test
    public void testPressureConversionPsi() {
        Unit unit = new Unit();

        assertEquals(0.0, unit.convertPres(0.0));
        assertEquals(100.0, unit.convertPres(100.0));
    }

    @Test
    public void testPressureConversionBar() {
        when(prefs.getUnitsPressureStringValue()).thenReturn(getContext().getString(R.string.list_pressure_value_bar));
        Unit unit = new Unit();

        assertEquals(0.0, unit.convertPres(0.0));
        assertEquals(6.89475729, unit.convertPres(100.0));
    }

    @Test
    public void testTemperatureConversionMetric() {
        Unit unit = new Unit();

        assertEquals(0.0, unit.convertTemp(0.0));
        assertEquals(100.0, unit.convertTemp(100.0));
    }

    @Test
    public void testTemperatureConversionUs() {
        when(prefs.getUnitsTemperatureStringValue()).thenReturn(getContext().getString(R.string.list_temperature_value_f));
        Unit unit = new Unit();

        assertEquals(32.0, unit.convertTemp(0.0));
        assertEquals(212.0, unit.convertTemp(100.0));
    }

    @Test
    public void testDistanceConversionMetric() {
        Unit unit = new Unit();

        assertEquals(0.0, unit.convertDist(0.0));
        assertEquals(1000000.0, unit.convertDist(1000000.0));
    }

    @Test
    public void testDistanceConversionUs() {
        when(prefs.getUnitsDistanceStringValue()).thenReturn(getContext().getString(R.string.list_distance_value_mi));

        Unit unit = new Unit();

        assertEquals(0.0, unit.convertDist(0.0));
        assertEquals(621371.192, unit.convertDist(1000000.0));
    }

    @Test
    public void testConsumptionConversionPerDistanceMetric() {
        when(prefs.getUnitsEnergyConsumptionStringValue()).thenReturn(getContext().getString(R.string.list_energy_consumption_value_kwh_100km));
        Unit unit = new Unit();

        assertEquals(0.0, unit.convertConsumption(0.0));
        assertEquals(100.0, unit.convertConsumption(1000.0));
    }

    @Test
    public void testConsumptionConversionPerEnergyMetric() {
        when(prefs.getUnitsEnergyConsumptionStringValue()).thenReturn(getContext().getString(R.string.list_energy_consumption_value_km_kwh));
        Unit unit = new Unit();

        assertEquals(1000.0, unit.convertConsumption(1.0));
        assertEquals(1.0, unit.convertConsumption(1000.0));
    }

    @Test
    public void testConsumptionConversionPerDistanceUs() {
        when(prefs.getUnitsEnergyConsumptionStringValue()).thenReturn(getContext().getString(R.string.list_energy_consumption_value_kwh_100mi));
        Unit unit = new Unit();

        assertEquals(0.0, unit.convertConsumption(0.0));
        assertEquals(160.9344000614692, unit.convertConsumption(1000.0));
    }

    @Test
    public void testConsumptionConversionPerEnergyUs() {
        when(prefs.getUnitsEnergyConsumptionStringValue()).thenReturn(getContext().getString(R.string.list_energy_consumption_value_mi_kwh));
        Unit unit = new Unit();

        assertEquals(621.371192, unit.convertConsumption(1.0));
        assertEquals(0.621371192, unit.convertConsumption(1000.0));
    }
}
