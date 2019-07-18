package com.evranger.soulevspy.obd;

import android.test.AndroidTestCase;

import com.evranger.obd.ObdMessageData;

import junit.framework.Assert;

/**
 * Created by Tyrel on 10/17/2015.
 */
public class StateOfChargeWithOneDecimalMessageFilterTest extends AndroidTestCase {

    public void testProcessesZero() {
        SpeedPreciseMessageFilter filter = new SpeedPreciseMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F2 01 00 00 10 00 00 80 00");
        filter.doProcessMessage(messageData);
        Assert.assertEquals(0.0, filter.getSpeedInKmH());
    }

    public void testProcessesFF() {
        SpeedPreciseMessageFilter filter = new SpeedPreciseMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F2 01 FF 00 10 00 00 80 00");
        filter.doProcessMessage(messageData);
        Assert.assertEquals(127.5, filter.getSpeedInKmH());
    }

    public void testProcesses0102() {
        SpeedPreciseMessageFilter filter = new SpeedPreciseMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F2 20 0A 11 28 00 00 00 97"); // This testcase has been confirmed on Henrik's car!
        filter.doProcessMessage(messageData);
        Assert.assertEquals(133.0, filter.getSpeedInKmH());
    }

    public void testProcessesResponseWhenCharging() {
        SpeedPreciseMessageFilter filter = new SpeedPreciseMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F2 01 00");
        filter.doProcessMessage(messageData);
        Assert.assertEquals(-0.001, filter.getSpeedInKmH());
    }
}