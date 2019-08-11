package com.evranger.soulevspy.io;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.evranger.soulevspy.R;
import com.evranger.soulevspy.activity.MainActivity;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;

/**
 * Created by henrik on 09/06/2017.
 */

public class Position implements LocationListener {
    LocationManager locationManager = null;
    MainActivity mActivity = null;
    Context context;
    boolean mListening = true;

    public Position(MainActivity activity) {
        mActivity = activity;
        this.context = activity.getBaseContext();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        listen(true);
    }

    public void listen(boolean doListen) {
        mListening = doListen;
        if (mListening) {
            updateIfListening();
        } else {
            locationManager.removeUpdates(this);
        }
    }

    public void updateIfListening() {
        if (mListening) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                updateLocation(loc);
            }
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 3000, 10, this);
            }
        }
    }


    private Position() {};

    private void updateLocation(Location loc) {
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        Resources res = vals.getPreferences().getContext().getResources();
        vals.set(res.getString(R.string.col_route_lat_deg), Double.valueOf(loc.getLatitude()));
        vals.set(res.getString(R.string.col_route_lng_deg), Double.valueOf(loc.getLongitude()));
        vals.set(res.getString(R.string.col_route_elevation_m), Double.valueOf(loc.getAltitude()));
        vals.set(res.getString(R.string.col_route_time_s), Long.valueOf(loc.getTime()));  //DL
        vals.set(res.getString(R.string.col_route_speed_mps), loc.getSpeed());
    }

    @Override
    public void onLocationChanged(Location loc) {
        if (mListening) {
            updateLocation(loc);  //DL
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
