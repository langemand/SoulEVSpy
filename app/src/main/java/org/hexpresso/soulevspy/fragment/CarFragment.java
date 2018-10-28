package org.hexpresso.soulevspy.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;

import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.activity.MainActivity;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;
import org.hexpresso.soulevspy.util.KiaVinParser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-07.
 */
public class CarFragment extends ListFragment implements CurrentValuesSingleton.CurrentValueListener {

    private List<ListViewItem> mItems = new ArrayList<>();
    private CurrentValuesSingleton mValues = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.action_car_information);

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
        Object SOH_pct = mValues.get(R.string.col_calc_battery_soh_pct);
        if (SOH_pct != null) {
            mItems.add(new ListViewItem("Battery SOH %", new DecimalFormat("0.0").format(SOH_pct)));
        }
        Object DC_V = mValues.get(R.string.col_ELM327_voltage);
        if (DC_V != null) {
            mItems.add(new ListViewItem("12V", new String(DC_V.toString())));
        }
        Object amb = mValues.get(R.string.col_car_ambient_C);
        if (amb != null) {
            mItems.add(new ListViewItem("Ambient Temperature", amb.toString()));
        }
        Object vin_str = mValues.get(R.string.col_VIN);
        if (vin_str != null) {
            KiaVinParser vin = new KiaVinParser(getContext(), vin_str.toString()); //"KNDJX3AEXG7123456");
            String str = vin.getVIN();
            mItems.add(new ListViewItem("Vehicle Identification Number", str));
            if (str != "error") {
                mItems.add(new ListViewItem("Brand", vin.getBrand()));
                mItems.add(new ListViewItem("Model", vin.getModel()));
                mItems.add(new ListViewItem("Trim", vin.getTrim()));
                mItems.add(new ListViewItem("Engine", vin.getEngine()));
                mItems.add(new ListViewItem("Year", vin.getYear()));
                mItems.add(new ListViewItem("Sequential Number", vin.getSequentialNumber()));
                mItems.add(new ListViewItem("Production Plant", vin.getProductionPlant()));
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
/*
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        ListViewItem item = mItems.get(position);

        // do something
        Toast.makeText(getActivity(), item.title, Toast.LENGTH_SHORT).show();
    }
*/
}

