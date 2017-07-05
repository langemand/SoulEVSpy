package org.hexpresso.elm327.commands;

import android.os.SystemClock;

import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by henrik on 28/06/2017.
 */

public class TimeCommand implements Command {
    Response mEmptyResponse = new Response();
    String mKey = null;

    public TimeCommand(String key) {
        mEmptyResponse.setRawResponse("");
        mKey = key;
    }
    @Override
    public void execute(InputStream in, OutputStream out) throws IOException, InterruptedException {
    }

    public Response getResponse() {
        return mEmptyResponse;
    }

    @Override
    public void doProcessResponse() {
        CurrentValuesSingleton.getInstance().set(mKey, System.currentTimeMillis());
    }
}
