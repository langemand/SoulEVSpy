package com.evranger.soulevspy.obd;

import android.test.AndroidTestCase;
import android.util.Pair;

import com.evranger.soulevspy.Responder;
import com.evranger.soulevspy.obd.commands.BMS2019Command;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import junit.framework.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Bms2019CommandTest extends AndroidTestCase {

    ByteArrayInputStream input = null;
    ByteArrayOutputStream output = null;

    final String msgOk = "OK \r" +
            ">";

    final String msg220101 = "7EC 10 3E 62 01 01 FF F7 E7\r" +
"7EC 21 FF 85 42 68 42 68 03\r" +
"7EC 22 00 0C 0E CA 13 11 11\r" +
"7EC 23 11 11 13 00 00 13 C1 \r" +
"7EC 24 11 C1 2A 00 00 94 00\r" +
"7EC 25 00 1D EB 00 00 1C 9D\r" +
"7EC 26 00 00 0B 0E 00 00 0A \r" +
"7EC 27 50 00 0A 3B 29 0D 01\r" +
"7EC 28 7A 00 00 00 00 03 E8\r" +
">\r";

    final String msg220102 = "7EC 10 27 62 01 02 FF FF FF \r" +
            "7EC 21 FF C1 C1 C1 C1 C1 C1 \r" +
            "7EC 22 C1 C1 C1 C1 C1 C1 C1 \r" +
            "7EC 23 C1 C1 C1 C1 C1 C1 C1 \r" +
            "7EC 24 C1 C1 C1 C1 C1 C1 C1 \r" +
            "7EC 25 C1 C1 C1 C1 C1 AA AA \r" +
            ">";

    final String msg220103 = "7EC 10 27 62 01 03 FF FF FF \r" +
            "7EC 21 FF C1 C1 C1 C1 C1 C1 \r" +
            "7EC 22 C1 C1 C1 C1 C1 C1 C1 \r" +
            "7EC 23 C1 C1 C1 C1 C1 C1 C1 \r" +
            "7EC 24 C1 C1 C1 C1 C1 C1 C1 \r" +
            "7EC 25 C1 C1 C1 C1 C1 AA AA\r" +
            ">";

    final String msg220104 ="7EC 10 27 62 01 04 FF FF FF \r" +
            "7EC 21 FF C1 C1 C1 C1 C1 C1 \r" +
            "7EC 22 C1 C1 C1 C1 C1 C1 C1 \r" +
            "7EC 23 C1 C1 C1 C1 C1 C1 C1 \r" +
            "7EC 24 C1 C1 C1 C1 C1 C1 C1 \r" +
            "7EC 25 C1 C1 C1 C1 C1 AA AA\r" +
            ">";

    final String msg220105 ="EC 10 2E 62 01 05 00 3F FF \r" +
            "7EC 21 90 00 00 00 00 00 00 \r" +
            "7EC 22 00 00 00 00 00 00 42 \r" +
            "7EC 23 68 42 68 00 01 55 11 \r" +
            "7EC 24 00 03 E8 00 00 00 00 \r" +
            "7EC 25 89 00 00 C1 C1 00 00 \r" +
            "7EC 26 10 00 00 00 00 AA AA \r" +
            ">";

    final String msg220106 ="7EC 10 27 62 01 06 FF FF FF \r" +
            "7EC 21 FF 13 00 12 00 0A 00 \r" +
            "7EC 22 00 00 00 00 07 00 00 \r" +
            "7EC 23 B4 B3 00 10 28 00 00 \r" +
            "7EC 24 00 00 00 00 00 00 00 \r" +
            "7EC 25 00 00 00 00 00 AA AA \r" +
            ">";

    public void testBms2019eSoul() throws IOException {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("AT SH 7E4", msgOk),
                new Pair<String, String>("AT CRA 7EC", msgOk),
                new Pair<String, String>("22 01 01", msg220101),
                new Pair<String, String>("22 01 02", msg220102),
                new Pair<String, String>("22 01 03", msg220103),
                new Pair<String, String>("22 01 04", msg220104),
                new Pair<String, String>("22 01 05", msg220105),
                new Pair<String, String>("22 01 06", msg220106)
        );
        Responder responder = new Responder(reqres);

        BMS2019Command cmd = new BMS2019Command();

        try {
            cmd.execute(responder.getInput(), responder.getOutput());
            cmd.doProcessResponse();
        } catch (Exception e) {
            assertEquals("", e.toString());
        }

        Assert.assertEquals("", responder.getMessages());
        Assert.assertEquals(14.8, vals.get("battery.auxiliaryVoltage_V"));
        Assert.assertEquals(66.5, vals.get("battery.SOC_pct"));
    }
}
