package com.evranger.elm327.commands.protocol;

import com.evranger.elm327.commands.AbstractCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Henrik R. Scheel <henrik.scheel@spjeldager.dk> on 2019-05-22.
 */
public class StopCommand extends AbstractCommand {
    public StopCommand() {
        super(" ");
    }

    @Override
    public void execute(InputStream in, OutputStream out) throws IOException, InterruptedException {
        // Skip output from previous commands
        flushInput(in);

        // Send the command
        send(out);

        // Wait before trying to receive the command response
        Thread.sleep(mResponseTimeDelay);

        // Skip output
        flushInput(in);
    }
}