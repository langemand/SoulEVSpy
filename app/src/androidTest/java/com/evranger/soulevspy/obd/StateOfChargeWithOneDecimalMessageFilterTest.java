package com.evranger.soulevspy.obd;

import android.test.AndroidTestCase;
import android.util.Pair;

import com.evranger.elm327.commands.AbstractCommand;
import com.evranger.elm327.exceptions.DataErrorException;
import com.evranger.soulevspy.R;
import com.evranger.soulevspy.Responder;
import com.evranger.soulevspy.obd.commands.FilteredMonitorCommand;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import junit.framework.Assert;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Henrik on 2019-07-18.
 */
public class StateOfChargeWithOneDecimalMessageFilterTest extends AndroidTestCase {
    final String msgOk = "OK \r" +
            ">";

    final String msg594 = "594 54 17 28 23 C4 BE 01 00 <DATA ERROR \r" +
            ">";

    public void testProcessesOdoAndSpeedUsingResponder() throws IOException {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("AT CRA 594", msgOk),
                new Pair<String, String>("AT MA", msg594),
                new Pair<String, String>(".", "STOPPED\r>"),
                new Pair<String, String>("AT AR", msgOk)
        );

        Responder responder = new Responder(reqres);

        StateOfChargeWithOneDecimalMessageFilter filter = new StateOfChargeWithOneDecimalMessageFilter();
        AbstractCommand cmd = new FilteredMonitorCommand(filter);

        Exception caughtException = null;
        try {
            cmd.execute(responder.getInput(), responder.getOutput());
        }
        catch(Exception e)
        {
            caughtException = e;
        }
        cmd.doProcessResponse();

        Assert.assertTrue(caughtException instanceof DataErrorException);
        Assert.assertEquals("", responder.getMessages());
        Assert.assertEquals(95.1, vals.get(R.string.col_battery_decimal_SOC));
    }

}