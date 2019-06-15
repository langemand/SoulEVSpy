package com.evranger.elm327.commands.protocol;

import com.evranger.elm327.commands.AbstractCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

/**
 * Created by Henrik R. Scheel <henrik.scheel@spjeldager.dk> on 2019-05-22.
 */
public class StopCommand extends AbstractCommand {
    public StopCommand() {
        super(" ");
        mStopReadingAtQuestionMark=true;
        setTimeoutMs(5000); // Allow ELM327 time to stop monitoring overflow etc
    }

    @Override
    public void execute(InputStream in, OutputStream out) throws IOException, InterruptedException {
        // Skip possible output from previous commands
        flushInput(in);

        // Send the command to stop monitoring
        send(out);

        try {
            readRawData(in); // Read until '>' or '?', indicating ELM327 ready for next command
        } catch (TimeoutException ex) {
            // When could this happen?
        }
    }
}