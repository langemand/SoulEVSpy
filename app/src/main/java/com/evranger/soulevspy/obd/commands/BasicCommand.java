package com.evranger.soulevspy.obd.commands;

import com.evranger.elm327.commands.AbstractCommand;
import com.evranger.elm327.commands.ResponseFilter;

/**
 * Created by henrik on 16/12/2017.
 */

public class BasicCommand extends AbstractCommand {
    public BasicCommand(String command) {
        super(command);
    }
    @Override
    public AbstractCommand addResponseFilter(ResponseFilter filter) {
        return super.addResponseFilter(filter);
    }
}
