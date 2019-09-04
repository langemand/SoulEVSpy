package com.evranger.soulevspy;

import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import com.evranger.soulevspy.util.BatteryManagementSystemParser;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by Henrik Scheel <henrik.scheel@spjeldager.com> on 2018-11-03.
 */
@RunWith(AndroidJUnit4.class)
public class ObdBmsIwerTest {

    final String msg2101 = "7EC 10 3D 61 01 FF FF FF FF \r" +
            "7EC 21 91 22 C0 23 28 A3 FF \r" +
            "7EC 22 C0 0E CB 10 0E 10 0F \r" +
            "7EC 23 0E 0F 0E 00 10 C5 2A \r" +
            "7EC 24 C4 55 00 00 8F 00 00 \r" +
            "7EC 25 0E FA 00 00 0E DB 00 \r" +
            "7EC 26 00 05 AD 00 00 05 7D \r" +
            "7EC 27 00 02 D2 AB 09 01 77 \r";

    final String msg2102 = "7EC 10 26 61 02 FF FF FF FF \r" +
            "7EC 21 C5 C5 C5 C5 C5 C5 C5 \r" +
            "7EC 22 C5 C5 C5 C4 C5 C5 C5 \r" +
            "7EC 23 C5 C5 C5 C5 C5 C5 C5 \r" +
            "7EC 24 C5 C5 C5 C5 C5 C5 C5 \r" +
            "7EC 25 C5 C5 C5 C5 00 00 00 \r";

    final String msg2103 = "7EC 10 26 61 03 FF FF FF FF \r" +
            "7EC 21 C5 C5 C5 C5 C5 C5 C5 \r" +
            "7EC 22 C5 C5 C6 C5 C5 C5 C5 \r" +
            "7EC 23 C5 C5 C5 C5 C5 C5 C5 \r" +
            "7EC 24 C5 C5 C5 C5 C5 C5 C5 \r" +
            "7EC 25 C5 C5 C5 C5 00 00 00 \r";

    final String msg2104 = "7EC 10 26 61 04 FF FF FF FF \r" +
            "7EC 21 C5 C5 C5 C5 C5 C5 C5 \r" +
            "7EC 22 C5 C5 C5 C5 C5 C5 C5 \r" +
            "7EC 23 C5 C5 C5 C5 C5 C5 C4 \r" +
            "7EC 24 C5 C5 C5 C5 C5 C5 C5 \r" +
            "7EC 25 C5 C5 C5 C5 00 00 00 \r";

    final String msg2105 = "7EC 10 2C 61 05 FF FF FF FF \r" +
            "7EC 21 00 00 00 00 00 0F 0F \r" +
            "7EC 22 10 00 00 00 00 22 C0 \r" +
            "7EC 23 23 28 01 01 64 0E 0F \r" +
            "7EC 24 00 00 00 00 00 00 98 \r" +
            "7EC 25 00 00 00 00 00 00 00 \r" +
            "7EC 26 00 00 00 00 00 00 00 \r";

    public void test2101() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        assertTrue(parser.parseMessage2101(msg2101));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        assertEquals(72.5, parsedData.stateOfCharge);
        assertEquals(true, parsedData.bmsIsCharging);
        assertEquals(false, parsedData.bmsChademoIsPlugged);
        assertEquals(true, parsedData.bmsJ1772IsPlugged);
        assertEquals(378.7, parsedData.batteryDcVoltage, 1e-6);
        assertEquals(3.94, parsedData.maxCellVoltage);
        assertEquals(3.92, parsedData.minCellVoltage);
        assertEquals(88.96, parsedData.availableChargePower, 1e-6);
        assertEquals(90.0, parsedData.availableDischargePower);
        assertEquals(14.3, parsedData.auxiliaryBatteryVoltage, 1e-6);
        assertEquals(16, parsedData.batteryModuleTemperature[0]);
        assertEquals(14, parsedData.batteryModuleTemperature[1]);
        assertEquals(16, parsedData.batteryModuleTemperature[2]);
        assertEquals(15, parsedData.batteryModuleTemperature[3]);
        assertEquals(14, parsedData.batteryModuleTemperature[4]);
        assertEquals(15, parsedData.batteryModuleTemperature[5]);
        assertEquals(14, parsedData.batteryModuleTemperature[6]);
        assertEquals(16, parsedData.batteryInletTemperature);
        assertEquals(42, parsedData.maxCellVoltageNo);
        assertEquals(383.4, parsedData.accumulativeChargeCurrent, 1e-6);
        assertEquals(380.3, parsedData.accumulativeDischargeCurrent, 1e-6);
        assertEquals(145.3, parsedData.accumulativeChargePower, 1e-6);
        assertEquals(140.5, parsedData.accumulativeDischargePower, 1e-6);
        assertEquals(185003, parsedData.accumulativeOperatingTime);
        assertEquals(0, parsedData.driveMotorSpeed);
        assertEquals(BatteryManagementSystemParser.CoolingFanSpeeds.FAN_STOP, parsedData.fanStatus);
        assertEquals(0, parsedData.fanFeedbackSignal);
    }

    public void test2102() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        assertTrue(parser.parseMessage2102(msg2102));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        for( int i = 0; i < 32; ++i ) {
            if (i==10) {
                assertEquals(3.92, parsedData.batteryCellVoltage[i]);
            } else {
                assertEquals(3.94, parsedData.batteryCellVoltage[i]);
            }
        }
    }

    public void test2103() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        assertTrue(parser.parseMessage2103(msg2103));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        for( int i = 33; i < 64; ++i ) {
            if (i==41) {
                assertEquals(3.96, parsedData.batteryCellVoltage[i]);
            } else {
                assertEquals(3.94, parsedData.batteryCellVoltage[i]);
            }
        }
    }

    public void test2104() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        assertTrue(parser.parseMessage2104(msg2104));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        for( int i = 65; i < 96; ++i ) {
            if (i==84) {
                assertEquals(3.92, parsedData.batteryCellVoltage[i]);
            } else {
                assertEquals(3.94, parsedData.batteryCellVoltage[i]);
            }
        }
    }

    public void test2105() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        assertTrue(parser.parseMessage2105(msg2105));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();

        assertEquals(16, parsedData.batteryMaxTemperature);
        assertEquals(15, parsedData.batteryMinTemperature);
        assertEquals(100, parsedData.airbagHwireDuty);
        assertEquals(14, parsedData.heat1Temperature);
        assertEquals(15, parsedData.heat2Temperature);

        assertEquals(0.0, parsedData.maxDeterioration);
        assertEquals(0, parsedData.maxDeteriorationCellNo);
        assertEquals(0.0, parsedData.minDeterioration);
        assertEquals(0, parsedData.minDeteriorationCellNo);
        assertEquals(76.0, parsedData.stateOfChargeDisplay);
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