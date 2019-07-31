package com.evranger.elm327.commands.protocol;

import android.os.SystemClock;
import android.util.Log;

import com.evranger.elm327.commands.AbstractCommand;
import com.evranger.elm327.log.CommLog;

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
//        mStopReadingAtQuestionMark=false;
    }

    @Override
    public void execute(InputStream in, OutputStream out) throws IOException, InterruptedException {
        mRunStartTimestamp = System.currentTimeMillis();
        setTimeoutMs(100); // Allow ELM327 time to stop monitoring overflow etc
        // Skip possible output from previous commands
        try {
            flushInput(in);
        } catch (TimeoutException ex) {
            // When could this happen?
        }

        c = '\0';
        // Send a char to stop monitoring
        final String command = " ";
        Log.d(AbstractCommand.class.getSimpleName(), "send command: " + command);
        byte[] commandBytes = command.getBytes();
        out.write(commandBytes);
        out.flush();
        CommLog.getInstance().log("o:".getBytes());
        CommLog.getInstance().log(commandBytes);

        setTimeoutMs(2000); // Allow ELM327 time to stop monitoring overflow etc
        try {
            flushInput(in, true); // Read until '>', indicating ELM327 ready for next command
        } catch (TimeoutException ex) {
            // When could this happen?
        }
    }
}