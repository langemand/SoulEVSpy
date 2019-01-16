package org.hexpresso.soulevspy.obd.values;

import android.os.Environment;

import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.util.ClientSharedPreferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by henrik on 09/06/2017.
 */

public class CurrentValuesSingleton {
    public static final String separator = ",";
    public abstract interface CurrentValueListener {
        public void onValueChanged(String key, Object value);
    }
    private static CurrentValuesSingleton ourInstance = new CurrentValuesSingleton();
    private final Map<String, Object> mValues = new HashMap<String, Object>();
    private final Map<String, List<CurrentValueListener>> mListeners = new HashMap<String, List<CurrentValueListener>>();
    private final List<String> mColumnNamesLogged = new ArrayList<String>();

    private OutputStream mDataOutputStream = null;
    private ClientSharedPreferences mSharedPreferences = null;
    private final ReentrantLock mLock = new ReentrantLock();
    private static Date nextFlush = new Date(0L);


    public static CurrentValuesSingleton getInstance() {
        return ourInstance;
    }

    public static CurrentValuesSingleton reset() {
        ourInstance = new CurrentValuesSingleton();
        return ourInstance;
    }

    private CurrentValuesSingleton() {
    }

    public void set(String key, Object value){
        mLock.lock();
        try {
            mValues.put(key, value);
        } finally {
            mLock.unlock();
        }
        if (mListeners.containsKey(key)) {
            List<CurrentValueListener> listeners = mListeners.get(key);
            if (listeners != null) {
                for (CurrentValueListener listener : listeners) {
                    listener.onValueChanged(key, value);
                }
            }
        }
    }

    public void setPreferences(ClientSharedPreferences prefs) {
        mLock.lock();
        try {
            mSharedPreferences = prefs;
        } finally {
            mLock.unlock();
        }
    }

    public ClientSharedPreferences getPreferences() {
        mLock.lock();
        try {
           return mSharedPreferences;
        } finally {
            mLock.unlock();
        }
    }

    public void set(int res_key, Object value) {
        mLock.lock();
        try {
            if (mSharedPreferences == null) {
                throw new NullPointerException("CurrentValueSingleton.setResources must be called first!");
            }
            set(mSharedPreferences.getContext().getResources().getString(res_key), value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mLock.unlock();
        }
    }

    public void set(int res_key, Integer index, String append, Object value) {
        mLock.lock();
        try {
            if (mSharedPreferences == null) {
                throw new NullPointerException("CurrentValueSingleton.setResources must be called first!");
            }
            set(mSharedPreferences.getContext().getResources().getString(res_key) + index.toString() + append, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mLock.unlock();
        }
    }

    public Object get(String key) {
        mLock.lock();
        try {
            if(mValues.containsKey(key)) {
                return mValues.get(key);
            }
        } finally {
            mLock.unlock();
        }
        return null;
    }

    public Object get(int res_key) {
        mLock.lock();
        Object o = null;
        try {
            if (mSharedPreferences == null) {
                throw new NullPointerException("CurrentValueSingleton.setResources must be called first!");
            }
            o = get(mSharedPreferences.getContext().getResources().getString(res_key));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mLock.unlock();
        }
        return o;
    }

    public Map<String, Object> find(String keyStartsWith) {
        HashMap<String, Object> found = new HashMap<String, Object>();
        mLock.lock();
        try {
            for (String key : mValues.keySet()) {
                if (key.startsWith(keyStartsWith)) {
                    found.put(key, mValues.get(key));
                }
            }
        } finally {
            mLock.unlock();
        }
        return found;
    }

    public void addListener(String key, CurrentValueListener listener) {
        mLock.lock();
        try {
            List<CurrentValueListener> keyListeners = null;
            if (mListeners.containsKey(key)) {
                keyListeners = mListeners.get(key);
            } else {
                keyListeners = new ArrayList<CurrentValueListener>();
            }
            keyListeners.add(listener);
            mListeners.put(key, keyListeners);
        } finally {
            mLock.unlock();
        }
    }

    public void delListener(CurrentValueListener listener) {
        mLock.lock();
        try {
            for (List<CurrentValueListener> list : mListeners.values()) {
                if (list != null && list.contains(listener)) {
                    list.remove(listener);
                }
            }
        } finally {
            mLock.unlock();
        }
    }

    private void openDataFile(List<String> columnNamesToLog) {
        // Open data file
        final String dataFileName = "SoulData." + new SimpleDateFormat("yyyyMMdd_HHmm'.csv'").format(new Date());
        final File dataFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), dataFileName);
        mLock.lock();
        try {
            mDataOutputStream = new FileOutputStream(dataFile);
            StringBuilder str = new StringBuilder();
            boolean isFirst = true;
            for (String key : columnNamesToLog) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    str.append(separator);
                }
                str.append("\"");
                str.append(key);
                str.append("\"");
                mColumnNamesLogged.add(key);
            }
            SortedSet<String> keyset = new TreeSet<String>(mValues.keySet());
            for (String key : keyset) {
                if (!mColumnNamesLogged.contains(key) && key != getPreferences().getContext().getResources().getString(R.string.col_chargers_locations)) {
                    str.append(separator);
                    str.append("\"");
                    str.append(key);
                    str.append("\"");
                    mColumnNamesLogged.add(key);
                }
            }
            str.append("\n");
            mDataOutputStream.write(str.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mLock.unlock();
        }
    }

    public void log(List<String> columnNamesToLog) {
        mLock.lock();
        StringBuilder str = new StringBuilder();
        try {
            if (mDataOutputStream == null) {
                openDataFile(columnNamesToLog);
            }
            boolean isFirst = true;
            for (String key : mColumnNamesLogged) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    str.append(separator);
                }
                Object val = mValues.get(key);
                boolean do_quote = (val instanceof String);
                if (do_quote)
                    str.append("\"");
                str.append(val);
                if (do_quote)
                    str.append("\"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mLock.unlock();
        }
        try {
            str.append("\n");
            mDataOutputStream.write(str.toString().getBytes());
            Date now = new Date();
            if (now.after(nextFlush)) {
                mDataOutputStream.flush();
                nextFlush.setTime(now.getTime() + 60000L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
