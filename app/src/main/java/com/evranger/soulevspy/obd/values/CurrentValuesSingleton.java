package com.evranger.soulevspy.obd.values;

import com.evranger.soulevspy.util.ClientSharedPreferences;

import com.evranger.soulevspy.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    File m_DataFile = null;
    private FileOutputStream mDataOutputStream = null;
    private ClientSharedPreferences mSharedPreferences = null;
    private final ReentrantLock mLock = new ReentrantLock();
    private final ReentrantLock mListenerLock = new ReentrantLock();
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

    public void setDataFile(File dataFile) {
        m_DataFile = dataFile;
    }

    public void set(String key, Object value){
        mLock.lock();
        try {
            mValues.put(key, value);
        } finally {
            mLock.unlock();
        }
        mListenerLock.lock(); //DL
        try {
            if (mListeners.containsKey(key)) {
                List<CurrentValueListener> listeners = mListeners.get(key);
                if (listeners != null) {
                    for (CurrentValueListener listener : listeners) {
                        listener.onValueChanged(key, value); //DL
                    }
                }
            }
        } finally {
            mListenerLock.unlock();
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
        try {
            if (mSharedPreferences == null) {
                throw new NullPointerException("CurrentValueSingleton.setResources must be called first!");
            }
            set(mSharedPreferences.getContext().getResources().getString(res_key), value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set(int res_key, Integer index, String append, Object value) {
        try {
            if (mSharedPreferences == null) {
                throw new NullPointerException("CurrentValueSingleton.setResources must be called first!");
            }
            set(mSharedPreferences.getContext().getResources().getString(res_key) + index.toString() + append, value);
        } catch (Exception e) {
            e.printStackTrace();
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
        Object o = null;
        try {
            if (mSharedPreferences == null) {
                throw new NullPointerException("CurrentValueSingleton.setResources must be called first!");
            }
            o = get(mSharedPreferences.getContext().getResources().getString(res_key));
        } catch (Exception e) {
            e.printStackTrace();
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

    public void clear() {
        mLock.lock();
        try {
            for (String key : mValues.keySet()) {
                mValues.put(key, null);
            }
        } finally {
            mLock.unlock();
        }
    }

    public void addListener(String key, CurrentValueListener listener) {
        mListenerLock.lock();
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
            mListenerLock.unlock();
        }
    }

    public void delListener(CurrentValueListener listener) {
        mListenerLock.lock();
        try {
            for (List<CurrentValueListener> list : mListeners.values()) {
                if (list != null && list.contains(listener)) {
                    list.remove(listener);
                }
            }
        } finally {
            mListenerLock.unlock();
        }
    }

    private void openDataFile(List<String> columnNamesToLog) {
        // Open data file
        mLock.lock();
        try {
            if (m_DataFile != null) {
                mDataOutputStream = new FileOutputStream(m_DataFile);
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
            }
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

    public String closeLog() {
        if (mDataOutputStream != null) {
            try {
                mDataOutputStream.close();
            } catch (IOException ex) {
                // ?
            }
        }

        String path = null;
        if (m_DataFile != null) {
            path = m_DataFile.getAbsolutePath();
        }
        return path;
    }

    public String getString(int id) {
        return getPreferences().getContext().getResources().getString(id);
    }
}
