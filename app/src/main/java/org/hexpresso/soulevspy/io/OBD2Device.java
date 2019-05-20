package org.hexpresso.soulevspy.io;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.hexpresso.elm327.commands.Command;
import org.hexpresso.elm327.commands.TimeCommand;
import org.hexpresso.elm327.commands.general.EcuNameCommand;
import org.hexpresso.elm327.commands.protocol.ReadInputVoltageCommand;
import org.hexpresso.elm327.commands.protocol.obd.ObdGetDtcCodesCommand;
import org.hexpresso.elm327.io.ServiceStates;
import org.hexpresso.elm327.io.bluetooth.BluetoothService;
import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.activity.MainActivity;
import org.hexpresso.soulevspy.obd.AmbientTempMessageFilter;
import org.hexpresso.soulevspy.obd.BatteryChargingMessageFilter;
import org.hexpresso.soulevspy.obd.EstimatedRangeMessageFilter;
import org.hexpresso.soulevspy.obd.OdometerMessageFilter;
import org.hexpresso.soulevspy.obd.SpeedPreciseMessageFilter;
import org.hexpresso.soulevspy.obd.StateOfChargePreciseMessageFilter;
import org.hexpresso.soulevspy.obd.StateOfChargeWithOneDecimalMessageFilter;
import org.hexpresso.soulevspy.obd.Status050MessageFilter;
import org.hexpresso.soulevspy.obd.commands.BasicCommand;
import org.hexpresso.soulevspy.obd.commands.FilteredMonitorCommand;
import org.hexpresso.soulevspy.obd.commands.LowVoltageDCConverterSystemCommand;
import org.hexpresso.soulevspy.obd.commands.OnBoardChargerCommand;
import org.hexpresso.soulevspy.obd.commands.TirePressureMSCommand;
import org.hexpresso.soulevspy.obd.commands.VmcuCommand;
import org.hexpresso.soulevspy.util.ClientSharedPreferences;
import org.hexpresso.elm327.commands.general.VehicleIdentifierNumberCommand;
import org.hexpresso.soulevspy.obd.commands.BatteryManagementSystemCommand;

import java.util.ArrayList;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-03.
 */
public class OBD2Device implements BluetoothService.ServiceStateListener {
    final BluetoothService mBluetoothService;
    final ClientSharedPreferences mSharedPreferences;
    final Context mContext;
    String versionName;
    public ArrayList<Command> mLoopCommands = new ArrayList<Command>();
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
    public OBD2Device(ClientSharedPreferences sharedPreferences) {
        Log.d("OBD2Device", "Enter ctor");

        reconnectRunnable = new ReconnectRunnable(this);

        mSharedPreferences = sharedPreferences;
        mContext = sharedPreferences.getContext();

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
        mLoopCommands.add(new TimeCommand(sharedPreferences.getContext().getResources().getString(R.string.col_system_scan_start_time_ms)));
//        mLoopCommands.add(new BasicCommand("AT AR")); // Try Auto Receive
//        mLoopCommands.add(new BasicCommand("01 00")); // Try Get supported PIDs
        mLoopCommands.add(new ReadInputVoltageCommand());
        mLoopCommands.add(new BasicCommand("AT SH 7DF"));
        VehicleIdentifierNumberCommand vinCmd = new VehicleIdentifierNumberCommand();
//        vinCmd.setTimeoutMs(4000);
        mLoopCommands.add(vinCmd);
        mLoopCommands.add(new ObdGetDtcCodesCommand());  // Get stored DTC Codes
        mLoopCommands.add(new EcuNameCommand()); // Get ECU names
        mLoopCommands.add(new BatteryManagementSystemCommand());
        mLoopCommands.add(new OnBoardChargerCommand());
        mLoopCommands.add(new LowVoltageDCConverterSystemCommand());
        mLoopCommands.add(new VmcuCommand());
        mLoopCommands.add(new FilteredMonitorCommand(new AmbientTempMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new StateOfChargeWithOneDecimalMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new StateOfChargePreciseMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new SpeedPreciseMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new OdometerMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new BatteryChargingMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new EstimatedRangeMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new Status050MessageFilter()));
        mLoopCommands.add(new TirePressureMSCommand());

        // Note: No values extracted below - just logging interresting CAN PIDs for later analysis!
//        mLoopCommands.add(new FilteredMonitorCommand(new Status55DMessageFilter())); // No good
//        mLoopCommands.add(new FilteredMonitorCommand(new EstimatedRangeMessageFilter()));
//        mLoopCommands.add(new FilteredMonitorCommand(new StatusLoggingMessageFilter("202")));
//        mLoopCommands.add(new FilteredMonitorCommand(new StatusLoggingMessageFilter("55D")));
//        mLoopCommands.add(new FilteredMonitorCommand(new StatusLoggingMessageFilter("595")));

//        mLoopCommands.add(new BasicCommand("AT LP")); // Try Low Power
        mLoopCommands.add(new TimeCommand(sharedPreferences.getContext().getResources().getString(R.string.col_system_scan_end_time_ms)));

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
                            org.hexpresso.elm327.log.CommLog.getInstance().openFile("soulspy.log", "SoulEVSpy Version: " + versionName);
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