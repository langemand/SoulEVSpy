package com.evranger.soulevspy;

import android.test.AndroidTestCase;
import android.util.Pair;

import com.evranger.soulevspy.obd.commands.Vmcu2019Command;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import java.util.Arrays;
import java.util.List;

public class Vmcu2019CommandTest extends AndroidTestCase {
//>o:AT SH 7E2
//    i:AT SH 7E2
//    OK
//
//>o:AT CRA 7EA
//    i:AT CRA 7EA
//            OK

    final String msgOk = "OK \r" +
            ">";

    final String soulEv2020Vmcu2101 = "7EA 10 18 61 01 FF F8 00 00 \r" +
            "7EA 21 09 28 5A 06 06 0C 03 \r" +
            "7EA 22 00 00 A2 01 2E 75 34 \r" +
            "7EA 23 04 08 08 05 00 00 00 \r" +
            ">\r";

    final String soulEv2020Vmcu2102 = "7EA 10 27 61 02 F8 FF FC 00 \r" +
            "7EA 21 01 01 00 00 00 94 0F \r" +
            "7EA 22 BF 81 D1 39 D4 05 F8 \r" +
            "7EA 23 94 7C 38 20 80 54 14 \r" +
            "7EA 24 22 78 00 00 01 01 01 \r" +
            "7EA 25 00 00 00 07 00 00 00 \r" +
            ">";

    final String soulEv2020Vmcu1A80 = "7EA 10 63 5A 80 20 20 20 20 \r" +
            "7EA 21 20 20 20 20 20 20 1E \r" +
            "7EA 22 09 0D 14 4B 4E 41 4A \r" +
            "7EA 23 33 38 31 31 46 4C 37 \r" +
            "7EA 24 30 30 30 35 34 33 33 \r" +
            "7EA 25 36 36 30 31 2D 30 45 \r" +
            "7EA 26 41 43 30 20 20 20 20 \r" +
            "7EA 27 20 20 20 20 20 20 20 \r" +
            "7EA 28 1E 09 0D 14 53 4B 56 \r" +
            "7EA 29 4C 44 43 35 30 45 53 \r" +
            "7EA 2A 4B 45 4A 35 4D 2D 4E \r" +
            "7EA 2B 53 31 2D 44 30 30 30 \r" +
            "7EA 2C 53 4B 35 38 31 31 32 \r" +
            "7EA 2D 37 00 00 00 00 00 00 \r" +
            "7EA 2E 00 00 00 00 00 00 00 \r" +
            ">";


    public void testSoul2020VmcuCommand() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("AT SH 7E2", msgOk),
                new Pair<String, String>("AT CRA 7EA", msgOk),
                new Pair<String, String>("21 01", soulEv2020Vmcu2101),
                new Pair<String, String>("21 02", soulEv2020Vmcu2102),
                new Pair<String, String>("1A 80", soulEv2020Vmcu1A80)
        );
        Responder responder = new Responder(reqres);

        Vmcu2019Command cmd = new Vmcu2019Command();
        try {
            cmd.execute(responder.getInput(), responder.getOutput());
            cmd.doProcessResponse();
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }

        assertEquals("D", vals.get(R.string.col_vmcu_gear_state));
//        assertEquals(false, vals.get(R.string.col_vmcu_eco_off_switch));
//        assertEquals(false, vals.get(R.string.col_vmcu_brake_lamp_on_switch));
//        assertEquals(true, vals.get(R.string.col_vmcu_brake_off_switch));
//        assertEquals(false, vals.get(R.string.col_vmcu_ldc_inhibit));
//        assertEquals(false, vals.get(R.string.col_vmcu_fault_flag_of_mcu));
//        assertEquals(false, vals.get(R.string.col_vmcu_warning_flag_of_mcu));
//        assertEquals(false, vals.get(R.string.col_vmcu_radiator_fan_request_of_motor));
//        assertEquals(true, vals.get(R.string.col_vmcu_ignition_1));
//        assertEquals(33.0, vals.get(R.string.col_vmcu_accel_pedal_depth_pct));
        assertEquals(4.18, vals.get(R.string.col_vmcu_vehicle_speed_kph));
        assertEquals(14.46, vals.get(R.string.col_vmcu_aux_battery_V));
        assertEquals(84.0, vals.get(R.string.col_vmcu_aux_battery_SOC_pct));
//        assertEquals(380, vals.get(R.string.col_vmcu_inverter_input_V));
//        assertEquals(2564, vals.get(R.string.col_vmcu_motor_actual_speed_rpm));
//        assertEquals(411, vals.get(R.string.col_vmcu_motor_torque_command_Nm));
//        assertEquals(505, vals.get(R.string.col_vmcu_estimated_motor_torque_Nm));
//        assertEquals(14, vals.get(R.string.col_vmcu_temp_motor_C));
//        assertEquals(31, vals.get(R.string.col_vmcu_temp_mcu_C));
//        assertEquals(19, vals.get(R.string.col_vmcu_temp_heatsink_C));
        assertEquals("KNAJ#3811FL7$000543", vals.get("VIN"));
    }
}
