package com.evranger.obd;

import android.util.Pair;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.evranger.elm327.commands.protocol.obd.ObdGetDtcCodesCommand;
import com.evranger.soulevspy.Responder;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ObdGetDTCCodesCommandTest {
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

    private CurrentValuesSingleton vals;

    @Before
    public void init() {
        vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(InstrumentationRegistry.getTargetContext());
        vals.setPreferences(prefs);
    }

    @Test
    public void testDtcCodes_NoCodes() {
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

    @Test
    public void testDtcCodes_OneCodeOnCan() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(InstrumentationRegistry.getTargetContext());
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

    @Test
    public void testDtcCodes_CodeP1234OnCan() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(InstrumentationRegistry.getTargetContext());
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

    @Test
    public void testDtcCodes_2CodesOnCan() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(InstrumentationRegistry.getTargetContext());
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

    @Test
    public void testDtcCodes_CodesOnCan_multiple_lines() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(InstrumentationRegistry.getTargetContext());
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
