package com.evranger.soulevspy.obd.commands;

import com.evranger.elm327.commands.AbstractMultiCommand;
import com.evranger.elm327.commands.StopAfterDataCommand;
import com.evranger.elm327.commands.filters.RegularExpressionResponseFilter;
import com.evranger.elm327.commands.filters.RemoveSpacesResponseFilter;
import com.evranger.obd.ObdMessageFilter;

/**
 * Created by henrik on 09/06/2017.
 */

public class FilteredMonitorCommand extends AbstractMultiCommand {
    ObdMessageFilter mObdMessageFilter = null;
    StopAfterDataCommand mStopAfterDataCommand = null;

    /**
     * Constructor
     *
     * @param filter message identifier (ELM327 command to send)
     */
    public FilteredMonitorCommand(ObdMessageFilter filter) {
        mObdMessageFilter = filter;
        mStopAfterDataCommand = new StopAfterDataCommand(1000L, filter.messageIdentifier());
        addCommand(mStopAfterDataCommand);

        // TODO: Remove these to test if they are needed
        addResponseFilter(new RegularExpressionResponseFilter("^" + filter.messageIdentifier() + "(.*)$"));
        addResponseFilter(new RemoveSpacesResponseFilter());
    }

    @Override
    public void doProcessResponse() {
        mObdMessageFilter.receive(mStopAfterDataCommand.getResponse().rawResponse());
    }
}
