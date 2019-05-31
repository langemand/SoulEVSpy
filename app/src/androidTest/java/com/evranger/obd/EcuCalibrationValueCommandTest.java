package com.evranger.obd;

import android.test.AndroidTestCase;
import android.util.Pair;

import com.evranger.elm327.commands.general.EcuCalibrationValueCommand;
import com.evranger.soulevspy.Responder;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.ClientSharedPreferences;

import java.util.Arrays;
import java.util.List;

public class EcuCalibrationValueCommandTest extends AndroidTestCase {
    final String soulEv2015EcuCalibrationValue = "7EA 10 13 49 04 01 00 00 00 \r" +
            "7EC 10 13 49 04 01 50 53 45 \r" +
            "7EA 21 00 00 00 00 00 00 00 \r" +
            "7EC 21 56 42 35 31 30 30 52 \r" +
            "7EA 22 00 00 00 00 00 00 00 \r" +
            "7EC 22 00 00 00 00 00 00 00 \r" +
            ">";

    public void testSoulEcuCalibrationValue() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.reset();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

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
