package com.evranger.elm327.commands.general;

import com.evranger.elm327.commands.AbstractCommand;
import com.evranger.elm327.commands.Response;
import com.evranger.elm327.commands.filters.RegularExpressionResponseFilter;
import com.evranger.elm327.commands.filters.RemoveSpacesResponseFilter;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-29.
 */
public class EcuCalibrationValueCommand extends AbstractCommand {

    private Map<Integer, String> mEcu = null;

    public EcuCalibrationValueCommand() {
        super("09 04");

        withAutoProcessResponse(true);
        // This command assumes headers are turned on!
        addResponseFilter(new RegularExpressionResponseFilter("^([0-9A-F]{3} .*)$"));
        addResponseFilter(new RemoveSpacesResponseFilter());
    }

    public void doProcessResponse() {
        getValue();
        for (Integer senderAddress : mEcu.keySet()) {
            String key = "ECU.calibration." + String.format("%03X", senderAddress);
            CurrentValuesSingleton.getInstance().set(key, mEcu.get(senderAddress));
        }
    }

    public Map<Integer, String> getValue() {
        try {
            mEcu = new HashMap<>();
            Response r = getResponse();
            List<String> lines = r.getLines();
            for (String line : lines) {
                int senderAddress = Integer.parseInt(line.substring(0, 3), 16);
                int minLineLen = 12;
                if (!mEcu.containsKey(senderAddress)) {
                    mEcu.put(senderAddress, new String());
                } else {
                    minLineLen = 4;
                }
                String hex = line.substring(minLineLen+1); //.replaceAll("[\\s\\t\\n\\x0B\\f\\r]", "");
                StringBuilder str = new StringBuilder();
                while (hex.length() > 1) {
                    int ascii = Integer.parseInt(hex.substring(0, 2), 16);
                    if (ascii != 0) {
                        str.append((char) ascii);
                    }
                    hex = hex.substring(2);
                }
                mEcu.put(senderAddress, mEcu.get(senderAddress) + str.toString());
                skip(true);
            }
        } catch (Exception e) {
//            mECU = "error: " + str.toString();
        }
        return mEcu;
    }
}