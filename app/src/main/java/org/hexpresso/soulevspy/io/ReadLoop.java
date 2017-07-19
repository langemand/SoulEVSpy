package org.hexpresso.soulevspy.io;

import android.content.res.Resources;
import android.os.Environment;
import android.os.SystemClock;

import org.hexpresso.elm327.commands.Command;
import org.hexpresso.elm327.commands.protocol.TriggerCommand;
import org.hexpresso.elm327.io.Protocol;
import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;
import org.hexpresso.soulevspy.util.ClientSharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by henrik on 10/06/2017.
 */

public class ReadLoop {
    ClientSharedPreferences mSharedPreferences;
    private Protocol mProtocol;
    private ArrayList<Command> mCommands;
//    private TriggerCommand mEndTriggerCommand;
    private Thread mLoopThread = null;
    private List<String> mColumnsToLog = null;
//    volatile private boolean mIsScanOngoing = false;

//    private class EndTriggerCallback implements TriggerCommand.Callback {
//        ReadLoop mReadLoop;
//        EndTriggerCallback(ReadLoop readLoop) {
//            mReadLoop = readLoop;
//        }
//        public void call() {
//            CurrentValuesSingleton.getInstance().set("system.scan_end_time_s", Double.valueOf(System.currentTimeMillis() / 1000.0));
//            CurrentValuesSingleton.getInstance().log(mColumnsToLog);
//            mReadLoop.scanComplete();
//        }
//    }

    public ReadLoop(ClientSharedPreferences sharedPreferences, Protocol protocol, ArrayList<Command> commands) {
        mSharedPreferences = sharedPreferences;
        Resources res = mSharedPreferences.getContext().getResources();
        mColumnsToLog = Arrays.asList(res.getString(R.string.col_VIN)
                , res.getString(R.string.col_system_scan_start_time_ms)
                , res.getString(R.string.col_system_scan_end_time_ms)
                , res.getString(R.string.col_route_time_s)
                , res.getString(R.string.col_route_lat_deg)
                , res.getString(R.string.col_route_lng_deg)
                , res.getString(R.string.col_route_elevation_m)
                , res.getString(R.string.col_route_speed_mps)
                , res.getString(R.string.col_car_speed_kph)
                , res.getString(R.string.col_car_odo_km)
                , res.getString(R.string.col_car_ambient_C)
                , res.getString(R.string.col_car_lights_status)
                , res.getString(R.string.col_car_wipers_status)
                , res.getString(R.string.col_ldc_enabled)
                , res.getString(R.string.col_ldc_out_DC_voltage_V)
                , res.getString(R.string.col_ldc_out_DC_current_A)
                , res.getString(R.string.col_ldc_temperature_C)
                , res.getString(R.string.col_range_estimate_km)
                , res.getString(R.string.col_range_estimate_for_climate_km)
                , res.getString(R.string.col_charging_power_kW)
                , res.getString(R.string.col_battery_is_charging)
                , res.getString(R.string.col_battery_display_SOC)
                , res.getString(R.string.col_battery_SOC)
                , res.getString(R.string.col_battery_decimal_SOC)
                , res.getString(R.string.col_battery_precise_SOC)
                , res.getString(R.string.col_battery_DC_voltage_V)
                , res.getString(R.string.col_battery_DC_current_A)
                , res.getString(R.string.col_battery_accumulative_operating_time_s)
                , res.getString(R.string.col_battery_accumulative_charge_power_kWh)
                , res.getString(R.string.col_battery_accumulative_discharge_power_kWh)
                , res.getString(R.string.col_battery_fan_feedback_signal)
                , res.getString(R.string.col_battery_inlet_temperature_C)
                , res.getString(R.string.col_battery_min_temperature_C)
                , res.getString(R.string.col_battery_max_temperature_C)
                , res.getString(R.string.col_battery_module_temperature) + "0_C"
                , res.getString(R.string.col_battery_module_temperature) + "1_C"
                , res.getString(R.string.col_battery_module_temperature) + "2_C"
                , res.getString(R.string.col_battery_module_temperature) + "3_C"
                , res.getString(R.string.col_battery_module_temperature) + "4_C"
                , res.getString(R.string.col_battery_module_temperature) + "5_C"
                , res.getString(R.string.col_battery_module_temperature) + "6_C"
                , res.getString(R.string.col_battery_module_temperature) + "7_C"
                , res.getString(R.string.col_battery_heat1_temperature_C)
                , res.getString(R.string.col_battery_heat2_temperature_C)
                , res.getString(R.string.col_battery_auxiliaryVoltage_V)
        );
        mProtocol = protocol;
        mCommands = commands;

        // Thread used to run commands in loop
        mLoopThread = new Thread(new Runnable() {

            @Override
            public void run() {
                runCommands();
            }
        });
        mLoopThread.setName("ReadLoopThread");
    }

    public synchronized void start() {
        mLoopThread.start();
    }

    public synchronized void stop() {
        mLoopThread.interrupt();
    }

//    private synchronized void scanComplete() {
//        mIsScanOngoing = false;
//    }

    private void runCommands() {
        Resources res = mSharedPreferences.getContext().getResources();
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        long last_log_time = 0L;
        while (!mLoopThread.isInterrupted()) {
            if (mProtocol.numberOfQueuedCommands() > 0) { // Communication issues may delay response, wait a bit in that case
                SystemClock.sleep(100L);
            } else {
                for (Command command : mCommands) {
                    mProtocol.addCommand(command);
                }
                SystemClock.sleep(2000L);
                long scan_start_time = (Long)vals.get(R.string.col_system_scan_start_time_ms);
                while (vals.get(R.string.col_system_scan_end_time_ms) == null ||
                        (Long)vals.get(R.string.col_system_scan_end_time_ms) < (Long)vals.get(R.string.col_system_scan_start_time_ms) ||
                        (Long)vals.get(R.string.col_system_scan_start_time_ms) == last_log_time) {
                    SystemClock.sleep(100L);
                }
                long scan_end_time = (Long)vals.get(R.string.col_system_scan_end_time_ms);
                long time_now = System.currentTimeMillis();
                long timeToWait = (long)(mSharedPreferences.getScanIntervalFloatValue()*1000) - (time_now - scan_start_time);
                if (timeToWait > 0) {
                    SystemClock.sleep(timeToWait);
                }
                CurrentValuesSingleton.getInstance().log(mColumnsToLog);
                last_log_time = scan_start_time;
            }
        }
    }
}
