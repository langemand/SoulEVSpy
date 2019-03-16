package org.hexpresso.soulevspy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.activity.MainActivity;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.Locale;


/**
 * Copied and modified by Henrik Scheel <henrik.scheel@spjeldager.dk> from original source in CanZE on 2019-03-10.
 * Thanks to Jeroen Meijer <meijer.jwm@gmail.com> and Robert Fisch <bob@fisch.lu> for the original source.
 */
public class BatteryCellmapFragment extends Fragment implements CurrentValuesSingleton.CurrentValueListener {
    private CurrentValuesSingleton mValues = null;
    private double mean = 0;
    private double meantemp = 0;
    private double viewmean = 0;
    private double viewmeantemp = 0;
    private double cutoff;
    private double viewcutoff;
    private double[] lastVoltage = new double[100];
    private int[] lastTemperature = new int[8];
    private int lastCell = 0;
    private int lastModule = 0;
    private int viewlastCell = 0;
    private int viewlastModule = 0;
    String packageName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cellmap_voltage, container, false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        packageName = activity.getPackageName();
        activity.setTitle(R.string.action_battery_cellmap);

        mValues = CurrentValuesSingleton.getInstance();
        onValueChanged(null, null);
        mValues.addListener(mValues.getPreferences().getContext().getResources().getString(R.string.col_system_scan_end_time_ms), this);
    }

    @Override
    public void onDestroy() {
        mValues.delListener(this);
        super.onDestroy();
    }

    synchronized public void onValueChanged(String trig_key, Object val) {
        meantemp = 0;
        lastModule = 0;
        for (int i = 0; i < 8; ++i) {
            Object obj = mValues.get(mValues.getPreferences().getContext().getResources().getString(R.string.col_battery_module_temperature) + (i+1) + "_C");
            if (obj != null) {
                Integer value = (Integer)obj;
                lastModule = i;
                lastTemperature[i] = value;
                meantemp += value;
            }
        }
        meantemp /= (lastModule+1);

        mean = 0;
        double lowest = 5;
        double highest = 3;
        for (int j = 0; j < 100; ++j) {
            Object obj = mValues.get(mValues.getPreferences().getContext().getResources().getString(R.string.col_battery_cell_voltage) + j + "_V");
            if (obj == null) {
                break;
            }
            Double value = (Double)obj;
            if (value < 3) {
                break;
            }
            lastCell = j;
            lastVoltage[j] = value;
            mean += lastVoltage[j];
            if (lastVoltage[j] < lowest) lowest = lastVoltage[j];
            if (lastVoltage[j] > highest) highest = lastVoltage[j];
        }

        if (mean == 0) {
            return;
        }
        mean /= (lastCell+1);
        cutoff = lowest < 3.712 ? mean - (highest - mean) * 1.5 - 0.0199 : 2;

        viewmean = mean;
        viewmeantemp = meantemp;
        viewlastCell = lastCell;
        viewlastModule = lastModule;
        viewcutoff = cutoff;

        // the update has to be done in a separate thread
        // otherwise the UI will not be repainted
        ((MainActivity) mValues.getPreferences().getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Module temperatures
                    for (int i = 1; i <= 8; ++i) {
                        int value = lastTemperature[i-1];
                        TextView tv = (TextView) ((MainActivity) mValues.getPreferences().getContext()).findViewById(getResources().getIdentifier("text_module_" + i + "_temperature", "id", packageName));
                        if (tv == null) {
                            continue;
                        }
                        int color = (int) (100 * (value - viewmeantemp));
                        if (i <= viewlastModule+1) {
                            tv.setText(String.format(Locale.getDefault(), "%d", value));

                            if (color > 62) {
                                color = 0xffffc0c0;
                            } else if (color > 0) {
                                color = 0xffc0c0c0 + (color * 0x010000); // one tick is one red
                            } else if (color >= -62) {
                                color = 0xffc0c0c0 - color; // one degree below is a 16th blue added
                            } else {
                                color = 0xffc0c0ff;
                            }
                        } else {
                            tv.setText("");
                            color = 0xffc0c0c0;
                        }
                        tv.setBackgroundColor(color);
                    }
                    // Cell voltages
                    for (int i = 0; i < 100; i++) {
                        TextView tv = (TextView) ((MainActivity) mValues.getPreferences().getContext()).findViewById(getResources().getIdentifier("text_cell_" + (i+1) + "_voltage", "id", packageName));
                        if (tv == null) {
                            continue;
                        }
                        int color = (int) (2500 * (lastVoltage[i] - viewmean)); // color is temp minus mean. 1mV difference is 5 color ticks
                        if (i <= viewlastCell) {
                            tv.setText(String.format(Locale.getDefault(), "%.2f", lastVoltage[i]));
                            if (lastVoltage[i] <= viewcutoff) {
                                color = 0xffff4040;
                            } else if (color > 62) {
                                color = 0xffffc0c0;
                            } else if (color > 0) {
                                color = 0xffc0c0c0 + (color * 0x010000); // one tick is one red
                            } else if (color >= -62) {
                                color = 0xffc0c0c0 - color; // one degree below is a 16th blue added
                            } else {
                                color = 0xffc0c0ff;
                            }
                        } else {
                            tv.setText("");
                            color = 0xffc0c0c0;
                        }
                        tv.setBackgroundColor(color);
                    }
                } catch (IllegalStateException ex) {
                    // Probably the fragment was closed when user selected another
                }
            }
        });
    }
}
