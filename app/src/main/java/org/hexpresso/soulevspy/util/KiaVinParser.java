package org.hexpresso.soulevspy.util;

import android.content.Context;
import android.util.Log;

import org.hexpresso.soulevspy.R;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-07.
 */
public class KiaVinParser {

    final static String YEARS = "EFGHJKLMNPRSTVWXY123456789";
    final static int FIRST_YEAR = 2014;

    private String mVIN;
    private boolean mIsValid = false;

    private String mBrand;
    private String mModel;
    private String mTrim;
    private String mEngine;
    private String mYear;
    private String mSequentialNumber;
    private String mProductionPlant;


    public KiaVinParser(Context context, String vehicleIdentificationNumber ) {
        if ( vehicleIdentificationNumber == null || vehicleIdentificationNumber.length() != 17 )
        {
            // The string must be 17 characters!
            Log.d("KiaVinParser", "Invalid String!");
            return;
        }

        // Make sure it's in uppercase
        vehicleIdentificationNumber = vehicleIdentificationNumber.toUpperCase();

        // World Manufacturer Identifier (WMI)
        final String wmi = vehicleIdentificationNumber.substring(0, 3);
        if ( !wmi.equals("KNA") && !wmi.equals("KNC") && !wmi.equals("KND") && !wmi.equals("KNH") )
        {
            // Not a Kia vehicle!
            Log.d("KiaVinParser", "Not a Kia! " + wmi);
            return;
        }

        // Vehicle line (J = Soul)
        final Character vehicleLine = vehicleIdentificationNumber.charAt(3);
        if ( !vehicleLine.equals('J') )
        {
            Log.d("KiaVinParser", "Not a Soul! " + vehicleLine);
            return;
        }

        // Motor type
        final Character motorType = vehicleIdentificationNumber.charAt(7);
        if ( !motorType.equals('E') )
        {
            Log.d("KiaVinParser", "Not a Soul EV! " + motorType);
            return;
        }

        // At that point, we are sure that it's a Kia Soul EV!
        mIsValid = true;
        mVIN = vehicleIdentificationNumber;
        mBrand = context.getString(R.string.car_kia);
        mModel = context.getString(R.string.car_soulev);
        mEngine = context.getString(R.string.car_engine_e);

        // Model & series
        final Character trim = vehicleIdentificationNumber.charAt(4);
        switch(trim)
        {
            case 'P':
                mTrim = context.getString(R.string.car_trim_base);
                break;

            case 'X':
                mTrim = context.getString(R.string.car_trim_plus);
                break;

            default:
                mTrim = context.getString(R.string.car_unknown);
                break;
        }

        // Body/Cabin type, Gross vehicle weight rating (UNUSED)
        // final Character type = vehicleIdentificationNumber.charAt(5);

        // Restraint system, brake system (UNUSED)
        //final Character brakeSystem = vehicleIdentificationNumber.charAt(6);

        // Check digit (UNUSED)
        //final Character checkDigit = vehicleIdentificationNumber.charAt(8);

        // Model year
        final Character year = vehicleIdentificationNumber.charAt(9);
        final int yearValue = YEARS.indexOf(year);
        if ( yearValue != -1 )
        {
            mYear = Integer.toString(yearValue + FIRST_YEAR);
        } else {
            mYear = context.getString(R.string.car_unknown);
        }

        // Production plant
        final Character plant = vehicleIdentificationNumber.charAt(10);
        switch (plant)
        {
            case '5':
                mProductionPlant = context.getString(R.string.car_plant_hwaseong) + " (" + context.getString(R.string.car_south_korea) + ')';
                break;
            case '6':
                mProductionPlant = context.getString(R.string.car_plant_soha_ri) + " (" + context.getString(R.string.car_south_korea) + ')';
                break;
            case '7':
                mProductionPlant = context.getString(R.string.car_plant_gwangju) + " (" + context.getString(R.string.car_south_korea) + ')';
                break;
            case 'T':
                mProductionPlant = context.getString(R.string.car_plant_seosan) + " (" + context.getString(R.string.car_south_korea) + ')';
                break;
            default:
                mProductionPlant = context.getString(R.string.car_unknown) + " (" + plant + ')';
                break;
        }

        // Vehicle production sequence number
        mSequentialNumber = vehicleIdentificationNumber.substring(11, 17);
    }

    /**
     *
     * @return
     */
    public boolean isValid() { return mIsValid; }

    public String getVIN() { return mVIN; }
    // Manufacturer
    public String getBrand() { return mBrand; }
    public String getModel() { return mModel; }
    public String getTrim() { return mTrim; }
    public String getEngine() { return mEngine; }
    public String getYear() { return mYear; }
    public String getSequentialNumber() { return mSequentialNumber; }
    public String getProductionPlant() { return mProductionPlant; }

}
