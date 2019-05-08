package org.hexpresso.soulevspy;

import android.test.AndroidTestCase;
import android.util.Pair;

import org.hexpresso.soulevspy.obd.commands.VmcuCommand;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;
import org.hexpresso.soulevspy.util.ClientSharedPreferences;

import java.util.Arrays;
import java.util.List;

public class VmcuCommandTest extends AndroidTestCase {
    final String msgOk = "OK \r" +
            ">";

    final String soulEv2015Vmcu2100 = "7EA 10 10 61 00 F8 00 00 00 \r" +
            "7EA 21 01 01 FF 10 FF 5A FF \r" +
            "7EA 22 B0 1F 10 00 00 00 00\r" +
            ">";

    final String soulEv2015Vmcu2101 = "7EA 10 0E 61 01 F0 00 00 00 \r" +
            "7EA 21 0A 0F 96 07 CC 42 25 \r" +
            "7EA 22 00 00 00 00 00 00 00\r" +
            ">";

    final String soulEv2015Vmcu2102 = "7EA 10 21 61 02 FF FF 80 00 \r" +
            "7EA 21 8F BE 00 00 04 0A 9B \r" +
            "7EA 22 01 F9 01 04 00 00 00 \r" +
            "7EA 23 00 00 3B 03 36 47 3B \r" +
            "7EA 24 05 08 FE 07 82 51 00\r" +
            ">";

    public void testSoulVmcuCommand() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("AT SH 7DF", msgOk),
                new Pair<String, String>("AT CRA 7EA", msgOk),
                new Pair<String, String>("21 00", soulEv2015Vmcu2100),
                new Pair<String, String>("21 01", soulEv2015Vmcu2101),
                new Pair<String, String>("21 02", soulEv2015Vmcu2102)
        );
        Responder responder = new Responder(reqres);

        VmcuCommand cmd = new VmcuCommand();
        try {
            cmd.execute(responder.getInput(), responder.getOutput());
            cmd.doProcessResponse();
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }

        assertEquals("B", vals.get(R.string.col_vmcu_gear_state));
        assertEquals(false, vals.get(R.string.col_vmcu_eco_off_switch));
        assertEquals(false, vals.get(R.string.col_vmcu_brake_lamp_on_switch));
        assertEquals(true, vals.get(R.string.col_vmcu_brake_off_switch));
        assertEquals(false, vals.get(R.string.col_vmcu_ldc_inhibit));
        assertEquals(false, vals.get(R.string.col_vmcu_fault_flag_of_mcu));
        assertEquals(false, vals.get(R.string.col_vmcu_warning_flag_of_mcu));
        assertEquals(false, vals.get(R.string.col_vmcu_radiator_fan_request_of_motor));
        assertEquals(true, vals.get(R.string.col_vmcu_ignition_1));
        assertEquals(33.0, vals.get(R.string.col_vmcu_accel_pedal_depth_pct));
        assertEquals(37, vals.get(R.string.col_vmcu_vehicle_speed_kph));
        assertEquals(14.3, vals.get(R.string.col_vmcu_aux_battery_V));
        assertEquals(380, vals.get(R.string.col_vmcu_inverter_input_V));
        assertEquals(2564, vals.get(R.string.col_vmcu_motor_actual_speed_rpm));
        assertEquals(411, vals.get(R.string.col_vmcu_motor_torque_command_Nm));
        assertEquals(505, vals.get(R.string.col_vmcu_estimated_motor_torque_Nm));
        assertEquals(14, vals.get(R.string.col_vmcu_motor_temp_C));
        assertEquals(31, vals.get(R.string.col_vmcu_mcu_temp_C));
        assertEquals(19, vals.get(R.string.col_vmcu_heatsink_temp_C));
    }
}
