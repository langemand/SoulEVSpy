package com.evranger.soulevspy.util;

import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;

public class Unit {

    public String mTempUnit;
    private double mTempFactor;
    private double mTempOffset;
    public String mDistUnit;
    private double mDistFactor;
    public String mConsumptionUnit;
    private boolean mPerDistance;
    private double mConsumptionDistanceFactor;


    public Unit() {
        if (CurrentValuesSingleton.getInstance().getPreferences().getUnitsTemperatureStringValue().contentEquals("f")) {
            mTempUnit = "F";
            mTempFactor = 1.8;
            mTempOffset = 32;
        } else {
            mTempUnit = "C";
            mTempFactor = 1;
            mTempOffset = 0;
        }
        if (CurrentValuesSingleton.getInstance().getPreferences().getUnitsDistanceStringValue().contentEquals("mi")) {
            mDistUnit = "mi";
            mDistFactor = 0.621371192;
        } else {
            mDistUnit = "km";
            mDistFactor = 1;
        }
        if (CurrentValuesSingleton.getInstance().getPreferences().getUnitsEnergyConsumptionStringValue().contentEquals("kwh_100km")) {
            mConsumptionUnit = "kWh/100km";
            mPerDistance = true;
            mConsumptionDistanceFactor = 0.01;
        } else if (CurrentValuesSingleton.getInstance().getPreferences().getUnitsEnergyConsumptionStringValue().contentEquals("km_kwh")) {
            mConsumptionUnit = "km/kWh";
            mPerDistance = false;
            mConsumptionDistanceFactor = 1;
        } else if (CurrentValuesSingleton.getInstance().getPreferences().getUnitsEnergyConsumptionStringValue().contentEquals("kwh_100mi")) {
            mConsumptionUnit = "kWh/100mi";
            mPerDistance = true;
            mConsumptionDistanceFactor = 0.00621371192;
        } else { // mi_kwh
            mConsumptionUnit = "mi/kWh";
            mPerDistance = false;
            mConsumptionDistanceFactor = 0.621371192;;
        }
    }

    public double convertTemp(double tempC) {
        return tempC * mTempFactor + mTempOffset;
    };

    public double convertDist(double distkm) {
        return distkm * mDistFactor;
    };

    public double convertConsumption(double wh_per_km) {
        if (mPerDistance) {
            return wh_per_km / 1000 / mConsumptionDistanceFactor;
        } else {
            return mConsumptionDistanceFactor / (wh_per_km / 1000);
        }
    };
}
