package org.hexpresso.soulevspy.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;

import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.activity.MainActivity;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by henrik on 29/06/2017.
 */

public class EnergyFragment extends ListFragment implements CurrentValuesSingleton.CurrentValueListener {
    private ListViewAdapter mListViewAdapter = null;
    private List<ListViewItem> mItems = new ArrayList<>();
    private List<ListViewItem> mListItems = new ArrayList<>();
    private CurrentValuesSingleton mValues = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.action_energy);

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
            mValues.addListener(mValues.getPreferences().getContext().getString(R.string.col_watcher_consumption)+"_done_time_ms", this);
//            mValues.addListener(mValues.getPreferences().getContext().getResources().getString(R.string.col_system_scan_end_time_ms), this);
        }
    }

    @Override
    public void onDestroy() {
        mValues.delListener(this);
        super.onDestroy();
    }

    public void onValueChanged(String trig_key, Object value) {
        mItems.clear();
        Object battery_display_SOC = mValues.get(R.string.col_battery_display_SOC);
        Object battery_decimal_SOC = mValues.get(R.string.col_battery_decimal_SOC);
        Object battery_precise_SOC = mValues.get(R.string.col_battery_precise_SOC);
        if (battery_display_SOC != null && battery_decimal_SOC != null && battery_precise_SOC != null) {
            mItems.add(new ListViewItem("Battery SOC (%) disp / actual / prec",
                    new DecimalFormat("0.0").format(battery_display_SOC).concat(" / ").concat(
                            new DecimalFormat("0.0").format(battery_decimal_SOC)).concat(" / ").concat(
                                    new DecimalFormat("0.00").format(battery_precise_SOC).concat(" %")
                    )));
        }

        Double amps = (Double)mValues.get(R.string.col_battery_DC_current_A);
        Double volts = (Double)mValues.get(R.string.col_battery_DC_voltage_V);
        if (amps != null && volts != null) {
            double battery_watts = amps * volts;
            mItems.add(new ListViewItem("Battery energy (kW) / (V) / (A)",
                    new DecimalFormat("0.0").format(battery_watts / 1000).concat(" kW / ").concat(
                            new DecimalFormat("0.0").format(volts)).concat(" V / ").concat(
                                    new DecimalFormat("0.0").format(amps)).concat(" A")));
        }

        Object remainingRange = mValues.get(R.string.col_range_estimate_km);
        if (remainingRange != null) {
            mItems.add(new ListViewItem("Car estimated remaining range (km)", remainingRange.toString()));
        }

        Map<String, Object> consumptions = mValues.find(mValues.getPreferences().getContext().getString(R.string.col_watcher_consumption));
        TreeSet<String> ranges = new TreeSet<String>();
        ranges.addAll(consumptions.keySet());
        for(String key : ranges) {
            Object val = mValues.get(key);
            if (val instanceof Double) {
                String header = key.replace(mValues.getPreferences().getContext().getString(R.string.col_watcher_consumption).concat("_"), "last ").replace(
                        "_WhPerkm", " kms");
                while (header.charAt(0) == '0') {
                    header = header.substring(1);
                }
                mItems.add(new ListViewItem(header, new DecimalFormat("0.0").format((Double) mValues.get(key)).concat(" Wh/km")));
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
