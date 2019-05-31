package com.evranger.soulevspy.io;

import android.content.Context;
import android.net.Uri;

import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReplayLoop {
    private CurrentValuesSingleton mCurrentValuesSingleton = CurrentValuesSingleton.getInstance();
    private BufferedReader mReader;
    private Thread mLoopThread = null;
    private long mStartTime = System.currentTimeMillis();
    private String[] mHeaders;
    private Object[] mValues;
    final private String regex = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    public ReplayLoop(Uri filepath, Context context) {
        try {
            InputStream is = context.getContentResolver().openInputStream(filepath);
            open(is);
        } catch (Exception ex) {
//
        }
    }

    public ReplayLoop(InputStream is) {
        mCurrentValuesSingleton.clear();

        open(is);
    }

    public void open(InputStream is) {
        try {
            mReader = new BufferedReader(new InputStreamReader(is));
            String header = mReader.readLine();
            mHeaders = header.split(regex);
            for (int i = 0; i < mHeaders.length; ++i) {
                mHeaders[i] = mHeaders[i].replaceAll("\"", "");
            }
            String line = mReader.readLine();
            mValues = line.split(regex);

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
                                String str = mValues[i].toString();
                                if (str.contentEquals("null")) {
                                    obj = null;
                                } else if (str.contains("\"")) {
                                    obj = str.replaceAll("\"", "");
                                } else if (str.contentEquals("true") || str.contentEquals("false")) {
                                    obj = Boolean.parseBoolean(str);
                                } else if ((mHeaders[i].endsWith("_ms") || mHeaders[i].endsWith("_s")) && !str.contains(".")) {
                                    obj = Long.parseLong(str);
                                } else {
                                    try {
                                        if (mHeaders[i].startsWith("battery.module_temperature") && str.endsWith(".0")) {
                                            str = str.substring(0, str.length()-2);
                                        }
                                        obj = Integer.parseInt(str);
                                    } catch (Exception ex) {
                                        try {
                                            obj = Double.parseDouble(str);
                                        } catch (Exception ex2) {
                                            if (!str.contentEquals("null")) {
                                                obj = String.valueOf(mValues[i]);
                                            }
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
                                    if (millis < 500) {
                                        millis = 500;
                                    }
                                    if (millis > 3000) {
                                        millis = 3000;
                                    }
                                    Thread.sleep(millis);
                                }
                            }
                            String line = mReader.readLine();
                            if (line == null) {
                                break;
                            }
                            mValues = line.split(regex);
                        }
                    } catch (Exception ex) {
                        //
                    }
                }
            });
            mLoopThread.setName("ReplayLoopThread");
            start();
        } catch (Exception ex) {
            //
        }
    }

    private synchronized void start() {
        if (mLoopThread != null) {
            mLoopThread.start();
        }
    }

    public synchronized void stop() {
        if (mLoopThread != null) {
            mLoopThread.interrupt();
        }
    }

}
