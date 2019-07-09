package com.evranger.soulevspy;

import android.test.AndroidTestCase;
import android.util.Log;

import com.evranger.soulevspy.util.BMS2019Parser;
import com.evranger.soulevspy.util.BatteryManagementSystemParser;

import junit.framework.Assert;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-15.
 *
 * Note : this structure needs to be serializable into a database so that we can plot graphs later.
 */
public class ObdBms2019Test extends AndroidTestCase {

    final String msg220101 = "7EC 10 3E 62 01 01 FF F7 E7 \r" +
            "7EC 21 FF 86 42 68 42 68 03 \r" +
            "7EC 22 FF AD 0E DA 12 11 12 \r" +
            "7EC 23 11 11 12 00 00 12 C2 \r" +
            "7EC 24 20 C1 27 00 00 91 00 \r" +
            "7EC 25 00 1D F8 00 00 1C 9D \r" +
            "7EC 26 00 00 0B 13 00 00 0A \r" +
            "7EC 27 50 00 0A 3E FE 09 01 \r" +
            "7EC 28 7C 00 00 00 00 03 E8 \r" +
            ">";

    final String msg220102 = "7EC 10 27 62 01 02 FF FF FF \r" +
            "7EC 21 FF C1 C2 C2 C2 C2 C1 \r" +
            "7EC 22 C2 C1 C2 C2 C2 C1 C2 \r" +
            "7EC 23 C2 C2 C2 C2 C2 C2 C1 \r" +
            "7EC 24 C2 C2 C2 C2 C1 C2 C2 \r" +
            "7EC 25 C1 C2 C2 C1 C2 AA AA \r" +
            ">";

    final String msg220103 = "7EC 10 27 62 01 03 FF FF FF \r" +
            "7EC 21 FF C2 C2 C2 C2 C2 C1 \r" +
            "7EC 22 C1 C1 C2 C1 C2 C2 C2 \r" +
            "7EC 23 C2 C2 C1 C2 C1 C2 C2 \r" +
            "7EC 24 C2 C2 C2 C2 C2 C1 C1 \r" +
            "7EC 25 C1 C1 C1 C1 C1 AA AA \r" +
            ">";

    final String msg220104 = "7EC 10 27 62 01 04 FF FF FF \r" +
            "7EC 21 FF C1 C1 C1 C2 C2 C2 \r" +
            "7EC 22 C2 C2 C2 C1 C2 C2 C2 \r" +
            "7EC 23 C1 C1 C1 C1 C1 C1 C1 \r" +
            "7EC 24 C1 C1 C1 C1 C2 C2 C2 \r" +
            "7EC 25 C1 C1 C2 C2 C1 AA AA \r" +
            ">";

    final String msg220105 = "7EC 10 2E 62 01 05 00 3F FF \r" +
            "7EC 21 90 00 00 00 00 00 00 \r" +
            "7EC 22 00 00 00 00 00 00 42 \r" +
            "7EC 23 68 42 68 00 49 64 11 \r" +
            "7EC 24 00 03 E8 00 00 00 00 \r" +
            "7EC 25 8A 00 00 C1 C1 00 00 \r" +
            "7EC 26 10 00 00 00 00 AA AA \r" +
            ">";

    final String msg220106_charging = "7EC 10 27 62 01 06 FF FF FF \r" +
            "7EC 21 FF 12 00 12 00 0A 00 \r" +
            "7EC 22 00 00 00 00 06 B4 B3 \r" +
            "7EC 23 00 00 00 0B 28 00 00 \r" +
            "7EC 24 00 00 00 00 00 00 00 \r" +
            "7EC 25 00 00 00 00 00 AA AA \r" +
            ">";

    final String msg220106_not_charging = "7EC 10 27 62 01 06 FF FF FF \r" +
            "7EC 21 FF 12 00 12 00 0A 00 \r" +
            "7EC 22 00 00 00 00 63 00 B3 \r" +
            "7EC 23 B4 00 00 0B 28 00 00 \r" +
            "7EC 24 00 00 00 00 00 00 00 \r" +
            "7EC 25 00 00 00 00 00 AA AA \r" +
            ">";

    public void test220106() {
        BMS2019Parser parser = new BMS2019Parser();
        Assert.assertTrue(parser.parseMessage220106(msg220106_charging));

        BMS2019Parser.Data parsedData = parser.getParsedData();

        Assert.assertEquals(18, parsedData.coolingWaterTemperature);
    }
}