package com.evranger.soulevspy.obd;

import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.obd.ObdMessageData;
import com.evranger.soulevspy.R;

import java.util.ArrayList;

import com.evranger.obd.ObdMessageFilter;

/**
 * Created by henrik on 29/06/2017.
 */

public class StateOfChargeWithOneDecimalMessageFilter extends ObdMessageFilter {
    public StateOfChargeWithOneDecimalMessageFilter() {
        super("594");
    }

    @Override
    protected boolean doProcessMessage(ObdMessageData messageData) {
        ArrayList<String> data = messageData.getData();
        if (data.size() != 8) {
            return false;
        }

        // Full SOC value is Little Endian on bytes 4-5
        double anSOCValue = ( ( messageData.getDataByte(5) / 2.0D ) +
                ( messageData.getDataByte(6) & 0x7 ) / 10.0D );

        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        vals.set(vals.getPreferences().getContext().getString(R.string.col_battery_decimal_SOC), anSOCValue);

        return true;
    }
}
