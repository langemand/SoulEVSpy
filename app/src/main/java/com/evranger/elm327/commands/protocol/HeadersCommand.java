package com.evranger.elm327.commands.protocol;

import com.evranger.elm327.commands.AbstractCommand;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-25.
 */
public class HeadersCommand extends AbstractCommand {
    public HeadersCommand(boolean isOn) {
        super("AT H" + (isOn ? "1" : "0"));
    }
}
