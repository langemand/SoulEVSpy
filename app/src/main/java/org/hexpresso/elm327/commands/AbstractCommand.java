package org.hexpresso.elm327.commands;

import org.hexpresso.elm327.exceptions.BusInitException;
import org.hexpresso.elm327.exceptions.MisunderstoodCommandException;
import org.hexpresso.elm327.exceptions.NoDataException;
import org.hexpresso.elm327.exceptions.ResponseException;
import org.hexpresso.elm327.exceptions.StoppedException;
import org.hexpresso.elm327.exceptions.UnableToConnectException;
import org.hexpresso.elm327.exceptions.UnknownErrorException;
import org.hexpresso.elm327.exceptions.UnsupportedCommandException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

/**
 * The AbstractCommand class represents an ELM327 command.
 * <p/>
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-24.
 */
public abstract class AbstractCommand implements Command {

    protected String mCommand = null;                          // ELM327 command
    protected long mResponseTimeDelay = 10;                   // Time delay before receiving the response, in milliseconds
    protected Response mResponse = new Response();             // Response object

    private long mRunStartTimestamp;                           // Timestamp before sending the command
    private long mRunEndTimestamp;                             // Timestamp after receiving the command response
    private boolean mWithAutoProcessResponse = false;          //

    /**
     * Error classes to be tested in order
     */
    private final static Class[] ERROR_CLASSES = {
            UnableToConnectException.class,
            BusInitException.class,
            MisunderstoodCommandException.class,
            NoDataException.class,
            StoppedException.class,
            UnknownErrorException.class,
            UnsupportedCommandException.class
    };

    /**
     * Constructor
     *
     * @param command ELM327 command to send
     */
    public AbstractCommand(String command) {
        mCommand = command;
    }

    private AbstractCommand() {
    }

    /**
     *
     * @param in
     * @param out
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void execute(InputStream in, OutputStream out) throws IOException, InterruptedException {

        Log.d(AbstractCommand.class.getSimpleName(), "Enter execute");
        // Send the command
        mRunStartTimestamp = System.currentTimeMillis();
        send(out);

        // Wait before trying to receive the command response
        Thread.sleep(mResponseTimeDelay);

        // Receive the response
        receive(in);
        mRunEndTimestamp = System.currentTimeMillis();
        Log.d(AbstractCommand.class.getSimpleName(), "Exit execute");
    }

    /**
     *
     * @param out
     * @throws IOException
     * @throws InterruptedException
     */
    protected void send(OutputStream out) throws IOException, InterruptedException {
        final String command = mCommand + '\r';
        Log.d(AbstractCommand.class.getSimpleName(), "send command: " + command);
        out.write(command.getBytes());
        out.flush();
    }

    /**
     *
     * @param in
     */
    protected void receive(InputStream in) throws IOException {
        // Receive the response from the stream
        Log.d(AbstractCommand.class.getSimpleName(), "Enter receive");
        readRawData(in);

        // Check for errors
        checkForErrors();
        Log.d(AbstractCommand.class.getSimpleName(), "Exit receive");
    }

    protected void readRawData(InputStream in) throws IOException {
        StringBuilder res = new StringBuilder();

        // read until '>' arrives OR end of stream reached
        // TODO : Also, add a default timeout value
        while(true)
        {
            final byte b = (byte)in.read();
            if(b == -1) // -1 if the end of the stream is reached
            {
                // End of stream reached
                break;
            }

            final char c = (char)b;
            if(c == '>')
            {
                // read until '>' arrives
                break;
            }
            res.append(c);
        }

    /*
     * Imagine the following response 41 0c 00 0d.
     *
     * ELM sends strings!! So, ELM puts spaces between each "byte". And pay
     * attention to the fact that I've put the word byte in quotes, because 41
     * is actually TWO bytes (two chars) in the socket. So, we must do some more
     * processing..
     */
        String rawResponse = res.toString();
        Log.d("AbstractCommand", rawResponse);

        rawResponse = rawResponse.replaceAll("SEARCHING", "");

        // TODO check this
        //rawResponse = rawResponse.replaceAll("(BUS INIT)|(BUSINIT)|(\\.)", "");

    /*
     * Data may have echo or informative text like "INIT BUS..." or similar.
     * The response ends with two carriage return characters. So we need to take
     * everything from the last carriage return before those two (trimmed above).
     */
        //kills multiline.. rawData = rawData.substring(rawData.lastIndexOf(13) + 1);
        //mResponse = mResponse.replaceAll("\\s", "");//removes all [ \t\n\x0B\f\r]

        // Generate the Response object
        mResponse.setRawResponse(rawResponse);
        if (mWithAutoProcessResponse) {
            mResponse.process();
        }
    }

    protected void checkForErrors() {
        for (Class<? extends ResponseException> errorClass : ERROR_CLASSES) {
            ResponseException messageError;

            try {
                messageError = errorClass.newInstance();
                messageError.setCommand(this.mCommand);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (messageError.isError(mResponse.rawResponse())) {
                throw messageError;
            }
        }
    }

    @Override
    public Response getResponse() {
        return mResponse;
    }

    protected AbstractCommand addResponseFilter(ResponseFilter responseFilter) {
        mResponse.addResponseFilter(responseFilter);
        return this;
    }

    /**
     *
     *
     * @param autoProcessResponse
     * @return
     */
    public AbstractCommand withAutoProcessResponse(boolean autoProcessResponse) {
        mWithAutoProcessResponse = autoProcessResponse;
        return this;
    }
}