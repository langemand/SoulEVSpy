package org.hexpresso.soulevspy.io;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

/**
 * Created by henrik on 09/06/2017.
 */

public class Position implements LocationListener {
    public Position(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc != null) {
            updateLocation(loc);
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, this);
    }

    private Position() {};

    private void updateLocation(Location loc) {
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        Resources res = vals.getPreferences().getContext().getResources();
        vals.set(res.getString(R.string.col_route_lat_deg), Double.valueOf(loc.getLatitude()));
        vals.set(res.getString(R.string.col_route_lng_deg), Double.valueOf(loc.getLongitude()));
        vals.set(res.getString(R.string.col_route_elevation_m), Double.valueOf(loc.getAltitude()));
        vals.set(res.getString(R.string.col_route_time_s), Long.valueOf(loc.getTime()));
        vals.set(res.getString(R.string.col_route_speed_mps), loc.getSpeed());
    }

    @Override
    public void onLocationChanged(Location loc) {
        updateLocation(loc);
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
