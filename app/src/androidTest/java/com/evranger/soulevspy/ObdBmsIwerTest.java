package com.evranger.soulevspy;

import android.test.AndroidTestCase;
import android.util.Log;

import junit.framework.Assert;

import com.evranger.soulevspy.util.BatteryManagementSystemParser;

/**
 * Created by Henrik Scheel <henrik.scheel@spjeldager.com> on 2018-11-03.
 */
public class ObdBmsIwerTest extends AndroidTestCase {

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
        Assert.assertTrue(parser.parseMessage2101(msg2101));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        Assert.assertEquals(72.5, parsedData.stateOfCharge);
        Assert.assertEquals(true, parsedData.bmsIsCharging);
        Assert.assertEquals(false, parsedData.bmsChademoIsPlugged);
        Assert.assertEquals(true, parsedData.bmsJ1772IsPlugged);
        Assert.assertEquals(378.7, parsedData.batteryDcVoltage, 1e-6);
        Assert.assertEquals(3.94, parsedData.maxCellVoltage);
        Assert.assertEquals(3.92, parsedData.minCellVoltage);
        Assert.assertEquals(88.96, parsedData.availableChargePower, 1e-6);
        Assert.assertEquals(90.0, parsedData.availableDischargePower);
        Assert.assertEquals(14.3, parsedData.auxiliaryBatteryVoltage, 1e-6);
        Assert.assertEquals(16, parsedData.batteryModuleTemperature[0]);
        Assert.assertEquals(14, parsedData.batteryModuleTemperature[1]);
        Assert.assertEquals(16, parsedData.batteryModuleTemperature[2]);
        Assert.assertEquals(15, parsedData.batteryModuleTemperature[3]);
        Assert.assertEquals(14, parsedData.batteryModuleTemperature[4]);
        Assert.assertEquals(15, parsedData.batteryModuleTemperature[5]);
        Assert.assertEquals(14, parsedData.batteryModuleTemperature[6]);
        Assert.assertEquals(16, parsedData.batteryInletTemperature);
        Assert.assertEquals(42, parsedData.maxCellVoltageNo);
        Assert.assertEquals(383.4, parsedData.accumulativeChargeCurrent, 1e-6);
        Assert.assertEquals(380.3, parsedData.accumulativeDischargeCurrent, 1e-6);
        Assert.assertEquals(145.3, parsedData.accumulativeChargePower, 1e-6);
        Assert.assertEquals(140.5, parsedData.accumulativeDischargePower, 1e-6);
        Assert.assertEquals(185003, parsedData.accumulativeOperatingTime);
        Assert.assertEquals(0, parsedData.driveMotorSpeed);
        Assert.assertEquals(BatteryManagementSystemParser.CoolingFanSpeeds.FAN_STOP, parsedData.fanStatus);
        Assert.assertEquals(0, parsedData.fanFeedbackSignal);
    }

    public void test2102() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        Assert.assertTrue(parser.parseMessage2102(msg2102));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        for( int i = 0; i < 32; ++i ) {
            if (i==10) {
                Assert.assertEquals(3.92, parsedData.batteryCellVoltage[i]);
            } else {
                Assert.assertEquals(3.94, parsedData.batteryCellVoltage[i]);
            }
        }
    }

    public void test2103() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        Assert.assertTrue(parser.parseMessage2103(msg2103));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        for( int i = 33; i < 64; ++i ) {
            if (i==41) {
                Assert.assertEquals(3.96, parsedData.batteryCellVoltage[i]);
            } else {
                Assert.assertEquals(3.94, parsedData.batteryCellVoltage[i]);
            }
        }
    }

    public void test2104() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        Assert.assertTrue(parser.parseMessage2104(msg2104));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        for( int i = 65; i < 96; ++i ) {
            if (i==84) {
                Assert.assertEquals(3.92, parsedData.batteryCellVoltage[i]);
            } else {
                Assert.assertEquals(3.94, parsedData.batteryCellVoltage[i]);
            }
        }
    }

    public void test2105() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        Assert.assertTrue(parser.parseMessage2105(msg2105));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();

        Assert.assertEquals(16, parsedData.batteryMaxTemperature);
        Assert.assertEquals(15, parsedData.batteryMinTemperature);
        Assert.assertEquals(100, parsedData.airbagHwireDuty);
        Assert.assertEquals(14, parsedData.heat1Temperature);
        Assert.assertEquals(15, parsedData.heat2Temperature);

        Assert.assertEquals(0.0, parsedData.maxDeterioration);
        Assert.assertEquals(0, parsedData.maxDeteriorationCellNo);
        Assert.assertEquals(0.0, parsedData.minDeterioration);
        Assert.assertEquals(0, parsedData.minDeteriorationCellNo);
        Assert.assertEquals(76.0, parsedData.stateOfChargeDisplay);
    }

    public void testToString() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        Assert.assertTrue(parser.parseMessage2101(msg2101));
        Assert.assertTrue(parser.parseMessage2102(msg2102));
        Assert.assertTrue(parser.parseMessage2103(msg2103));
        Assert.assertTrue(parser.parseMessage2104(msg2104));
        Assert.assertTrue(parser.parseMessage2105(msg2105));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();

        final String str = parsedData.toString();
        Log.d("ObdBmsTest-toString", str);
        Assert.assertFalse(str.isEmpty());
    }
}