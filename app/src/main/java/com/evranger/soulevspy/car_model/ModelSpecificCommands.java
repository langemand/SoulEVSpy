package com.evranger.soulevspy.car_model;

import com.evranger.elm327.commands.Command;
import com.evranger.elm327.commands.TimeCommand;
import com.evranger.elm327.commands.general.EcuNameCommand;
import com.evranger.elm327.commands.general.VehicleIdentifierNumberCommand;
import com.evranger.elm327.commands.protocol.ReadInputVoltageCommand;
import com.evranger.elm327.commands.protocol.obd.ObdGetDtcCodesCommand;
import com.evranger.soulevspy.R;
import com.evranger.soulevspy.obd.AmbientTempMessageFilter;
import com.evranger.soulevspy.obd.BatteryChargingMessageFilter;
import com.evranger.soulevspy.obd.EstimatedRangeMessageFilter;
import com.evranger.soulevspy.obd.SpeedAndOdometerMessageFilter;
import com.evranger.soulevspy.obd.StateOfChargePreciseMessageFilter;
import com.evranger.soulevspy.obd.StateOfChargeWithOneDecimalMessageFilter;
import com.evranger.soulevspy.obd.Status050MessageFilter;
import com.evranger.soulevspy.obd.commands.BMS2019Command;
import com.evranger.soulevspy.obd.commands.BasicCommand;
import com.evranger.soulevspy.obd.commands.BatteryManagementSystemCommand;
import com.evranger.soulevspy.obd.commands.Clu2019Command;
import com.evranger.soulevspy.obd.commands.FilteredMonitorCommand;
import com.evranger.soulevspy.obd.commands.LowVoltageDCConverterSystemCommand;
import com.evranger.soulevspy.obd.commands.Mcu2019Command;
import com.evranger.soulevspy.obd.commands.Obc2019Command;
import com.evranger.soulevspy.obd.commands.OnBoardChargerCommand;
import com.evranger.soulevspy.obd.commands.TPMS2019Command;
import com.evranger.soulevspy.obd.commands.TirePressureMSCommand;
import com.evranger.soulevspy.obd.commands.Vmcu2019Command;
import com.evranger.soulevspy.obd.commands.VmcuCommand;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import java.util.ArrayList;

public class ModelSpecificCommands {
    public enum DCConnector {
        ChaDeMo,
        CCS,
        Unknown
    }

    private DCConnector mDCConnector = DCConnector.Unknown;
    private double mFullRange = 212;
    private ArrayList<Command> mLoopCommands = new ArrayList<Command>();
    private boolean mHasLdcData = false;

    public ModelSpecificCommands(ClientSharedPreferences sharedPreferences) {
        if (sharedPreferences.getCarModelStringValue().contentEquals(sharedPreferences.getContext().getString(R.string.list_car_model_value_IoniqEV))) {
            setHyundaiIoniqEV(sharedPreferences);
        } else if (sharedPreferences.getCarModelStringValue().contentEquals(sharedPreferences.getContext().getString(R.string.list_car_model_value_eSoul)) ||
                sharedPreferences.getCarModelStringValue().contentEquals(sharedPreferences.getContext().getString(R.string.list_car_model_value_KonaEV)) ||
                sharedPreferences.getCarModelStringValue().contentEquals(sharedPreferences.getContext().getString(R.string.list_car_model_value_eNiro))) {
            setKiaeSoul(sharedPreferences);
        } else if (sharedPreferences.getCarModelStringValue().contentEquals(sharedPreferences.getContext().getString(R.string.list_car_model_value_SoulEV2015)) ||
                sharedPreferences.getCarModelStringValue().contentEquals(sharedPreferences.getContext().getString(R.string.list_car_model_value_BlueOnEV)) ||
                sharedPreferences.getCarModelStringValue().contentEquals(sharedPreferences.getContext().getString(R.string.list_car_model_value_RayEV))) {
            setKiaSoulEV(sharedPreferences);
        } else if (sharedPreferences.getCarModelStringValue().contentEquals(sharedPreferences.getContext().getString(R.string.list_car_model_value_MonitorMode))) {
            setMonitorMode(sharedPreferences);
        } else { // Unknown car model ...?
            setKiaSoulEV(sharedPreferences);
        }
    }

    private void setMonitorMode(ClientSharedPreferences sharedPreferences) {
        mDCConnector = DCConnector.CCS;
        mFullRange = 500;

        mLoopCommands.add(new TimeCommand(sharedPreferences.getContext().getResources().getString(R.string.col_system_scan_start_time_ms)));
        mLoopCommands.add(new ReadInputVoltageCommand());
        mLoopCommands.add(new BasicCommand("AT SH 7DF"));
        mLoopCommands.add(new ObdGetDtcCodesCommand());  // Get stored DTC Codes
        mLoopCommands.add(new EcuNameCommand()); // Get ECU names
        mLoopCommands.add(new BasicCommand("AT CRA"));
        mLoopCommands.add(new BasicCommand("AT AR"));
        BasicCommand monitorAllCmd = new BasicCommand("AT MA");
        monitorAllCmd.setTimeoutMs(60000);
        mLoopCommands.add(monitorAllCmd); // Monitor all messages

        mLoopCommands.add(new TimeCommand(sharedPreferences.getContext().getResources().getString(R.string.col_system_scan_end_time_ms)));
    }

    private void setKiaeSoul(ClientSharedPreferences sharedPreferences) {
        mDCConnector = DCConnector.CCS;
        mFullRange = 452;

        mLoopCommands.add(new TimeCommand(sharedPreferences.getContext().getResources().getString(R.string.col_system_scan_start_time_ms)));
        mLoopCommands.add(new ReadInputVoltageCommand());
        mLoopCommands.add(new BasicCommand("AT SH 7DF"));
        mLoopCommands.add(new ObdGetDtcCodesCommand());  // Get stored DTC Codes
        mLoopCommands.add(new EcuNameCommand()); // Get ECU names
        mLoopCommands.add(new Mcu2019Command());
        mLoopCommands.add(new Obc2019Command());
        mLoopCommands.add(new Vmcu2019Command());
        mLoopCommands.add(new BMS2019Command());
        mLoopCommands.add(new TPMS2019Command());
        mLoopCommands.add(new Clu2019Command());
//        mLoopCommands.add(new Aircon2019Command());

        mLoopCommands.add(new TimeCommand(sharedPreferences.getContext().getResources().getString(R.string.col_system_scan_end_time_ms)));
    }

    private void setHyundaiIoniqEV(ClientSharedPreferences sharedPreferences) {
        mDCConnector = DCConnector.CCS;
        mFullRange = 280;

        mLoopCommands.add(new TimeCommand(sharedPreferences.getContext().getResources().getString(R.string.col_system_scan_start_time_ms)));
        mLoopCommands.add(new ReadInputVoltageCommand());
        mLoopCommands.add(new ObdGetDtcCodesCommand());  // Get stored DTC Codes
        mLoopCommands.add(new EcuNameCommand()); // Get ECU names
        mLoopCommands.add(new BatteryManagementSystemCommand(false));

        mLoopCommands.add(new Vmcu2019Command());
        mLoopCommands.add(new Mcu2019Command());
        mLoopCommands.add(new TPMS2019Command());
// Doesn't work on Lottes Ioniq EV?
        mLoopCommands.add(new Obc2019Command());
        mLoopCommands.add(new Clu2019Command());
// end

        mLoopCommands.add(new TimeCommand(sharedPreferences.getContext().getResources().getString(R.string.col_system_scan_end_time_ms)));
    }

    private void setKiaSoulEV(ClientSharedPreferences sharedPreferences) {
        mDCConnector = DCConnector.ChaDeMo;
        mFullRange = 212;
        mHasLdcData = true;

        mLoopCommands.add(new TimeCommand(sharedPreferences.getContext().getResources().getString(R.string.col_system_scan_start_time_ms)));
//        mLoopCommands.add(new BasicCommand("AT AR")); // Try Auto Receive
//        mLoopCommands.add(new BasicCommand("01 00")); // Try Get supported PIDs
        mLoopCommands.add(new ReadInputVoltageCommand());
        mLoopCommands.add(new BasicCommand("AT SH 7DF"));
        VehicleIdentifierNumberCommand vinCmd = new VehicleIdentifierNumberCommand();
        vinCmd.setTimeoutMs(4000);
        mLoopCommands.add(vinCmd);
        mLoopCommands.add(new ObdGetDtcCodesCommand());  // Get stored DTC Codes
        mLoopCommands.add(new EcuNameCommand()); // Get ECU names
        mLoopCommands.add(new BatteryManagementSystemCommand(true));
        mLoopCommands.add(new OnBoardChargerCommand());
        mLoopCommands.add(new LowVoltageDCConverterSystemCommand());
        mLoopCommands.add(new VmcuCommand());
        mLoopCommands.add(new FilteredMonitorCommand(new AmbientTempMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new StateOfChargeWithOneDecimalMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new StateOfChargePreciseMessageFilter()));
//        mLoopCommands.add(new FilteredMonitorCommand(new SpeedPreciseMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new SpeedAndOdometerMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new BatteryChargingMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new EstimatedRangeMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new Status050MessageFilter()));
//        mLoopCommands.add(new FilteredMonitorCommand(new Status55DMessageFilter()));
        mLoopCommands.add(new TirePressureMSCommand());

        // Note: No values extracted below - just logging interresting CAN PIDs for later analysis!
//        mLoopCommands.add(new FilteredMonitorCommand(new Status55DMessageFilter())); // No good
//        mLoopCommands.add(new FilteredMonitorCommand(new EstimatedRangeMessageFilter()));
//        mLoopCommands.add(new FilteredMonitorCommand(new StatusLoggingMessageFilter("202")));
//        mLoopCommands.add(new FilteredMonitorCommand(new StatusLoggingMessageFilter("55D")));
//        mLoopCommands.add(new FilteredMonitorCommand(new StatusLoggingMessageFilter("595")));

//        mLoopCommands.add(new BasicCommand("AT LP")); // Try Low Power
        mLoopCommands.add(new TimeCommand(sharedPreferences.getContext().getResources().getString(R.string.col_system_scan_end_time_ms)));
    }

    public ArrayList<Command> getLoopCommands() {
        return mLoopCommands;
    }

    public boolean hasChademo() {
        return mDCConnector == DCConnector.ChaDeMo;
    }

    public boolean hasCCS() {
        return mDCConnector == DCConnector.CCS;
    }

    public double getFullRange() {
        return mFullRange;
    }

    public boolean hasLdcData() {
        return mHasLdcData;
    }
}
