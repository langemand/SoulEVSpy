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

public class AdvisoryFragment extends ListFragment implements CurrentValuesSingleton.CurrentValueListener {
    private List<ListViewItem> mItems = new ArrayList<>();
    private CurrentValuesSingleton mValues = null;
    private ChargeStations mChargeStations;
    private ArrayList<ChargeLocation> nearbyChargers;
    private Pos lastLookupPos = new Pos(0.0, 0.0);

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
        mValues.delListener(this);
        super.onDestroy();
    }

    public void onValueChanged(String trig_key, Object value) {
        mItems.clear();
//        Object speed = mValues.get(R.string.col_car_speed_kph);
//        if (speed != null) {
//            mItems.add(new ListViewItem("Current Speed (km/h)", new String(speed.toString())));
//        }
//
//        Object battery_precise_SOC = mValues.get(R.string.col_battery_precise_SOC);
//        if (battery_precise_SOC != null) {
//            mItems.add(new ListViewItem("Precise SOC (%)", new DecimalFormat("0.00").format(battery_precise_SOC)));
//        }
//
//        Double amps = (Double)mValues.get(R.string.col_battery_DC_current_A);
//        Double volts = (Double)mValues.get(R.string.col_battery_DC_voltage_V);
//        if (amps != null && volts != null) {
//            double battery_watts = amps * volts;
//            mItems.add(new ListViewItem("Current energy (kW)", new DecimalFormat("0.0").format(battery_watts / 1000)));
//        }

        Double remainingRange = (Double) mValues.get("range_estimate_km");
        if (remainingRange != null) {
            mItems.add(new ListViewItem("Car estimated remaining range (km)", new DecimalFormat("0.0").format(remainingRange)));
        }

        mItems.add(new ListViewItem("Nearest Quick-chargers", ((Integer)nearbyChargers.size()).toString() + " Chademo chargers in remaining range"));

        Object obj = mValues.get(R.string.col_chargers_locations);
        if (obj != null) {
            ArrayList<ChargeLocation> nearChargers = (ArrayList<ChargeLocation>) obj;
            // Sort by distance
            Collections.sort(nearChargers, new ChargeLocationComparator());
            // Display nearest 10
            for (int i = 0; i < Math.min(10, nearChargers.size()); ++i) {
                ChargeLocation charger = nearChargers.get(i);
                double dist_deca_m = Math.round(charger.get_distFromLookupPos()/100);
                Double dist_km = dist_deca_m / 10;
                boolean verified = false;
                try {
                    verified = (boolean) charger.get_origJson().get("verified");
                } catch(Exception ex) {
                    //ignore
                }
                String infoStr = "Straight distance: " + dist_km.toString() + " km. " + (verified ? "Verified" : "");
                mItems.add(new ListViewItem(charger.get_readableName(), infoStr));
            }
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
