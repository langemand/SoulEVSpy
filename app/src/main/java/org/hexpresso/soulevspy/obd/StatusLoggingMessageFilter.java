package org.hexpresso.soulevspy.obd;

import org.hexpresso.obd.ObdMessageData;
import org.hexpresso.obd.ObdMessageFilter;
import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

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
