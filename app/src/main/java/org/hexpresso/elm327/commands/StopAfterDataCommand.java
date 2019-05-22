package org.hexpresso.elm327.commands;

import org.hexpresso.elm327.commands.filters.RegularExpressionResponseFilter;
import org.hexpresso.elm327.commands.filters.RemoveSpacesResponseFilter;
import org.hexpresso.elm327.log.CommLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

/**
 * Created by henrik on 28/06/2017.
 */

public class StopAfterDataCommand extends AbstractCommand {
    long mStopMonitorTimeDelay = 100;
    long mTimeout = 0;
    String mFilter = new String();

    public StopAfterDataCommand(long timeout, String filter) {
        mTimeout = timeout;
        mFilter = filter;
        if (filter.length() == 3) {
            addResponseFilter(new RegularExpressionResponseFilter("^" + filter + "(.*)$"));
            addResponseFilter(new RemoveSpacesResponseFilter());
        }
    }


    @Override
    public void execute(InputStream in, OutputStream out) throws IOException, InterruptedException, TimeoutException {
        long runStartTimestamp = System.currentTimeMillis();
        String rawResponse = new String();

        // Clean up after last iteration
        mResponse.setRawResponse(rawResponse);
        mResponse.process();

        mCommand = "AT CRA " + mFilter;
        send(out);
        String str = readRawData(in);
        mCommand = "AT MA";
        send(out);

        try {
            setStopReadingAtLineEnd(true);
            while (mResponse.getLines() == null || mResponse.getLines().size() == 0 && System.currentTimeMillis() < runStartTimestamp + mTimeout) {
                // Wait before trying to receive the monitor messages
                //            Thread.sleep(mResponseTimeDelay);

                // Receive the response - NOTE: This will throw exception on connection error etc!
                String raw = readRawData(in);
                mResponse.setRawResponse(raw);
                mResponse.process();
                rawResponse = raw;
            }
        } catch (TimeoutException e) {
            // Too bad
        }
        out.write(' '); // Stop monitoring
        setStopReadingAtLineEnd(false);
        mResponse.setRawResponse(rawResponse);

        // Wait before trying to receive the command response
        Thread.sleep(mResponseTimeDelay);

        str = readRawData(in);

        String ar_response = "";
        while (!ar_response.contains("OK\r") && System.currentTimeMillis() < runStartTimestamp+mTimeout) {
            mCommand = "AT AR";
            send(out);
            ar_response = readRawData(in);
        }
    }
}
