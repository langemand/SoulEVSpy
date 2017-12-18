package org.hexpresso.soulevspy.obd;

import android.test.AndroidTestCase;

import org.hexpresso.soulevspy.obd.commands.TirePressureMSCommand;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;
import org.hexpresso.soulevspy.util.ClientSharedPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * Created by henrik on 16/12/2017.
 */

public class TpmsTest extends AndroidTestCase {
    final String msg2106_fromAlex =
            "OK \r" +
            "> \r" +
            "7D6 10 22 61 06 01 37 4D EE \r" +
            "7D6 21 00 0E AD 11 9D 4B 26 02 \r" +
            "7D6 22 00 0E AD 11 9C 4A 26 02 \r" +
            "7D6 23 00 0E AD 11 9B 49 26 02 \r" +
            "7D6 24 00 0E AD 11 9A 48 26 02 \r" +
            "> \r";

    final String msg2106 = // Henriks car in december
            "OK \r" +
            "> \r" +
            "OK \r" +
            "> \r" +
            "7DE 10 22 61 06 00 1E 6B 8B \r" +
            "7DE 21 88 39 1E 02 00 1E C7 \r" +
            "7DE 22 D1 8B 38 1E 02 00 1E \r" +
            "7DE 23 E2 CC 8A 37 1E 02 00 \r" +
            "7DE 24 1E B3 1F 8B 39 0E 02 \r" +
            "> \r";

    public void test2106() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        TirePressureMSCommand cmd = new TirePressureMSCommand();

        ByteArrayInputStream in = new ByteArrayInputStream(msg2106.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            cmd.execute(in, output);
        } catch (Exception ex) {
            assertTrue(false);
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
