package org.hexpresso.elm327.commands.protocol.can;

import org.hexpresso.elm327.commands.AbstractCommand;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-25.
 */
public class CANSetProtocolCommand extends AbstractCommand {
    public CANSetProtocolCommand(int protocolNo) {
        super("AT SP A" + protocolNo);
    }
}
