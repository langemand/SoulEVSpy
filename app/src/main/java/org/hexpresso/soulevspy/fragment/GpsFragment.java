package org.hexpresso.soulevspy.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;

import org.hexpresso.obd.ObdMessageFilter;
import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.activity.MainActivity;
import org.hexpresso.soulevspy.obd.StateOfChargePreciseMessageFilter;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * Created by henrik on 09/06/2017.
 */

public class GpsFragment extends ListFragment implements CurrentValuesSingleton.CurrentValueListener {

    private List<ListViewItem> mItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("GPS");
//        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
//        Map<String, Object> routeVals = vals.find("route.");
//        SortedSet<String> keyset = new TreeSet<String>(routeVals.keySet());
//
//        for (String key : keyset) {
//            vals.addListener(key, this);
//        }
        onValueChanged(null, null);
    }

    @Override
    public void onDestroy() {
        CurrentValuesSingleton.getInstance().delListener(this);
        super.onDestroy();
    }

    public void onValueChanged(String key, Object value) {
        CurrentValuesSingleton cur = CurrentValuesSingleton.getInstance();
        Resources res = cur.getPreferences().getContext().getResources();
        Object lat = cur.get(res.getString(R.string.col_route_lat_deg));
        Object lng = cur.get(res.getString(R.string.col_route_lng_deg));
        Object alt = cur.get(res.getString(R.string.col_route_elevation_m));
        Object spd = cur.get(res.getString(R.string.col_route_speed_mps));
        Object tim = cur.get(res.getString(R.string.col_route_time_s));
        Object amb = cur.get(res.getString(R.string.col_car_ambient_C));
        mItems.clear();
        if (amb != null) {
            mItems.add(new ListViewItem("Temperature", amb.toString()));
        }
        if (lat != null && lng != null && alt != null && tim != null && spd != null) {
            mItems.add(new ListViewItem("Lattitude", lat.toString()));
            mItems.add(new ListViewItem("Longtitude", lng.toString()));
            mItems.add(new ListViewItem("Altitude", alt.toString()));
            mItems.add(new ListViewItem("Speed", spd.toString()));
            Long utim = (Long) tim;
            if (utim != null) {
                DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date(utim);
                String formatted = format.format(date);
                mItems.add(new ListViewItem("Time", formatted));
            }
        }
        // initialize and set the list adapter
        ((MainActivity)cur.getPreferences().getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    setListAdapter(new ListViewAdapter(activity, mItems));
                }
            }
        });
    }
}
