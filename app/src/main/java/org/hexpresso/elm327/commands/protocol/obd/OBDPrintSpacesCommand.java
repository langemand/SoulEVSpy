package org.hexpresso.elm327.commands.protocol.obd;

import org.hexpresso.elm327.commands.AbstractCommand;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-25.
 */
public class OBDPrintSpacesCommand extends AbstractCommand {
    public OBDPrintSpacesCommand(boolean isOn) {
        super("AT S" + (isOn ? "1" : "0"));
    }
    public void doProcessResponse() {}
}
