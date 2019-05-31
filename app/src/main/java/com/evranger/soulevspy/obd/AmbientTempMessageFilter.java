package com.evranger.soulevspy.obd;

import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.obd.ObdMessageData;
import com.evranger.obd.ObdMessageFilter;
import com.evranger.soulevspy.R;

/**
 * Created by Tyrel Haveman <tyrel@binarypeople.net> on 11/30/2015.
 */
public class AmbientTempMessageFilter extends ObdMessageFilter {
    private double ambientTemperature;

    public AmbientTempMessageFilter() {
        super("653");
    }

    @Override
    protected boolean doProcessMessage(ObdMessageData messageData) {
        if ( messageData.getSize() != 8 ) {
            return false;
        }

        ambientTemperature = messageData.getDataByte(5) / 2.0 - 40.0;
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        vals.set(vals.getPreferences().getContext().getString(R.string.col_car_ambient_C), ambientTemperature);

        return true;
    }

    public double getAmbientTemperature() {
        return ambientTemperature;
    }
}
