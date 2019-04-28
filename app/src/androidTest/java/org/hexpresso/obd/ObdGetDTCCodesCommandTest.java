package org.hexpresso.obd;

import android.test.AndroidTestCase;
import android.util.Pair;

import org.hexpresso.elm327.commands.protocol.obd.ObdGetSupportedPIDServicesCommand;
import org.hexpresso.soulevspy.Responder;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;
import org.hexpresso.soulevspy.util.ClientSharedPreferences;

import java.util.Arrays;
import java.util.List;

public class ObdGetDTCCodesCommandTest extends AndroidTestCase {
    final String msgOk = "OK \r" +
            ">";

    final String msg0101 = "7EA 06 41 01 00 04 00 00 \r" +
            ">";

    final String msg03 = "7EC 02 43 00 \r" +
            "7EA 02 43 00 \r" +
            ">";


    public void testNoDtcCodes() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>(".*", msg0101),
                new Pair<String, String>(".*", msg03)
        );
        Responder responder = new Responder(reqres);

        ObdGetSupportedPIDServicesCommand cmd = new ObdGetSupportedPIDServicesCommand("01");
        try {
            cmd.execute(responder.getInput(), responder.getOutput());
            cmd.doProcessResponse();
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }

// TODO: Make a proper testcase        Assert.assertEquals("U123", vals.get("OBD.DtcCodes"));
    }
    
}
