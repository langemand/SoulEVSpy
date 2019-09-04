package com.evranger.obd;

import android.util.Pair;

import androidx.test.InstrumentationRegistry;

import junit.framework.Assert;

import com.evranger.elm327.commands.protocol.obd.ObdGetSupportedPIDServicesCommand;
import com.evranger.soulevspy.Responder;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static junit.framework.TestCase.assertEquals;


public class ObdGetSupportedPIDServicesCommandTest {
    final String msgOk = "OK \r" +
            ">";

    final String msg0100 = "7EA 06 41 00 80 00 00 01 \r" +
            "7EC 06 41 00 80 00 00 01 \r" +
            ">";

    final String msg0900 = "7EC 06 49 00 14 40 00 00 \r" +
            "7EA 06 49 00 54 40 00 00 \r" +
            ">";

    final String ioniq0900 = "7EB 06 49 00 14 40 00 00 \r" +
            "7EC 06 49 00 14 40 00 00 \r" +
            ">";

    final String badresponse0900 = "7EB 06 49 00 \r" +
            ">";

    private CurrentValuesSingleton vals;

    @Before
    public void init() {
        vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(InstrumentationRegistry.getTargetContext());
        vals.setPreferences(prefs);
    }

    @Test
    public void testSomeSupportedPIDs01() throws InterruptedException, TimeoutException, IOException {
        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("01.?00", msg0100)
        );
        Responder responder = new Responder(reqres);

        ObdGetSupportedPIDServicesCommand cmd = new ObdGetSupportedPIDServicesCommand("01");
        cmd.execute(responder.getInput(), responder.getOutput());
        cmd.doProcessResponse();

        Thread.sleep(20);

        assertEquals("01,20", vals.get("OBD.SupportedPids.01.7EA"));
        assertEquals("01,20", vals.get("OBD.SupportedPids.01.7EC"));
    }

    @Test
    public void testSomeSupportedPIDs09() throws InterruptedException, TimeoutException, IOException {
        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("09.?00", msg0900)
        );
        Responder responder = new Responder(reqres);

        ObdGetSupportedPIDServicesCommand cmd = new ObdGetSupportedPIDServicesCommand("09");
        cmd.execute(responder.getInput(), responder.getOutput());
        cmd.doProcessResponse();

        assertEquals("04,06,0A", vals.get("OBD.SupportedPids.09.7EC"));
        assertEquals("02,04,06,0A", vals.get("OBD.SupportedPids.09.7EA"));
    }

    @Test
    public void testObdSupportedPidsServicesCommand_hyundayIoniq() {
        ObdGetSupportedPIDServicesCommand cmd = new ObdGetSupportedPIDServicesCommand("09");
        Responder r = new Responder(
                Arrays.asList(
                        new Pair<>(".*", ioniq0900)
                ));

        try {
            cmd.execute(r.getInput(), r.getOutput());
            cmd.doProcessResponse();
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }

        Object ioniqPids7eb = vals.get("OBD.SupportedPids.09.7EB");
        assertEquals("04,06,0A", ioniqPids7eb);

        Object ioniqPids7ec = vals.get("OBD.SupportedPids.09.7EC");
        assertEquals("04,06,0A", ioniqPids7ec);
    }

    @Test
    public void testBadResponsePIDs09() throws InterruptedException, TimeoutException, IOException {
        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("09.?00", badresponse0900)
        );
        Responder responder = new Responder(reqres);

        ObdGetSupportedPIDServicesCommand cmd = new ObdGetSupportedPIDServicesCommand("09");
        cmd.execute(responder.getInput(), responder.getOutput());
        cmd.doProcessResponse();

        Thread.sleep(20);

        assertEquals(null, vals.get("OBD.SupportedPids.01.7EB"));
        assertEquals(null, vals.get("OBD.SupportedPids.01.7EC"));
    }
}
