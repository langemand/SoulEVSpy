package com.evranger.soulevspy;

import android.util.Log;
import android.util.Pair;

import com.evranger.elm327.commands.AbstractCommand;
import com.evranger.elm327.exceptions.UnsupportedCommandException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

public class Responder {
    InputStream mIs;
    PipedOutputStream mOs;
    InputStream tIs;
    PipedOutputStream tOs;
    List<Pair<String, String>> mRequestResponseList;
    Thread mResponseThread;
    String mMessages = "";
    boolean mComplete = false;

    public Responder(List<Pair<String, String>> requestResponseList) {
        try {
            tOs = new PipedOutputStream();
            mIs = new BufferedInputStream(new PipedInputStream(tOs));
            mOs = new PipedOutputStream();
            tIs = new BufferedInputStream(new PipedInputStream(mOs));
            mRequestResponseList = requestResponseList;

            mResponseThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        respond();
                    } catch (Exception ex) {
                        mMessages = ex.getMessage();
                    }
                }
            });
            mResponseThread.start();
        } catch (Exception e) {
            //
        }
    }

    private void respond() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(mIs));
        OutputStreamWriter ow = new OutputStreamWriter(mOs);
        for (int i = 0; i < mRequestResponseList.size(); ++i) {
            Pair reqRes = mRequestResponseList.get(i);
            String req = (String)reqRes.first;
            String res = (String)reqRes.second;
//            Log.d(Responder.class.getSimpleName(), "respond to: " + req + " with " + res);

            String received = "";
            char c = 'a';
            while (reader.ready() || received.isEmpty()) {
                int b = reader.read();
                c = (char) b;
                if (c != '\r') {
                    received += c;
                }
                if (received.matches(req)) {
                    break;
                }
            }
            if (! received.matches(req)) {
                boolean rematched = false;
                Log.d(this.getClass().getSimpleName(), "Command Mismatch: Excepcted '" + req + "' got '" + received + "'!");
                // Look ahead
                for (int j = i + 1; j < mRequestResponseList.size(); ++j) {
                    req = mRequestResponseList.get(j).first;
                    res = mRequestResponseList.get(j).second;
                    Log.d(this.getClass().getSimpleName(), "checking whether '" + received + "' matches: '" + req + "'");
                    if (received.matches(req)) {
                        i=j;
                        rematched = true;
                        break;
                    }
                }
                if (! rematched) {
                    throw new IOException("Responder is unable to match: '" + received + "'!");
                }
            }
            Log.d(Responder.class.getSimpleName(), "responding : '" + res + "'");
            ow.write(res);
            ow.flush();
        }
        mComplete = true;
    }

    public InputStream getInput() {
        return tIs;
    }

    public OutputStream getOutput() {
        return tOs;
    }

    public String getMessages() throws IOException {
        mResponseThread.interrupt();

        tIs.close();
        tOs.close();
        mIs.close();
        mOs.close();

        return mMessages;
    }

    public List getResponseList() {
        return mRequestResponseList;
    }

    public boolean isComplete() {
        return mComplete;
    }
}
