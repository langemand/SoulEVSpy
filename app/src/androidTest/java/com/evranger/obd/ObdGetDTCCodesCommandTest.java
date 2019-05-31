package com.evranger.obd;

import android.test.AndroidTestCase;
import android.util.Pair;

import com.evranger.elm327.commands.protocol.obd.ObdGetDtcCodesCommand;
import com.evranger.soulevspy.Responder;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import java.util.Arrays;
import java.util.List;

public class ObdGetDTCCodesCommandTest extends AndroidTestCase {
    final String msgOk = "OK \r" +
            ">";

    final String msg03NoDtcCodes = "7EC 02 43 00 \r" +
            "7EA 02 43 00 \r" +
            ">";

    final String msgDtcCodeOnCan = "7EC 02 43 01 01 2C\r" +
            ">";

    final String msg03DtcCodeP1234 = "7EC 02 43 01 12 34\r" +
            ">";

    final String msg03DtcCodes2 = "7EC 02 43 02 12 34 56 78\r" +
            ">";

    // I am guessing! Need a real-world response to get it right
    final String msg03DtcCodes4 = "7EC 02 43 04 12 34 56 78\r" +
            "7EC 12 9A BC DE F0 00 00 00\r" +
            ">";

    public void testDtcCodes_NoCodes() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>(".*", msg03NoDtcCodes)
        );
        Responder responder = new Responder(reqres);

        ObdGetDtcCodesCommand cmd = new ObdGetDtcCodesCommand();
        try {
            cmd.execute(responder.getInput(), responder.getOutput());
            cmd.doProcessResponse();
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }

        assertEquals(0, ((String)vals.get("OBD.DtcCodes.7EC")).length());
    }

    public void testDtcCodes_OneCodeOnCan() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>(".*", msgDtcCodeOnCan)
        );
        Responder responder = new Responder(reqres);

        ObdGetDtcCodesCommand cmd = new ObdGetDtcCodesCommand();
        try {
            cmd.execute(responder.getInput(), responder.getOutput());
            cmd.doProcessResponse();
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }

        assertEquals(5, ((String)vals.get("OBD.DtcCodes.7EC")).length());
        assertEquals("P012C", ((String)vals.get("OBD.DtcCodes.7EC")));
    }

    public void testDtcCodes_CodeP1234OnCan() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>(".*", msg03DtcCodeP1234)
        );
        Responder responder = new Responder(reqres);

        ObdGetDtcCodesCommand cmd = new ObdGetDtcCodesCommand();
        try {
            cmd.execute(responder.getInput(), responder.getOutput());
            cmd.doProcessResponse();
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }

        int i = 0;

        assertEquals(5, ((String)vals.get("OBD.DtcCodes.7EC")).length());
        assertEquals("P1234", ((String)vals.get("OBD.DtcCodes.7EC")));
    }

    public void testDtcCodes_2CodesOnCan() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>(".*", msg03DtcCodes2)
        );
        Responder responder = new Responder(reqres);

        ObdGetDtcCodesCommand cmd = new ObdGetDtcCodesCommand();
        try {
            cmd.execute(responder.getInput(), responder.getOutput());
            cmd.doProcessResponse();
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }

        int i = 0;

        assertEquals(11, ((String)vals.get("OBD.DtcCodes.7EC")).length());
        assertEquals("C1678,P1234", ((String)vals.get("OBD.DtcCodes.7EC")));
    }

    public void testDtcCodes_CodesOnCan_multiple_lines() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>(".*", msg03DtcCodes4)
        );
        Responder responder = new Responder(reqres);

        ObdGetDtcCodesCommand cmd = new ObdGetDtcCodesCommand();
        try {
            cmd.execute(responder.getInput(), responder.getOutput());
            cmd.doProcessResponse();
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }

        assertEquals("B1ABC,C1678,P1234,U1EF0", ((String)vals.get("OBD.DtcCodes.7EC")));
    }
}
