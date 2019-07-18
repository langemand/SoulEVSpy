package com.evranger.soulevspy;

import android.util.Log;
import android.util.Pair;

import com.evranger.elm327.commands.AbstractCommand;

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
        for (Pair reqRes : mRequestResponseList) {
            String req = (String)reqRes.first;
            String res = (String)reqRes.second;
            Log.d(Responder.class.getSimpleName(), "respond to: " + req + " with " + res);

            String received = "";
            char c = 'a';
            while (! received.matches(req)) {
                int b = reader.read();
                c = (char) b;
                if (c != '\r') {
                    received += c;
                }
            }
            if (! received.matches(req)) {
                throw new Exception("Command Mismatch for "+req+": Excepcted " + req + " got " + received + "!");
            }
            Log.d(Responder.class.getSimpleName(), "responding : " + res);
            ow.write(res);
            ow.flush();
        }
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
}
