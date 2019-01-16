package org.hexpresso.soulevspy.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.widget.Toast;

import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.activity.MainActivity;
import org.hexpresso.soulevspy.advisor.ChargeLocation;
import org.hexpresso.soulevspy.advisor.ChargeLocationComparator;
import org.hexpresso.soulevspy.advisor.ChargeStations;
import org.hexpresso.soulevspy.advisor.Pos;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by henrik on 29/06/2017.
 */

public class EnergyFragment extends ListFragment implements CurrentValuesSingleton.CurrentValueListener {
    private List<ListViewItem> mItems = new ArrayList<>();
    private CurrentValuesSingleton mValues = null;
    private ChargeStations mChargeStations;
    private Pos lastLookupPos = new Pos(0.0, 0.0);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.action_energy);

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
        mItems.clear();

        Object battery_display_SOC = mValues.get(R.string.col_battery_display_SOC);
        if (battery_display_SOC != null) {
            mItems.add(new ListViewItem("Display SOC of avail. capacity (%)", new DecimalFormat("0.0").format(battery_display_SOC)));
        }

        Object battery_decimal_SOC = mValues.get(R.string.col_battery_decimal_SOC);
        if (battery_decimal_SOC != null) {
            mItems.add(new ListViewItem("Actual SOC of original capacity (%)", new DecimalFormat("0.0").format(battery_decimal_SOC)));
        }

        Double amps = (Double)mValues.get(R.string.col_battery_DC_current_A);
        Double volts = (Double)mValues.get(R.string.col_battery_DC_voltage_V);
        if (amps != null && volts != null) {
            double battery_watts = amps * volts;
            mItems.add(new ListViewItem("Battery energy (kW)", new DecimalFormat("0.0").format(battery_watts / 1000)));
            mItems.add(new ListViewItem("Battery voltage (V)", new DecimalFormat("0.0").format(volts)));
            mItems.add(new ListViewItem("Battery current (A)", new DecimalFormat("0.0").format(amps)));
        }

        Double remainingRange = (Double) mValues.get("range_estimate_km");
        if (remainingRange != null) {
            mItems.add(new ListViewItem("Car estimated remaining range (km)", new DecimalFormat("0.0").format(remainingRange)));
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
