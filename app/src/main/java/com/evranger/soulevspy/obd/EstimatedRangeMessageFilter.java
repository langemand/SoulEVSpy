package com.evranger.soulevspy.obd;

import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.obd.ObdMessageData;
import com.evranger.obd.ObdMessageFilter;
import com.evranger.soulevspy.R;

import java.util.ArrayList;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-13.
 * CAN Message ID 0x200
 */
public class EstimatedRangeMessageFilter extends ObdMessageFilter {

    private int mEstimatedRangeKm = 0;
    private double mAdditionalRangeWithClimateOffKm = 0;

    public EstimatedRangeMessageFilter() {
        super("200");
    }

    @Override
    protected boolean doProcessMessage(ObdMessageData messageData) {
        ArrayList<String> data = messageData.getData();
        if ( data.size() != 8 )
        {
            return false;
        }

        // High Rate Byte 5 high nybble counts 3,7,B,F;
        // Byte 7 high nybble counts 2,6,A,E, or other values remaining km =9bit (no offset)
        mEstimatedRangeKm = ( messageData.getDataByte(2) << 1 )+
                            ( messageData.getDataByte(1) >> 7 );

        mAdditionalRangeWithClimateOffKm = messageData.getDataByte(0) / 10.0;

        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        vals.set(vals.getPreferences().getContext().getString(R.string.col_range_estimate_km), mEstimatedRangeKm);
        vals.set(vals.getPreferences().getContext().getString(R.string.col_range_estimate_for_climate_km), mAdditionalRangeWithClimateOffKm);

        return true;
    }

    public int getEstimatedRangeKm() {
        return mEstimatedRangeKm;
    }
    public double getAdditionalRangeWithClimateOffKm() {
        return mAdditionalRangeWithClimateOffKm;
    }
}