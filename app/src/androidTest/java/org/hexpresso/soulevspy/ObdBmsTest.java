package org.hexpresso.soulevspy;

import android.test.AndroidTestCase;
import android.util.Log;

import junit.framework.Assert;

import org.hexpresso.soulevspy.util.BatteryManagementSystemParser;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-15.
 *
 * Note : this structure needs to be serializable into a database so that we can plot graphs later.
 */
public class ObdBmsTest extends AndroidTestCase {

    final String msg2101 = "7EC 10 3D 61 01 FF FF FF FF \r" +
                           "7EA 10 0E 61 01 F0 00 00 00 \r" +
                           "7EC 21 15 23 28 1E C8 A3 00 \r" +
                           "7EA 21 ED 05 02 03 00 00 00 \r" +
                           "7EC 22 1E 0C DD 0E 0D 0E 0D \r" +
                           "7EA 22 00 00 00 00 00 00 00 \r" +
                           "7EC 23 0D 0D 0C 00 0F AB 34 \r" +
                           "7EC 24 AB 43 06 56 84 00 00 \r" +
                           "7EC 25 44 D4 00 00 49 F8 00 \r" +
                           "7EC 26 00 19 B3 00 00 1A EA \r" +
                           "7EC 27 00 09 EC 96 45 01 45 \r" +
                           "7EC 28 00 00 00 00 03 E8 00 \r";

    final String msg2102 = "7EA 10 21 61 02 FF FF 80 00 \r" +
                           "7EC 10 26 61 02 FF FF FF FF \r" +
                           "7EA 21 83 A2 7D 7E 00 00 00 \r" +
                           "7EC 21 AB AB AB AB AB AB AB \r" +
                           "7EA 22 00 00 00 04 00 00 00 \r" +
                           "7EC 22 AB AB AB AB AB AB AB \r" +
                           "7EA 23 00 00 1C 00 22 21 21 \r" +
                           "7EC 23 AB AB AB AB AB AB AB \r" +
                           "7EA 24 FE 07 FF 07 AE 51 00 \r" +
                           "7EC 24 AB AB AB AB AB AB AB \r" +
                           "7EC 25 AB AB AB AB 00 00 00 \r";

    final String msg2103 = "7EA 03 7F 21 12 \r" +
                           "7EC 10 26 61 03 FF FF FF FF \r" +
                           "7EC 21 AB AB AB AB AB AB AB \r" +
                           "7EC 22 AB AB AB AB AB AB AB \r" +
                           "7EC 23 AB AB AB AB AB AB AB \r" +
                           "7EC 24 AB AB AB AB AB AB AB \r" +
                           "7EC 25 AB AB AB AB 00 00 00 \r";

    final String msg2104 = "7EA 03 7F 21 12 \r" +
                           "7EC 10 26 61 04 FF FF FF FF \r" +
                           "7EC 21 AB AB AB AB AB AB AB \r" +
                           "7EC 22 AB AB AB AB AB AB AB \r" +
                           "7EC 23 AB AB AB AB AB AB AB \r" +
                           "7EC 24 AB AB AB AB AB AB AB \r" +
                           "7EC 25 AB AB AB AB 00 00 00 \r";

    final String msg2105 = "7EA 03 7F 21 12 \r" +
                           "7EC 10 2C 61 05 FF FF FF FF \r" +
                           "7EC 21 00 00 00 00 00 0D 0D \r" +
                           "7EC 22 0E 00 00 00 00 23 28 \r" +
                           "7EC 23 1E C8 00 01 50 0D 0C \r" +
                           "7EC 24 00 28 07 00 08 06 13 \r" +
                           "7EC 25 00 00 00 00 00 00 00 \r" +
                           "7EC 26 00 00 00 00 00 00 00 \r";

    public void test2101() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        Assert.assertTrue(parser.parseMessage2101(msg2101));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        Assert.assertEquals(10.5, parsedData.stateOfCharge);
        Assert.assertEquals(true, parsedData.bmsIsCharging);
        Assert.assertEquals(false, parsedData.bmsChademoIsPlugged);
        Assert.assertEquals(true, parsedData.bmsJ1772IsPlugged);
        Assert.assertEquals(329.3, parsedData.batteryDcVoltage);
        Assert.assertEquals(3.42, parsedData.maxCellVoltage);
        Assert.assertEquals(3.42, parsedData.minCellVoltage);
        Assert.assertEquals(90.0, parsedData.availableChargePower);
        Assert.assertEquals(78.8, parsedData.availableDischargePower);
        Assert.assertEquals(13.2, parsedData.auxiliaryBatteryVoltage, 1e-6);
        Assert.assertEquals(14, parsedData.batteryModuleTemperature[0]);
        Assert.assertEquals(13, parsedData.batteryModuleTemperature[1]);
        Assert.assertEquals(14, parsedData.batteryModuleTemperature[2]);
        Assert.assertEquals(13, parsedData.batteryModuleTemperature[3]);
        Assert.assertEquals(13, parsedData.batteryModuleTemperature[4]);
        Assert.assertEquals(13, parsedData.batteryModuleTemperature[5]);
        Assert.assertEquals(12, parsedData.batteryModuleTemperature[6]);
        Assert.assertEquals(15, parsedData.batteryModuleTemperature[7]);
        Assert.assertEquals(15, parsedData.batteryModuleTemperature[7]);
        Assert.assertEquals(52, parsedData.maxCellVoltageNo);
        Assert.assertEquals(1762.0, parsedData.accumulativeChargeCurrent);
        Assert.assertEquals(1893.6, parsedData.accumulativeDischargeCurrent, 1e-6);
        Assert.assertEquals(657.9, parsedData.accumulativeChargePower, 1e-6);
        Assert.assertEquals(689.0, parsedData.accumulativeDischargePower, 1e-6);
        Assert.assertEquals(650390, parsedData.accumulativeOperatingTime);
        Assert.assertEquals(0, parsedData.driveMotorSpeed);
        Assert.assertEquals(BatteryManagementSystemParser.CoolingFanSpeeds.FAN_6TH, parsedData.fanStatus);
        Assert.assertEquals(86, parsedData.fanFeedbackSignal);
    }

    public void test2102() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        Assert.assertTrue(parser.parseMessage2102(msg2102));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        for( int i = 0; i < 32; ++i ) {
            Assert.assertEquals(3.42, parsedData.batteryCellVoltage[i]);
        }
    }

    public void test2103() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        Assert.assertTrue(parser.parseMessage2103(msg2103));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        for( int i = 33; i < 64; ++i ) {
            Assert.assertEquals(3.42, parsedData.batteryCellVoltage[i]);
        }
    }

    public void test2104() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        Assert.assertTrue(parser.parseMessage2104(msg2104));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();
        for( int i = 65; i < 96; ++i ) {
            Assert.assertEquals(3.42, parsedData.batteryCellVoltage[i]);
        }
    }

    public void test2105() {
        BatteryManagementSystemParser parser = new BatteryManagementSystemParser();
        Assert.assertTrue(parser.parseMessage2105(msg2105));

        BatteryManagementSystemParser.Data parsedData = parser.getParsedData();

        Assert.assertEquals(14, parsedData.batteryMaxTemperature);
        Assert.assertEquals(13, parsedData.batteryMinTemperature);
        Assert.assertEquals(13, parsedData.batteryInletTemperature);
        Assert.assertEquals(80, parsedData.airbagHwireDuty);
        Assert.assertEquals(13, parsedData.heat1Temperature);
        Assert.assertEquals(12, parsedData.heat2Temperature);

        Assert.assertEquals(4.0, parsedData.maxDeterioration);
        Assert.assertEquals(7, parsedData.maxDeteriorationCellNo);
        Assert.assertEquals(0.8, parsedData.minDeterioration);
        Assert.assertEquals(6, parsedData.minDeteriorationCellNo);
        Assert.assertEquals(9.5, parsedData.stateOfChargeDisplay);
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