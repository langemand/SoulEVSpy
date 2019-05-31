package com.evranger.elm327.commands.general;

import com.evranger.elm327.commands.AbstractCommand;
import com.evranger.elm327.commands.Response;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.elm327.commands.filters.RegularExpressionResponseFilter;
import com.evranger.elm327.commands.filters.RemoveSpacesResponseFilter;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-29.
 */
public class VehicleIdentifierNumberCommand extends AbstractCommand {

    private String mVIN = null;

    public VehicleIdentifierNumberCommand() {
        super("09 02");

        withAutoProcessResponse(true);
        // This command assumes headers are turned on!
        addResponseFilter(new RegularExpressionResponseFilter("^[0-9A-F]{3}(.*)$"));
        addResponseFilter(new RemoveSpacesResponseFilter());
    }

    public void doProcessResponse() {
        CurrentValuesSingleton.getInstance().set("VIN", getValue());
    }

    public String getValue() {
        final Response r = getResponse();
        StringBuilder str = new StringBuilder();
        try {
            str.append((char) r.get(0, 5));
            str.append((char) r.get(0, 6));
            str.append((char) r.get(0, 7));
            for (int line = 1; line <= 2; line++) {
                for (int index = 1; index <= 7; index++) {
                    str.append((char) r.get(line, index));
                }
            }
            mVIN = str.toString();
            skip(true);
        } catch (Exception e) {
            mVIN = "error: " + str.toString();
            // Check if 09 02 is supported by car
            Object supported09Pids = CurrentValuesSingleton.getInstance().get("OBD.SupportedPids.09.7EA");
            if (supported09Pids != null && supported09Pids instanceof String && !((String)supported09Pids).contains("02")) {
                // 09 02 is not supported, no use to keep trying
                skip(true);
            }
        }
        return mVIN;
    }
}