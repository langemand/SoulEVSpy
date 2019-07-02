package com.evranger.soulevspy.io;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.evranger.elm327.commands.Command;
import com.evranger.elm327.io.bluetooth.BluetoothService;
import com.evranger.elm327.log.CommLog;
import com.evranger.soulevspy.activity.MainActivity;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.evranger.elm327.io.ServiceStates;

import com.evranger.soulevspy.R;

import com.evranger.soulevspy.util.ClientSharedPreferences;

import java.util.ArrayList;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-03.
 */
public class OBD2Device implements BluetoothService.ServiceStateListener {
    final BluetoothService mBluetoothService;
    final ClientSharedPreferences mSharedPreferences;
    final Context mContext;
    String versionName;
    public ArrayList<Command> mLoopCommands;
    ReadLoop mReadLoop = null;
    Handler mAutoReconnectHandler = new Handler();
    boolean mConnectWanted = false;
    boolean mIsConnected = false;
    boolean mConnectInProgress = false;

    class ReconnectRunnable implements Runnable {
        OBD2Device me = null;
        public ReconnectRunnable(OBD2Device obd2Device) {
            me = obd2Device;
        }

        @Override
        public void run() {
            if (mSharedPreferences.getAutoReconnectBooleanValue()) {
                if (me.mConnectWanted) {
                    me.doConnect();
                }
            }
        }
    }
    ReconnectRunnable reconnectRunnable = null;

    /**
     * Constructor
     * @param sharedPreferences
     */
    public OBD2Device(ClientSharedPreferences sharedPreferences, ArrayList<Command> loopCommands) {
        Log.d("OBD2Device", "Enter ctor");

        mLoopCommands = loopCommands;
        reconnectRunnable = new ReconnectRunnable(this);

        mSharedPreferences = sharedPreferences;
        mContext = mSharedPreferences.getContext();

        mBluetoothService = new BluetoothService();
        mBluetoothService.setServiceStateListener(this);

        try {
            versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (Exception e) {
            versionName = "UnknownVersion";
        }

        // Start Bluetooth service
        if (mBluetoothService.isBluetoothAvailable()) {
            mBluetoothService.useSecureConnection(true);
        }
        Log.d("OBD2Device", "Exit ctor");
    }

    public boolean connect() {
        mConnectWanted = true;
        return doConnect();
    }

    private boolean doConnect() {
        if (mConnectInProgress) return true;
        mConnectInProgress = true;
        Log.d("OBD2Device", "Enter connect");
        boolean isDeviceValid = mBluetoothService.isBluetoothAvailable();

        if ( isDeviceValid ) {
            String btAddress = mSharedPreferences.getBluetoothDeviceStringValue();
            BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

            Log.d("SOULEV", "Trying to connect to ELM327 device : " + btAddress);

            if (!btAddress.equals(mSharedPreferences.DEFAULT_BLUETOOTH_DEVICE) && (bta != null)) {
                // Set the bluetooth adapter name as summary
                try {
                    mBluetoothService.setDevice(btAddress);
                    mBluetoothService.connect();
                    isDeviceValid = true;
                } catch (IllegalArgumentException e) {
                    isDeviceValid = false;
                }
            }
            else {
                ((MainActivity)mContext).runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         Toast.makeText(mContext, R.string.error_no_bluetooth_device, Toast.LENGTH_LONG).show();
                     }
                });
                isDeviceValid = false;
            }
        } else {
            ((MainActivity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, R.string.error_bluetooth_not_available, Toast.LENGTH_LONG).show();
                }
            });
        }

        if(!isDeviceValid)
        {
            disconnect();
        }

        Log.d("OBD2Device", "Exit connect");
        return isDeviceValid;
    }

    public boolean disconnect() {
        mConnectWanted = false;
        return doDisconnect();
    }

    private boolean doDisconnect() {
        if (mReadLoop != null) {
            mReadLoop.stop();
        }
        mBluetoothService.disconnect();
        return true;
    }

    @Override
    public void onServiceStateChanged(ServiceStates state) {
        Log.d("OBD2Device", "Enter onServiceStateChanged");
        String message = null;
        switch(state) {
            case STATE_CONNECTING:
                message = mContext.getResources().getString(R.string.dongle_connecting);
                break;
            case STATE_CONNECTED:
                mIsConnected = true;
                message = mContext.getResources().getString(R.string.dongle_connected);
                Log.d("OBD2Device", "Starting ReadLoop");
                ((MainActivity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CommLog.getInstance().openFile("soulspy.log", "SoulEVSpy Version: " + versionName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (mBluetoothService != null) {
                            mReadLoop = new ReadLoop(mSharedPreferences, mBluetoothService, mLoopCommands);
                            mReadLoop.start();
                            logBluetoothEvent("connected");
                        }
                    }
                });
                break;
            case STATE_DISCONNECTING:
                message = mContext.getResources().getString(R.string.dongle_disconnecting);
                break;
            case STATE_DISCONNECTED:
                mIsConnected = false;
                message = mContext.getResources().getString(R.string.dongle_disconnected);
                if (mReadLoop != null) {
                    mReadLoop.stop();
                }
                logBluetoothEvent("disconnected");
                if (mSharedPreferences.getAutoReconnectBooleanValue() && mConnectWanted) {
                    final OBD2Device me = this;
                    mAutoReconnectHandler.removeCallbacks(reconnectRunnable);
                    mAutoReconnectHandler.postDelayed(reconnectRunnable, 10000);
                }
                mConnectInProgress = false;
                break;
            default:
                // Do nothing
                break;
        }

        final String msg = message;

        // TODO : This is just for a test!
        ((MainActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("OBD2Device", "Exit onServiceStateChanged");
    }

    public boolean isConnected() {
        return mIsConnected;
    }

    public void logBluetoothEvent(String event) {
        Bundle params = new Bundle();
        params.putString("event", event);
        FirebaseAnalytics.getInstance(mContext).logEvent("bluetooth_event", params);
    }

}