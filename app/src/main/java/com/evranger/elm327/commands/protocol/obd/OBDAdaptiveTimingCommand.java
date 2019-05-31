package com.evranger.elm327.commands.protocol.obd;

import com.evranger.elm327.commands.AbstractCommand;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-26.
 */
public class OBDAdaptiveTimingCommand extends AbstractCommand {

    public OBDAdaptiveTimingCommand(final OBDAdaptiveTimingModes mode) {
        super("AT AT" + mode.getValue());
    }
    public void doProcessResponse() {}
}
