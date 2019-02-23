package org.hexpresso.soulevspy.obd.commands;

import org.hexpresso.elm327.commands.AbstractMultiCommand;
import org.hexpresso.elm327.commands.AbstractCommand;
import org.hexpresso.elm327.commands.filters.RegularExpressionResponseFilter;
import org.hexpresso.elm327.commands.filters.RemoveSpacesResponseFilter;
import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;
import org.hexpresso.soulevspy.util.BatteryManagementSystemParser;


/**
 * Battery Management System command for the Soul EV
 * Note : This command assumes that headers are active since 2 ECUs answer to those commands!
 *
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-22.
 */
public class BatteryManagementSystemCommand extends AbstractMultiCommand {

    BasicCommand mCmd2101;
    BasicCommand mCmd2102;
    BasicCommand mCmd2103;
    BasicCommand mCmd2104;
    BasicCommand mCmd2105;

    private Double mBatteryStateOfCharge = null;
    BatteryManagementSystemParser mBmsParser = new BatteryManagementSystemParser();
    /**
     * Constructor
     */
    public BatteryManagementSystemCommand() {
        mCmd2101 = new BasicCommand("21 01");
        mCmd2102 = new BasicCommand("21 02");
        mCmd2103 = new BasicCommand("21 03");
        mCmd2104 = new BasicCommand("21 04");
        mCmd2105 = new BasicCommand("21 05");

        addCommand(new BasicCommand("AT SH 7DF")); //"AT SH 7E4"));
        addCommand(new BasicCommand("AT CRA 7EC"));
        addCommand(mCmd2101);
        addCommand(mCmd2102);
        addCommand(mCmd2103);
        addCommand(mCmd2104);
        addCommand(mCmd2105);

//        withAutoProcessResponse(true);
        // Only keep messages from 7EC ECU
        addResponseFilter(new RegularExpressionResponseFilter("^7EC(.*)$"));
        addResponseFilter(new RemoveSpacesResponseFilter());
    }

    @Override
    public void doProcessResponse() {
        boolean ok = true;
        ok = ok && mBmsParser.parseMessage2101(mCmd2101.getResponse().rawResponse());
        ok = ok && mBmsParser.parseMessage2102(mCmd2102.getResponse().rawResponse());
        ok = ok && mBmsParser.parseMessage2103(mCmd2103.getResponse().rawResponse());
        ok = ok && mBmsParser.parseMessage2104(mCmd2104.getResponse().rawResponse());
        ok = ok && mBmsParser.parseMessage2105(mCmd2105.getResponse().rawResponse());
        if (!ok)
            return;

        BatteryManagementSystemParser.Data data = mBmsParser.getParsedData();
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
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
        vals.set(R.string.col_battery_heat2_temperature_C, Integer.valueOf(data.heat2Temperature));
        int i = 1;
        for (int temp : data.batteryModuleTemperature) {
            vals.set(R.string.col_battery_module_temperature, i++, "_C", Integer.valueOf(temp));
        }
        i = 0;
        for (double volt : data.batteryCellVoltage) {
            vals.set(R.string.col_battery_cell_voltage, i++, "_V", Double.valueOf(volt));
        }
        vals.set(R.string.col_battery_max_cell_voltage_V, Double.valueOf(data.maxCellVoltage));
        vals.set(R.string.col_battery_max_cell_voltage_n, Integer.valueOf(data.maxCellVoltageNo));
        vals.set(R.string.col_battery_min_cell_voltage_V, Double.valueOf(data.minCellVoltage));
        vals.set(R.string.col_battery_min_cell_voltage_n, Integer.valueOf(data.minCellVoltageNo));
        vals.set(R.string.col_battery_max_cell_detoriation_pct, Double.valueOf(data.maxDeterioration));
        vals.set(R.string.col_battery_max_cell_detoriation_n, Integer.valueOf(data.maxDeteriorationCellNo));
        vals.set(R.string.col_battery_min_cell_detoriation_pct, Double.valueOf(data.minDeterioration));
        vals.set(R.string.col_battery_min_cell_detoriation_n, Integer.valueOf(data.minDeteriorationCellNo));
        vals.set(R.string.col_battery_auxiliaryVoltage_V, Double.valueOf(data.auxiliaryBatteryVoltage));
        vals.set(R.string.col_battery_fan_status, data.fanStatus.toString());
        vals.set(R.string.col_battery_fan_feedback_signal, Integer.valueOf(data.fanFeedbackSignal));
        vals.set(R.string.col_battery_airbag_hwire_duty, Integer.valueOf(data.airbagHwireDuty));
    }

    /**
     * State of Charge (%)
     */
    public double getStateOfCharge() {
        mBatteryStateOfCharge = getResponse().get(1, 1) * 0.5;
        return mBatteryStateOfCharge;
    }
    public double getBatteryCurrent() {
        int msb = getResponse().get(1, 7);
        double batteryCurrent = msb + getResponse().get(2, 1) / 256.0;
        if (msb > 128)
            batteryCurrent = batteryCurrent - 256.0;
        return batteryCurrent;
    }
    public double getBatteryVoltage() {
        double batteryVoltage = getResponse().get(2, 2) + getResponse().get(2, 3) << 8;
        return batteryVoltage;
    }
}