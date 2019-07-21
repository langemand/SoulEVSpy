package com.evranger.soulevspy;

import android.util.Pair;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class LogFileResponder {
    Responder mResponder;

    public LogFileResponder(InputStream fis) throws Exception {
        List responseList = responseListFromLog(fis);

        mResponder = new Responder(responseList);
    }

    public InputStream getInput() {
        return mResponder.getInput();
    }

    public OutputStream getOutput() {
        return mResponder.getOutput();
    }

    public List getResponseList() {
        return mResponder.getResponseList();
    }

    public String getMessages() throws IOException {
        return mResponder.getMessages();
    }

    public boolean isComplete() {
        return mResponder.isComplete();
    }

    private List responseListFromLog(InputStream fis) throws Exception {
        List responses = new ArrayList();
        InputStream bis = new BufferedInputStream(fis);

        // Skip first line:
        while (bis.available() > 0) {
            char c = (char)bis.read();
            if (c == '\n')
                break;
        }

        StringBuilder str = new StringBuilder();
        char prev = '\0';
        String req = null;
        boolean lastOut = false;
        while (bis.available() > 0) {
            int b = bis.read();
            char c = (char)b;
            if (c == '\n') {
                c = '\r';
            }
            if (c == ':') {
                if (prev == 'o') {
                    if (req != null) {
                        responses.add(new Pair(req, str.toString()));
                        req = null;
                        str.setLength(0); // Clear the contents of str
                    }
                }
                if (prev == 'o' || prev == 'i' || prev == 'f') {
                    if (str.length() > 1) {
                        req = str.toString().trim();
                    } else if (str.length() > 0) {
                        req = str.toString();
                    }
                    str.setLength(0);
                }
                if (prev == 'o' && lastOut && req != null) {
                    responses.add(new Pair(req, ""));
                }
                lastOut = prev == 'o';
                c = '\0';
            } else {
                if (prev != '\0') {
                    str.append(prev);
                }
            }
            prev = c;
        }

        return responses;
    }
}
