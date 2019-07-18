package com.evranger.soulevspy.obd;

import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.obd.ObdMessageData;
import com.evranger.obd.ObdMessageFilter;
import com.evranger.soulevspy.R;

/**
 * Created by Tyrel on 10/17/2015.
 */
public class SpeedAndOdometerMessageFilter extends ObdMessageFilter {
    double mSpeedKmH = 0.0;
    double odometerKM;

    public SpeedAndOdometerMessageFilter() {
        super("4F0");
    }

    @Override
    protected boolean doProcessMessage(ObdMessageData messageData) {
        if (messageData.getSize() < 8) {
            mSpeedKmH = -0.001;
            return false;
        }

        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        mSpeedKmH = (messageData.getDataByte(1) | ((messageData.getDataByte(2) & 0x01) << 8)) / 2.0;
        vals.set(R.string.col_car_speed_kph, mSpeedKmH);

        odometerKM = (messageData.getDataByte(5) | messageData.getDataByte(6) << 8 |
                messageData.getDataByte(7) << 16) / 10.0;
        vals.set(R.string.col_car_odo_km, odometerKM);

        return true;
    }

    public double getOdometerKM()
    {
        return odometerKM;
    }

    public double getSpeedInKmH() {
        return mSpeedKmH;
    }

}
