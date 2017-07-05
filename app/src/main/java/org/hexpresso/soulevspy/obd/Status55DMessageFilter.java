package org.hexpresso.soulevspy.obd;

import org.hexpresso.obd.ObdMessageData;
import org.hexpresso.obd.ObdMessageFilter;
import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

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
        double aux_power = ( (messageData.getDataByte(0) + ( messageData.getDataByte(1) << 8) ) ) / 5.0;
        double other_power = (messageData.getDataByte(2) + ( ( messageData.getDataByte(3) & 0x03) << 8) ) / 5.0;
// These are no good
        vals.set("log.aux_power_W", Double.valueOf(aux_power));
        vals.set("log.other_power_W", Double.valueOf(other_power));
        vals.set("log.55D", messageData.getRawData());

        return true;
    }
}
