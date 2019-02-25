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
    private ListViewAdapter mListViewAdapter = null;
    private List<ListViewItem> mItems = new ArrayList<>();
    private List<ListViewItem> mListItems = new ArrayList<>();
    private CurrentValuesSingleton mValues = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("GPS");
        mValues = CurrentValuesSingleton.getInstance();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            // initialize the list adapter
            mListViewAdapter = new ListViewAdapter(getActivity(), mListItems);
            ((MainActivity) mValues.getPreferences().getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setListAdapter(mListViewAdapter);
                }
            });
            onValueChanged(null, null);
            mValues.addListener(mValues.getPreferences().getContext().getResources().getString(R.string.col_route_time_s), this);
        }
    }

    @Override
    public void onDestroy() {
        mValues.delListener(this);
        super.onDestroy();
    }

    public void onValueChanged(String key, Object value) {
        Resources res = mValues.getPreferences().getContext().getResources();
        Object lat = mValues.get(res.getString(R.string.col_route_lat_deg));
        Object lng = mValues.get(res.getString(R.string.col_route_lng_deg));
        Object alt = mValues.get(res.getString(R.string.col_route_elevation_m));
        Object spd = mValues.get(res.getString(R.string.col_route_speed_mps));
        Object tim = mValues.get(res.getString(R.string.col_route_time_s));
        mItems.clear();
        if (lat != null && lng != null && alt != null && tim != null && spd != null) {
            mItems.add(new ListViewItem("Lattitude (deg)", lat.toString()));
            mItems.add(new ListViewItem("Longtitude (deg)", lng.toString()));
            mItems.add(new ListViewItem("Altitude (m)", alt.toString()));
            mItems.add(new ListViewItem("Speed (m/s)", spd.toString()));
            Long utim = (Long) tim;
            if (utim != null) {
                DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date(utim);
                String formatted = format.format(date);
                mItems.add(new ListViewItem("Time", formatted));
            }
        }

        // update the list adapter display
        ((MainActivity) mValues.getPreferences().getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListItems.clear();
                mListItems.addAll(mItems);
                mListViewAdapter.notifyDataSetChanged();
            }
        });
    }
}
