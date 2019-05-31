package com.evranger.soulevspy.obd;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import com.evranger.obd.ObdMessageData;
import com.evranger.soulevspy.obd.OdometerMessageFilter;

/**
 * Created by Tyrel on 10/17/2015.
 */
public class OdometerMessageFilterTest extends AndroidTestCase {

    public void testProcessesZero() {
        OdometerMessageFilter filter = new OdometerMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F0 40 00 00 00 00 00 00 00");
        Assert.assertTrue(filter.doProcessMessage(messageData));
        Assert.assertEquals(0.0, filter.getOdometerKM());
    }

    public void testProcesses000001() {
        OdometerMessageFilter filter = new OdometerMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F0 40 00 00 00 00 01 00 00");
        Assert.assertTrue(filter.doProcessMessage(messageData));
        Assert.assertEquals(0.1, filter.getOdometerKM());
    }

    public void testProcesses000101() {
        OdometerMessageFilter filter = new OdometerMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F0 40 00 00 00 00 01 01 00");
        Assert.assertTrue(filter.doProcessMessage(messageData));
        Assert.assertEquals(25.7, filter.getOdometerKM());
    }

    public void testProcesses010101() {
        OdometerMessageFilter filter = new OdometerMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F0 40 00 00 00 00 01 01 01");
        Assert.assertTrue(filter.doProcessMessage(messageData));
        Assert.assertEquals(6579.3, filter.getOdometerKM());
    }

    public void testProcessesFFFFFF() {
        OdometerMessageFilter filter = new OdometerMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F0 40 00 00 00 00 FF FF FF");
        Assert.assertTrue(filter.doProcessMessage(messageData));
        Assert.assertEquals(1677721.5, filter.getOdometerKM());
    }

}
