package com.evranger.soulevspy;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import com.evranger.soulevspy.util.KiaVinParser;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-07.
 */
public class KiaVinParserTest extends AndroidTestCase {

    // Bogus strings
    private final static String VIN_EMPTY      = "";
    private final static String VIN_NOT_KIA    = "KNXJX3AE5F7123456";
    private final static String VIN_NOT_SOUL   = "KNDXX3AE5F7123456";
    private final static String VIN_NOT_SOULEV = "KNDJX3AX5F7123456";

    // Test VIN strings
    private final static String VIN_2015_LUXURY_WHITE    = "KNDJX3AE5F7123456"; // Mario
    private final static String VIN_2016_LUXURY_TITANIUM = "KNDJX3AEXG7123456"; // Our car
    private final static String VIN_2016_LUXURY_WHITE    = "KNDJX3AE1G7123456"; // EEKO, 2016 luxury white
    private final static String VIN_2017_FAKE            = "KNDJX3AE0H0123456"; // Fake 2017
    private final static String VIN_2016_TYREL           = "KNDJX3AE2G7006329";
    private final static String VIN_2015_HENRIK          = "KNAJX81EFF7002432";
    private final static String VIN_2016_IWER            = "KNAJX81EFG7008726";

    // AVT
    private final static String VIN_2015_AVT_1908        = "KNDJX3AE6F7001908"; // AVT VIN 1908
    private final static String VIN_2015_AVT_1918        = "KNDJX3AE9F7001918"; // AVT VIN 1918
    private final static String VIN_2015_AVT_1919        = "KNDJX3AE0F7001919"; // AVT VIN 1919
    private final static String VIN_2015_AVT_1920        = "KNDJX3AE7F7001920"; // AVT VIN 1920

    // On the web
    private final static String VIN_2016_5477            = "KNDJX3AE1G7005477"; // westonkia.com
    private final static String VIN_2015_3847            = "KNDJP3AE0F7003847"; // 2015 Kia Soul EV Base (kia.mycommunitycar.com)
    private final static String VIN_2015_3819            = "KNDJP3AE6F7003819"; // 2015 Kia Soul EV Base (kia.mycommunitycar.com)
    private final static String VIN_2015_3798            = "KNDJX3AE2F7003798"; // 2015 Kia Soul EV Plus (kia.mycommunitycar.com)
    private final static String VIN_2015_3644            = "KNDJX3AE8F7003644"; // 2015 Kia Soul EV Plus (kia.mycommunitycar.com)

    // Matts car
    private final static String VIN_MATT                = "KNDJX3AE2F7001565"; // Matts VIN

    // Henriks e-Soul 2020
    private final static String VIN_ESOUL                = "KNAJ3811FL7000543"; // Henriks eSoul VIN

    // Model year - A = 2010, B = 2011; but I, O, Q, U, Z are skipped, and after Y comes 1, 2, 3, etc
    private final static String VIN_2014                = "KNDJX3AE1E7005477";
    private final static String VIN_2015                = "KNDJX3AE1F7005477";
    private final static String VIN_2016                = "KNDJX3AE1G7005477";
    private final static String VIN_2017                = "KNDJX3AE1H7005477";
    private final static String VIN_2018                = "KNDJX3AE1J7005477";
    // Skip I
    private final static String VIN_2019                = "KNDJX3AE1K7005477";
    private final static String VIN_2020                = "KNDJX3AE1L7005477";
    private final static String VIN_2021                = "KNDJX3AE1M7005477";
    private final static String VIN_2022                = "KNDJX3AE1N7005477";
    // Skip O
    private final static String VIN_2023                = "KNDJX3AE1P7005477";
    // Skip Q
    private final static String VIN_2024                = "KNDJX3AE1R7005477";
    private final static String VIN_2025                = "KNDJX3AE1S7005477";
    private final static String VIN_2026                = "KNDJX3AE1T7005477";
    // Skip U
    private final static String VIN_2027                = "KNDJX3AE1V7005477";
    private final static String VIN_2028                = "KNDJX3AE1W7005477";
    private final static String VIN_2029                = "KNDJX3AE1X7005477";
    private final static String VIN_2030                = "KNDJX3AE1Y7005477";
    private final static String VIN_2031                = "KNDJX3AE117005477";
    private final static String VIN_2032                = "KNDJX3AE127005477";
    private final static String VIN_2033                = "KNDJX3AE137005477";
    private final static String VIN_2034                = "KNDJX3AE147005477";
    private final static String VIN_2035                = "KNDJX3AE157005477";
    private final static String VIN_2036                = "KNDJX3AE167005477";
    private final static String VIN_2037                = "KNDJX3AE177005477";
    private final static String VIN_2038                = "KNDJX3AE187005477";
    private final static String VIN_2039                = "KNDJX3AE197005477";

    // Text constants
    private final static String SOULEV_BASE       = "Base";
    private final static String SOULEV_PLUS       = "Plus/Luxury";
    private final static String SOULEV_ENGINE     = "Battery [LiPB 350 V, 75 Ah] + Motor [3-phase AC 80KW]";
    private final static String SOULEV_PROD_PLANT = "Gwangju (South Korea)";
    private final static String SOULEV2020_EXCLUSIVE  = "Exclusive";
    private final static String SOULEV2020_ENGINE     = "Battery [LiPo 356 V, 180 Ah] + Motor [3-phase AC 150KW]";

    /**
     * Illegal Strings
     */
    public void testLegalStrings() {
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2015_LUXURY_WHITE).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2016_LUXURY_TITANIUM).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2016_LUXURY_WHITE).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2017_FAKE).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2015_AVT_1908).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2015_AVT_1918).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2015_AVT_1919).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2015_AVT_1920).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2016_5477).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2015_3847).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2015_3819).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2015_3798).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2015_3644).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2016_TYREL).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2015_HENRIK).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_2016_IWER).isValid());
        Assert.assertTrue(new KiaVinParser(getContext(), VIN_ESOUL).isValid());
    }

    /**
     * LEgal Strings
     */
    public void testIllegalStrings() {
        Assert.assertFalse(new KiaVinParser(getContext(), VIN_EMPTY).isValid());
        Assert.assertFalse(new KiaVinParser(getContext(), VIN_NOT_KIA).isValid());
        Assert.assertFalse(new KiaVinParser(getContext(), VIN_NOT_SOUL).isValid());
        Assert.assertFalse(new KiaVinParser(getContext(), VIN_NOT_SOULEV).isValid());
    }

    /**
     * Lower case input
     */
    public void testLowerCase() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_2016_LUXURY_TITANIUM.toLowerCase());
        Assert.assertTrue(vin.isValid());
        Assert.assertEquals(vin.getVIN(), VIN_2016_LUXURY_TITANIUM);
        Assert.assertEquals(vin.getBrand(), "Kia");
        Assert.assertEquals(vin.getModel(), "Soul EV");
        Assert.assertEquals(vin.getTrim(), SOULEV_PLUS);
        Assert.assertEquals(vin.getEngine(), SOULEV_ENGINE);
        Assert.assertEquals(vin.getYear(), "2016");
        Assert.assertEquals(vin.getSequentialNumber(), "123456");
        Assert.assertEquals(vin.getProductionPlant(), SOULEV_PROD_PLANT);
    }

    /**
     * Valid VIN from a 2016 Luxury canadian model
     */
    public void test2016LuxuryTitanium() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_2016_LUXURY_TITANIUM);
        Assert.assertTrue(vin.isValid());
        Assert.assertEquals(vin.getVIN(), VIN_2016_LUXURY_TITANIUM);
        Assert.assertEquals(vin.getBrand(), "Kia");
        Assert.assertEquals(vin.getModel(), "Soul EV");
        Assert.assertEquals(vin.getTrim(), SOULEV_PLUS);
        Assert.assertEquals(vin.getEngine(), SOULEV_ENGINE);
        Assert.assertEquals(vin.getYear(), "2016");
        Assert.assertEquals(vin.getSequentialNumber(), "123456");
        Assert.assertEquals(vin.getProductionPlant(), SOULEV_PROD_PLANT);
    }

    /**
     * Valid VIN from a 2016 Luxury canadian model
     */
    public void test2016LuxuryWhite() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_2016_LUXURY_WHITE);
        Assert.assertTrue(vin.isValid());
        Assert.assertEquals(vin.getVIN(), VIN_2016_LUXURY_WHITE);
        Assert.assertEquals(vin.getBrand(), "Kia");
        Assert.assertEquals(vin.getModel(), "Soul EV");
        Assert.assertEquals(vin.getTrim(), SOULEV_PLUS);
        Assert.assertEquals(vin.getEngine(), SOULEV_ENGINE);
        Assert.assertEquals(vin.getYear(), "2016");
        Assert.assertEquals(vin.getSequentialNumber(), "123456");
        Assert.assertEquals(vin.getProductionPlant(), SOULEV_PROD_PLANT);
    }

    /**
     * Valid VIN from a 2016 Base US model
     */
    public void test2015BaseWhite() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_2015_3847);
        Assert.assertTrue(vin.isValid());
        Assert.assertEquals(vin.getVIN(), VIN_2015_3847);
        Assert.assertEquals(vin.getBrand(), "Kia");
        Assert.assertEquals(vin.getModel(), "Soul EV");
        Assert.assertEquals(vin.getTrim(), SOULEV_BASE);
        Assert.assertEquals(vin.getEngine(), SOULEV_ENGINE);
        Assert.assertEquals(vin.getYear(), "2015");
        Assert.assertEquals(vin.getSequentialNumber(), "003847");
        Assert.assertEquals(vin.getProductionPlant(), SOULEV_PROD_PLANT);
    }

    /**
     * Valid VIN from a 2017 fake model (shows unknown plant)
     */
    public void test2017Fake() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_2017_FAKE);
        Assert.assertTrue(vin.isValid());
        Assert.assertEquals(vin.getVIN(), VIN_2017_FAKE);
        Assert.assertEquals(vin.getBrand(), "Kia");
        Assert.assertEquals(vin.getModel(), "Soul EV");
        Assert.assertEquals(vin.getTrim(), SOULEV_PLUS);
        Assert.assertEquals(vin.getEngine(), SOULEV_ENGINE);
        Assert.assertEquals(vin.getYear(), "2017");
        Assert.assertEquals(vin.getSequentialNumber(), "123456");
        Assert.assertEquals(vin.getProductionPlant(), "Unknown (0)");
    }

    /**
     * Valid VIN from a 2020 model
     */
    public void test2020() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_ESOUL);
        Assert.assertTrue(vin.isValid());
        Assert.assertEquals(vin.getVIN(), VIN_ESOUL);
        Assert.assertEquals(vin.getBrand(), "Kia");
        Assert.assertEquals(vin.getModel(), "Soul EV");
        Assert.assertEquals(vin.getTrim(), SOULEV2020_EXCLUSIVE);
        Assert.assertEquals(vin.getEngine(), SOULEV2020_ENGINE);
        Assert.assertEquals(vin.getYear(), "2020");
        Assert.assertEquals(vin.getSequentialNumber(), "000543");
        Assert.assertEquals(vin.getProductionPlant(), SOULEV_PROD_PLANT);
    }

    /**
     * Model year letter doesn't use all letters of the alfabet
     */
    public void testModelYear() {
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2014);
            Assert.assertEquals(vin.getYear(), "2014");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2015);
            Assert.assertEquals(vin.getYear(), "2015");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2016);
            Assert.assertEquals(vin.getYear(), "2016");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2017);
            Assert.assertEquals(vin.getYear(), "2017");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2018);
            Assert.assertEquals(vin.getYear(), "2018");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2019);
            Assert.assertEquals(vin.getYear(), "2019");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2020);
            Assert.assertEquals(vin.getYear(), "2020");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2021);
            Assert.assertEquals(vin.getYear(), "2021");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2022);
            Assert.assertEquals(vin.getYear(), "2022");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2023);
            Assert.assertEquals(vin.getYear(), "2023");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2024);
            Assert.assertEquals(vin.getYear(), "2024");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2025);
            Assert.assertEquals(vin.getYear(), "2025");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2026);
            Assert.assertEquals(vin.getYear(), "2026");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2027);
            Assert.assertEquals(vin.getYear(), "2027");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2028);
            Assert.assertEquals(vin.getYear(), "2028");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2029);
            Assert.assertEquals(vin.getYear(), "2029");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2030);
            Assert.assertEquals(vin.getYear(), "2030");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2031);
            Assert.assertEquals(vin.getYear(), "2031");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2032);
            Assert.assertEquals(vin.getYear(), "2032");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2033);
            Assert.assertEquals(vin.getYear(), "2033");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2034);
            Assert.assertEquals(vin.getYear(), "2034");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2035);
            Assert.assertEquals(vin.getYear(), "2035");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2036);
            Assert.assertEquals(vin.getYear(), "2036");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2037);
            Assert.assertEquals(vin.getYear(), "2037");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2038);
            Assert.assertEquals(vin.getYear(), "2038");
        }
    }


    /**
     * Valid VIN from a 2016 Luxury canadian model
     */
    public void testMattsVIN() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_MATT);
        Assert.assertTrue(vin.isValid());
        Assert.assertEquals(vin.getVIN(), VIN_MATT);
        Assert.assertEquals(vin.getBrand(), "Kia");
        Assert.assertEquals(vin.getModel(), "Soul EV");
        Assert.assertEquals(vin.getTrim(), SOULEV_PLUS);
        Assert.assertEquals(vin.getEngine(), SOULEV_ENGINE);
        Assert.assertEquals(vin.getYear(), "2015");
        Assert.assertEquals(vin.getSequentialNumber(), "001565");
        Assert.assertEquals(vin.getProductionPlant(), SOULEV_PROD_PLANT);
    }


    // TODO PEM : Add a parser for the 0902 message!
    /*
                // KNDJX3AE1G7123456
                "0902\n" +
                "7EA 10 14 49 02 01 4B 4E 44 \n" +
                "7EA 21 4A 58 33 41 45 31 47 \n" +
                "7EA 22 37 31 32 33 34 35 36 \n" +
     */
}