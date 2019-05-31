package com.evranger.soulevspy.util;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-09-28.
 */
public class EnergyConsumption {

    // Conversion factors
    private static final double KM_TO_MI_INTERNATIONAL = 1.609344;

    static double toKmPerKwh( double kwhPer100km )
    {
        return 100.0 / kwhPer100km;
    }

    static double toKwhPer100mi( double kwhPer100km )
    {
        return kwhPer100km * KM_TO_MI_INTERNATIONAL;
    }

    static double toMiPerKwh( double kwhPer100km )
    {
        return 100.0 / ( kwhPer100km * KM_TO_MI_INTERNATIONAL );
    }
}
