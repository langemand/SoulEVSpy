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
        Double aux_V = (Double)mValues.get("log.aux_volts_V");
        Double aux_A = (Double)mValues.get("log.aux_amps_A");
        String aux_W = "undefined";
        if (aux_A != null && aux_V != null) {
            aux_W = new Double(aux_A * aux_V).toString();
        }
        mItems.add(new ListViewItem("AUX Voltage, V", aux_V != null ? new String(aux_V.toString()) : "undefined"));
        mItems.add(new ListViewItem("AUX Current, A", aux_A != null ? new String(aux_A.toString()) : "undefined"));
        mItems.add(new ListViewItem("AUX Power, W", aux_W));
        Map<String, Object> kvals = mValues.find("ldc.");
        SortedSet<String> keyset = new TreeSet<String>(kvals.keySet());
        mItems.clear();
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
