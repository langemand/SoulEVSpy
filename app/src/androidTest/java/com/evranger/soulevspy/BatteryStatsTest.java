package com.evranger.soulevspy;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.BatteryStats;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Henrik Scheel on 2018-07-19.
 */
@RunWith(AndroidJUnit4.class)
public class BatteryStatsTest {
    private CurrentValuesSingleton vals;

    @Before
    public void init() {
        vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(InstrumentationRegistry.getTargetContext());
        vals.setPreferences(prefs);
    }

    @Test
    public void testBatteryStats_27kWh() {
        BatteryStats stats = new BatteryStats();
        assertEquals(null, vals.get(R.string.col_calc_battery_soh_pct));
        vals.set(R.string.col_VIN, "KNDJX3AE0H0123456");
        assertEquals(null, vals.get(R.string.col_calc_battery_soh_pct));
        vals.set(R.string.col_battery_min_cell_deterioration_pct, 0.0);
        vals.set(R.string.col_battery_max_cell_deterioration_pct, 0.0);
        assertEquals(null, vals.get(R.string.col_calc_battery_soh_pct));
        vals.set(R.string.col_system_scan_end_time_ms, 42);
        assertEquals(111.47, Math.round(((Double)vals.get(R.string.col_calc_battery_soh_pct))*100.0)/100.0);

        vals.set(R.string.col_battery_min_cell_deterioration_pct, 10.0);
        vals.set(R.string.col_system_scan_end_time_ms, 42);
        assertEquals(100.8, Math.round(((Double)vals.get(R.string.col_calc_battery_soh_pct))*100.0)/100.0);

        vals.set(R.string.col_battery_max_cell_deterioration_pct, 30.0);
        vals.set(R.string.col_system_scan_end_time_ms, 42);
        assertEquals(79.47, Math.round(((Double)vals.get(R.string.col_calc_battery_soh_pct))*100.0)/100.0);
    }

    @Test
    public void testBatteryStats_27kWh_40_pct_SOH() {
        BatteryStats stats = new BatteryStats();
        assertEquals(null, vals.get(R.string.col_calc_battery_soh_pct));
        vals.set(R.string.col_VIN, "KNDJX3AE2F7002960");
        assertEquals(null, vals.get(R.string.col_calc_battery_soh_pct));
        vals.set(R.string.col_battery_min_cell_deterioration_pct, 0.0);
        vals.set(R.string.col_battery_max_cell_deterioration_pct, 64.7);
        assertEquals(null, vals.get(R.string.col_calc_battery_soh_pct));
        vals.set(R.string.col_system_scan_end_time_ms, 42);
        assertEquals(42.45, Math.round(((Double)vals.get(R.string.col_calc_battery_soh_pct))*100.0)/100.0);
    }

    @Test
    public void testBatteryStats_30kWh() {
        BatteryStats stats = new BatteryStats();
        assertEquals(null, vals.get("calc.battery_SOH_pct"));
        vals.set(R.string.col_VIN, "KNDJX3AE1J7005477");
        assertEquals(null, vals.get("calc.battery_SOH_pct"));
        vals.set(R.string.col_battery_min_cell_deterioration_pct, 0.0);
        vals.set(R.string.col_battery_max_cell_deterioration_pct, 0.0);
        vals.set(R.string.col_system_scan_end_time_ms, 42);
        assertEquals(null, vals.get(R.string.col_calc_battery_soh_pct));

        vals.set(R.string.col_battery_max_cell_deterioration_pct, 10.0);
        vals.set(R.string.col_system_scan_end_time_ms, 42);

        assertEquals(null, vals.get(R.string.col_calc_battery_soh_pct));
    }
}
