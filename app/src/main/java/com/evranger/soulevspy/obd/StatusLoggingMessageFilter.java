package com.evranger.soulevspy.obd;

import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.obd.ObdMessageData;
import com.evranger.obd.ObdMessageFilter;

import java.util.ArrayList;

/**
 * Created by henrik on 29/06/2017.
 */

public class StatusLoggingMessageFilter extends ObdMessageFilter {
    public StatusLoggingMessageFilter(String filter) {
        super(filter);
    }

    @Override
    protected boolean doProcessMessage(ObdMessageData messageData) {
        ArrayList<String> data = messageData.getData();
        if (data.size() != 8) {
            return false;
        }

        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        String key = new String("log." + messageIdentifier());
        String str = messageData.getRawData();
        vals.set(key, str);
        return true;
    }
}
