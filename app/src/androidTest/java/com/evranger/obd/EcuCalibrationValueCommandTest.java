package com.evranger.obd;

import android.util.Pair;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.evranger.elm327.commands.general.EcuCalibrationValueCommand;
import com.evranger.soulevspy.Responder;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class EcuCalibrationValueCommandTest {
    final String soulEv2015EcuCalibrationValue = "7EA 10 13 49 04 01 00 00 00 \r" +
            "7EC 10 13 49 04 01 50 53 45 \r" +
            "7EA 21 00 00 00 00 00 00 00 \r" +
            "7EC 21 56 42 35 31 30 30 52 \r" +
            "7EA 22 00 00 00 00 00 00 00 \r" +
            "7EC 22 00 00 00 00 00 00 00 \r" +
            ">";

    private CurrentValuesSingleton vals;

    @Before
    public void init() {
        vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(InstrumentationRegistry.getTargetContext());
        vals.setPreferences(prefs);
    }

    @Test
    public void testSoulEcuCalibrationValue() {
        List<Pair<String, String>> reqres = Arrays.asList(
                new Pair<String, String>("09 04", soulEv2015EcuCalibrationValue)
        );
        Responder responder = new Responder(reqres);

        EcuCalibrationValueCommand cmd = new EcuCalibrationValueCommand();
        try {
            cmd.execute(responder.getInput(), responder.getOutput());
            cmd.doProcessResponse();
        } catch (Exception e) {
            assertEquals("", e.getMessage());
        }

        assertEquals("", ((String)vals.get("ECU.calibration.7EA")));
        assertEquals("PSEVB5100R", ((String)vals.get("ECU.calibration.7EC")));
    }
}
