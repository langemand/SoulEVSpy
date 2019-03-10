package org.hexpresso.soulevspy.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v7.util.SortedList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hexpresso.obd.ObdMessageFilter;
import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.activity.MainActivity;
import org.hexpresso.soulevspy.obd.StateOfChargePreciseMessageFilter;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-01.
 */
public class BatteryFragment extends ListFragment implements CurrentValuesSingleton.CurrentValueListener {
    private ListViewAdapter mListViewAdapter = null;
    private List<ListViewItem> mItems = new ArrayList<>();
    private List<ListViewItem> mListItems = new ArrayList<>();
    private CurrentValuesSingleton mValues = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle(R.string.action_battery);

        mValues = CurrentValuesSingleton.getInstance();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            mListViewAdapter = new ListViewAdapter(getActivity(), mListItems);
            // initialize the list adapter
            ((MainActivity) mValues.getPreferences().getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setListAdapter(mListViewAdapter);
                }
            });
            onValueChanged(null, null);
            mValues.addListener(mValues.getPreferences().getContext().getResources().getString(R.string.col_system_scan_end_time_ms), this);
        }
    }

    @Override
    public void onDestroy() {
        mValues.delListener(this);
        super.onDestroy();
    }

    public void onValueChanged(String trig_key, Object value) {
        Map<String, Object> battVals = mValues.find("battery.");
        SortedSet<String> keyset = new TreeSet<String>(battVals.keySet());
        mItems.clear();
        for (String key : keyset) {
            if (key.startsWith(mValues.getPreferences().getContext().getResources().getString(R.string.col_battery_cell_voltage)) ||
                key.startsWith(mValues.getPreferences().getContext().getResources().getString(R.string.col_battery_module_temperature))) {
                continue;
            }
            Object val = battVals.get(key);
            if (val != null) {
                if (val instanceof Double) {
                    mItems.add(new ListViewItem(key, new DecimalFormat("0.00####").format(val)));
                } else {
                    mItems.add(new ListViewItem(key, val.toString()));
                }
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
