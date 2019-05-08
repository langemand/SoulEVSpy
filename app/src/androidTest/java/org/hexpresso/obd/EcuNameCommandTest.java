package org.hexpresso.obd;

import android.test.AndroidTestCase;
import android.util.Pair;

import org.hexpresso.elm327.commands.general.EcuNameCommand;
import org.hexpresso.soulevspy.Responder;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;
import org.hexpresso.soulevspy.util.ClientSharedPreferences;

import java.util.Arrays;
import java.util.List;

public class EcuNameCommandTest extends AndroidTestCase {
    final String msgOk = "OK \r" +
            ">";

    final String soulEv2015EcuName = "7EA 10 13 49 04 01 00 00 00 \r" +
            "7EC 10 13 49 04 01 50 53 45 \r" +
            "7EA 21 00 00 00 00 00 00 00 \r" +
            "7EC 21 56 42 35 31 30 30 52 \r" +
            "7EA 22 00 00 00 00 00 00 00 \r" +
            "7EC 22 00 00 00 00 00 00 00 \r" +
            ">";

    public void testSoulEcuName() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>(".*", soulEv2015EcuName)
        );
        Responder responder = new Responder(reqres);

        EcuNameCommand cmd = new EcuNameCommand();
        try {
            cmd.execute(responder.getInput(), responder.getOutput());
            cmd.doProcessResponse();
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }

        assertEquals("", ((String)vals.get("ECU.name.7EA")));
        assertEquals("PSEVB5100R", ((String)vals.get("ECU.name.7EC")));
    }
}
