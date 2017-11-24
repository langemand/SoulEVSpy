package org.hexpresso.soulevspy.obd;

import org.hexpresso.obd.ObdMessageData;
import org.hexpresso.obd.ObdMessageFilter;
import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.ArrayList;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-13.
 */
public class Status050MessageFilter extends ObdMessageFilter {

    public enum TurnSignal {
        OFF,
        LEFT,
        RIGHT
    }

    public enum WiperSpeed {
        OFF,
        INTER_0, // Slowest
        INTER_1,
        INTER_2,
        INTER_3,
        INTER_4, // Fastest
        NORMAL,
        FAST
    }

    public enum LightsMode {
        OFF,
        PARKING,
        ON,
        AUTOMATIC
    }

    private TurnSignal mTurnSignal = TurnSignal.OFF;
    private LightsMode mLightsMode = LightsMode.OFF;
    private WiperSpeed mWiperSpeed = WiperSpeed.OFF;

    public Status050MessageFilter() {
        super("050");
    }
// byte 1
    private static final int MASK_LIGHTS       = 0x03;
    private static final int MASK_WIPERS_INTER = 0xF0;
// byte 2
    private static final int MASK_WIPERS       = 0x07;
    private static final int MASK_TURN_SIGNAL  = 0x30;

    @Override
    protected boolean doProcessMessage(ObdMessageData messageData) {
        ArrayList<String> data = messageData.getData();
        if (data.size() < 4) {
            return false;
        }

        // Get interesting bytes
        final int byte1 = messageData.getDataByte(1);
        final int byte2 = messageData.getDataByte(2);

        // Lights
        mLightsMode = LightsMode.OFF;
        final int lightsStatus = ( byte1 & MASK_LIGHTS );
        switch ( lightsStatus ) {
            case 0x01:
                mLightsMode = LightsMode.PARKING;
                break;
            case 0x02:
                mLightsMode = LightsMode.ON;
                break;
            case 0x03:
                mLightsMode = LightsMode.AUTOMATIC;
                break;
        }

        // Turn signal
        mTurnSignal = TurnSignal.OFF;
        final int turnSignal = ( byte2 & MASK_TURN_SIGNAL);
        switch ( turnSignal )
        {
            case 0x10:
                mTurnSignal = TurnSignal.RIGHT;
                break;
            case 0x20:
                mTurnSignal = TurnSignal.LEFT;
                break;
        }

        // Wiper
        mWiperSpeed = WiperSpeed.OFF;
        final int wiperSpeed = ( byte2 & MASK_WIPERS );
        switch ( wiperSpeed )
        {
            case 0x01:
                mWiperSpeed = WiperSpeed.NORMAL;
                break;
            case 0x02:
                // Intermediate speed
                final int wiperInterSpeed = ( byte1 & MASK_WIPERS_INTER );
                switch ( wiperInterSpeed )
                {
                    case 0x80:
                        // Slowest
                        mWiperSpeed = WiperSpeed.INTER_0;
                        break;
                    case 0x60:
                        mWiperSpeed = WiperSpeed.INTER_1;
                        break;
                    case 0x40:
                        mWiperSpeed = WiperSpeed.INTER_2;
                        break;
                    case 0x20:
                        mWiperSpeed = WiperSpeed.INTER_3;
                        break;
                    case 0x00:
                        // Fastest
                        mWiperSpeed = WiperSpeed.INTER_4;
                        break;
                }
                break;
            case 0x04:
                mWiperSpeed = WiperSpeed.FAST;
                break;
        }

        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        vals.set(vals.getPreferences().getContext().getString(R.string.col_car_lights_status), mLightsMode.toString());
        vals.set(vals.getPreferences().getContext().getString(R.string.col_car_wipers_status), mWiperSpeed.toString());

        return true;
    }

    public TurnSignal getTurnSignalStatus() {
        return mTurnSignal;
    }

    public WiperSpeed getWiperSpeedStatus() {
        return mWiperSpeed;
    }

    public LightsMode getLightsMode() {
        return mLightsMode;
    }
}
