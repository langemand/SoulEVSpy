package org.hexpresso.soulevspy.advisor;

import android.test.AndroidTestCase;

import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;
import org.hexpresso.soulevspy.util.ClientSharedPreferences;

public class EnergyWatcherTest extends AndroidTestCase {
    static double minDiff = 5.0;
    public void testEnergyWatcher() {
        CurrentValuesSingleton mValues = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        mValues.setPreferences(prefs);

        EnergyWatcher sut = new EnergyWatcher();

        mValues.set(R.string.col_orig_capacity_kWh, 98.8);

        mValues.set(R.string.col_system_scan_start_time_ms, 0L);
        mValues.set(R.string.col_route_lat_deg, 10.0);
        mValues.set(R.string.col_route_lng_deg, 10.0);
        mValues.set(R.string.col_car_odo_km, 0.0);
        mValues.set(R.string.col_battery_precise_SOC, 100.0);
        mValues.set(R.string.col_car_speed_kph, 0.0);
        mValues.set(R.string.col_system_scan_end_time_ms, 1L);// Trig listener


        // We drive 100 meters in one minute, using 0.1 % of battery capacity
        mValues.set(R.string.col_system_scan_start_time_ms, 60000L);
        mValues.set(R.string.col_route_lat_deg, 10.0009);
        mValues.set(R.string.col_route_lng_deg, 10.0);
        mValues.set(R.string.col_car_odo_km, 0.1);
        mValues.set(R.string.col_battery_precise_SOC, 99.9);
        mValues.set(R.string.col_car_speed_kph, 6.0);
        mValues.set(R.string.col_system_scan_end_time_ms, 60001L); // Trig listener

        assertTrue(Math.abs(1000.0 - (Double)mValues.get("calc.consumption_01_WhPerkm")) < minDiff );
        assertTrue(Math.abs(1000.0 - (Double)mValues.get("calc.consumption_02_WhPerkm")) < minDiff );
        assertTrue(Math.abs(1000.0 - (Double)mValues.get("calc.consumption_05_WhPerkm")) < minDiff );
        assertTrue(Math.abs(1000.0 - (Double)mValues.get("calc.consumption_10_WhPerkm")) < minDiff );
        assertTrue(Math.abs(1000.0 - (Double)mValues.get("calc.consumption_20_WhPerkm")) < minDiff );
        assertTrue(Math.abs(1000.0 - (Double)mValues.get("calc.consumption_50_WhPerkm")) < minDiff );


        // We drive 100 meters in 30 seconds, using 0.2 % of battery capacity
        mValues.set(R.string.col_system_scan_start_time_ms, 90000L);
        mValues.set(R.string.col_route_lat_deg, 10.0018);
        mValues.set(R.string.col_route_lng_deg, 10.0);
        mValues.set(R.string.col_car_odo_km, 0.2);
        mValues.set(R.string.col_battery_precise_SOC, 99.7);
        mValues.set(R.string.col_car_speed_kph, 12.0);
        mValues.set(R.string.col_system_scan_end_time_ms, 90001L); // Trig listener

        double above = 1500;
        assertTrue(Math.abs(above - (Double)mValues.get("calc.consumption_01_WhPerkm")) < minDiff );
        assertTrue(Math.abs(above - (Double)mValues.get("calc.consumption_02_WhPerkm")) < minDiff );
        assertTrue(Math.abs(above - (Double)mValues.get("calc.consumption_05_WhPerkm")) < minDiff );
        assertTrue(Math.abs(above - (Double)mValues.get("calc.consumption_10_WhPerkm")) < minDiff );
        assertTrue(Math.abs(above - (Double)mValues.get("calc.consumption_20_WhPerkm")) < minDiff );
        assertTrue(Math.abs(above - (Double)mValues.get("calc.consumption_50_WhPerkm")) < minDiff );


        // We drive 100 meters in 20 seconds, using 0.4 % of battery capacity
        mValues.set(R.string.col_system_scan_start_time_ms, 110000L);
        mValues.set(R.string.col_route_lat_deg, 10.0027);
        mValues.set(R.string.col_route_lng_deg, 10.0);
        mValues.set(R.string.col_car_odo_km, 0.3);
        mValues.set(R.string.col_battery_precise_SOC, 99.3);
        mValues.set(R.string.col_car_speed_kph, 18.0);
        mValues.set(R.string.col_system_scan_end_time_ms, 110001L); // Trig listener

        double above2 = 2333;
        assertTrue(Math.abs(above2 - (Double) mValues.get("calc.consumption_01_WhPerkm")) < minDiff);
        assertTrue(Math.abs(above2 - (Double) mValues.get("calc.consumption_02_WhPerkm")) < minDiff);
        assertTrue(Math.abs(above2 - (Double) mValues.get("calc.consumption_05_WhPerkm")) < minDiff);
        assertTrue(Math.abs(above2 - (Double) mValues.get("calc.consumption_10_WhPerkm")) < minDiff);
        assertTrue(Math.abs(above2 - (Double) mValues.get("calc.consumption_20_WhPerkm")) < minDiff);
        assertTrue(Math.abs(above2 - (Double) mValues.get("calc.consumption_50_WhPerkm")) < minDiff);
    }

}
