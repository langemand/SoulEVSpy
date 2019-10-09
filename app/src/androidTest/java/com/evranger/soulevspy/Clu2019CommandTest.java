package com.evranger.soulevspy;

import android.util.Pair;

import androidx.test.InstrumentationRegistry;

import com.evranger.soulevspy.obd.commands.Clu2019Command;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class Clu2019CommandTest  {

    final String msgOk = "OK \r" +
            ">";

    // Kia eSoul 2020:
    final String eSoul2020Clu22b002 = "7CE 10 0F 62 B0 02 E0 00 00 \r" +
            "7CE 21 00 FF BE 00 12 6A 00 \r" +
            "7CE 22 00 00 00 00 00 00 00 \r" +
            "\r" +
            ">";

    private CurrentValuesSingleton vals;

    @Before
    public void init() {
        vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(InstrumentationRegistry.getTargetContext());
        vals.setPreferences(prefs);
    }

    @Test
    public void test_eSoul2020CluCommand() {
        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("AT SH 7C6", msgOk),
                new Pair<String, String>("AT CRA 7CE", msgOk),
                new Pair<String, String>("22 B0 02", eSoul2020Clu22b002)
        );
        Responder responder = new Responder(reqres);

        Clu2019Command cmd = new Clu2019Command();
        try {
            cmd.execute(responder.getInput(), responder.getOutput());
            cmd.doProcessResponse();
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }

        assertEquals(4714.0, vals.get(R.string.col_car_odo_km));
        int i = 0;
    }
}
