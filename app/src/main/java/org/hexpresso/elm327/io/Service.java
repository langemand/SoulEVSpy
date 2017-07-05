package org.hexpresso.elm327.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-28.
 */
public abstract class Service {

    // TODO handle high level service here, then we can have 2 ELM327 flavors : Bluetooth and WiFi

    private ServiceStateListener mServiceStateListener = null;

    private Protocol mProtocol = null;

    private ServiceStates mState = ServiceStates.STATE_DISCONNECTED;
    protected InputStream mInputStream = null;
    protected OutputStream mOutputStream = null;

    /**
     * Constructor
     */
    protected Service() {
    }

    public interface ServiceStateListener {
        void onServiceStateChanged(ServiceStates state);
    }

    /**
     * Connects to the ELM327 device
     */
    public abstract void connect();

    /**
     * Disconnects from the ELM327 device
     */
    public abstract void disconnect();

    /**
     * Returns the current Service state
     *
     * @return current Service state.
     */
    public synchronized ServiceStates getState() {
        return mState;
    }

    /**
     * Updates the Service state to the specified state. If a ServiceStateListener is available, it
     * will be notified.
     *
     * @param state new Service state to be set
     */
    protected synchronized void setState(final ServiceStates state) {
        mState = state;

        // Notify the listeners, if any
        if( mServiceStateListener != null) {
            Thread notificationThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    mServiceStateListener.onServiceStateChanged(state);
                }
            });
            notificationThread.start();
            notificationThread.setName("BluetoothServiceNotificationThread");
        }
    }

    /**
     * Close the input and output streams if open
     */
    protected void closeStreams() {
        if(mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void startProtocol() {
        // TODO verify streams not null
        Log.d(Service.class.getSimpleName(), "Enter startProtocol");
//        if(mState == ServiceStates.STATE_CONNECTED) {
            mProtocol = new Protocol();
            mProtocol.start(mInputStream, mOutputStream);
            mProtocol.init();
//        }
        Log.d(Service.class.getSimpleName(), "Exit startProtocol");
    }

    protected void stopProtocol() {
        Log.d(Service.class.getSimpleName(), "Enter stopProtocol");
        if(mProtocol != null) {
            mProtocol.stop();
        }
        Log.d(Service.class.getSimpleName(), "Exit stopProtocol");
    }

    public Protocol getProtocol() {
        Log.d(Service.class.getSimpleName(), "getProtocol");
        return mProtocol;
    }

    public void setServiceStateListener(ServiceStateListener listener) {
        mServiceStateListener = listener;
    }
}
