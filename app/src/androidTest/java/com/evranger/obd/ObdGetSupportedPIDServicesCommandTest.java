package com.evranger.obd;

import android.test.AndroidTestCase;
import android.util.Pair;

import junit.framework.Assert;

import com.evranger.elm327.commands.protocol.obd.ObdGetSupportedPIDServicesCommand;
import com.evranger.soulevspy.Responder;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ObdGetSupportedPIDServicesCommandTest extends AndroidTestCase {
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

    public void testSomeSupportedPIDs01() throws InterruptedException, TimeoutException, IOException {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("01.?00", msg0100)
        );
        Responder responder = new Responder(reqres);

        ObdGetSupportedPIDServicesCommand cmd = new ObdGetSupportedPIDServicesCommand("01");
        cmd.execute(responder.getInput(), responder.getOutput());
        cmd.doProcessResponse();

        Thread.sleep(20);

        Assert.assertEquals("01,20", vals.get("OBD.SupportedPids.01.7EA"));
        Assert.assertEquals("01,20", vals.get("OBD.SupportedPids.01.7EC"));
    }

    public void testSomeSupportedPIDs09() throws InterruptedException, TimeoutException, IOException {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("09.?00", msg0900)
        );
        Responder responder = new Responder(reqres);

        ObdGetSupportedPIDServicesCommand cmd = new ObdGetSupportedPIDServicesCommand("09");
        cmd.execute(responder.getInput(), responder.getOutput());
        cmd.doProcessResponse();

        Assert.assertEquals("04,06,0A", vals.get("OBD.SupportedPids.09.7EC"));
        Assert.assertEquals("02,04,06,0A", vals.get("OBD.SupportedPids.09.7EA"));
    }

    public void testObdSupportedPidsServicesCommand_hyundayIoniq() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

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

    public void testBadResponsePIDs09() throws InterruptedException, TimeoutException, IOException {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("09.?00", badresponse0900)
        );
        Responder responder = new Responder(reqres);

        ObdGetSupportedPIDServicesCommand cmd = new ObdGetSupportedPIDServicesCommand("09");
        cmd.execute(responder.getInput(), responder.getOutput());
        cmd.doProcessResponse();

        Thread.sleep(20);

        Assert.assertEquals(null, vals.get("OBD.SupportedPids.01.7EB"));
        Assert.assertEquals(null, vals.get("OBD.SupportedPids.01.7EC"));
    }
}
