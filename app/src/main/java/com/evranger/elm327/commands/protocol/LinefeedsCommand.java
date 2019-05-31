package com.evranger.elm327.commands.protocol;

import com.evranger.elm327.commands.AbstractCommand;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-25.
 */
public class LinefeedsCommand extends AbstractCommand {
    public LinefeedsCommand(boolean isOn) {
        super("AT L" + (isOn ? "1" : "0"));
    }
}
