package org.hexpresso.soulevspy.obd;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import org.hexpresso.obd.ObdMessageData;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;
import org.hexpresso.soulevspy.util.ClientSharedPreferences;

/**
 * Created by Tyrel on 10/17/2015.
 */
public class SpeedPreciseMessageFilterTest extends AndroidTestCase {

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
        ObdMessageData messageData = new ObdMessageData("4F2 01 02 80 10 00 00 80 00"); // Not avalid testscase on Henrik's car!
        filter.doProcessMessage(messageData);
        Assert.assertEquals(129.0, filter.getSpeedInKmH());
    }
}