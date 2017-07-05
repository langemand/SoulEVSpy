package org.hexpresso.elm327.commands.protocol.obd;

import org.hexpresso.elm327.commands.AbstractCommand;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-25.
 */
public class OBDAutomaticallyReceiveCommand extends AbstractCommand {

    public OBDAutomaticallyReceiveCommand() {
        super("AT AR");
    }
    public void doProcessResponse() {}
}
