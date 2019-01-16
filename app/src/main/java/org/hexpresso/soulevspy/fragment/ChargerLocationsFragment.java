package org.hexpresso.soulevspy.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
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
import java.util.List;

/**
 * Created by henrik on 29/06/2017.
 */

public class ChargerLocationsFragment extends ListFragment implements CurrentValuesSingleton.CurrentValueListener {
    private List<ListViewItem> mItems = new ArrayList<>();
    private CurrentValuesSingleton mValues = null;
    private ChargeStations mChargeStations;
    private ArrayList<ChargeLocation> nearChargers;
    private Pos lastLookupPos = new Pos(0.0, 0.0);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.action_charger_locations);

        mValues = CurrentValuesSingleton.getInstance();
        mValues.addListener(mValues.getPreferences().getContext().getResources().getString(R.string.col_chargers_locations), this);
        onValueChanged(null, null);
    }

    @Override
    public void onDestroy() {
        mValues.delListener(this);
        super.onDestroy();
    }

    public void onValueChanged(String trig_key, Object value) {
        mItems.clear();
//
// TODO: average Wh/km for last n minutes, and remaining range at that rate
//
        Double remainingRange = (Double) mValues.get("range_estimate_km");
        if (remainingRange != null) {
            mItems.add(new ListViewItem("Car estimated remaining range (km)", new DecimalFormat("0.0").format(remainingRange)));
        }

//        mItems.add(new ListViewItem("Nearest Quick-chargers", ((Integer)nearbyChargers.size()).toString() + " Chademo chargers in remaining range"));

        Object obj = mValues.get(R.string.col_chargers_locations);
        if (obj != null) {
            nearChargers = (ArrayList<ChargeLocation>) obj;
            // Sort by distance
            Collections.sort(nearChargers, new ChargeLocationComparator());
            // Display nearest 10
            for (int i = 0; i < Math.min(10, nearChargers.size()); ++i) {
                ChargeLocation charger = nearChargers.get(i);
                double dist_deca_m = Math.round(charger.get_distFromLookupPos() / 100);
                Double dist_km = dist_deca_m / 10;
                boolean verified = false;
                try {
                    verified = (boolean) charger.get_origJson().get("verified");
                } catch (Exception ex) {
                    //ignore
                }
                String infoStr = "Straight distance: " + dist_km.toString() + " km. " + (verified ? "Verified" : "");
                mItems.add(new ListLocationItem(infoStr, charger.get_readableName(), charger));
            }
        }

        // initialize and set the list adapter
        ((MainActivity) mValues.getPreferences().getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    setListAdapter(new ListViewAdapter(activity, mItems));
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        ListLocationItem locItem = (ListLocationItem)getListView().getItemAtPosition(pos);
        if (locItem != null) {
            ChargeLocation loc = locItem.mLocation;
            Toast.makeText(getActivity(), "Open route to " + loc.get_readableName(), Toast.LENGTH_SHORT).show();
            Uri.Builder directionsBuilder = new Uri.Builder()
                    .scheme("https")
                    .authority("www.google.com")
                    .appendPath("maps")
                    .appendPath("dir")
                    .appendPath("")
                    .appendQueryParameter("api", "1")
                    .appendQueryParameter("destination", loc.get_pos().mLat + "," + loc.get_pos().mLng);

            startActivity(new Intent(Intent.ACTION_VIEW, directionsBuilder.build()));
        }
    }

}
