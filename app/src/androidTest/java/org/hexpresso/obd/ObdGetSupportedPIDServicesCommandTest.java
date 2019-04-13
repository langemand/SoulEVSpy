package org.hexpresso.obd;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import org.hexpresso.elm327.commands.protocol.obd.ObdGetSupportedPIDServicesCommand;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;
import org.hexpresso.soulevspy.util.ClientSharedPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ObdGetSupportedPIDServicesCommandTest extends AndroidTestCase {
    ByteArrayInputStream input = null;
    ByteArrayOutputStream output = null;

    final String msgOk = "OK \r" +
            ">";

    final String msg0100 = "7EA 06 41 00 80 00 00 01 \r" +
            "7EC 06 41 00 80 00 00 01 \r" +
            ">";

    final String msg0900 = "7EC 06 49 00 14 40 00 00 \r" +
            "7EA 06 49 00 54 40 00 00 \r" +
            ">";

//            >o:01 01
//    i:7EA 06 41 01 00 04 00 00
//
//            >o:03
//    i:7EC 02 43 00
//            7EA 02 43 00

    public void testSomeSupportedPIDs01() throws InterruptedException, TimeoutException, IOException {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        input = new ByteArrayInputStream((msg0100).getBytes());

        ObdGetSupportedPIDServicesCommand cmd = new ObdGetSupportedPIDServicesCommand("01");
            cmd.execute(input, output);
            cmd.doProcessResponse();

        Assert.assertEquals("01,20", vals.get("OBD.SupportedPids.01.7EA"));
        Assert.assertEquals("01,20", vals.get("OBD.SupportedPids.01.7EC"));
    }

    public void testSomeSupportedPIDs09() throws InterruptedException, TimeoutException, IOException {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        input = new ByteArrayInputStream((msg0900).getBytes());

        ObdGetSupportedPIDServicesCommand cmd = new ObdGetSupportedPIDServicesCommand("09");
        cmd.execute(input, output);
        cmd.doProcessResponse();

        Assert.assertEquals("04,06,0A", vals.get("OBD.SupportedPids.09.7EC"));
        Assert.assertEquals("02,04,06,0A", vals.get("OBD.SupportedPids.09.7EA"));
    }

    /**
     *
     */
    @Override
    protected void setUp() {
        output = new ByteArrayOutputStream();
    }

}
