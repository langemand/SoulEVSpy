package org.hexpresso.elm327.commands.general;

import org.hexpresso.elm327.commands.AbstractCommand;
import org.hexpresso.elm327.commands.Response;
import org.hexpresso.elm327.commands.filters.RegularExpressionResponseFilter;
import org.hexpresso.elm327.commands.filters.RemoveSpacesResponseFilter;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

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
        if (mVIN == null) {
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
            } catch (Exception e) {
                mVIN = "error";
            }
        }
        return mVIN;
    }
}