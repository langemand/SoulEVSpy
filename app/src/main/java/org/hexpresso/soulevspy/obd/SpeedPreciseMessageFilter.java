package org.hexpresso.soulevspy.obd;

import org.hexpresso.obd.ObdMessageData;
import org.hexpresso.obd.ObdMessageFilter;
import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.ArrayList;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-13.
 */
public class SpeedPreciseMessageFilter extends ObdMessageFilter {

    double mSpeedKmH = 0.0;

    public SpeedPreciseMessageFilter() {
        super("4F2");
    }

    protected boolean doProcessMessage(ObdMessageData messageData) {
        ArrayList<String> data = messageData.getData();
        if (data.size() != 8) {
            return false;
        }

        mSpeedKmH = (messageData.getDataByte(1) /* Speed was 128 when parked?!?: | ((messageData.getDataByte(2) & 0x80) << 1)*/) / 2.0;

        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        vals.set(vals.getPreferences().getContext().getString(R.string.col_car_speed_kph), mSpeedKmH);

        return true;
    }

    public double getSpeedInKmH() {
        return mSpeedKmH;
    }
}
