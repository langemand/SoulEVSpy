package com.evranger.soulevspy.obd.commands;

import com.evranger.elm327.commands.AbstractMultiCommand;
import com.evranger.elm327.commands.filters.RegularExpressionResponseFilter;
import com.evranger.elm327.commands.filters.RemoveSpacesResponseFilter;
import com.evranger.soulevspy.R;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.BMS2019Parser;


/**
 * Battery Management System command for the Kona, e-Niro and e-Soul
 * Note : This command assumes that headers are active since multiple ECUs can answer to those commands!
 *
 * Originally created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-22.
 * Cloned from BatteryManagementSystemCommand to BMS2019Command by Henrik Scheel <henrik.scheel@spjeldager.dk> on 2019-07-02.
 */
public class BMS2019Command extends AbstractMultiCommand {

    BasicCommand mCmd210101;
    BasicCommand mCmd210102;
    BasicCommand mCmd210103;
    BasicCommand mCmd210104;
    BasicCommand mCmd210105;
    BasicCommand mCmd210106;

    private Double mBatteryStateOfCharge = null;
    BMS2019Parser mBmsParser = new BMS2019Parser();
    /**
     * Constructor
     */
    public BMS2019Command() {
        mCmd210101 = new BasicCommand("22 01 01");
        mCmd210102 = new BasicCommand("22 01 02");
        mCmd210103 = new BasicCommand("22 01 03");
        mCmd210104 = new BasicCommand("22 01 04");
        mCmd210105 = new BasicCommand("22 01 05");
        mCmd210106 = new BasicCommand("22 01 06");

        addCommand(new BasicCommand("AT SH 7E4"));
        addCommand(new BasicCommand("AT CRA 7EC"));
        addCommand(mCmd210101);
        addCommand(mCmd210102);
        addCommand(mCmd210103);
        addCommand(mCmd210104);
        addCommand(mCmd210105);
        addCommand(mCmd210106);

//        withAutoProcessResponse(true);
        // Only keep messages from 7EC ECU
        addResponseFilter(new RegularExpressionResponseFilter("^7EC(.*)$"));
        addResponseFilter(new RemoveSpacesResponseFilter());
    }

    @Override
    public void doProcessResponse() {
        boolean ok = true;
        ok = ok && mBmsParser.parseMessage220101(mCmd210101.getResponse().rawResponse());
        ok = ok && mBmsParser.parseMessage220102(mCmd210102.getResponse().rawResponse());
        ok = ok && mBmsParser.parseMessage220103(mCmd210103.getResponse().rawResponse());
        ok = ok && mBmsParser.parseMessage220104(mCmd210104.getResponse().rawResponse());
        ok = ok && mBmsParser.parseMessage220105(mCmd210105.getResponse().rawResponse());
        ok = ok && mBmsParser.parseMessage220106(mCmd210106.getResponse().rawResponse());
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        if (!ok) {
            vals.set("BMS.data_status", "OLD");
            return;
        }

        BMS2019Parser.Data data = mBmsParser.getParsedData();
        vals.set(R.string.col_battery_is_charging, Boolean.valueOf(data.bmsIsCharging));
        vals.set(R.string.col_battery_ChaDeMo_is_plugged, Boolean.valueOf(data.bmsChademoIsPlugged));
        vals.set(R.string.col_battery_J1772_is_plugged, Boolean.valueOf(data.bmsJ1772IsPlugged));
        vals.set(R.string.col_battery_SOC, Double.valueOf(data.stateOfCharge));
        vals.set(R.string.col_battery_display_SOC, Double.valueOf(data.stateOfChargeDisplay));
        vals.set(R.string.col_battery_DC_current_A, Double.valueOf(data.batteryCurrent));
        vals.set(R.string.col_battery_DC_voltage_V, Double.valueOf(data.batteryDcVoltage));
        vals.set(R.string.col_battery_available_charge_power_kW, Double.valueOf(data.availableChargePower));
        vals.set(R.string.col_battery_available_discharge_power_kW, Double.valueOf(data.availableDischargePower));
        vals.set(R.string.col_battery_accumulative_charge_current_Ah, Double.valueOf(data.accumulativeChargeCurrent));
        vals.set(R.string.col_battery_accumulative_discharge_current_Ah, Double.valueOf(data.accumulativeDischargeCurrent));
        vals.set(R.string.col_battery_accumulative_charge_power_kWh, Double.valueOf(data.accumulativeChargePower));
        vals.set(R.string.col_battery_accumulative_discharge_power_kWh, Double.valueOf(data.accumulativeDischargePower));
        vals.set(R.string.col_battery_accumulative_operating_time_s, Integer.valueOf(data.accumulativeOperatingTime));
        vals.set(R.string.col_battery_drive_motor_rpm, Integer.valueOf(data.driveMotorSpeed));
        vals.set(R.string.col_battery_inlet_temperature_C, Integer.valueOf(data.batteryInletTemperature));
        vals.set(R.string.col_battery_max_temperature_C, Integer.valueOf(data.batteryMaxTemperature));
        vals.set(R.string.col_battery_min_temperature_C, Integer.valueOf(data.batteryMinTemperature));
        vals.set(R.string.col_battery_heat1_temperature_C, Integer.valueOf(data.heat1Temperature));
        vals.set(R.string.col_battery_cooling_water_temperature_C, Integer.valueOf(data.coolingWaterTemperature));
        int i = 1;
        for (int temp : data.batteryModuleTemperature) {
            vals.set(R.string.col_battery_module_temperature, i++, "_C", Integer.valueOf(temp));
        }
        i = 0;
        for (double volt : data.batteryCellVoltage) {
            if (i>=data.numberOfCells) {
                break;
            }
            vals.set(R.string.col_battery_cell_voltage, i++, "_V", Double.valueOf(volt));
        }
        vals.set(R.string.col_battery_max_cell_voltage_V, Double.valueOf(data.maxCellVoltage));
        vals.set(R.string.col_battery_max_cell_voltage_n, Integer.valueOf(data.maxCellVoltageNo));
        vals.set(R.string.col_battery_min_cell_voltage_V, Double.valueOf(data.minCellVoltage));
        vals.set(R.string.col_battery_min_cell_voltage_n, Integer.valueOf(data.minCellVoltageNo));
        vals.set(R.string.col_battery_state_of_health_pct, Double.valueOf(data.stateOfHealth));
        vals.set(R.string.col_battery_max_cell_deterioration_n, Integer.valueOf(data.maxDeteriorationCellNo));
        vals.set(R.string.col_battery_min_cell_deterioration_pct, Double.valueOf(data.minDeterioration));
        vals.set(R.string.col_battery_min_cell_deterioration_n, Integer.valueOf(data.minDeteriorationCellNo));
        vals.set(R.string.col_battery_auxiliaryVoltage_V, Double.valueOf(data.auxiliaryBatteryVoltage));
        vals.set(R.string.col_battery_fan_status, data.fanStatus.toString());
        vals.set(R.string.col_battery_fan_feedback_signal, Integer.valueOf(data.fanFeedbackSignal));
        vals.set(R.string.col_battery_airbag_hwire_duty, Integer.valueOf(data.airbagHwireDuty));
        vals.set("BMS.data_status", "OK");
    }
}