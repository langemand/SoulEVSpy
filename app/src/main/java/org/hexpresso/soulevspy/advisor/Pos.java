package org.hexpresso.soulevspy.advisor;

public class Pos {
    public double mLat = 0;
    public double mLng = 0;

    public Pos(Double lat, Double lng) {
        if (lat != null && lng != null) {
            mLat = lat;
            mLng = lng;
        }
    }

    public boolean isDefined() {
        return (mLat != 0 && mLng != 0);
    }

    public double distance(Pos pos) {
        double r = 6371e3; // metres
        double φ1 = Math.toRadians(mLat);
        double φ2 = Math.toRadians(pos.mLat);
        double Δφ = Math.toRadians(pos.mLat-mLat);
        double Δλ = Math.toRadians(pos.mLng-mLng);

        double a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
                Math.cos(φ1) * Math.cos(φ2) *
                        Math.sin(Δλ/2) * Math.sin(Δλ/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double d = r * c;
        return d;
    }
}
