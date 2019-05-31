package com.evranger.soulevspy.obd;

import com.evranger.obd.ObdMessageData;
import com.evranger.obd.ObdMessageFilter;

import java.util.ArrayList;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-13.
 */
public class ParkingBrakeMessageFilter extends ObdMessageFilter {

    boolean mIsParkingBrakeOn = false;

    public ParkingBrakeMessageFilter() {
        super("433");
    }

    @Override
    protected boolean doProcessMessage(ObdMessageData messageData) {
        ArrayList<String> data = messageData.getData();
        if (data.size() <= 2) {
            mIsParkingBrakeOn = false;
            return true;
        }

        mIsParkingBrakeOn = (messageData.getDataByte(2) & 0x08) != 0;

        return true;
    }

    public boolean getIsParkingBrakeOn() {
        return mIsParkingBrakeOn;
    }
}
