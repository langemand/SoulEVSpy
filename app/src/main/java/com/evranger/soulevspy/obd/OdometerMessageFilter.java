package com.evranger.soulevspy.obd;

import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.obd.ObdMessageData;
import com.evranger.obd.ObdMessageFilter;
import com.evranger.soulevspy.R;

/**
 * Created by Tyrel on 10/17/2015.
 */
public class OdometerMessageFilter extends ObdMessageFilter {
    double odometerKM;

    public OdometerMessageFilter() {
        super("4F0");
    }

    @Override
    protected boolean doProcessMessage(ObdMessageData messageData) {
        if (messageData.getSize() != 8) {
            return false;
        }

        odometerKM = (messageData.getDataByte(5) | messageData.getDataByte(6) << 8 |
                messageData.getDataByte(7) << 16) / 10.0;

        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        vals.set(vals.getPreferences().getContext().getString(R.string.col_car_odo_km), odometerKM);

        return true;
    }

    public double getOdometerKM()
    {
        return odometerKM;
    }
}
