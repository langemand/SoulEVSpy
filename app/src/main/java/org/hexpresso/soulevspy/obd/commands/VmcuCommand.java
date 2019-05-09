package org.hexpresso.soulevspy.obd.commands;

import org.hexpresso.elm327.commands.AbstractMultiCommand;
import org.hexpresso.elm327.commands.filters.RegularExpressionResponseFilter;
import org.hexpresso.obd.ObdMessageData;
import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.List;

/**
 * Created by henrik on 08/05/2019.
 */

public class VmcuCommand extends AbstractMultiCommand {
    private BasicCommand mCmd2100 = null;
    private BasicCommand mCmd2101 = null;
    private BasicCommand mCmd2102 = null;

    public VmcuCommand() {
        addCommand(new BasicCommand("AT SH 7DF")); //"AT SH 7C5"));
        addCommand(new BasicCommand("AT CRA 7EA"));
        mCmd2100 = new BasicCommand("21 00");
        mCmd2101 = new BasicCommand("21 01");
        mCmd2102 = new BasicCommand("21 02");
        addCommand(mCmd2100);
        addCommand(mCmd2101);
        addCommand(mCmd2102);

        mCmd2100.addResponseFilter(new RegularExpressionResponseFilter("^7EA(.*)$"));
        mCmd2101.addResponseFilter(new RegularExpressionResponseFilter("^7EA(.*)$"));
        mCmd2102.addResponseFilter(new RegularExpressionResponseFilter("^7EA(.*)$"));
    }

    public void doProcessResponse() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        try {
            mCmd2100.getResponse().process();
            List<String> lines00 = mCmd2100.getResponse().getLines();
            if (lines00.size() != 3)
                return;

            ObdMessageData obdData00_1 = new ObdMessageData(lines00.get(1));

            // TODO: Immobilizer built-in

            // Gearstick
            int gearByte = obdData00_1.getDataByte(4);
            StringBuilder gear = new StringBuilder();
            if ((gearByte & 1) != 0) gear.append("P");
            if ((gearByte & 2) != 0) gear.append("R");
            if ((gearByte & 4) != 0) gear.append("N");
            if ((gearByte & 8) != 0) gear.append("D");
            if ((gearByte & 16) != 0) gear.append("B");
            vals.set(R.string.col_vmcu_gear_state, gear.toString());

            // ECO button
            boolean ecoOffSwitch = (obdData00_1.getDataByte(4) & 32) != 0;
            vals.set(R.string.col_vmcu_eco_off_switch, ecoOffSwitch);

            // TODO: Charge Cancel Switch

            // TODO: VCU Immobilizer Authentication

            // Brake Lamp Switch
            boolean brakeLampSwitch = (obdData00_1.getDataByte(6) & 1) != 0;
            vals.set(R.string.col_vmcu_brake_lamp_on_switch, brakeLampSwitch);

            // Brake Switch
            boolean brakeOffSwitch = (obdData00_1.getDataByte(6) >>1 & 1) != 0;
            vals.set(R.string.col_vmcu_brake_off_switch, brakeOffSwitch);

            // TODO: Start Key
            // TODO: EV Ready
            // TODO: VCU Ready
            // TODO: Main Relay Off Request
            // TODO: Power Enable

            // LDC Inhibit
            boolean ldcInhibit = (obdData00_1.getDataByte(6) >>7 & 1) != 0;
            vals.set(R.string.col_vmcu_ldc_inhibit, ldcInhibit);

            ObdMessageData obdData00_2 = new ObdMessageData(lines00.get(2));

            // Fault Flag of MCU
            boolean faultFlagOfMcu = (obdData00_2.getDataByte(1) & 1) != 0;
            vals.set(R.string.col_vmcu_fault_flag_of_mcu, faultFlagOfMcu);

            // Warning Flag of MCU
            boolean warningFlagOfMcu = (obdData00_2.getDataByte(1) >>1 & 1) != 0;
            vals.set(R.string.col_vmcu_warning_flag_of_mcu, warningFlagOfMcu);

            // TODO: Actuation Test Mode of MCU
            // TODO: Service Lamp Request of MCU
            // TODO: Motor Inverter Controller Ready
            // TODO: Motor Controllable
            // TODO: Main Relay Cut off Request from Motor Inverter
            // TODO: MCU Anti-Jerk Activation Flag
            // TODO: Requirement Flag for Motor Resolver Calibration Process
            // TODO: EWP Status

            // Radiator Fan On/Off Request of Motor
            boolean radiatorFanRequestOfMotor = (obdData00_2.getDataByte(3) >>3 & 1) != 0;
            vals.set(R.string.col_vmcu_radiator_fan_request_of_motor, radiatorFanRequestOfMotor);

            // Ignition 1
            boolean ignition1 = (obdData00_2.getDataByte(3) >>4 & 1) != 0;
            vals.set(R.string.col_vmcu_ignition_1, ignition1);

            mCmd2101.getResponse().process();
            List<String> lines01 = mCmd2101.getResponse().getLines();
            if (lines01.size() != 3)
                return;

            // TODO: APS Sensor1 Voltage Output
            // TODO: APS Sensor2 Voltage Output

            ObdMessageData obdData01_1 = new ObdMessageData(lines01.get(1));

            // Accel Pedal Depth
            double accelPedalDepthPct = obdData01_1.getDataByte(6)/2.0;
            vals.set(R.string.col_vmcu_accel_pedal_depth_pct, accelPedalDepthPct);

            // Vehicle Speed
            int vehicleSpeed = obdData01_1.getDataByte(7);
            vals.set(R.string.col_vmcu_vehicle_speed_kph, vehicleSpeed);

            mCmd2102.getResponse().process();
            List<String> lines02 = mCmd2102.getResponse().getLines();
            if (lines02.size() != 5)
                return;

            ObdMessageData obdData02_1 = new ObdMessageData(lines02.get(1));

            // Aux Battery Voltage
            double auxVoltage = obdData02_1.getDataByte(1)/10.0;
            vals.set(R.string.col_vmcu_aux_battery_V, auxVoltage);

            // Inverter Input Voltage
            int inverterInputVoltage = obdData02_1.getDataByte(2)*2;
            vals.set(R.string.col_vmcu_inverter_input_V, inverterInputVoltage);

            // TODO: Unknown
            // TODO: Unknown

            // Motor Actual Speed
            int msb = obdData02_1.getDataByte(6);
            int motorActualRpm = msb*256 + obdData02_1.getDataByte(5);
            if ((msb & 0x80) != 0) {
                motorActualRpm = motorActualRpm -  - 65536;
            }
            vals.set(R.string.col_vmcu_motor_actual_speed_rpm, motorActualRpm);

            ObdMessageData obdData02_2 = new ObdMessageData(lines02.get(2));

            // Motor Torque Command
            msb = obdData02_2.getDataByte(1);
            int motorTorqueCommandNm = msb*256 + obdData02_1.getDataByte(7);
            if ((msb & 0x80) != 0) {
                motorTorqueCommandNm = motorTorqueCommandNm -  - 65536;
            }
            vals.set(R.string.col_vmcu_motor_torque_command_Nm, motorTorqueCommandNm);

            // Estimated Motor Torque
            msb = obdData02_2.getDataByte(3);
            int estimatedMotorTorqueNm = msb*256 + obdData02_2.getDataByte(2);
            if ((msb & 0x80) != 0) {
                estimatedMotorTorqueNm = estimatedMotorTorqueNm -  - 65536;
            }
            vals.set(R.string.col_vmcu_estimated_motor_torque_Nm, estimatedMotorTorqueNm);

            // TODO: Motor Resolver CAL Command
            // TODO: Motor Resolver Mal Counter
            // TODO: MCU GB Fault Counter

            ObdMessageData obdData02_3 = new ObdMessageData(lines02.get(3));

            // Motor Phase Current (RMS value)
            double motorPhaseCurrentRmsA = (obdData02_3.getDataByte(4)*256 + obdData02_3.getDataByte(3)) / 10.0;
            vals.set(R.string.col_vmcu_motor_phase_current_RMS_A, motorPhaseCurrentRmsA);

            // Motor Temperature
            int motorTempC = obdData02_3.getDataByte(5) - 40;
            vals.set(R.string.col_vmcu_temp_motor_C, motorTempC);

            // MCU Temperature
            int mcuTempC = obdData02_3.getDataByte(6) - 40;
            vals.set(R.string.col_vmcu_temp_mcu_C, mcuTempC);

            // Heat Sink Temperature
            int heatsinkTempC = obdData02_3.getDataByte(7) - 40;
            vals.set(R.string.col_vmcu_temp_heatsink_C, heatsinkTempC);

            // TODO: Motor U Phase Current Sensor Offset
            // TODO: Motor V Phase Current Sensor Offset
            // TODO: Motor Resolver Offset
        } catch (Exception e) {
            //
        }
    }
}