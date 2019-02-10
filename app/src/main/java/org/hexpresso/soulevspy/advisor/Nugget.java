package org.hexpresso.soulevspy.advisor;

public class Nugget {

    private double mTime_s;
    private double mOdo_m;
    private double mSoc_pct;
    private double mSpeed_mps;
    private double mGpsDist_m;

    public Nugget(long time, double odo_m, double soc, double speed, double dist) {
        mTime_s = time / 1000.0;
        mOdo_m = odo_m;
        mSoc_pct = soc;
        mSpeed_mps = speed * 1000.0 / 3600.0;
        mGpsDist_m = dist;
    }

    public double getTime_s() {
        return mTime_s;
    }

    public double getOdo_m() {
        return mOdo_m;
    }

    public double getSoc_pct() {
        return mSoc_pct;
    }

    public double getSpeed_mps() {
        return mSpeed_mps;
    }

    public double getmGpsDist_m() {
        return mGpsDist_m;
    }
}
