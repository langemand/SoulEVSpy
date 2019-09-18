package com.evranger.soulevspy.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evranger.soulevspy.R;
import com.evranger.soulevspy.activity.MainActivity;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.Unit;

import java.text.DecimalFormat;
import java.util.Locale;

import static android.view.View.VISIBLE;


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
    Unit unit = new Unit();
    private final static int color_red = 0xffffa0a0;
    private final static int color_blue = 0xffa0a0ff;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        packageName = activity.getPackageName();
        activity.setTitle(R.string.action_battery_cellmap);

        mValues = CurrentValuesSingleton.getInstance();
        mValues.addListener(mValues.getPreferences().getContext().getResources().getString(R.string.col_system_scan_end_time_ms), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cellmap_voltage, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onValueChanged(null, null);
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

        if (mean != 0) {
            mean /= (lastCell + 1);
            cutoff = lowest < 3.712 ? mean - (highest - mean) * 1.5 - 0.0199 : 2;

            viewmean = mean;
            viewmeantemp = meantemp;
            viewlastCell = lastCell;
            viewlastModule = lastModule;
            viewcutoff = cutoff;
        }

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
                        if (tv == null || value == 0) {
                            break;
                        }
                        int color = (int) (100 * (value - viewmeantemp));
                        if (i <= viewlastModule+1) {
                            tv.setText(new DecimalFormat("0.0").format(unit.convertTemp(value)));

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
                        if (tv == null || viewmean == 0) {
                            break;
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
                    // Main Battery Amps
                    Object amps_obj = mValues.get(R.string.col_battery_DC_current_A);
                    ProgressBar apgn = ((MainActivity) mValues.getPreferences().getContext()).findViewById(getResources().getIdentifier("progress_bar_negative_amps", "id", packageName));
                    ProgressBar apgp = ((MainActivity) mValues.getPreferences().getContext()).findViewById(getResources().getIdentifier("progress_bar_positive_amps", "id", packageName));
                    if (apgn != null && apgp != null) {
                        TextView left_col_above = ((MainActivity) mValues.getPreferences().getContext()).findViewById(getResources().getIdentifier("_97", "id", packageName));
                        TextView left_col = ((MainActivity) mValues.getPreferences().getContext()).findViewById(getResources().getIdentifier("text_power_text", "id", packageName));
                        if (left_col_above != null && left_col != null) {
                            ViewGroup.LayoutParams params = left_col.getLayoutParams();
                            int lw = left_col_above.getWidth();
                            params.width = lw;
                            if (lw == 0) {
                                left_col.setVisibility(View.INVISIBLE);
                                apgn.setVisibility(View.INVISIBLE);
                                apgp.setVisibility(View.INVISIBLE);
                                return;
                            }
                            left_col.setLayoutParams(params);
                        }
                        TextView tvll = (TextView) ((MainActivity) mValues.getPreferences().getContext()).findViewById(getResources().getIdentifier("text_cell_97_voltage", "id", packageName));
                        TextView tvlr = (TextView) ((MainActivity) mValues.getPreferences().getContext()).findViewById(getResources().getIdentifier("text_cell_100_voltage", "id", packageName));
                        if (tvll != null && tvlr != null) {
                            ViewGroup.LayoutParams params = apgn.getLayoutParams();
                            params.width = (int)(tvlr.getX()+tvlr.getWidth()-tvll.getX());
                            apgn.setLayoutParams(params);
                        }
                        TextView tvrl = (TextView) ((MainActivity) mValues.getPreferences().getContext()).findViewById(getResources().getIdentifier("text_cell_101_voltage", "id", packageName));
                        TextView tvrr = (TextView) ((MainActivity) mValues.getPreferences().getContext()).findViewById(getResources().getIdentifier("text_cell_104_voltage", "id", packageName));
                        if (tvrl != null && tvrr != null) {
                            ViewGroup.LayoutParams params = apgp.getLayoutParams();
                            params.width = (int)(tvrr.getX()+tvrr.getWidth()-tvrl.getX());
                            apgp.setLayoutParams(params);
                        }

                        Drawable progressDrawableNeg = apgn.getProgressDrawable().mutate();
                        progressDrawableNeg.setColorFilter(color_blue, android.graphics.PorterDuff.Mode.SRC_IN);
                        apgn.setProgressDrawable(progressDrawableNeg);
                        apgn.setRotation(180);

                        Drawable progressDrawablePos = apgp.getProgressDrawable().mutate();
                        progressDrawablePos.setColorFilter(color_red, android.graphics.PorterDuff.Mode.SRC_IN);
                        apgp.setProgressDrawable(progressDrawablePos);
                        apgn.setProgress(0);
                        apgp.setProgress(0);
                        if (amps_obj != null && amps_obj instanceof Double) {
                            double amps = (double) mValues.get(R.string.col_battery_DC_current_A);
                            // TODO: Map -200 to +0 amps to min to max
                            int minn = 0; //apg.getMin();
                            int maxn = apgn.getMax();
                            // Map 0 to +200 amps to min to max
                            int minp = 0;
                            int pos = (int) (amps / 200 * (apgp.getMax() - minp)) + minp;
                            if (amps > 0) {
                                apgn.setProgress(0);
                                apgp.setProgress(pos);
                            } else {
                                apgp.setProgress(0);
                                apgn.setProgress(-pos);
                            }
                            left_col.setVisibility(VISIBLE);
                            apgn.setVisibility(VISIBLE);
                            apgp.setVisibility(VISIBLE);
                        }
                    }
                    TextView tv = (TextView) ((MainActivity) mValues.getPreferences().getContext()).findViewById(getResources().getIdentifier("title_temperatures", "id", packageName));
                    tv.setText(mValues.getString(R.string._temp)+" "+unit.mTempUnit);
                } catch (IllegalStateException ex) {
                    // Probably the fragment was closed when user selected another
                    int j = 5;
                }
            }
        });
    }
}
