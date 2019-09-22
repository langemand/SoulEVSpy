package com.evranger.soulevspy;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.evranger.soulevspy.util.KiaVinParser;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-07.
 */
@Ignore("TODO: Fix the language setting to en for unittest run")
@RunWith(AndroidJUnit4.class)
public class KiaVinParserTest {

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

    // e-Soul 2020
    private final static String VIN_ESOUL_ADVANCE_PLUS  = "KNAJ3811FL7000543"; // Henriks eSoul VIN

    // Geoffs eSoul 2020
    private final static String VIN_ESOUL_GEOFF         = "KNDJ33A16L7002223";

    // Soul EV 2016
    private final static String VIN_2016_6682           = "KNDJX3AE7G7006682"; // Soul EV 2016

    // Andy's Soul EV 2019
    private final static String VIN_SOUL_EV_2019       = "KNDJX3AE2K7040070";

    // Lottes Ioniq
    private final static String VIN_IONIQEV_2017_TREND  = "KMHC751HFHU017366";

    // Ioniq PHEV 2018 SE
    private final static String VIN_IONIQ_PHEV_2018_SE  = "KMHC65LD9JU104158";

    // Ionic HEV from nrpla.de
    private final static String VIN_IONIQ_HEV_ADVANCED  = "KMHC851CGHU013795";

    // 40% SOH Soul EV
    private final static String VIN_40_SOH              = "KNDJX3AE2F7002960";

    // Tobias' Kona Trend
    private final static String VIN_KONA_2018_TREND     = "KMHK381GFKU017151";

    // Peters Kona
    private final static String VIN_KONA_64_KWH         = "KMHK381GFKU011487";

    // Fra nrpla.de, elsæder, skiltegenkendelse
    private final static String VIN_KONA_ADVANCE_64_KWH = "KMHK581GFKU037419";

    // Fra nrpla.de, mindre udstyr?
    private final static String VIN_KONA_64_KWH_2       = "KMHK581GFKU007456";

    // Fra nrpla.de, Kona 39 kWH
    private final static String VIN_KONA_39_KWH         = "KMHK381HFKU014102";

    // Peters eNiro, 39 kWh batt
    private final static String VIN_ENIRO_39_KWH        = "KNACB81HFK5006641";

    // eNiro 64 kWh fra nrpla.de
    private final static String VIN_ENIRO_64_KWH        = "KNACB81GFK5006898";

    // NIRO PHEV fra nrpla.de
    private final static String VIN_NIRO_PHEV           = "KNACD81DGJ5118039";

    private final static String VIN_NIRO_NEWER_PHEV     = "KNACM81DGK5225933";

    // Oinqs Optima PHEV
    private final static String VIN_OPTIMA_PHEV         = "KNAGV41DBH5004230";

    // Kia Optima 4 door PHEV fra nrpla.de
    private final static String VIN_OPTIMA_PHEV_4DOOR   = "KNAGV41DBH5004005";

    // Kia Optima PHEV st car fra nrpla.de
    private final static String VIN_OPTIMA_STCAR_PHEV   = "KNAGV81FBJ5027605";

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
    private final static String SOULEV_ENGINE     = "Battery [LiPB 350 V, 75 Ah] + Motor [3-phase AC 80 kW]";
    private final static String SOULEV_PROD_PLANT = "Gwangju (South Korea)";
    private final static String SOULEV2020_EXCLUSIVE  = "Exclusive";
    private final static String SOULEV2020_ENGINE     = "Battery [LiPo 356 V, 180 Ah] + Motor [3-phase AC 150 kW]";
    private final static String ENIRO_39KWH_ENGINE    = "Battery [LiPo 327 V, 120 Ah] + Motor [3-phase AC 100 kW]";

    private final static String IONIQ_TREND       = "Trend";
    private final static String IONIQ_SE          = "SE";
    private final static String IONIQ_EV_ENGINE   = "Battery [LiPB 360 V, 78 Ah] + Motor [3-phase AC 88 kW]";
    private final static String IONIQ_PHEV_ENGINE = "Battery [LiPo 360 V, 24.7 Ah] + Motor [3-phase AC 45 kW]";
    private final static String IONIQ_HEV_ENGINE  = "Battery [LiPo 240 V, 6.5 Ah] + Motor [3-phase AC 32 kW]";
    private final static String IONIQ_PROD_PLANT  = "Ulsan (South Korea)";

    private final static String KONA_TREND        = "Trend";

    private final static String ENIRO_PROD_PLANT  = "Hwaseong (South Korea)";

    Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }

    /**
     * Illegal Strings
     */
    @Test
    public void testLegalStrings() {
        assertTrue(new KiaVinParser(getContext(), VIN_2015_LUXURY_WHITE).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_2016_LUXURY_TITANIUM).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_2016_LUXURY_WHITE).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_2017_FAKE).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_2015_AVT_1908).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_2015_AVT_1918).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_2015_AVT_1919).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_2015_AVT_1920).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_2016_5477).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_2015_3847).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_2015_3819).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_2015_3798).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_2015_3644).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_2016_TYREL).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_2015_HENRIK).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_2016_IWER).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_ESOUL_ADVANCE_PLUS).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_40_SOH).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_KONA_2018_TREND).isValid());
        assertTrue(new KiaVinParser(getContext(), VIN_KONA_64_KWH).isValid());
    }

    /**
     * LEgal Strings
     */
    @Test
    public void testIllegalStrings() {
        assertFalse(new KiaVinParser(getContext(), VIN_EMPTY).isValid());
        assertFalse(new KiaVinParser(getContext(), VIN_NOT_KIA).isValid());
        assertFalse(new KiaVinParser(getContext(), VIN_NOT_SOUL).isValid());
        assertFalse(new KiaVinParser(getContext(), VIN_NOT_SOULEV).isValid());
    }

    /**
     * Lower case input
     */
    @Test
    public void testLowerCase() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_2016_LUXURY_TITANIUM.toLowerCase());
        assertTrue(vin.isValid());
        assertEquals(vin.getVIN(), VIN_2016_LUXURY_TITANIUM);
        assertEquals(vin.getBrand(), "Kia");
        assertEquals(vin.getModel(), "Soul EV");
        assertEquals(vin.getTrim(), SOULEV_PLUS);
        assertEquals(vin.getEngine(), SOULEV_ENGINE);
        assertEquals(vin.getYear(), "2016");
        assertEquals(vin.getSequentialNumber(), "123456");
        assertEquals(vin.getProductionPlant(), SOULEV_PROD_PLANT);
    }

    /**
     * Valid VIN from a 2016 Luxury canadian model
     */
    @Test
    public void test2016LuxuryTitanium() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_2016_LUXURY_TITANIUM);
        assertTrue(vin.isValid());
        assertEquals(vin.getVIN(), VIN_2016_LUXURY_TITANIUM);
        assertEquals(vin.getBrand(), "Kia");
        assertEquals(vin.getModel(), "Soul EV");
        assertEquals(vin.getTrim(), SOULEV_PLUS);
        assertEquals(vin.getEngine(), SOULEV_ENGINE);
        assertEquals(vin.getYear(), "2016");
        assertEquals(vin.getSequentialNumber(), "123456");
        assertEquals(vin.getProductionPlant(), SOULEV_PROD_PLANT);
    }

    /**
     * Valid VIN from a 2016 Luxury canadian model
     */
    @Test
    public void test2016LuxuryWhite() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_2016_LUXURY_WHITE);
        assertTrue(vin.isValid());
        assertEquals(vin.getVIN(), VIN_2016_LUXURY_WHITE);
        assertEquals(vin.getBrand(), "Kia");
        assertEquals(vin.getModel(), "Soul EV");
        assertEquals(vin.getTrim(), SOULEV_PLUS);
        assertEquals(vin.getEngine(), SOULEV_ENGINE);
        assertEquals(vin.getYear(), "2016");
        assertEquals(vin.getSequentialNumber(), "123456");
        assertEquals(vin.getProductionPlant(), SOULEV_PROD_PLANT);
    }

    /**
     * Valid VIN from a 2016 Base US model
     */
    @Test
    public void test2015BaseWhite() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_2015_3847);
        assertTrue(vin.isValid());
        assertEquals(vin.getVIN(), VIN_2015_3847);
        assertEquals(vin.getBrand(), "Kia");
        assertEquals(vin.getModel(), "Soul EV");
        assertEquals(vin.getTrim(), SOULEV_BASE);
        assertEquals(vin.getEngine(), SOULEV_ENGINE);
        assertEquals(vin.getYear(), "2015");
        assertEquals(vin.getSequentialNumber(), "003847");
        assertEquals(vin.getProductionPlant(), SOULEV_PROD_PLANT);
    }

    /**
     * Valid VIN from a 2017 fake model (shows unknown plant)
     */
    @Test
    public void test2017Fake() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_2017_FAKE);
        assertTrue(vin.isValid());
        assertEquals(vin.getVIN(), VIN_2017_FAKE);
        assertEquals(vin.getBrand(), "Kia");
        assertEquals(vin.getModel(), "Soul EV");
        assertEquals(vin.getTrim(), SOULEV_PLUS);
        assertEquals(vin.getEngine(), SOULEV_ENGINE);
        assertEquals(vin.getYear(), "2017");
        assertEquals(vin.getSequentialNumber(), "123456");
        assertEquals(vin.getProductionPlant(), "Unknown (0)");
    }

    /**
     * Valid VIN from a Kia e-Soul 2020
     */
    @Test
    public void test2020() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_ESOUL_ADVANCE_PLUS);
        assertTrue(vin.isValid());
        assertEquals(vin.getVIN(), VIN_ESOUL_ADVANCE_PLUS);
        assertEquals(vin.getBrand(), "Kia");
        assertEquals(vin.getModel(), "Soul EV");
        assertEquals(vin.getTrim(), SOULEV2020_EXCLUSIVE);
        assertEquals(vin.getEngine(), SOULEV2020_ENGINE);
        assertEquals(vin.getYear(), "2020");
        assertEquals(vin.getSequentialNumber(), "000543");
        assertEquals(vin.getProductionPlant(), SOULEV_PROD_PLANT);
    }

    /**
     * Model year letter doesn't use all letters of the alfabet
     */
    @Test
    public void testModelYear() {
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2014);
            assertEquals(vin.getYear(), "2014");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2015);
            assertEquals(vin.getYear(), "2015");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2016);
            assertEquals(vin.getYear(), "2016");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2017);
            assertEquals(vin.getYear(), "2017");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2018);
            assertEquals(vin.getYear(), "2018");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2019);
            assertEquals(vin.getYear(), "2019");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2020);
            assertEquals(vin.getYear(), "2020");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2021);
            assertEquals(vin.getYear(), "2021");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2022);
            assertEquals(vin.getYear(), "2022");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2023);
            assertEquals(vin.getYear(), "2023");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2024);
            assertEquals(vin.getYear(), "2024");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2025);
            assertEquals(vin.getYear(), "2025");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2026);
            assertEquals(vin.getYear(), "2026");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2027);
            assertEquals(vin.getYear(), "2027");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2028);
            assertEquals(vin.getYear(), "2028");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2029);
            assertEquals(vin.getYear(), "2029");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2030);
            assertEquals(vin.getYear(), "2030");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2031);
            assertEquals(vin.getYear(), "2031");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2032);
            assertEquals(vin.getYear(), "2032");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2033);
            assertEquals(vin.getYear(), "2033");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2034);
            assertEquals(vin.getYear(), "2034");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2035);
            assertEquals(vin.getYear(), "2035");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2036);
            assertEquals(vin.getYear(), "2036");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2037);
            assertEquals(vin.getYear(), "2037");
        }
        {
            KiaVinParser vin = new KiaVinParser(getContext(), VIN_2038);
            assertEquals(vin.getYear(), "2038");
        }
    }


    /**
     * Valid VIN from a 2016 Luxury canadian model
     */
    @Test
    public void testMattsVIN() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_MATT);
        assertTrue(vin.isValid());
        assertEquals(vin.getVIN(), VIN_MATT);
        assertEquals(vin.getBrand(), "Kia");
        assertEquals(vin.getModel(), "Soul EV");
        assertEquals(vin.getTrim(), SOULEV_PLUS);
        assertEquals(vin.getEngine(), SOULEV_ENGINE);
        assertEquals(vin.getYear(), "2015");
        assertEquals(vin.getSequentialNumber(), "001565");
        assertEquals(vin.getProductionPlant(), SOULEV_PROD_PLANT);
    }


    /**
     * Valid VIN from a 2016 model
     */
    @Test
    public void testDrmarshallsVIN() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_2016_6682);
        assertTrue(vin.isValid());
        assertEquals(vin.getVIN(), VIN_2016_6682);
        assertEquals(vin.getBrand(), "Kia");
        assertEquals(vin.getModel(), "Soul EV");
        assertEquals(vin.getTrim(), SOULEV_PLUS);
        assertEquals(vin.getEngine(), SOULEV_ENGINE);
        assertEquals(vin.getYear(), "2016");
        assertEquals(vin.getSequentialNumber(), "006682");
        assertEquals(vin.getProductionPlant(), SOULEV_PROD_PLANT);
    }

    /**
     * VIN from a 2017 Ioniq EV
     */
    @Test
    public void testIoniqVIN() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_IONIQEV_2017_TREND);
        assertTrue(vin.isValid());
        assertEquals(vin.getVIN(), VIN_IONIQEV_2017_TREND);
        assertEquals(vin.getBrand(), "Hyundai");
        assertEquals(vin.getModel(), "Ioniq EV");
        assertEquals(vin.getTrim(), IONIQ_TREND);
        assertEquals(vin.getEngine(), IONIQ_EV_ENGINE);
        assertEquals(vin.getYear(), "2017");
        assertEquals(vin.getSequentialNumber(), "017366");
        assertEquals(vin.getProductionPlant(), IONIQ_PROD_PLANT);
    }

    /**
     * Valid VIN from Tobias' Hyundai Kona 2018
     */
    @Test
    public void testKona2018Trend() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_KONA_2018_TREND);
        assertTrue(vin.isValid());
        assertEquals(vin.getVIN(), VIN_KONA_2018_TREND);
        assertEquals(vin.getBrand(), "Hyundai");
        assertEquals(vin.getModel(), "Kona EV");
        assertEquals(vin.getTrim(), KONA_TREND);
        assertEquals(vin.getEngine(), SOULEV2020_ENGINE);
        assertEquals(vin.getYear(), "2019");
        assertEquals(vin.getSequentialNumber(), "017151");
        assertEquals(vin.getProductionPlant(), IONIQ_PROD_PLANT);
    }

    /**
     * Valid VIN from Peters' Hyundai Kona 2018
     */
    @Test
    public void testKona2018Exclusive() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_KONA_64_KWH);
        assertTrue(vin.isValid());
        assertEquals(vin.getVIN(), VIN_KONA_64_KWH);
        assertEquals(vin.getBrand(), "Hyundai");
        assertEquals(vin.getModel(), "Kona EV");
        assertEquals(vin.getTrim(), KONA_TREND);
        assertEquals(vin.getEngine(), SOULEV2020_ENGINE);
        assertEquals(vin.getYear(), "2019");
        assertEquals(vin.getSequentialNumber(), "011487");
        assertEquals(vin.getProductionPlant(), IONIQ_PROD_PLANT);
    }

    /**
     * Valid VIN from Peters' Kia eNiro 39 kWH 2018
     */
    @Test
    public void testeNiro39kwh() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_ENIRO_39_KWH);
        assertTrue(vin.isValid());
        assertEquals(vin.getVIN(), VIN_ENIRO_39_KWH);
        assertEquals(vin.getBrand(), "Kia");
        assertEquals(vin.getModel(), "e-Niro");
        assertEquals(vin.getTrim(), "First Edition Base");
        assertEquals(vin.getEngine(), ENIRO_39KWH_ENGINE);
        assertEquals(vin.getYear(), "2019");
        assertEquals(vin.getSequentialNumber(), "006641");
        assertEquals(vin.getProductionPlant(), ENIRO_PROD_PLANT);
    }


    /**
     * VIN from a 2018 Ioniq PHEV
     */
    @Test
    public void testIoniqPHEV_VIN() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_IONIQ_PHEV_2018_SE);
        assertTrue(vin.isValid());
        assertEquals(vin.getVIN(), VIN_IONIQ_PHEV_2018_SE);
        assertEquals(vin.getBrand(), "Hyundai");
        assertEquals(vin.getModel(), "Ioniq PHEV");
        assertEquals(vin.getTrim(), IONIQ_SE);
        assertEquals(vin.getEngine(), IONIQ_PHEV_ENGINE);
        assertEquals(vin.getYear(), "2018");
        assertEquals(vin.getSequentialNumber(), "104158");
        assertEquals(vin.getProductionPlant(), IONIQ_PROD_PLANT);
    }

    /**
     * VIN from a 2018 Ioniq PHEV
     */
    @Test
    public void testIoniqHEV_VIN() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_IONIQ_HEV_ADVANCED);
        assertTrue(vin.isValid());
        assertEquals(vin.getVIN(), VIN_IONIQ_HEV_ADVANCED);
        assertEquals(vin.getBrand(), "Hyundai");
        assertEquals(vin.getModel(), "Ioniq HEV");
        assertEquals(vin.getTrim(), IONIQ_SE);
        assertEquals(vin.getEngine(), IONIQ_HEV_ENGINE);
        assertEquals(vin.getYear(), "2017");
        assertEquals(vin.getSequentialNumber(), "013795");
        assertEquals(vin.getProductionPlant(), IONIQ_PROD_PLANT);
    }

    /**
     * Valid VIN from Geoffs Kia e-Soul 2020
     */
    @Test
    public void testGeoffs2020() {
        KiaVinParser vin = new KiaVinParser(getContext(), VIN_ESOUL_GEOFF);
        assertTrue(vin.isValid());
        assertEquals(vin.getVIN(), VIN_ESOUL_GEOFF);
        assertEquals(vin.getBrand(), "Kia");
        assertEquals(vin.getModel(), "Soul EV");
        assertEquals(vin.getTrim(), SOULEV2020_EXCLUSIVE);
        assertEquals(vin.getEngine(), SOULEV2020_ENGINE);
        assertEquals(vin.getYear(), "2020");
        assertEquals(vin.getSequentialNumber(), "002223");
        assertEquals(vin.getProductionPlant(), SOULEV_PROD_PLANT);
    }

}