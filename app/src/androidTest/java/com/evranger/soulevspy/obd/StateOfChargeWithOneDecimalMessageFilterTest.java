package com.evranger.soulevspy.obd;

import android.util.Pair;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.evranger.elm327.commands.AbstractCommand;
import com.evranger.elm327.exceptions.DataErrorException;
import com.evranger.soulevspy.R;
import com.evranger.soulevspy.Responder;
import com.evranger.soulevspy.obd.commands.FilteredMonitorCommand;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Henrik on 2019-07-18.
 */
@RunWith(AndroidJUnit4.class)
public class StateOfChargeWithOneDecimalMessageFilterTest {
    final String msgOk = "OK \r" +
            ">";

    final String msg594 = "594 54 17 28 23 C4 BE 01 00 <DATA ERROR \r" +
            ">";

    private CurrentValuesSingleton vals;

    @Before
    public void init() {
        vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(InstrumentationRegistry.getTargetContext());
        vals.setPreferences(prefs);
    }

    @Test
    public void testProcessesOdoAndSpeedUsingResponder() throws IOException {
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

        assertTrue(caughtException instanceof DataErrorException);
        assertEquals("", responder.getMessages());
        assertEquals(95.1, vals.get(R.string.col_battery_decimal_SOC));
    }

}