package com.evranger.soulevspy.obd;

import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.obd.ObdMessageData;
import com.evranger.obd.ObdMessageFilter;

import java.util.ArrayList;

/**
 * Created by henrik on 29/06/2017.
 */

public class Status55DMessageFilter extends ObdMessageFilter {
    public Status55DMessageFilter() {
        super("55D");
    }

    @Override
    protected boolean doProcessMessage(ObdMessageData messageData) {
        ArrayList<String> data = messageData.getData();
        if (data.size() != 8) {
            return false;
        }

        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        double amps = (128 - ( messageData.getDataByte(1))) / 10.0;
        double aux_power = ( 32768 - (messageData.getDataByte(0) + ( messageData.getDataByte(1) << 8) ) ) / 5.0;
        double other_power = (32768 - messageData.getDataByte(2) + ( ( messageData.getDataByte(3) & 0x03) << 8) ) / 5.0;
        int temp_C = messageData.getDataByte(4);
// These are no good
        vals.set("log.aux_current_A", Double.valueOf(amps));
        vals.set("log.aux_power_W", Double.valueOf(aux_power));
        vals.set("log.other_power_W", Double.valueOf(other_power));
        vals.set("log.temperature_C", Double.valueOf(temp_C));
        vals.set("log.55D", messageData.getRawData());

        return true;
    }
}
