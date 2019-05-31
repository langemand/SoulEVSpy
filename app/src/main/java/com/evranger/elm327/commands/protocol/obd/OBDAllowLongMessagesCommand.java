package com.evranger.elm327.commands.protocol.obd;

import com.evranger.elm327.commands.AbstractCommand;

/**
 * Allow  Long (>7 byte) messages
 *
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-25.
 */
public class OBDAllowLongMessagesCommand extends AbstractCommand {
    public OBDAllowLongMessagesCommand() {
        super("AT AL");
    }
    public void doProcessResponse() {}
}
