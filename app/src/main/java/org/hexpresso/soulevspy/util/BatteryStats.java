package org.hexpresso.soulevspy.util;

import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

public class BatteryStats implements CurrentValuesSingleton.CurrentValueListener {
    private CurrentValuesSingleton mValues = null;
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
                    try {
                        // Total battery capacity (totcap) for the 2015-2017 models were 30.5 kWh, for the 2018 onwards 31.8 kWh
                        // according to Wikipedia: https://en.wikipedia.org/wiki/Kia_Soul_EV
                        if (Integer.parseInt(yearString) < 2018) {
                            totcap = 30.5;
                            nomcap = 27.0;
                        } else {
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
        Double detmax = (Double)mValues.get(R.string.col_battery_max_cell_detoriation_pct);
        if (detmax != null && nomcap != 0) {
            double sohpct = (totcap * (1-detmax/100.0) / nomcap * 100.0);
            mValues.set(R.string.col_calc_battery_soh_pct, sohpct);
//        ((MainActivity)mContext).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//               Toast.makeText(mValues.getPreferences().getContext(), "State Of Health: " + sohpct + " %", Toast.LENGTH_LONG).show();
//         });
        }
    }
}