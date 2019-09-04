package com.evranger.soulevspy;

import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.evranger.soulevspy.util.BatteryManagementSystemParser;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-15.
 */
@RunWith(AndroidJUnit4.class)
public class ObdBms2018Test {

    final String msg2101 = "7EC 10 3D 61 01 FF FF FF FF \r" +
                           "7EC 21 76 23 28 23 28 03 00 \r" +
                           "7EC 22 24 0E 71 07 05 07 06 \r" +
                           "7EC 23 06 05 05 00 08 B8 15 \r" +
                           "7EC 24 B8 01 00 00 8C 00 00 \r" +
                           "7EC 25 89 27 00 00 89 FF 00 \r" +
                           "7EC 26 00 34 B9 00 00 33 9E \r" +
                           "7EC 27 00 23 1E 16 2D 01 71 \r" +
                           "7EC 28 00 00 00 00 03 E8 00 \r";

    final String msg2102 = "7EC 10 26 61 02 FF FF FF FF \r" +
                           "7EC 21 B8 B8 B8 B8 B8 B8 B8 \r" +
                           "7EC 22 B8 B8 B8 B8 B8 B8 B8 \r" +
                           "7EC 23 B8 B8 B8 B8 B8 B8 B8 \r" +
                           "7EC 24 B8 B8 B8 B8 B8 B8 B8 \r" +
                           "7EC 25 B8 B8 B8 B8 00 00 00 \r";

    final String msg2103 = "7EC 10 26 61 03 FF FF FF FF \r" +
                           "7EC 21 B8 B8 B8 B8 B8 B8 B8 \r" +
                           "7EC 22 B8 B8 B8 B8 B8 B8 B8 \r" +
                           "7EC 23 B8 B8 B8 B8 B8 B8 B8 \r" +
                           "7EC 24 B8 B8 B8 B8 B8 B8 B8 \r" +
                           "7EC 25 B8 B8 B8 B8 00 00 00 \r";

    final String msg2104 = "7EC 10 26 61 04 FF FF FF FF \r" +
                           "7EC 21 B8 B8 B8 B8 B8 B8 B8 \r" +
                           "7EC 22 B8 B8 B8 B8 B8 B8 B8 \r" +
                           "7EC 23 B8 B8 B8 B8 B8 B8 B8 \r" +
                           "7EC 24 B8 B8 B8 B8 B8 B8 B8 \r" +
                           "7EC 25 B8 B8 B8 B8 00 00 00 \r";

    final String msg2105 = "7EC 10 2D 61 05 FF FF FF FF \r" +
                           "7EC 21 B8 B8 B8 B8 B8 05 06 \r" +
                           "7EC 22 05 00 00 00 00 23 28 \r" +
                           "7EC 23 23 28 00 01 4B 07 06 \r" +
                           "7EC 24 00 00 00 00 00 00 7A \r" +
                           "7EC 25 00 00 B8 B9 00 00 00 \r" +
                           "7EC 26 00 00 00 00 00 00 00 \r";

    @Test
    public void test2101() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        assertTrue(parser.parseMessage2101(msg2101));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        assertEquals(59.0, parsedData.stateOfCharge, 1e-6);
        assertEquals(false, parsedData.bmsIsCharging);
        assertEquals(false, parsedData.bmsChademoIsPlugged);
        assertEquals(false, parsedData.bmsJ1772IsPlugged);
        assertEquals(369.7, parsedData.batteryDcVoltage, 1e-6);
        assertEquals(3.68, parsedData.maxCellVoltage, 1e-6);
        assertEquals(3.68, parsedData.minCellVoltage, 1e-6);
        assertEquals(90.0, parsedData.availableChargePower, 1e-6);
        assertEquals(90.0, parsedData.availableDischargePower, 1e-6);
        assertEquals(14.0, parsedData.auxiliaryBatteryVoltage, 1e-6);
        assertEquals(7, parsedData.batteryModuleTemperature[0]);
        assertEquals(5, parsedData.batteryModuleTemperature[1]);
        assertEquals(7, parsedData.batteryModuleTemperature[2]);
        assertEquals(6, parsedData.batteryModuleTemperature[3]);
        assertEquals(6, parsedData.batteryModuleTemperature[4]);
        assertEquals(5, parsedData.batteryModuleTemperature[5]);
        assertEquals(5, parsedData.batteryModuleTemperature[6]);
        assertEquals(8, parsedData.batteryInletTemperature);
        assertEquals(21, parsedData.maxCellVoltageNo);
        assertEquals(3511.1, parsedData.accumulativeChargeCurrent, 1e-6);
        assertEquals(3532.7, parsedData.accumulativeDischargeCurrent, 1e-6);
        assertEquals(1349.7, parsedData.accumulativeChargePower, 1e-6);
        assertEquals(1321.4, parsedData.accumulativeDischargePower, 1e-6);
        assertEquals(2301462, parsedData.accumulativeOperatingTime);
        assertEquals(0, parsedData.driveMotorSpeed);
        assertEquals(BatteryManagementSystemParser.CoolingFanSpeeds.FAN_STOP, parsedData.fanStatus);
        assertEquals(0, parsedData.fanFeedbackSignal);
    }

    @Test
    public void test2102() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        assertTrue(parser.parseMessage2102(msg2102));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        for( int i = 0; i < 32; ++i ) {
            assertEquals(3.68, parsedData.batteryCellVoltage[i]);
        }
    }

    @Test
    public void test2103() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        assertTrue(parser.parseMessage2103(msg2103));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        for( int i = 33; i < 64; ++i ) {
            assertEquals(3.68, parsedData.batteryCellVoltage[i]);
        }
    }

    @Test
    public void test2104() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        assertTrue(parser.parseMessage2104(msg2104));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        for( int i = 65; i < 96; ++i ) {
            assertEquals(3.68, parsedData.batteryCellVoltage[i]);
        }
    }

    @Test
    public void test2105() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        assertTrue(parser.parseMessage2105(msg2105));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();

        for( int i = 97; i < 101; ++i ) {
            assertEquals(3.68, parsedData.batteryCellVoltage[i]);
        }
        assertEquals(5, parsedData.batteryMaxTemperature);
        assertEquals(6, parsedData.batteryMinTemperature);
        assertEquals(75, parsedData.airbagHwireDuty);
        assertEquals(7, parsedData.heat1Temperature);
        assertEquals(6, parsedData.heat2Temperature);

        assertEquals(0.0, parsedData.maxDeterioration);
        assertEquals(0, parsedData.maxDeteriorationCellNo);
        assertEquals(0.0, parsedData.minDeterioration);
        assertEquals(0, parsedData.minDeteriorationCellNo);
        assertEquals(61.0, parsedData.stateOfChargeDisplay);
    }

    @Test
    public void testToString() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        assertTrue(parser.parseMessage2101(msg2101));
        assertTrue(parser.parseMessage2102(msg2102));
        assertTrue(parser.parseMessage2103(msg2103));
        assertTrue(parser.parseMessage2104(msg2104));
        assertTrue(parser.parseMessage2105(msg2105));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();

        final String str = parsedData.toString();
        Log.d("ObdBmsTest-toString", str);
        assertFalse(str.isEmpty());
    }
}