package org.hexpresso.soulevspy.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.Toast;

import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.activity.MainActivity;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by henrik on 29/06/2017.
 */

public class AdvisoryFragment extends ListFragment implements CurrentValuesSingleton.CurrentValueListener {
    private List<ListViewItem> mItems = new ArrayList<>();
    private CurrentValuesSingleton mValues = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.action_advisor);

        mValues = CurrentValuesSingleton.getInstance();
        mValues.addListener(mValues.getPreferences().getContext().getResources().getString(R.string.col_system_scan_end_time_ms), this);
        onValueChanged(null, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mValues.delListener(this);

    }

    public void onValueChanged(String trig_key, Object value) {
        Map<String, Object> kvals = mValues.find("log.");
        SortedSet<String> keyset = new TreeSet<String>(kvals.keySet());

        mItems.clear();
        for (String key : keyset) {
            mItems.add(new ListViewItem(key, new String(kvals.get(key).toString())));
        }
        // initialize and set the list adapter
        ((MainActivity)mValues.getPreferences().getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setListAdapter(new ListViewAdapter(getActivity(), mItems));
            }
        });
    }
}
