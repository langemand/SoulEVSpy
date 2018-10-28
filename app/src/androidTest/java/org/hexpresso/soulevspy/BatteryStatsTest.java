package org.hexpresso.soulevspy;

import android.test.AndroidTestCase;

import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;
import org.hexpresso.soulevspy.util.BatteryStats;
import org.hexpresso.soulevspy.util.ClientSharedPreferences;

/**
 * Created by Henrik Scheel on 2018-07-19.
 */
public class BatteryStatsTest extends AndroidTestCase {
    public void testBatteryStats_27kWh() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        BatteryStats stats = new BatteryStats();
        assertEquals(null, vals.get("calc.battery_SOH_pct"));
        vals.set("VIN", "KNDJX3AE0H0123456");
        assertEquals(null, vals.get("calc.battery_SOH_pct"));
        vals.set("battery.max_cell_detoriation_pct", 0.0);
        assertEquals(112.96, Math.round(((Double)vals.get("calc.battery_SOH_pct"))*100.0)/100.0);

        vals.set("battery.max_cell_detoriation_pct", 15.0);
        assertEquals(96.02, Math.round(((Double)vals.get("calc.battery_SOH_pct"))*100.0)/100.0);
    }

    public void testBatteryStats_30kWh() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        BatteryStats stats = new BatteryStats();
        assertEquals(null, vals.get("calc.battery_SOH_pct"));
        vals.set("VIN", "KNDJX3AE1J7005477");
        assertEquals(null, vals.get("calc.battery_SOH_pct"));
        vals.set("battery.max_cell_detoriation_pct", 0.0);
        assertEquals(106.00, Math.round(((Double)vals.get("calc.battery_SOH_pct"))*100.0)/100.0);

        vals.set("battery.max_cell_detoriation_pct", 15.0);
        assertEquals(90.10, Math.round(((Double)vals.get("calc.battery_SOH_pct"))*100.0)/100.0);
    }
}
