package org.hexpresso.elm327.log;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by henrik on 08/06/2017.
 */
public class CommLog {
    private String mLogFileName = null;
    private File mCommLogFile = null;
    private OutputStream mCommLogOs = null;

    private static final CommLog ourInstance = new CommLog();

    public static CommLog getInstance() {
        return ourInstance;
    }

    private CommLog() {
    }

    public void openFile(String logFileName) throws FileNotFoundException {
        mLogFileName = logFileName + new SimpleDateFormat("yyyyMMddHHmm'.txt'").format(new Date());
        mCommLogFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), mLogFileName);
        mCommLogOs = new FileOutputStream(mCommLogFile);
    }

    public void log(byte[] bytes) throws IOException {
        if (mCommLogOs != null) {
            mCommLogOs.write(bytes);
        }
    }

    public void flush() {
        if (mCommLogOs != null) {
            try {
                mCommLogOs.flush();
           } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}