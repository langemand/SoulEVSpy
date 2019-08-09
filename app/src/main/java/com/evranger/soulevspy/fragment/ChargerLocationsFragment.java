package com.evranger.soulevspy.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.evranger.soulevspy.activity.MainActivity;
import com.evranger.soulevspy.advisor.ChargeLocation;
import com.evranger.soulevspy.advisor.ChargeLocationComparator;
import com.evranger.soulevspy.advisor.ChargeStations;
import com.evranger.soulevspy.advisor.Pos;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;

import com.evranger.soulevspy.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by henrik on 29/06/2017.
 */

public class ChargerLocationsFragment extends ListFragment implements View.OnClickListener, CurrentValuesSingleton.CurrentValueListener {
    private ListViewAdapter mListViewAdapter = null;
    private List<ListViewItem> mListItems = new ArrayList<>();
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
        FragmentActivity activity = getActivity();
        if (activity != null) {
            // initialize the list adapter
            mListViewAdapter = new ListViewAdapter(getActivity(), mListItems, this);
            ((MainActivity) mValues.getPreferences().getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setListAdapter(mListViewAdapter);
                }
            });
            onValueChanged(null, null);
            mValues.addListener(mValues.getPreferences().getContext().getResources().getString(R.string.col_chargers_locations), this);
        }
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
        } else {
            remainingRange = 452.0;
        }

        boolean warningAdded = false;
        Object obj = mValues.get(R.string.col_chargers_locations);
        if (obj != null) {
            nearChargers = (ArrayList<ChargeLocation>) obj;
            // Sort by distance
            Collections.sort(nearChargers, new ChargeLocationComparator());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String dateTimeAsString = "";
            try {
                dateTimeAsString = formatter.format(mValues.get(R.string.charger_locations_update_time_ms));
            } catch (Exception ex) {
                // Ignore
            }
            mItems.add(new ListViewItem("From goingelectric.de: " + dateTimeAsString,
                    "Click text to see details in browser"));
            // Display nearest
            for (int i = 0; i < Math.min(500, nearChargers.size()); ++i) {
                ChargeLocation charger = nearChargers.get(i);
                double dist_m = charger.get_distFromLookupPos();

                if (remainingRange != null && !warningAdded && dist_m > remainingRange * 1000) {
                    warningAdded = true;
                    mItems.add(new ListViewItem("-------------------------------------------------------------------------------",
                            "Below are out of range!"));
                }
                double dist_deca_m = Math.round(dist_m / 100);
                Double dist_km = dist_deca_m / 10;
                boolean verified = false;
                try {
                    verified = (boolean) charger.get_origJson().get("verified");
                } catch (Exception ex) {
                    //ignore
                }
                String infoStr = "Straight dist: " + dist_km.toString() + " km. " + (verified ? "Verified" : "");
                mItems.add(new ListLocationItem(infoStr, charger.get_readableName(), charger));
            }
            if (nearChargers.size() == 0) {
                mItems.add(new ListViewItem("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!",
                        "No DC-chargers nearby!"));
                // TODO: Fetch AC-charger-locations
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

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        Object item = getListView().getItemAtPosition(pos);
        if (item instanceof ListLocationItem) {
            ListLocationItem locItem = (ListLocationItem) item;
            if (locItem != null) {
                ChargeLocation loc = locItem.mLocation;
                Uri uri = null;
// Open GoingElectric uri, as per goingelectric.de API requirements:
                try {
                    String uriStr = loc.get_origJson().getString("url");
                    uri = Uri.parse("https:" + uriStr);
                } catch (Exception e) {
                    // Do Nothing?
                }

                if (uri != null) {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int pos = (int)v.getTag();
        Object item = getListView().getItemAtPosition(pos);
        if (item instanceof ListLocationItem) {
            ListLocationItem locItem = (ListLocationItem) item;
            ChargeLocation loc = locItem.mLocation;

            // Open Google Maps routing:
            Uri.Builder directionsBuilder = new Uri.Builder()
                    .scheme("https")
                    .authority("www.google.com")
                    .appendPath("maps")
                    .appendPath("dir")
                    .appendPath("")
                    .appendQueryParameter("api", "1")
                    .appendQueryParameter("destination", loc.get_pos().mLat + "," + loc.get_pos().mLng)
                    .appendQueryParameter("travelmode", "driving");
            Uri uri = directionsBuilder.build();

            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }
}
