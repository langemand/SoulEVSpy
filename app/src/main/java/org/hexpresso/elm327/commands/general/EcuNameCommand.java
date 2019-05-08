package org.hexpresso.elm327.commands.general;

import org.hexpresso.elm327.commands.AbstractCommand;
import org.hexpresso.elm327.commands.Response;
import org.hexpresso.elm327.commands.filters.RegularExpressionResponseFilter;
import org.hexpresso.elm327.commands.filters.RemoveSpacesResponseFilter;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-29.
 */
public class EcuNameCommand extends AbstractCommand {

    private Map<Integer, String> mEcu = null;

    public EcuNameCommand() {
        super("09 04");

        withAutoProcessResponse(true);
        // This command assumes headers are turned on!
        addResponseFilter(new RegularExpressionResponseFilter("^[0-9A-F]{3}(.*)$"));
        addResponseFilter(new RemoveSpacesResponseFilter());
    }

    public void doProcessResponse() {
        CurrentValuesSingleton.getInstance().set("ECU", getValue());
    }

    public Map<Integer, String> getValue() {
        try {
            mEcu = new HashMap<>();
            Response r = getResponse();
            StringBuilder str = new StringBuilder();
            int minLineLen = 17;
            for (String line : r.getLines()) {
                int senderAddress = Integer.parseInt(line.substring(0, 3), 16);
                if (!mEcu.containsKey(senderAddress)) {
                    mEcu.put(senderAddress, new String());
                }
                String hex = line.substring(minLineLen+1).replaceAll("[\\s\\t\\n\\x0B\\f\\r]", "");
                while (hex.length() > 1) {
                    int ascii = Integer.parseInt(hex.substring(0, 1), 16);
                    str.append((char)ascii);
                }
                mEcu.put(senderAddress, mEcu.get(senderAddress) + str.toString());
            }
            skip(true);
        } catch (Exception e) {
//            mECU = "error: " + str.toString();
        }
        return mEcu;
    }
}