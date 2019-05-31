package com.evranger.soulevspy.obd;

import com.evranger.obd.ObdMessageData;
import com.evranger.obd.ObdMessageFilter;

import java.util.ArrayList;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-13.
 */
public class TireRotationSpeedMessageFilter extends ObdMessageFilter {
    private double mLeftFrontSpeedKmH  = 0.0;
    private double mRightFrontSpeedKmH = 0.0;
    private double mLeftBackSpeedKmH   = 0.0;
    private double mRightBackSpeedKmH  = 0.0;

    public TireRotationSpeedMessageFilter() {
        super("4B0");
    }

    @Override
    protected boolean doProcessMessage(ObdMessageData messageData) {
        ArrayList<String> data = messageData.getData();
        if ( data.size() != 8 )
        {
            return false;
        }

        mLeftFrontSpeedKmH  = getTireRotationSpeedInKmH(messageData, 1, 0);
        mRightFrontSpeedKmH = getTireRotationSpeedInKmH(messageData, 3, 2);
        mLeftBackSpeedKmH   = getTireRotationSpeedInKmH(messageData, 5, 4);
        mRightBackSpeedKmH  = getTireRotationSpeedInKmH(messageData, 7, 6);

        return true;
    }

    private double getTireRotationSpeedInKmH( ObdMessageData messageData, int msb, int lsb) {
        return ( (messageData.getDataByte(msb) << 8) +
                 (messageData.getDataByte(lsb))) / 30.0;
    }

    public double getLeftFrontSpeedKmH() {
        return mLeftFrontSpeedKmH;
    }

    public double getRightFrontSpeedKmH() {
        return mRightFrontSpeedKmH;
    }

    public double getLeftBackSpeedKmH() {
        return mLeftBackSpeedKmH;
    }

    public double getRightBackSpeedKmH() {
        return mRightBackSpeedKmH;
    }
}
