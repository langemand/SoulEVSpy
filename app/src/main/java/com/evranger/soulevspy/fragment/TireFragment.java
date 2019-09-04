package com.evranger.soulevspy.fragment;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.ListFragment;
import android.os.Bundle;

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
 * Created by henrik on 16/12/2017.
 */

public class TireFragment extends ListFragment implements CurrentValuesSingleton.CurrentValueListener {
    private ListViewAdapter mListViewAdapter = null;
    private List<ListViewItem> mItems = new ArrayList<>();
    private List<ListViewItem> mListItems = new ArrayList<>();
    private CurrentValuesSingleton mValues = null;
    Unit unit = new Unit();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.action_tires);

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
            mValues.addListener(mValues.getPreferences().getContext().getResources().getString(R.string.col_system_scan_end_time_ms), this);
        }
    }

    @Override
    public void onDestroy() {
        mValues.delListener(this);
        super.onDestroy();
    }

    public void onValueChanged(String trig_key, Object value) {
        Map<String, Object> kvals = mValues.find("tire.");
        SortedSet<String> keyset = new TreeSet<String>(kvals.keySet());
        mItems.clear();
        for (String key : keyset) {
            Object obj = kvals.get(key);
            if (obj != null) {
                if (key.contains("temperature")) {
                    mItems.add(new ListViewItem(key.substring(0, key.length() - 2), new DecimalFormat("0.0").format(unit.convertTemp((int)obj)) + " " + unit.mTempUnit));
                } else {
                    mItems.add(new ListViewItem(key, new DecimalFormat("0.0").format(unit.convertPres((double)obj)) + " " + unit.mPresUnit));
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
