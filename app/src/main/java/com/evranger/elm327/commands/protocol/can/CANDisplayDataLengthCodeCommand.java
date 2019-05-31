package com.evranger.elm327.commands.protocol.can;

import com.evranger.elm327.commands.AbstractCommand;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-26.
 */
public class CANDisplayDataLengthCodeCommand extends AbstractCommand {
    public CANDisplayDataLengthCodeCommand(boolean isOn) {
        super("AT D" + (isOn ? "1" : "0"));
    }
}
