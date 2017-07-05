package org.hexpresso.soulevspy.io;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

//import org.hexpresso.elm327.log;
import org.hexpresso.elm327.commands.Command;
import org.hexpresso.elm327.commands.TimeCommand;
import org.hexpresso.elm327.io.ServiceStates;
import org.hexpresso.elm327.io.bluetooth.BluetoothService;
import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.activity.MainActivity;
import org.hexpresso.soulevspy.obd.AmbientTempMessageFilter;
import org.hexpresso.soulevspy.obd.EstimatedRangeMessageFilter;
import org.hexpresso.soulevspy.obd.OdometerMessageFilter;
import org.hexpresso.soulevspy.obd.SpeedPreciseMessageFilter;
import org.hexpresso.soulevspy.obd.StateOfChargePreciseMessageFilter;
import org.hexpresso.soulevspy.obd.StateOfChargeWithOneDecimalMessageFilter;
import org.hexpresso.soulevspy.obd.Status050MessageFilter;
import org.hexpresso.soulevspy.obd.Status55DMessageFilter;
import org.hexpresso.soulevspy.obd.StatusLoggingMessageFilter;
import org.hexpresso.soulevspy.obd.commands.FilteredMonitorCommand;
import org.hexpresso.soulevspy.obd.commands.LowVoltageDCConverterSystemCommand;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;
import org.hexpresso.soulevspy.util.ClientSharedPreferences;
import org.hexpresso.elm327.commands.general.VehicleIdentifierNumberCommand;
import org.hexpresso.soulevspy.obd.commands.BatteryManagementSystemCommand;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-03.
 */
public class OBD2Device implements BluetoothService.ServiceStateListener {
    final BluetoothService mBluetoothService;
    final ClientSharedPreferences mSharedPreferences;
    final Context mContext;
    public VehicleIdentifierNumberCommand mVehicleIdentifierNumberCommand = null;
    public ArrayList<Command> mLoopCommands = new ArrayList<Command>();
    ReadLoop mReadLoop = null;
    Handler mAutoReconnectHandler = new Handler();
    boolean mConnectWanted = false;


    /**
     * Constructor
     * @param sharedPreferences
     */
    public OBD2Device(ClientSharedPreferences sharedPreferences) {
        Log.d("OBD2Device", "Enter ctor");

        mSharedPreferences = sharedPreferences;
        mContext = sharedPreferences.getContext();

        mBluetoothService = new BluetoothService();
        mBluetoothService.setServiceStateListener(this);

        // Start Bluetooth service
        if (mBluetoothService.isBluetoothAvailable()) {
            mBluetoothService.useSecureConnection(true);
        }
        mVehicleIdentifierNumberCommand = new VehicleIdentifierNumberCommand();
        mLoopCommands.add(new TimeCommand(sharedPreferences.getContext().getResources().getString(R.string.col_system_scan_start_time_ms)));
        mLoopCommands.add(new BatteryManagementSystemCommand());
        mLoopCommands.add(new LowVoltageDCConverterSystemCommand());
        mLoopCommands.add(new FilteredMonitorCommand(new AmbientTempMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new StateOfChargeWithOneDecimalMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new StateOfChargePreciseMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new SpeedPreciseMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new OdometerMessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new Status050MessageFilter()));
        mLoopCommands.add(new FilteredMonitorCommand(new Status55DMessageFilter())); // No good

        // Note: No values extracted below - just logging interresting CAN PIDs for analysis!
//        mLoopCommands.add(new FilteredMonitorCommand(new EstimatedRangeMessageFilter()));
//        mLoopCommands.add(new FilteredMonitorCommand(new StatusLoggingMessageFilter("202")));
//        mLoopCommands.add(new FilteredMonitorCommand(new StatusLoggingMessageFilter("55D")));
//        mLoopCommands.add(new FilteredMonitorCommand(new StatusLoggingMessageFilter("595")));

        mLoopCommands.add(new TimeCommand(sharedPreferences.getContext().getResources().getString(R.string.col_system_scan_end_time_ms)));

        Log.d("OBD2Device", "Exit ctor");
    }

    public boolean connect() {
        mConnectWanted = true;
        return doConnect();
    }

    private boolean doConnect() {
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
                Toast.makeText(mContext, R.string.error_no_bluetooth_device, Toast.LENGTH_LONG).show();
                isDeviceValid = false;
            }
        } else {
            Toast.makeText(mContext, R.string.error_bluetooth_not_available, Toast.LENGTH_LONG).show();
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
                message = "Connecting...";
                break;
            case STATE_CONNECTED:
                message = "Connected";
                Log.d("OBD2Device", "Adding VehicleIdentifierNumberCommand");
                ((MainActivity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            org.hexpresso.elm327.log.CommLog.getInstance().openFile("soulspy.log");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        org.hexpresso.elm327.io.Protocol protocol = mBluetoothService.getProtocol();
                        if (protocol != null) {
                            protocol.addCommand(mVehicleIdentifierNumberCommand);
                            //if (mReadLoop == null) {
                                mReadLoop = new ReadLoop(mSharedPreferences, protocol, mLoopCommands);
                            //}
                            mReadLoop.start();
                        }
                    }
                });
                break;
            case STATE_DISCONNECTING:
                message = "Disconnecting...";
                break;
            case STATE_DISCONNECTED:
                message = "Disconnected";
                if (mReadLoop != null) {
                    mReadLoop.stop();
                }
                if (mSharedPreferences.getAutoReconnectBooleanValue() && mConnectWanted) {
                    final OBD2Device me = this;
                    mAutoReconnectHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mSharedPreferences.getAutoReconnectBooleanValue()) {
                                me.connect();
                            }
                        }
                    }, 10000);
                }
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
}