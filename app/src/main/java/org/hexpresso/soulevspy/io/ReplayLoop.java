package org.hexpresso.soulevspy.io;

import android.net.Uri;

import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReplayLoop {
    private CurrentValuesSingleton mCurrentValuesSingleton = CurrentValuesSingleton.getInstance();
    private BufferedReader mReader;
    private Thread mLoopThread = null;
    private long mStartTime = System.currentTimeMillis();
    private String[] mHeaders;
    private Object[] mValues;

    public ReplayLoop(Uri filepath) {
        final File dataFile = new File(filepath.getPath());
        try {
            InputStream is = new FileInputStream(dataFile);
            open(is);
        } catch (Exception ex) {
//
        }
    }

    public ReplayLoop(InputStream is) {
        open(is);
    }

    public void open(InputStream is) {
        try {
            mReader = new BufferedReader(new InputStreamReader(is));
            String header = mReader.readLine();
            mHeaders = header.split(",");
            for (int i = 0; i < mHeaders.length; ++i) {
                mHeaders[i] = mHeaders[i].replaceAll("\"", "");
            }
            String line = mReader.readLine();
            mValues = line.split(",");

            // Thread used to run commands in loop
            mLoopThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    long firsttime = 0;
                    long lasttime = 0;
                    int idxScanStartTime = -1;
                    try {
                        while (!mLoopThread.isInterrupted() && mValues.length >= mHeaders.length) {
                            for (int i = 0; i < mHeaders.length; ++i) {
                                Object obj = null;
                                if (mValues[i].toString().contains("\"")) {
                                    obj = mValues[i].toString().replaceAll("\"", "");
                                } else if ((mHeaders[i].endsWith("_ms") || mHeaders[i].endsWith("_s")) && !mValues[i].toString().contains(".")) {
                                    obj = Long.parseLong(mValues[i].toString());
                                } else {
                                    try {
                                        obj = Integer.parseInt(mValues[i].toString());
                                    } catch (Exception ex) {
                                        try {
                                            obj = Double.parseDouble(mValues[i].toString());
                                        } catch (Exception ex2) {
                                            obj = mValues[i].toString();
                                        }
                                    }
                                }
                                mCurrentValuesSingleton.set(mHeaders[i], obj);
                                //Thread.sleep(1);
                                if (idxScanStartTime < 0 && mHeaders[i].contains("scan_end_time_ms")) {
                                    idxScanStartTime = i;
                                    if (firsttime == 0) {
                                        firsttime = Long.parseLong(mValues[idxScanStartTime].toString());
                                    }
                                } else if (i == idxScanStartTime) {
                                    lasttime = Long.parseLong(mValues[idxScanStartTime].toString());
                                    long millis = (lasttime - firsttime) - (System.currentTimeMillis() - mStartTime);
                                    if (millis < 300) {
                                        millis = 300;
                                    }
                                    Thread.sleep(millis);
                                }
                            }
                            String line = mReader.readLine();
                            mValues = line.split(",");
                        }
                    } catch (Exception ex) {
                        int i = 0;
                        //
                    }
                }
                int i = 1;
            });
            mLoopThread.setName("ReplayLoopThread");
            start();
        } catch (Exception ex) {
            int i = 7;
            //
        }
    }

    public synchronized void start() {
        mLoopThread.start();
    }

    public synchronized void stop() {
        mLoopThread.interrupt();
    }

}
