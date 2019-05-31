package com.evranger.soulevspy.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;

import com.evranger.soulevspy.activity.MainActivity;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.Unit;

import com.evranger.soulevspy.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Henrik Reichhardt Scheel <henrik.scheel@spjeldager.dk> on 2019-05-08.
 */
public class VmcuFragment extends ListFragment implements CurrentValuesSingleton.CurrentValueListener {
    private ListViewAdapter mListViewAdapter = null;
    private List<ListViewItem> mItems = new ArrayList<>();
    private List<ListViewItem> mListItems = new ArrayList<>();
    private CurrentValuesSingleton mValues = null;
    private Unit unit = new Unit();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle(R.string.action_vmcu_information);

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
        Map<String, Object> vmcuVals = mValues.find("vmcu.");
        SortedSet<String> keyset = new TreeSet<String>(vmcuVals.keySet());
        mItems.clear();
        for (String key : keyset) {
            Object val = vmcuVals.get(key);
            if (val != null) {
                if (val instanceof Integer) {
                    val = new Double((int)val);
                }
                if (val instanceof Double) {
                    if (key.endsWith("_C")) {
                        mItems.add(new ListViewItem(key.substring(0, key.length() - 2), new DecimalFormat("0.#").format(unit.convertTemp((double) val)) + " " + unit.mTempUnit));
                    } else if (key.endsWith("_kph")) {
                        mItems.add(new ListViewItem(key.substring(0,key.length()-4), new DecimalFormat("0.#").format(unit.convertDist((double)val)) + " " + unit.mDistUnit+"/h"));
                    } else {
                        mItems.add(new ListViewItem(key, new DecimalFormat("0.######").format(val)));
                    }
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
