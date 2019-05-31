package com.evranger.soulevspy.obd;

import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.obd.ObdMessageData;
import com.evranger.obd.ObdMessageFilter;
import com.evranger.soulevspy.R;

import java.util.ArrayList;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-13.
 */
public class StateOfChargePreciseMessageFilter extends ObdMessageFilter {

    double mSOCValue = 0.0;

    public StateOfChargePreciseMessageFilter() {
        super("598");
    }

    @Override
    protected boolean doProcessMessage(ObdMessageData messageData) {
        ArrayList<String> data = messageData.getData();
        if (data.size() != 8) {
            return false;
        }

        // Full SOC value is Little Endian on bytes 4-5
        mSOCValue = ( ( messageData.getDataByte(5) << 8 ) +
                      ( messageData.getDataByte(4) ) ) / 256.0;

        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        vals.set(vals.getPreferences().getContext().getString(R.string.col_battery_precise_SOC), mSOCValue);

        return true;
    }

    public double getSOC() {
        return mSOCValue;
    }

}
