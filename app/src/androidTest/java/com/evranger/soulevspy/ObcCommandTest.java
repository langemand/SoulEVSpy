package com.evranger.soulevspy;

import android.util.Pair;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.evranger.soulevspy.obd.commands.OnBoardChargerCommand;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ObcCommandTest {
    final String msgOk = "OK \r" +
            ">";

    final String soulEv2015Obc2102 = "79C 10 17 61 02 E8 03 1F 00 \r" +
            "79C 21 01 00 09 23 10 0C 12 \r" +
            "79C 22 8F 0E E0 00 85 00 4A \r" +
            "79C 23 18 12 1C 00 00 00 00\r" +
            ">";

    private CurrentValuesSingleton vals;

    @Before
    public void init() {
        vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(InstrumentationRegistry.getTargetContext());
        vals.setPreferences(prefs);
    }

    @Test
    public void testSoulVmcuCommand() {
        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("AT SH 7DF", msgOk),
                new Pair<String, String>("AT CRA 79C", msgOk),
                new Pair<String, String>("21 02", soulEv2015Obc2102)
        );
        Responder responder = new Responder(reqres);

        OnBoardChargerCommand cmd = new OnBoardChargerCommand();
        try {
            cmd.execute(responder.getInput(), responder.getOutput());
            cmd.doProcessResponse();
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }

        assertEquals(233.9, vals.get(R.string.col_obc_ac_in_V));
        assertEquals(380.8, vals.get(R.string.col_obc_dc_out_V));
        assertEquals(13.3, vals.get(R.string.col_obc_ac_in_A));
        assertEquals(24.666666666666668, vals.get(R.string.col_obc_pilot_duty_cycle));
        assertEquals(24, vals.get(R.string.col_obc_temp_1_C));
        assertEquals(18, vals.get(R.string.col_obc_temp_2_C));
        assertEquals(28, vals.get(R.string.col_obc_temp_3_C));
    }
}
