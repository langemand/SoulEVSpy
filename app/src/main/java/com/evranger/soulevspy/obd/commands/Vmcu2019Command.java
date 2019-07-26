package com.evranger.soulevspy.obd.commands;

import com.evranger.elm327.commands.AbstractMultiCommand;
import com.evranger.elm327.commands.Response;
import com.evranger.elm327.commands.filters.RegularExpressionResponseFilter;
import com.evranger.obd.ObdMessageData;
import com.evranger.soulevspy.R;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.Unit;

import java.util.List;

/**
 * Created by henrik on 08/05/2019.
 */

public class Vmcu2019Command extends AbstractMultiCommand {
    private BasicCommand mCmd2101 = null;
    private BasicCommand mCmd2102 = null;
    private BasicCommand mCmd1a80 = null;
    private double mKmsPerMile;

    public Vmcu2019Command() {
        mKmsPerMile = 1.0 / Unit.milesPerKm;
        addCommand(new BasicCommand("AT SH 7E2"));
        addCommand(new BasicCommand("AT CRA 7EA"));
        mCmd2101 = new BasicCommand("21 01");
        mCmd2102 = new BasicCommand("21 02");
        mCmd1a80 = new BasicCommand("1A 80");
        mCmd1a80.setNumberOfLinesToRead(5);
        addCommand(mCmd2101);
        addCommand(mCmd2102);
        addCommand(mCmd1a80);

        mCmd2101.addResponseFilter(new RegularExpressionResponseFilter("^\\s*7EA(.*)$"));
        mCmd2102.addResponseFilter(new RegularExpressionResponseFilter("^\\s*7EA(.*)$"));
        mCmd1a80.addResponseFilter(new RegularExpressionResponseFilter("^\\s*7EA(.*)$"));
    }

    public void doProcessResponse() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        try {
            mCmd2101.getResponse().process();
            List<String> lines01 = mCmd2101.getResponse().getLines();
            if (lines01.size() < 4)
                return;

            ObdMessageData obdData01_1 = new ObdMessageData(lines01.get(1));

            // Gearstick
            int gearByte = obdData01_1.getDataByte(2);
            StringBuilder gear = new StringBuilder();
            if ((gearByte & 1) != 0) gear.append("P");
            if ((gearByte & 2) != 0) gear.append("R");
            if ((gearByte & 4) != 0) gear.append("N");
            if ((gearByte & 8) != 0) gear.append("D");
            if ((gearByte & 16) != 0) gear.append("B");
            vals.set(R.string.col_vmcu_gear_state, gear.toString());

            // ECO button
//2015            boolean ecoOffSwitch = (obdData01_1.getDataByte(4) & 32) != 0;
//2015            vals.set(R.string.col_vmcu_eco_off_switch, ecoOffSwitch);

            // Brake Lamp Switch
//2015            boolean brakeLampSwitch = (obdData01_1.getDataByte(6) & 1) != 0;
//2015            vals.set(R.string.col_vmcu_brake_lamp_on_switch, brakeLampSwitch);

            // Brake Switch
//2015            boolean brakeOffSwitch = (obdData01_1.getDataByte(6) >>1 & 1) != 0;
//2015            vals.set(R.string.col_vmcu_brake_off_switch, brakeOffSwitch);

            // LDC Inhibit
//2015            boolean ldcInhibit = (obdData01_1.getDataByte(6) >>7 & 1) != 0;
//2015            vals.set(R.string.col_vmcu_ldc_inhibit, ldcInhibit);

            ObdMessageData obdData00_2 = new ObdMessageData(lines01.get(2));

            // Fault Flag of MCU
//2015            boolean faultFlagOfMcu = (obdData00_2.getDataByte(1) & 1) != 0;
//2015            vals.set(R.string.col_vmcu_fault_flag_of_mcu, faultFlagOfMcu);

            // Warning Flag of MCU
//2015            boolean warningFlagOfMcu = (obdData00_2.getDataByte(1) >>1 & 1) != 0;
//2015            vals.set(R.string.col_vmcu_warning_flag_of_mcu, warningFlagOfMcu);

            // Radiator Fan On/Off Request of Motor
//2015            boolean radiatorFanRequestOfMotor = (obdData00_2.getDataByte(3) >>3 & 1) != 0;
//2015            vals.set(R.string.col_vmcu_radiator_fan_request_of_motor, radiatorFanRequestOfMotor);

            // Ignition 1
//2015            boolean ignition1 = (obdData00_2.getDataByte(3) >>4 & 1) != 0;
//2015            vals.set(R.string.col_vmcu_ignition_1, ignition1);

            // Vehicle Speed in (almost) Miles per hour - or perhaps PRECISELY Mph, before adding a bit for display?
            int msb = obdData00_2.getDataByte(4);
            int vehicleSpeed = (msb<<8) | obdData00_2.getDataByte(3);
            if ((msb & 0x80) != 0) {
                vehicleSpeed = vehicleSpeed - 65536;
            }
            double vehicleSpeed_kph = vehicleSpeed * mKmsPerMile / 100;
            vals.set(R.string.col_vmcu_vehicle_speed_kph, vehicleSpeed_kph);

            mCmd2102.getResponse().process();
            List<String> lines02 = mCmd2102.getResponse().getLines();
            if (lines02.size() < 4)
                return;

            ObdMessageData obdData02_1 = new ObdMessageData(lines02.get(1));

            // Accel Pedal Depth
//2015            double accelPedalDepthPct = obdData02_1.getDataByte(6)/2.0;
//2015            vals.set(R.string.col_vmcu_accel_pedal_depth_pct, accelPedalDepthPct);

            // Vehicle Speed
//2015            int vehicleSpeed = obdData02_1.getDataByte(7);
//2015            vals.set(R.string.col_vmcu_vehicle_speed_kph, vehicleSpeed);

            // Motor Actual RPM (not on e-Soul and Ioniq)
            msb = obdData02_1.getDataByte(3);
            int motorActualRpm = msb*256 + obdData02_1.getDataByte(4);
            if ((msb & 0x80) != 0) {
                motorActualRpm = motorActualRpm - 65536;
            }
            if (motorActualRpm != 0) {
                vals.set(R.string.col_vmcu_motor_actual_speed_rpm, motorActualRpm);
            }

            // Aux Battery Voltage
            ObdMessageData obdData02_3 = new ObdMessageData(lines02.get(3));
            double auxBatteryVoltage = ((obdData02_3.getDataByte(3) << 8) | obdData02_3.getDataByte(2) ) / 1000.0;
            vals.set(R.string.col_vmcu_aux_battery_V, auxBatteryVoltage);

//            // Aux Battery Current
//            msb = obdData02_3.getDataByte(5);
//            int auxBatteryCurrent = ((msb << 8) | obdData02_3.getDataByte(4) );
////            if ((msb & 0x80) != 0) {
//                auxBatteryCurrent = auxBatteryCurrent - 32768;
////            }
//            vals.set(R.string.col_vmcu_aux_battery_A, auxBatteryCurrent / 1000.0);

            double auxSOC = obdData02_3.getDataByte(6);
            if (auxSOC > 0) { // This is zero on Ioniq...
                vals.set(R.string.col_vmcu_aux_battery_SOC_pct, auxSOC);
            }

            final Response r = mCmd1a80.getResponse();
            r.process();

            String vin;
            StringBuilder str = new StringBuilder();
            try {
                str.append((char) r.get(2, 4));
                str.append((char) r.get(2, 5));
                str.append((char) r.get(2, 6));
                str.append((char) r.get(2, 7));
                str.append((char) r.get(3, 1));
                str.append((char) r.get(3, 2));
                str.append((char) r.get(3, 3));
                str.append((char) r.get(3, 4));
                str.append((char) r.get(3, 5));
                str.append((char) r.get(3, 6));
                str.append((char) r.get(3, 7));
                str.append((char) r.get(4, 1));
                str.append((char) r.get(4, 2));
                str.append((char) r.get(4, 3));
                str.append((char) r.get(4, 4));
                str.append((char) r.get(4, 5));
                str.append((char) r.get(4, 6));
                vin = str.toString();
                skip(true);
            } catch (Exception e) {
                vin = "error: " + str.toString();
            }
            CurrentValuesSingleton.getInstance().set("VIN", vin);

//            // Inverter Input Voltage
//            int inverterInputVoltage = obdData02_1.getDataByte(2)*2;
//            vals.set(R.string.col_vmcu_inverter_input_V, inverterInputVoltage);
//
//            // Motor Actual RPM
//            msb = obdData02_1.getDataByte(6);
//            int motorActualRpm = msb*256 + obdData02_1.getDataByte(5);
//            if ((msb & 0x80) != 0) {
//                motorActualRpm = motorActualRpm - 65536;
//            }
//            vals.set(R.string.col_vmcu_motor_actual_speed_rpm, motorActualRpm);
//
//            ObdMessageData obdData02_2 = new ObdMessageData(lines02.get(2));
//
//            // Motor Torque Command
//            msb = obdData02_2.getDataByte(1);
//            int motorTorqueCommandNm = msb*256 + obdData02_1.getDataByte(7);
//            if ((msb & 0x80) != 0) {
//                motorTorqueCommandNm = motorTorqueCommandNm - 65536;
//            }
//            vals.set(R.string.col_vmcu_motor_torque_command_Nm, motorTorqueCommandNm);
//
//            // Estimated Motor Torque
//            msb = obdData02_2.getDataByte(3);
//            int estimatedMotorTorqueNm = msb*256 + obdData02_2.getDataByte(2);
//            if ((msb & 0x80) != 0) {
//                estimatedMotorTorqueNm = estimatedMotorTorqueNm - 65536;
//            }
//            vals.set(R.string.col_vmcu_estimated_motor_torque_Nm, estimatedMotorTorqueNm);
//
//            ObdMessageData obdData02_3 = new ObdMessageData(lines02.get(3));
//
//            // Motor Phase Current (RMS value)
//            double motorPhaseCurrentRmsA = (obdData02_3.getDataByte(4)*256 + obdData02_3.getDataByte(3)) / 10.0;
//            vals.set(R.string.col_vmcu_motor_phase_current_RMS_A, motorPhaseCurrentRmsA);
//
//            // Motor Temperature
//            int motorTempC = obdData02_3.getDataByte(5) - 40;
//            vals.set(R.string.col_vmcu_temp_motor_C, motorTempC);
//
//            // MCU Temperature
//            int mcuTempC = obdData02_3.getDataByte(6) - 40;
//            vals.set(R.string.col_vmcu_temp_mcu_C, mcuTempC);
//
//            // Heat Sink Temperature
//            int heatsinkTempC = obdData02_3.getDataByte(7) - 40;
//            vals.set(R.string.col_vmcu_temp_heatsink_C, heatsinkTempC);
        } catch (Exception e) {
            int i = 0;
            //
        }
    }
}