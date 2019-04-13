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

public class ObdGetDTCCodesCommandTest extends AndroidTestCase {
    ByteArrayInputStream input = null;
    ByteArrayOutputStream output = null;

    final String msgOk = "OK \r" +
            ">";

    final String msg0101 = "7EA 06 41 01 00 04 00 00 \r" +
            ">";

    final String msg03 = "7EC 02 43 00 \r" +
            "7EA 02 43 00 \r" +
            ">";


    public void testNoDtcCodes() throws InterruptedException, TimeoutException, IOException {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        input = new ByteArrayInputStream((msg0101 + msg03).getBytes());

        ObdGetSupportedPIDServicesCommand cmd = new ObdGetSupportedPIDServicesCommand("01");
            cmd.execute(input, output);
            cmd.doProcessResponse();

// TODO: Make a proper testcase        Assert.assertEquals("U123", vals.get("OBD.DtcCodes"));
    }
    
    /**
     *
     */
    @Override
    protected void setUp() {
        output = new ByteArrayOutputStream();
    }

}
