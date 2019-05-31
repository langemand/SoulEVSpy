package com.evranger.obd;

import android.test.AndroidTestCase;
import android.util.Pair;

import com.evranger.elm327.commands.general.EcuNameCommand;
import com.evranger.soulevspy.Responder;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import java.util.Arrays;
import java.util.List;

public class EcuNameCommandTest extends AndroidTestCase {
    final String soulEv2015EcuName = "7EC 10 17 49 0A 01 42 45 43 \r" +
            "7EA 10 17 49 0A 01 00 00 00 \r" +
            "7EC 21 4D 2D 42 2B 45 6E 65 \r" +
            "7EA 21 00 00 00 00 00 00 00 \r" +
            "7EA 22 00 00 00 00 00 00 00 \r" +
            "7EC 22 72 67 79 43 74 72 6C \r" +
            "7EC 23 00 00 00 00 00 00 00 \r" +
            "7EA 23 00 00 00 00 00 00 00\r" +
            ">";

    public void testSoulEcuName() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("09 0A", soulEv2015EcuName)
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
        assertEquals("BECM-B+EnergyCtrl", ((String)vals.get("ECU.name.7EC")));
    }
}
