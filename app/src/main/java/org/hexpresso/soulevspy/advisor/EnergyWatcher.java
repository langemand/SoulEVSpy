package org.hexpresso.soulevspy.advisor;

import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.HashMap;

public class EnergyWatcher implements CurrentValuesSingleton.CurrentValueListener  {
    private CurrentValuesSingleton mValues = null;
    private HashMap<Double, Nugget> mNuggets = new HashMap<Double, Nugget>();
    private long mInterval = 59;
    private long mLatestTime = 0;
    private double mGpsDist = 0;
    private Pos mLastPos = new Pos(0.0,0.0);

    public EnergyWatcher() {
        mValues = CurrentValuesSingleton.getInstance();
        String key = mValues.getPreferences().getContext().getResources().getString(R.string.col_system_scan_end_time_ms);
        mValues.addListener(key, this);
    }

    public void finalize() {
        Close();
    }

    public void Close() {
        mValues.delListener(this);
    }

    @Override
    public void onValueChanged(String key, Object value) {
        long time = (Long)mValues.get(R.string.col_system_scan_start_time_ms);
        Pos pos = new Pos((Double)mValues.get(R.string.col_route_lat_deg), (Double)mValues.get(R.string.col_route_lng_deg));
        if (mLastPos.isDefined() && pos.isDefined())
        {
            mGpsDist = mGpsDist + pos.distance(mLastPos);
        }
        if (time > mLatestTime + mInterval) {
            Nugget nugget = new Nugget(time,
                    (Double)mValues.get(R.string.col_car_odo_km),
                    (Double)mValues.get(R.string.col_battery_precise_SOC),
                    (Double)mValues.get(R.string.col_car_speed_kph),
                    mGpsDist);
            mNuggets.put(nugget.getTime_s(), nugget);
            mLatestTime = time;
        }
        mLastPos = pos;
    }
}
