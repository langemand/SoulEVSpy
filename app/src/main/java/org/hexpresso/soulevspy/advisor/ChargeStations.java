package org.hexpresso.soulevspy.advisor;

import android.content.Context;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class ChargeStations implements CurrentValuesSingleton.CurrentValueListener {
    private CurrentValuesSingleton mValues = null;
    private JSONArray chargeLocations;
    private Pos mLastPosLookedUp;
    private Pos mLastPosReDist;
    private Pos mLastPosRequested = new Pos(0.0,0.0);
    private double mLastRangeRequested = 25.0;
    private boolean mLastSucceeded = true;
    // Instantiate the RequestQueue.
    RequestQueue requestQueue = null;

    private Context mContext = null;

    Bundle mRequestEventParams = new Bundle();

    public ChargeStations(Context context) {
        mLastPosLookedUp = new Pos(0.0,0.0);
        mLastPosReDist = new Pos(0.0, 0.0);
        mContext = context;
        requestQueue = Volley.newRequestQueue(mContext);
        mValues = CurrentValuesSingleton.getInstance();
        File jsonFile = new File(mContext.getFilesDir(),mContext.getString(R.string.file_chargerlocations_json));
        if (jsonFile.length() > 0) {
            onValueChanged(mValues.getPreferences().getContext().getResources().getString(R.string.charger_locations_update_time_ms), null);
        } else {
            try {
                JSONObject chargeStations = new JSONObject(loadJSONFromAsset(mContext));
                chargeLocations = chargeStations.getJSONArray("chargelocations");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        String key = mValues.getPreferences().getContext().getResources().getString(R.string.col_route_time_s);
        mValues.addListener(key, this);
        mValues.addListener(mValues.getPreferences().getContext().getResources().getString(R.string.charger_locations_update_time_ms), this);
    }

    @Override
    synchronized public void onValueChanged(String key, Object value) {
        Object obj = mValues.get(R.string.col_chargers_locations);
        if (key.contentEquals(mValues.getPreferences().getContext().getResources().getString(R.string.charger_locations_update_time_ms))) {
            // Fetch chargers from cloud...
            String json = loadJSONFromFile(mContext.getFilesDir(),mContext.getString(R.string.file_chargerlocations_json));
            try {
                JSONObject chargeStations = new JSONObject(json);
                chargeLocations = chargeStations.getJSONArray("chargelocations");
                logChargeStationsReceivedEvent(chargeLocations.length());
                obj = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
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
        Double remainingRange = (Double) mValues.get("range_estimate_km");
        if (remainingRange == null) {
            remainingRange = 200000.0;
        } else {
            // Convert km to meter
            remainingRange = remainingRange * 1E3;
        }
        if (obj == null || dist > 5000) {
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

        // Consider requesting an update of the charger locations
        if (mLastPosRequested.distance(curPos) > mLastRangeRequested / 2 || !mLastSucceeded) {
            mLastPosRequested = curPos;
            mLastRangeRequested = Math.max(40.0, Math.max(mLastRangeRequested, remainingRange));
            getJSONFromMobileDe(curPos, mLastRangeRequested);
        }
    }

    private String loadJSONFromAsset(Context context) {
        String json = "";
        try {
            InputStream is = context.getAssets().open("chademo_near_cph.json");
            json = readStream(is);

        } catch (IOException ex) {
            ex.printStackTrace();
            return json;
        }
        return json;
    }

    private String loadJSONFromFile(File dir, String filename) {
        String json = "";
        try {
            File file = new File(dir, filename);
            InputStream is = new FileInputStream(file);
            try {
                json = readStream(is);
            } finally {
                is.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            return json;
        }
    }

    private String readStream(InputStream is) throws IOException {
        String json;
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        json = new String(buffer, "UTF-8");
        return json;
    }

    private void getJSONFromMobileDe(Pos pos, double range) {
        String url = "https://api.goingelectric.de/chargepoints?key=" + mContext.getString(R.string.goingelectric_de_api_key) + "&lat=" + pos.mLat + "&lng=" + pos.mLng + "&radius=" + range/1000 + "&clustering=0&plugs=CHAdeMO";
        mLastSucceeded = true;

        logChargeStationsRequestEvent(pos, range);

// Request a string response from the URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String json) {
                        if (json.length() > 1) {
                            try {
                                File file = new File(mContext.getFilesDir(),mContext.getString(R.string.file_chargerlocations_json));
                                OutputStream out = new FileOutputStream(file, false);
                                byte[] contents = json.getBytes();
                                out.write(contents);
                                out.flush();
                                out.close();
                                CurrentValuesSingleton.getInstance().set(R.string.charger_locations_update_time_ms, System.currentTimeMillis());
                            } catch (Exception ex) {
//
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mLastSucceeded = false;
            }
        });

// Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    public ArrayList<ChargeLocation> getChargersInRange(Pos myPos, double range) {
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

    public void onDestroy() {
        if (mValues != null) {
            mValues.delListener(this);
        }
    }

    public void logChargeStationsRequestEvent(Pos pos, Double range) {
        mRequestEventParams = new Bundle();
        mRequestEventParams.putString("lat_lng_range", new DecimalFormat("0.000").format(Double.valueOf(pos.mLat))+"_"+new DecimalFormat("0.000").format(Double.valueOf(pos.mLng))+"_"+new DecimalFormat("0").format(range/1000));
    }

    public void logChargeStationsReceivedEvent(int numstations) {
        mRequestEventParams.putInt("number_of_stations", numstations);
        FirebaseAnalytics.getInstance(mContext).logEvent("chargestations_request", mRequestEventParams);
    }
}
