package com.evranger.soulevspy.obd;

import androidx.test.runner.AndroidJUnit4;

import com.evranger.obd.ObdMessageData;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Tyrel on 10/17/2015.
 */
@RunWith(AndroidJUnit4.class)
public class SpeedPreciseMessageFilterTest {

    @Test
    public void testProcessesZero() {
        SpeedPreciseMessageFilter filter = new SpeedPreciseMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F2 01 00 00 10 00 00 80 00");
        filter.doProcessMessage(messageData);
        assertEquals(0.0, filter.getSpeedInKmH());
    }

    @Test
    public void testProcessesFF() {
        SpeedPreciseMessageFilter filter = new SpeedPreciseMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F2 01 FF 00 10 00 00 80 00");
        filter.doProcessMessage(messageData);
        assertEquals(127.5, filter.getSpeedInKmH());
    }

    @Test
    public void testProcesses0102() {
        SpeedPreciseMessageFilter filter = new SpeedPreciseMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F2 20 0A 11 28 00 00 00 97"); // This testcase has been confirmed on Henrik's car!
        filter.doProcessMessage(messageData);
        assertEquals(133.0, filter.getSpeedInKmH());
    }

    @Test
    public void testProcessesResponseWhenCharging() {
        SpeedPreciseMessageFilter filter = new SpeedPreciseMessageFilter();
        ObdMessageData messageData = new ObdMessageData("4F2 01 00");
        filter.doProcessMessage(messageData);
        assertEquals(-0.001, filter.getSpeedInKmH());
    }
}