package com.evranger.soulevspy.obd;

import android.util.Pair;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import com.evranger.soulevspy.Responder;
import com.evranger.soulevspy.obd.commands.TirePressureMSCommand;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;


/**
 * Created by henrik on 16/12/2017.
 */
@RunWith(AndroidJUnit4.class)
public class TpmsTest {
    final String msg2106_fromAlex =
            "7D6 10 22 61 06 01 37 4D EE \r" +
            "7D6 21 00 0E AD 11 9D 4B 26 02 \r" +
            "7D6 22 00 0E AD 11 9C 4A 26 02 \r" +
            "7D6 23 00 0E AD 11 9B 49 26 02 \r" +
            "7D6 24 00 0E AD 11 9A 48 26 02 \r" +
            ">";

    final String msg2106 = // Henriks car in december
            "7DE 10 22 61 06 00 1E 6B 8B \r" +
            "7DE 21 88 39 1E 02 00 1E C7 \r" +
            "7DE 22 D1 8B 38 1E 02 00 1E \r" +
            "7DE 23 E2 CC 8A 37 1E 02 00 \r" +
            "7DE 24 1E B3 1F 8B 39 0E 02 \r" +
            ">";

    final String msgok =
            "OK \r" +
            ">";
    private CurrentValuesSingleton vals;

    @Before
    public void init() {
        vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(InstrumentationRegistry.getTargetContext());
        vals.setPreferences(prefs);
    }

    @Test
    public void test2106() {
        TirePressureMSCommand cmd = new TirePressureMSCommand();

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("AT SH 7DF", msgok),
                new Pair<String, String>("AT CRA 7DE", msgok),
                new Pair<String, String>("21 06", msg2106)
        );
        Responder responder = new Responder(reqres);

        try {
            cmd.execute(responder.getInput(), responder.getOutput());
        } catch (Exception ex) {
            Assert.assertEquals("", ex.getMessage());
        }

        cmd.doProcessResponse();

        assertEquals(34.00, vals.get("tire.pressure1_psi"));
        assertEquals(34.75, vals.get("tire.pressure2_psi"));
        assertEquals(34.50, vals.get("tire.pressure3_psi"));
        assertEquals(34.75, vals.get("tire.pressure4_psi"));

        assertEquals(2, vals.get("tire.temperature1_C"));
        assertEquals(1, vals.get("tire.temperature2_C"));
        assertEquals(0, vals.get("tire.temperature3_C"));
        assertEquals(2, vals.get("tire.temperature4_C"));
    }
}
