package com.evranger.elm327.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-26.
 */
public abstract class AbstractMultiCommand extends AbstractCommand {

    private List<Command> mCommands = new ArrayList<>();
//    protected Response mRawResponse = null;                    // Raw response data (for all commands)
    boolean mTimedOut = false;

    @Override
    public void execute(InputStream in, OutputStream out) throws IOException, InterruptedException, TimeoutException {
        mTimedOut = false;
        String rawResponse = "";
        try {
            for (Command command : mCommands) {
                command.execute(in, out);
                rawResponse += command.getResponse().rawResponse() + "\r";
            }

            mResponse.setRawResponse(rawResponse);
        } catch (TimeoutException e) {
            mTimedOut = true;
            throw e;
        }
    }

//    @Override
//    public Response getResponse() {
//        return mResponse;
//    }

    @Override
    public void doProcessResponse() {
        if (!mTimedOut) {
            for (Command command : mCommands) {
                command.doProcessResponse();
            }
        }
    }

    protected AbstractMultiCommand addCommand(Command command) {
        mCommands.add(command);
        return this;
    }
}
