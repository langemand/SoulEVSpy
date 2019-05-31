package com.evranger.soulevspy;

import android.test.AndroidTestCase;

import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.BatteryStats;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import com.evranger.soulevspy.R;

/**
 * Created by Henrik Scheel on 2018-07-19.
 */
public class BatteryStatsTest extends AndroidTestCase {
    public void testBatteryStats_27kWh() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        BatteryStats stats = new BatteryStats();
        assertEquals(null, vals.get(R.string.col_calc_battery_soh_pct));
        vals.set(R.string.col_VIN, "KNDJX3AE0H0123456");
        assertEquals(null, vals.get(R.string.col_calc_battery_soh_pct));
        vals.set(R.string.col_battery_min_cell_detoriation_pct, 0.0);
        vals.set(R.string.col_battery_max_cell_detoriation_pct, 0.0);
        assertEquals(null, vals.get(R.string.col_calc_battery_soh_pct));
        vals.set(R.string.col_system_scan_end_time_ms, 42);
        assertEquals(110.00, Math.round(((Double)vals.get(R.string.col_calc_battery_soh_pct))*100.0)/100.0);

        vals.set(R.string.col_battery_max_cell_detoriation_pct, 30.0);
        vals.set(R.string.col_system_scan_end_time_ms, 42);
        assertEquals(95.00, Math.round(((Double)vals.get(R.string.col_calc_battery_soh_pct))*100.0)/100.0);

        vals.set(R.string.col_battery_min_cell_detoriation_pct, 10.0);
        vals.set(R.string.col_system_scan_end_time_ms, 42);
        assertEquals(90.00, Math.round(((Double)vals.get(R.string.col_calc_battery_soh_pct))*100.0)/100.0);
    }

    public void testBatteryStats_30kWh() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        BatteryStats stats = new BatteryStats();
        assertEquals(null, vals.get("calc.battery_SOH_pct"));
        vals.set(R.string.col_VIN, "KNDJX3AE1J7005477");
        assertEquals(null, vals.get("calc.battery_SOH_pct"));
        vals.set(R.string.col_battery_min_cell_detoriation_pct, 0.0);
        vals.set(R.string.col_battery_max_cell_detoriation_pct, 0.0);
        vals.set(R.string.col_system_scan_end_time_ms, 42);
        assertEquals(null, vals.get(R.string.col_calc_battery_soh_pct));

        vals.set(R.string.col_battery_max_cell_detoriation_pct, 10.0);
        vals.set(R.string.col_system_scan_end_time_ms, 42);

        assertEquals(null, vals.get(R.string.col_calc_battery_soh_pct));
    }
}
