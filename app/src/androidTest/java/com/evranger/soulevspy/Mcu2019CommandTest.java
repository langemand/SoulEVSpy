package com.evranger.soulevspy;

import android.test.AndroidTestCase;
import android.util.Pair;

import com.evranger.soulevspy.obd.commands.Mcu2019Command;
import com.evranger.soulevspy.obd.commands.Vmcu2019Command;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import java.util.Arrays;
import java.util.List;

public class Mcu2019CommandTest extends AndroidTestCase {

    final String msgOk = "OK \r" +
            ">";

    // Ioniq EV:
    final String ioniqEv2017Mcu2101 = "7EB 10 1E 61 01 00 00 03 FF \r" +
            "7EB 21 8C 3C 00 0C 88 71 39 \r" +
            "7EB 22 00 00 00 0A 00 4B 02 \r" +
            "7EB 23 64 04 CA 39 05 E9 ED \r" +
            "7EB 24 0F 0E 07 00 00 00 00 \r" +
            "\r" +
            ">";

    final String ioniqEv2017Mcu2102 = "7EB 10 38 61 02 07 FF FF FF \r" +
            "7EB 21 7D 03 A1 02 A1 02 58 \r" +
            "7EB 22 03 0A 17 0B 11 F5 FF \r" +
            "7EB 23 F6 FF A6 D5 CD AB C2 \r" +
            "7EB 24 CD 8E 00 E6 B4 0F 00 \r" +
            "7EB 25 C1 17 B7 26 BD 21 C1 \r" +
            "7EB 26 CD 07 00 3F 2C 00 00 \r" +
            "7EB 27 00 00 00 00 00 00 00 \r" +
            "7EB 28 00 00 00 00 00 00 00 \r" +
            "\r" +
            ">";

    final String ioniqEv2017Mcu2103 = "7EB 10 1B 61 03 00 00 00 00 \r" +
            "7EB 21 00 00 00 00 00 00 00 \r" +
            "7EB 22 00 00 00 00 00 00 00 \r" +
            "7EB 23 00 00 00 00 00 00 00 \r" +
            "\r" +
            ">";

    final String ioniqEv2017Mcu2104 = "7EB 03 7F 21 12 \r" +
            "\r" +
            ">";

    final String ioniqEv2017Mcu2105 = "7EB 03 7F 21 12 \r" +
            "\r" +
            ">";

    final String ioniqEv2017Mcu2106 = "7EB 03 7F 21 12 \r" +
            "\r" +
            ">";


    public void testIoniq2017McuCommand() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("AT SH 7E3", msgOk),
                new Pair<String, String>("AT CRA 7EB", msgOk),
                new Pair<String, String>("21 01", ioniqEv2017Mcu2101),
                new Pair<String, String>("21 02", ioniqEv2017Mcu2102),
                new Pair<String, String>("21 03", ioniqEv2017Mcu2103),
                new Pair<String, String>("21 04", ioniqEv2017Mcu2104),
                new Pair<String, String>("21 05", ioniqEv2017Mcu2105),
                new Pair<String, String>("21 06", ioniqEv2017Mcu2106)
        );
        Responder responder = new Responder(reqres);

        Mcu2019Command cmd = new Mcu2019Command();
        try {
            cmd.execute(responder.getInput(), responder.getOutput());
            cmd.doProcessResponse();
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }

        int i = 0;
    }
}
