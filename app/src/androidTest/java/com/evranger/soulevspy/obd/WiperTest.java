package com.evranger.soulevspy.obd;

import androidx.test.runner.AndroidJUnit4;

import com.evranger.obd.ObdMessageData;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by henrik on 23/11/2017.
 */
@RunWith(AndroidJUnit4.class)
public class WiperTest {
    private final static String WIPERS_OFF = "050 00 80 00 00";
    private final static String WIPERS_INTER0 = "050 00 80 02 00";
    private final static String WIPERS_INTER1 = "050 00 60 02 00";
    private final static String WIPERS_INTER2 = "050 00 40 02 00";
    private final static String WIPERS_INTER3 = "050 00 20 02 00";
    private final static String WIPERS_INTER4 = "050 00 00 02 00";
    private final static String WIPERS_NORMAL = "050 00 00 01 00";
    private final static String WIPERS_FAST = "050 00 00 04 00";

    /**
     * Wiper tests
     */
    @Test
    public void testWipers() {
        Status050MessageFilter status050MessageFilter = new Status050MessageFilter();
        status050MessageFilter.doProcessMessage(new ObdMessageData(WIPERS_OFF));
        assertEquals(Status050MessageFilter.WiperSpeed.OFF, status050MessageFilter.getWiperSpeedStatus());

        status050MessageFilter.doProcessMessage(new ObdMessageData(WIPERS_INTER0));
        assertEquals(Status050MessageFilter.WiperSpeed.INTER_0, status050MessageFilter.getWiperSpeedStatus());

        status050MessageFilter.doProcessMessage(new ObdMessageData(WIPERS_INTER1));
        assertEquals(Status050MessageFilter.WiperSpeed.INTER_1, status050MessageFilter.getWiperSpeedStatus());

        status050MessageFilter.doProcessMessage(new ObdMessageData(WIPERS_INTER2));
        assertEquals(Status050MessageFilter.WiperSpeed.INTER_2, status050MessageFilter.getWiperSpeedStatus());

        status050MessageFilter.doProcessMessage(new ObdMessageData(WIPERS_INTER3));
        assertEquals(Status050MessageFilter.WiperSpeed.INTER_3, status050MessageFilter.getWiperSpeedStatus());

        status050MessageFilter.doProcessMessage(new ObdMessageData(WIPERS_INTER4));
        assertEquals(Status050MessageFilter.WiperSpeed.INTER_4, status050MessageFilter.getWiperSpeedStatus());

        status050MessageFilter.doProcessMessage(new ObdMessageData(WIPERS_NORMAL));
        assertEquals(Status050MessageFilter.WiperSpeed.NORMAL, status050MessageFilter.getWiperSpeedStatus());

        status050MessageFilter.doProcessMessage(new ObdMessageData(WIPERS_FAST));
        assertEquals(Status050MessageFilter.WiperSpeed.FAST, status050MessageFilter.getWiperSpeedStatus());
    }
}
