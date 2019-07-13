package com.evranger.soulevspy.io;

import android.content.res.Resources;
import android.os.SystemClock;

import com.evranger.elm327.commands.Command;
import com.evranger.elm327.io.Service;
import com.evranger.elm327.io.ServiceStates;
import com.evranger.elm327.log.CommLog;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import com.evranger.soulevspy.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by henrik on 10/06/2017.
 */

public class ReadLoop {
    ClientSharedPreferences mSharedPreferences;
    private Service mService;
    private ArrayList<Command> mCommands;
    private Thread mLoopThread = null;
    private List<String> mColumnsToLog = null;
    private DecimalFormat oneDigitFormat = new DecimalFormat("0");

    public ReadLoop(ClientSharedPreferences sharedPreferences, Service service, ArrayList<Command> commands) {
        mSharedPreferences = sharedPreferences;
        Resources res = mSharedPreferences.getContext().getResources();
        mColumnsToLog = new ArrayList<String>();
                mColumnsToLog.addAll(Arrays.asList(res.getString(R.string.col_VIN)
                , "ECU.name.7EC"
                , "OBD.DtcCodes.7EC"
                , res.getString(R.string.col_ELM327_voltage)
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
                , res.getString(R.string.col_battery_max_temperature_C)));
        for (int i = 1; i <= 8; ++i) {
            mColumnsToLog.add(res.getString(R.string.col_battery_module_temperature) + oneDigitFormat.format(i) + "_C");
        }
        mColumnsToLog.addAll(Arrays.asList(res.getString(R.string.col_battery_heat1_temperature_C)
                , res.getString(R.string.col_battery_heat2_temperature_C)
                , res.getString(R.string.col_battery_auxiliaryVoltage_V)
        ));

        mColumnsToLog.addAll(Arrays.asList(res.getString(R.string.col_battery_ChaDeMo_is_plugged),
                res.getString(R.string.col_battery_J1772_is_plugged),
                res.getString(R.string.col_battery_accumulative_charge_current_Ah),
                res.getString(R.string.col_battery_accumulative_discharge_current_Ah),
                res.getString(R.string.col_battery_airbag_hwire_duty),
                res.getString(R.string.col_battery_available_charge_power_kW),
                res.getString(R.string.col_battery_available_discharge_power_kW)));
        for (int i = 0; i < 100; ++i) {
            mColumnsToLog.add("battery.cell_voltage" + oneDigitFormat.format(i) + "_V");
        }
        mColumnsToLog.addAll(Arrays.asList(res.getString(R.string.col_battery_drive_motor_rpm),
                res.getString(R.string.col_battery_fan_status),
                res.getString(R.string.col_battery_max_cell_deterioration_n),
                res.getString(R.string.col_battery_max_cell_deterioration_pct),
                res.getString(R.string.col_battery_max_cell_voltage_V),
                res.getString(R.string.col_battery_max_cell_voltage_n),
                res.getString(R.string.col_battery_min_cell_deterioration_n),
                res.getString(R.string.col_battery_min_cell_deterioration_pct),
                res.getString(R.string.col_battery_min_cell_voltage_V),
                res.getString(R.string.col_battery_min_cell_voltage_n),
                res.getString(R.string.col_calc_battery_soh_pct),
//                res.getString(R.string.col_watcher_consumption),
//                res.getString(R.string.col_nom_capacity_kWh),
//                res.getString(R.string.col_orig_capacity_kWh),
//                res.getString(R.string.charger_locations_update_time_ms),
                res.getString(R.string.col_ldc_in_DC_voltage_V)));
        mColumnsToLog.addAll(Arrays.asList(res.getString(R.string.col_obc_ac_in_V),
                res.getString(R.string.col_obc_dc_out_V),
                res.getString(R.string.col_obc_ac_in_A),
                res.getString(R.string.col_obc_pilot_duty_cycle),
                res.getString(R.string.col_obc_temp_1_C),
                res.getString(R.string.col_obc_temp_2_C),
                res.getString(R.string.col_obc_temp_3_C)));
        mColumnsToLog.addAll(Arrays.asList(res.getString(R.string.col_vmcu_gear_state),
                res.getString(R.string.col_vmcu_eco_off_switch),
                res.getString(R.string.col_vmcu_brake_lamp_on_switch),
                res.getString(R.string.col_vmcu_brake_off_switch),
                res.getString(R.string.col_vmcu_ldc_inhibit),
                res.getString(R.string.col_vmcu_fault_flag_of_mcu),
                res.getString(R.string.col_vmcu_warning_flag_of_mcu), 
                res.getString(R.string.col_vmcu_radiator_fan_request_of_motor),
                res.getString(R.string.col_vmcu_ignition_1),
                res.getString(R.string.col_vmcu_accel_pedal_depth_pct),
                res.getString(R.string.col_vmcu_vehicle_speed_kph),
                res.getString(R.string.col_vmcu_aux_battery_V),
                res.getString(R.string.col_vmcu_inverter_input_V),
                res.getString(R.string.col_vmcu_motor_actual_speed_rpm),
                res.getString(R.string.col_vmcu_motor_torque_command_Nm),
                res.getString(R.string.col_vmcu_estimated_motor_torque_Nm),
                res.getString(R.string.col_vmcu_motor_phase_current_RMS_A),
                res.getString(R.string.col_vmcu_temp_motor_C),
                res.getString(R.string.col_vmcu_temp_mcu_C),
                res.getString(R.string.col_vmcu_temp_heatsink_C)));
        for (int i = 1; i <=4; ++i) {
            mColumnsToLog.add("tire.pressure" + oneDigitFormat.format(i) + "_psi");
            mColumnsToLog.add("tire.temperature" + oneDigitFormat.format(i) + "_C");
        }
        mService = service;
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
        long lastTimeouts = mService.getProtocol().numberOfTimeouts();
        while (!mLoopThread.isInterrupted()) {
            if (mService.getState() != ServiceStates.STATE_CONNECTED || mService.getProtocol().numberOfQueuedCommands() > 0) { // Communication issues may delay response, wait a bit in that case
                SystemClock.sleep(100L);
            } else {
                long timeToWait = 0;
                CommLog.getInstance().flush();
                for (Command command : mCommands) {
                    mService.getProtocol().addCommand(command);
                }
                SystemClock.sleep(2000L);
                if (vals.get(R.string.col_system_scan_start_time_ms) != null) {
                    while (vals.get(R.string.col_system_scan_end_time_ms) == null ||
                            (Long) vals.get(R.string.col_system_scan_end_time_ms) < (Long) vals.get(R.string.col_system_scan_start_time_ms) ||
                            (Long) vals.get(R.string.col_system_scan_start_time_ms) == last_log_time) {
                        SystemClock.sleep(100L);
                    }
                }
                if (mLoopThread.isInterrupted()) {
                    break;
                }
                // Handle protocol exceptions by disconnect (and auto re-connect, if enabled)
                String status = mService.getProtocol().setStatus("");
                long thisTimeouts = mService.getProtocol().numberOfTimeouts();
                long sinceLastTimeouts = thisTimeouts - lastTimeouts;
                lastTimeouts = thisTimeouts;
                try {
                    CommLog.getInstance().log(("Status='"+status+"'\n").getBytes());
                    CommLog.getInstance().log(("TimeOuts="+sinceLastTimeouts+"\n").getBytes());
                } catch (Exception e) {
                    // Ignore
                }

                if (//vals.get("BMS.data_status") != "OK"
                    //||
                    //thisTimeouts >= 3
                    //||
                    (status.length() != 0 && (status.contains("ERR") || status.contains("BUFFER FULL") || status.contains("Broken pipe")))
                    // || vals.get(R.string.col_battery_available_discharge_power_kW) == null
                   )
                {
                    try {
//                        CommLog.getInstance().log(("Disconnected by ReadLoop. BMS.data_status ='"+vals.get("BMS.data_status")+"', status='"+status+"'\n").getBytes());
                        CommLog.getInstance().log(("Disconnected by ReadLoop. Timeouts ='"+thisTimeouts+"', status='"+status+"'\n").getBytes());
                    } catch (Exception e) {
                        // Ignore
                    }
                    CommLog.getInstance().flush();
                    mService.disconnect();
                    timeToWait = 5000;
                }

                if (!mLoopThread.isInterrupted()) {
                    long time_now = System.currentTimeMillis();
                    if (vals.get(R.string.col_system_scan_start_time_ms) != null) {
                        long scan_start_time = (Long) vals.get(R.string.col_system_scan_start_time_ms);
                        timeToWait = (long) (mSharedPreferences.getScanIntervalFloatValue() * 1000) - (time_now - scan_start_time);
                        if (timeToWait < 500) {
                            timeToWait = 500;
                        }
                        CurrentValuesSingleton.getInstance().log(mColumnsToLog);
                    }
                }
                if (vals.get(R.string.col_system_scan_start_time_ms) != null) {
                    last_log_time = (Long) vals.get(R.string.col_system_scan_start_time_ms);
                }
                SystemClock.sleep(timeToWait);
            }
        }
    }
}
