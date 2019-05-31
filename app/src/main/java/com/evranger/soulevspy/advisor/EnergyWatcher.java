package com.evranger.soulevspy.advisor;

import com.evranger.soulevspy.R;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class EnergyWatcher implements CurrentValuesSingleton.CurrentValueListener  {
    private CurrentValuesSingleton mValues = null;
    private List<Nugget> mNuggets = new ArrayList<Nugget>();
    private long mInterval = 59999;
    private long mLatestTime = -mInterval - 1;
    private double mGpsDist = 0;
    private Pos mLastPos = new Pos(0.0,0.0);
    private Nugget mLastNugget;
    private Set<Long> milestones = new TreeSet<Long>(new Comparator<Long>() {
        @Override
        public int compare(Long o1, Long o2) {
            return o2.compareTo(o1);
        }
    });
    private Long mMinMilestone = null;

    public EnergyWatcher() {
        mValues = CurrentValuesSingleton.getInstance();
        String key = mValues.getPreferences().getContext().getResources().getString(R.string.col_system_scan_end_time_ms);
        mValues.addListener(key, this);
        milestones.add(1000L);
        milestones.add(2000L);
        milestones.add(5000L);
        milestones.add(10000L);
        milestones.add(20000L);
        milestones.add(50000L);
        mMinMilestone = 100L;
    }

    public void finalize() {
        Close();
    }

    public void Close() {
        mValues.delListener(this);
    }

    @Override
    public void onValueChanged(String key, Object value) {
        if (mValues.get(R.string.col_system_scan_start_time_ms) == null ||
            mValues.get(R.string.col_car_odo_km) == null ||
            mValues.get(R.string.col_battery_precise_SOC) == null ||
            mValues.get(R.string.col_car_speed_kph) == null) {
            return;
        }
        long time = (Long)mValues.get(R.string.col_system_scan_start_time_ms);
        Pos pos = new Pos((Double)mValues.get(R.string.col_route_lat_deg), (Double)mValues.get(R.string.col_route_lng_deg));
        if (mLastPos.isDefined() && pos.isDefined())
        {
            mGpsDist = mGpsDist + pos.distance(mLastPos);
        }
        double currOdo_m = (Double)mValues.get(R.string.col_car_odo_km) * 1000;
        if (mLastNugget == null || time > (mLastNugget.getTime_s() * 1000 + mInterval) ||
                (currOdo_m - mLastNugget.getOdo_m()) >= mMinMilestone ||
                mGpsDist >= mMinMilestone) {
            Nugget nugget = new Nugget(time,
                    currOdo_m,
                    (Double)mValues.get(R.string.col_battery_precise_SOC),
                    (Double)mValues.get(R.string.col_car_speed_kph),
                    mGpsDist);
            mLatestTime = time;
            mNuggets.add(nugget);
            mLastNugget = nugget;

            calculateMeans();
        }
        mLastPos = pos;
        mValues.set(mValues.getPreferences().getContext().getString(R.string.col_watcher_consumption)+"_done_time_ms", System.currentTimeMillis());
    }

    public void calculateMeans() {
        Object obj = mValues.get(R.string.col_orig_capacity_kWh);
        if (obj != null && obj instanceof Double) {
            Double nomCap = (Double)obj;
            Double totCap = nomCap + 1.2;
            Map<Long, Double> milestoneConsumptions = new HashMap<Long, Double>();
            double currSoc = mLastNugget.getSoc_pct();
            Iterator miter = milestones.iterator();
            Long m = (Long)miter.next();
            boolean done = false;
            for (int i = 0; i < mNuggets.size() && !done; ++i) {
                Nugget nugget = mNuggets.get(i);
                double gpsDist = 0.0;
                if (nugget.getmGpsDist_m() > 0) {
                    gpsDist = mLastNugget.getmGpsDist_m() - nugget.getmGpsDist_m();
                }
                double odoDist = mLastNugget.getOdo_m() - nugget.getOdo_m();
                double dist = Math.max(odoDist, gpsDist);
                if (odoDist > 0.0) {
                    while (odoDist <= m || (gpsDist > 0 && gpsDist <= m)) {
                        double consumption = (nugget.getSoc_pct() - currSoc) / dist;
                        milestoneConsumptions.put(m, consumption);
                        if (!miter.hasNext()) {
                            done = true;
                            break;
                        }
                        m = (Long)miter.next();
                    }
                }
            }
            for (Long key : milestoneConsumptions.keySet()) {
                double pctPerMeter = milestoneConsumptions.get(key);
                double kilowattHoursPerm = pctPerMeter / 100 * totCap;
                Double wattHoursPerkm = kilowattHoursPerm * 1000 * 1000;
                mValues.set(mValues.getPreferences().getContext().getString(R.string.col_watcher_consumption)+"_"+new DecimalFormat("00").format(key/1000.0)+"_WhPerkm", wattHoursPerkm);
            }
        }
    }
}
