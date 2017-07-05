package org.hexpresso.soulevspy.obd.commands;

import org.hexpresso.elm327.commands.AbstractCommand;
import org.hexpresso.elm327.commands.AbstractMultiCommand;
import org.hexpresso.elm327.commands.Command;
import org.hexpresso.elm327.commands.TimeCommand;
import org.hexpresso.elm327.commands.StopAfterDataCommand;
import org.hexpresso.elm327.commands.filters.RegularExpressionResponseFilter;
import org.hexpresso.elm327.commands.filters.RemoveSpacesResponseFilter;
import org.hexpresso.elm327.commands.protocol.RawCommand;
import org.hexpresso.elm327.exceptions.StoppedException;
import org.hexpresso.obd.ObdMessageFilter;
import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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

//    private FilteredMonitorCommand() {}

//    @Override
//    public void execute(InputStream in, OutputStream out) throws IOException, InterruptedException {
//        String rawResponse = "";
//        for(Command command : mCommands) {
//            try {
//                command.execute(in, out);
//                rawResponse += command.getResponse().rawResponse() + "\\r";
//            } catch (StoppedException e) {
//                // To be expected when breaking the MA monitoring, just ignore
//            }
//        }
//
//        mResponse.setRawResponse(rawResponse);
//    }

    @Override
    public void doProcessResponse() {
        mObdMessageFilter.receive(mStopAfterDataCommand.getResponse().rawResponse());
    }
}
