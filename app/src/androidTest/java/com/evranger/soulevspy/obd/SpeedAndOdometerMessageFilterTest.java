package com.evranger.soulevspy.obd;

import android.test.AndroidTestCase;
import android.util.Pair;

import junit.framework.Assert;

import com.evranger.elm327.commands.AbstractCommand;
import com.evranger.elm327.exceptions.StoppedException;
import com.evranger.obd.ObdMessageData;
import com.evranger.soulevspy.R;
import com.evranger.soulevspy.Responder;
import com.evranger.soulevspy.obd.commands.FilteredMonitorCommand;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tyrel on 10/17/2015.
 */
public class SpeedAndOdometerMessageFilterTest extends AndroidTestCase {

    final String msgOk = "OK \r" +
            ">";

    final String msg4F0 = "4F0 00 0A 11 00 00 36 29 03 \r" +
            ">";

    final String steves4F0 = "4F0 40 00 1A 00 00 D8 0F 0B <DATA ERROR\r" +
            ">";

    public void testProcessesZero() {
        SpeedAndOdometerMessageFilter filter = new SpeedAndOdometerMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F0 40 00 00 00 00 00 00 00");
        Assert.assertTrue(filter.doProcessMessage(messageData));
        Assert.assertEquals(0.0, filter.getOdometerKM());
    }

    public void testProcesses000001() {
        SpeedAndOdometerMessageFilter filter = new SpeedAndOdometerMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F0 40 00 00 00 00 01 00 00");
        Assert.assertTrue(filter.doProcessMessage(messageData));
        Assert.assertEquals(0.1, filter.getOdometerKM());
    }

    public void testProcesses000101() {
        SpeedAndOdometerMessageFilter filter = new SpeedAndOdometerMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F0 40 00 00 00 00 01 01 00");
        Assert.assertTrue(filter.doProcessMessage(messageData));
        Assert.assertEquals(25.7, filter.getOdometerKM());
    }

    public void testProcesses010101() {
        SpeedAndOdometerMessageFilter filter = new SpeedAndOdometerMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F0 40 00 00 00 00 01 01 01");
        Assert.assertTrue(filter.doProcessMessage(messageData));
        Assert.assertEquals(6579.3, filter.getOdometerKM());
    }

    public void testProcessesFFFFFF() {
        SpeedAndOdometerMessageFilter filter = new SpeedAndOdometerMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F0 40 00 00 00 00 FF FF FF");
        Assert.assertTrue(filter.doProcessMessage(messageData));
        Assert.assertEquals(1677721.5, filter.getOdometerKM());
    }

    public void testProcessesOdoAndSpeed() {
        SpeedAndOdometerMessageFilter filter = new SpeedAndOdometerMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F0 00 0A 11 00 00 36 29 03 ");
        Assert.assertTrue(filter.doProcessMessage(messageData));
        Assert.assertEquals(133.0, filter.getSpeedInKmH());
        Assert.assertEquals(20715.8, filter.getOdometerKM());
    }

    public void testProcessesOdoAndSpeedUsingResponder() throws IOException {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("AT CRA 4F0", msgOk),
                new Pair<String, String>("AT MA", msg4F0),
                new Pair<String, String>(".", "STOPPED\r>"),
                new Pair<String, String>("AT AR", msgOk)
                );

        Responder responder = new Responder(reqres);

        SpeedAndOdometerMessageFilter filter = new SpeedAndOdometerMessageFilter();
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

//        Assert.assertTrue(caughtException instanceof StoppedException);
        Assert.assertEquals("", responder.getMessages());
        Assert.assertEquals(133.0, vals.get(R.string.col_car_speed_kph));
        Assert.assertEquals(20715.8, vals.get(R.string.col_car_odo_km));
    }

    public void testSpeedAndOdoMessageFilter() {
        CurrentValuesSingleton mValues = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        mValues.setPreferences(prefs);

        SpeedAndOdometerMessageFilter filter = new SpeedAndOdometerMessageFilter();

        ObdMessageData messageData = new ObdMessageData(steves4F0);
        filter.doProcessMessage(messageData);

        assertEquals(0.0, mValues.get(R.string.col_car_speed_kph));
        assertEquals(72495.2, mValues.get(R.string.col_car_odo_km));
    }
}
