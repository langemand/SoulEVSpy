package com.evranger.soulevspy.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;

import com.evranger.soulevspy.R;
import com.evranger.soulevspy.activity.MainActivity;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.Unit;

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
    Unit unit = new Unit();

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
            mValues.addListener(mValues.getPreferences().getContext().getResources().getString(R.string.col_system_scan_end_time_ms), this);
        }
    }

    @Override
    public void onDestroy() {
        mValues.delListener(this);
        super.onDestroy();
    }

    public void onValueChanged(String trig_key, Object value) {
        mItems.clear();
        int speedCount=0;
        Object carSpeed = mValues.get(R.string.col_car_speed_kph);
        StringBuilder speedHeader = new StringBuilder();
        speedHeader.append("Speed ("+unit.mDistUnit+"/h)");
        StringBuilder speedValue = new StringBuilder();
        if (carSpeed != null) {
            speedHeader.append(" car");
            speedValue.append(new DecimalFormat("0.0").format(unit.convertDist((double)carSpeed)));
            ++speedCount;
        }
        Object gpsSpeedObj = mValues.get(R.string.col_route_speed_mps);
        if (gpsSpeedObj != null) {
            double gpsSpeedKmh = Double.valueOf(gpsSpeedObj.toString()) * 3.6;
            if (speedCount > 0) {
                speedHeader.append(" /");
                speedValue.append(" / ");
            }
            speedHeader.append(" gps");
            speedValue.append(new DecimalFormat("0.0").format(unit.convertDist(gpsSpeedKmh)) + " " + unit.mDistUnit + "/h");
            ++speedCount;
        }
        if (speedCount > 0) {
            mItems.add(new ListViewItem(speedHeader.toString(), speedValue.toString()));
        }

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
        Object extraWithClimateOff = mValues.get(R.string.col_range_estimate_for_climate_km);
        if (remainingRange != null && extraWithClimateOff != null) {
            mItems.add(new ListViewItem("Car estimated remaining range ("+unit.mDistUnit+")",
                    new DecimalFormat("0.0").format(unit.convertDist((int)remainingRange))+" "+unit.mDistUnit+
                            ", AC off extra: " + new DecimalFormat("0.0").format(unit.convertDist((double)extraWithClimateOff)) + " "+unit.mDistUnit));
        }

        Map<String, Object> consumptions = mValues.find(mValues.getPreferences().getContext().getString(R.string.col_watcher_consumption));
        TreeSet<String> ranges = new TreeSet<String>();
        ranges.addAll(consumptions.keySet());
        for(String key : ranges) {
            Object val = mValues.get(key);
            if (val instanceof Double) {
                String header = key.replace(mValues.getPreferences().getContext().getString(R.string.col_watcher_consumption).concat("_"), "").replace(
                        "_WhPerkm", " kms");
                while (header.charAt(0) == '0') {
                    header = header.substring(1);
                }
                mItems.add(new ListViewItem("last " + header, new DecimalFormat("0.0").format(unit.convertConsumption((double)mValues.get(key))) + " " + unit.mConsumptionUnit));
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
