package com.evranger.elm327.commands;

import android.util.Log;

import com.evranger.elm327.commands.filters.RegularExpressionResponseFilter;
import com.evranger.elm327.commands.filters.RemoveSpacesResponseFilter;
import com.evranger.elm327.log.CommLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

/**
 * Created by henrik on 28/06/2017.
 */

public class StopAfterDataCommand extends AbstractCommand {
    String mFilter = new String();

    public StopAfterDataCommand(long timeout, String filter) {
        setTimeoutMs(timeout);
        mFilter = filter;
        if (filter.length() == 3) {
            addResponseFilter(new RegularExpressionResponseFilter("^" + filter + "(.*)$"));
            addResponseFilter(new RemoveSpacesResponseFilter());
        }
    }


    @Override
    public void execute(InputStream in, OutputStream out) throws IOException, InterruptedException, TimeoutException {
        mRunStartTimestamp = System.currentTimeMillis();
        String rawResponse = new String();

        // Clean up after last iteration
        mResponse.setRawResponse(rawResponse);
        mResponse.process();

        mCommand = "AT CRA " + mFilter;
        send(out);

        String str = readRawData(in);

        mCommand = "AT MA";
        send(out);

        TimeoutException caughtException = null;
        try {
            setNumberOfLinesToRead(1);
//            Log.d(this.getClass().getSimpleName(), (mRunStartTimestamp + mTimeout_ms) - System.currentTimeMillis() + " milliseconds to go in execute" );
            while ((mResponse.getLines() == null || mResponse.getLines().size() == 0) && (System.currentTimeMillis() < (mRunStartTimestamp + mTimeout_ms))) {
                // Wait before trying to receive the monitor messages
                // Thread.sleep(mResponseTimeDelay);

                // Receive the response - NOTE: This will throw exception on connection error etc!
//                mRunStartTimestamp = System.currentTimeMillis();
                String raw = readRawData(in);
                mResponse.setRawResponse(raw);
                mResponse.process();
                rawResponse = raw;
            }
        } catch (TimeoutException e) {
            caughtException = e;
            Log.d(this.getClass().getSimpleName(), "StopAfterDataCommand caught : " + e);
        }

        byte[] commandBytes = " ".getBytes();
        out.write(commandBytes);
        out.flush();
        CommLog.getInstance().log("o:".getBytes());
        CommLog.getInstance().log(commandBytes);// Stop monitoring

        setNumberOfLinesToRead(0);
        mResponse.setRawResponse(rawResponse);

        // Wait before trying to receive the command response
        Thread.sleep(mResponseTimeDelay);

        rawResponse = readRawData(in);
        flushInput(in);

        String ar_response = "";
        while ((!ar_response.endsWith(">")) && (System.currentTimeMillis() < (mRunStartTimestamp+mTimeout_ms))) {
            mCommand = "AT AR";
            send(out);
            try {
                ar_response = readRawData(in);
            } catch (Exception e) {
                int i = 0;
            }
            rawResponse = ar_response;
        }
//        mResponse.setRawResponse(rawResponse);
        if (caughtException != null) {
            throw caughtException;
        }
        checkForErrors();
    }
}
