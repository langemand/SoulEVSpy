package org.hexpresso.soulevspy.fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.os.Bundle;

import org.apache.commons.lang3.ObjectUtils;
import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.activity.MainActivity;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by henrik on 30/06/2017.
 */

public class LdcFragment extends ListFragment implements CurrentValuesSingleton.CurrentValueListener {
    private List<ListViewItem> mItems = new ArrayList<>();
    private CurrentValuesSingleton mValues = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.action_ldc);

        mValues = CurrentValuesSingleton.getInstance();
        mValues.addListener(mValues.getPreferences().getContext().getResources().getString(R.string.col_system_scan_end_time_ms), this);
        onValueChanged(null, null);
    }

    @Override
    public void onDestroy() {
        mValues.delListener(this);
        super.onDestroy();
    }

    public void onValueChanged(String trig_key, Object value) {
        Map<String, Object> kvals = mValues.find("ldc.");
        SortedSet<String> keyset = new TreeSet<String>(kvals.keySet());
        Object DC_V = mValues.get(R.string.col_ELM327_voltage);
        mItems.clear();
        if (DC_V != null) {
            mItems.add(new ListViewItem("12V", new String(DC_V.toString())));
        }
        for (String key : keyset) {
            mItems.add(new ListViewItem(key, new String(kvals.get(key).toString())));
        }
        // initialize and set the list adapter
        ((MainActivity)mValues.getPreferences().getContext()).runOnUiThread(new Runnable() {
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
