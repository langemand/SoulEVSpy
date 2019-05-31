package com.evranger.soulevspy.util;

import com.evranger.soulevspy.R;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;

public class BatteryStats implements CurrentValuesSingleton.CurrentValueListener {
    private CurrentValuesSingleton mValues = null;
    private int modelyear = 0;
    private double totcap = 0;
    private double nomcap = 0;

    public BatteryStats() {
        mValues = CurrentValuesSingleton.getInstance();
        String key = mValues.getPreferences().getContext().getResources().getString(R.string.col_system_scan_end_time_ms);
        mValues.addListener(key, this);
        onValueChanged(key, mValues.get(key));
    }

    public void finalize() {
        Close();
    }

    public void Close() {
        mValues.delListener(this);
    }

    public void onValueChanged(String key, Object value) {
        if (totcap == 0) {
            Object vin_str = mValues.get(R.string.col_VIN);
            if (vin_str != null) {
                KiaVinParser vin = new KiaVinParser(mValues.getPreferences().getContext(), vin_str.toString()); //"KNDJX3AEXG7123456");}
                String str = vin.getVIN();
                String yearString = vin.getYear();
                if (yearString != null) {
                    modelyear = Integer.parseInt(yearString);
                    try {
                        // Total battery capacity (totcap) for the 2015-2017 models were 30.5 kWh, for the 2018 onwards 31.8 kWh
                        // According to Jejusoul, the SOH is calculated based on 110% of 27 kWh = 29.7 kWh
                        // according to Wikipedia: https://en.wikipedia.org/wiki/Kia_Soul_EV
                        if (modelyear < 2018) {
                            totcap = 29.7; // Not 30.5
                            nomcap = 27.0;
                        } else {
                            // For the 2018 Kia Soul EV we have no values for deteoriation
                            totcap = 31.8;
                            nomcap = 30.0;
                        }
                        mValues.set(R.string.col_nom_capacity_kWh, nomcap);
                        mValues.set(R.string.col_orig_capacity_kWh, nomcap);
                    } catch(NumberFormatException ex){
                        totcap = 0;
                        nomcap = 0;
                    }
                }
            }
        }
        if (modelyear < 2018 && nomcap != 0) {
            Double detmin = (Double) mValues.get(R.string.col_battery_min_cell_detoriation_pct);
            Double detmax = (Double) mValues.get(R.string.col_battery_max_cell_detoriation_pct);
            if (detmax != null && detmin != null) {
                double detavg = (detmin + detmax) / 2;
                double sohpct = 110.0 - detavg;
                mValues.set(R.string.col_calc_battery_soh_pct, sohpct);
            }
        }
    }
}