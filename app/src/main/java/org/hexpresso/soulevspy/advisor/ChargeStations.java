package org.hexpresso.soulevspy.advisor;

import android.content.Context;

import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class ChargeStations implements CurrentValuesSingleton.CurrentValueListener {
    private CurrentValuesSingleton mValues = null;
    private JSONArray chargeLocations;
    Pos mLastPosLookedUp;
    Pos mLastPosReDist;

    public ChargeStations(Context context) {
        mValues = CurrentValuesSingleton.getInstance();
        try {
            JSONObject chargeStations = new JSONObject(loadJSONFromAsset(context));
            chargeLocations = chargeStations.getJSONArray("chargelocations");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mLastPosLookedUp = new Pos(0.0,0.0);
        mLastPosReDist = new Pos(0.0, 0.0);
        String key = mValues.getPreferences().getContext().getResources().getString(R.string.col_route_time_s);
        mValues.addListener(key, this);
    }

    @Override
    public void onValueChanged(String key, Object value) {
        Pos curPos = new Pos((Double)mValues.get(R.string.col_route_lat_deg), (Double)mValues.get(R.string.col_route_lng_deg));
        if (curPos == null || !curPos.isDefined())
            return;
        double dist = 200000;
        double redist = 200000;
        if (mLastPosLookedUp.isDefined() && curPos.isDefined()) {
            dist = curPos.distance(mLastPosLookedUp);
        }
        if (mLastPosReDist.isDefined() && curPos.isDefined()) {
            redist = curPos.distance(mLastPosReDist);
        }
        Object obj = mValues.get(R.string.col_chargers_locations);
        if (obj == null || dist > 5000) {
            Double remainingRange = (Double) mValues.get("range_estimate_km");
            if (remainingRange == null) {
                remainingRange = 200000.0;
            } else {
                // Convert km to meter
                remainingRange = remainingRange * 1E3;
            }
            obj = getChargersInRange(curPos, remainingRange);
            mLastPosLookedUp = curPos;
            mLastPosReDist = curPos;
        }
        ArrayList<ChargeLocation> nearby = (ArrayList<ChargeLocation>)obj;
        if (redist > 100) {
            mLastPosReDist = curPos;
            // Recalculate distances
            for (int i = 0; i < nearby.size(); ++i) {
                ChargeLocation charger = nearby.get(i);
                // TODO: Call a route planner to get distance along route
                charger.set_distFromLookupPos(curPos.distance(charger.get_pos()));
            }
            // Sort by distance
            Collections.sort(nearby, new ChargeLocationComparator());
            // Build route request
            for (int i = 0; i < 10; ++i) {

            }
        }
        mValues.set(R.string.col_chargers_locations, obj);
    }

    private String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("chademo_near_cph.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public ArrayList<ChargeLocation> getChargersInRange(Pos myPos, double range) {
        // TODO: Fetch chargers from cloud...
        ArrayList<ChargeLocation> nearChargers = new ArrayList<>();
        for (int i = 0; i < chargeLocations.length(); ++i) {
            try {
                JSONObject charger = chargeLocations.getJSONObject(i);
                JSONObject location = charger.getJSONObject("coordinates");
                Pos chargerPos = new Pos((Double)location.get("lat"), (Double)location.get("lng"));
                double distance = myPos.distance(chargerPos);
                if (distance < (range + 20)) {
                    JSONObject address = charger.getJSONObject("address");
                    String readableName = charger.get("network").toString() + ", " + (String)charger.get("name") + ", " + address.get("street") + ", " + address.get("postcode") + " " + address.get("city");
                    nearChargers.add(new ChargeLocation(distance, chargerPos, 0, readableName, charger));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                //return null;
            }
        }
        return nearChargers;
    }
}
