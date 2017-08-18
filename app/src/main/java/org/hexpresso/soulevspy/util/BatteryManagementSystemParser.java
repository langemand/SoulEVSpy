package org.hexpresso.soulevspy.util;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-16.
 */
public class BatteryManagementSystemParser {

    private final String BMS_ECU = "7EC";

    public enum CoolingFanSpeeds {
        FAN_1ST(1),
        FAN_2ND(2),
        FAN_3RD(3),
        FAN_4TH(4),
        FAN_5TH(5),
        FAN_6TH(6),
        FAN_7TH(7),
        FAN_8TH(8),
        FAN_9TH(9),
        FAN_STOP(0);

        private final int value;

        CoolingFanSpeeds(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static CoolingFanSpeeds fromInteger(int value) {
            for( CoolingFanSpeeds type : CoolingFanSpeeds.values() )  {
                if ( type.getValue() == value ) {
                    return type;
                }
            }

            return CoolingFanSpeeds.FAN_STOP;
        }

    }

    public class Data {

        // BMS Status Flags
        /*
        public boolean bmsMainRelayOnStatus;
        public boolean bmsControllableState;
        public boolean bmsWarning;
        public boolean bmsFault;
        public boolean bmsWeldFlag;

        public boolean mcuReady;
        public boolean mcuMainRelayOffRequest;
        public boolean mcuControllable;
        */

        //public boolean hcuReady;

        //public boolean quickChargingNormalStatus;

        // BMS Charger status
        public boolean bmsIsCharging;
        public boolean bmsChademoIsPlugged;
        public boolean bmsJ1772IsPlugged;

        // High-Voltage Battery General Information
        public double stateOfCharge;                            // %
        public double stateOfChargeDisplay;                     // %
        //public double stateOfHealth;                            // %

        public double batteryDcVoltage;                         // V
        public double batteryCurrent;                           // A

        public double availableChargePower;                     // kW
        public double availableDischargePower;                  // kW

        public double accumulativeChargeCurrent;                // Ah
        public double accumulativeDischargeCurrent;             // Ah

        public double accumulativeChargePower;                  // kWh
        public double accumulativeDischargePower;               // kWh

        public int    accumulativeOperatingTime;                // Sec

        public int    driveMotorSpeed;                          // RPM

        public int    batteryInletTemperature;                  // °C
        public int    batteryMaxTemperature;                    // °C
        public int    batteryMinTemperature;                    // °C

        public int    heat1Temperature;                         // °C
        public int    heat2Temperature;                         // °C

        //public int    isolationResistance;                      // kOhm

        // High-Voltage Battery Modules information
        public int    batteryModuleTemperature[] = new int[8];  // °C

        // High-Voltage Battery Cells Information
        public double batteryCellVoltage[] = new double[96];    // V
        //public double batteryCellVoltageDeviation;              // V

        public double maxCellVoltage;                           // V
        public int    maxCellVoltageNo;                         // Cell #

        public double minCellVoltage;                           // V
        public int    minCellVoltageNo;                         // Cell #

        public double maxDeterioration;                         // %
        public int    maxDeteriorationCellNo;                   // Cell #

        public double minDeterioration;                         // %
        public int    minDeteriorationCellNo;                   // Cell #

        // Auxiliary Battery General Information
        public double auxiliaryBatteryVoltage;                  // V

        // Cooling Fan
        public CoolingFanSpeeds fanStatus;                      // 0-9
        public int              fanFeedbackSignal;              // Hz

        // Other
        public int    airbagHwireDuty;                          // %
        //public double inverterCapacitorVoltage;                 // V


        /**
         * Print the data structure values (for debugging)
         * @return Data structure as string
         */
        @Override
        public String toString()
        {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE, false);
        }
    }

    Data bmsData = null;

    public Data getParsedData() {
        return bmsData;
    }

    public BatteryManagementSystemParser() {
        bmsData = new Data();
    }

    public boolean parseMessage2101(String rawData) {
        ParsedRawData data = new ParsedRawData(rawData);
        if( !data.isValid() )
        {
            return false;
        }

        // Get the data blocks
        final ArrayList<String> line21 = data.getData("21");
        final ArrayList<String> line22 = data.getData("22");
        final ArrayList<String> line23 = data.getData("23");
        final ArrayList<String> line24 = data.getData("24");
        final ArrayList<String> line25 = data.getData("25");
        final ArrayList<String> line26 = data.getData("26");
        final ArrayList<String> line27 = data.getData("27");
        final ArrayList<String> line28 = data.getData("28");

        bmsData.stateOfCharge       = HexToInteger(line21.get(0)) * 0.5;
        bmsData.bmsIsCharging       = (HexToInteger(line21.get(5)) & 0x80) != 0;
        bmsData.bmsChademoIsPlugged = (HexToInteger(line21.get(5)) & 0x40) != 0;
        bmsData.bmsJ1772IsPlugged   = (HexToInteger(line21.get(5)) & 0x20) != 0;

        bmsData.batteryDcVoltage = ( ( HexToInteger(line22.get(1) ) << 8) + HexToInteger(line22.get(2)) ) * 0.1;
        bmsData.maxCellVoltage = HexToInteger(line23.get(5)) * 0.02;
        bmsData.minCellVoltage = HexToInteger(line24.get(0)) * 0.02;
        int msb = HexToInteger(line21.get(6));
        bmsData.batteryCurrent = ( (msb << 8) + (HexToInteger(line22.get(0) ))) * 0.1;
        if ((msb & 0x80) != 0) { // negative value = charging
            bmsData.batteryCurrent = bmsData.batteryCurrent - 6553.6;
        }
        bmsData.availableChargePower = ( ( HexToInteger(line21.get(1) ) << 8) + HexToInteger(line21.get(2)) ) * 0.01;
        bmsData.availableDischargePower = ( ( HexToInteger(line21.get(3) ) << 8) + HexToInteger(line21.get(4)) ) * 0.01;
        bmsData.auxiliaryBatteryVoltage = HexToInteger(line24.get(4)) * 0.1;

        bmsData.batteryModuleTemperature[0] = HexToInteger(line22.get(3));
        bmsData.batteryModuleTemperature[1] = HexToInteger(line22.get(4));
        bmsData.batteryModuleTemperature[2] = HexToInteger(line22.get(5));
        bmsData.batteryModuleTemperature[3] = HexToInteger(line22.get(6));
        bmsData.batteryModuleTemperature[4] = HexToInteger(line23.get(0));
        bmsData.batteryModuleTemperature[5] = HexToInteger(line23.get(1));
        bmsData.batteryModuleTemperature[6] = HexToInteger(line23.get(2));
        bmsData.batteryModuleTemperature[7] = HexToInteger(line23.get(4));

        bmsData.maxCellVoltageNo = HexToInteger(line23.get(6));
        bmsData.minCellVoltageNo = HexToInteger(line24.get(1));

        bmsData.fanStatus = CoolingFanSpeeds.fromInteger(HexToInteger(line24.get(2)));
        bmsData.fanFeedbackSignal = HexToInteger(line24.get(3));

        bmsData.accumulativeChargeCurrent = ( ( HexToInteger(line24.get(5) ) << 24) +
                                              ( HexToInteger(line24.get(6) ) << 16) +
                                              ( HexToInteger(line25.get(0) ) << 8) +
                                              ( HexToInteger(line25.get(1) ) ) ) * 0.1;
        bmsData.accumulativeDischargeCurrent = ( ( HexToInteger(line25.get(2) ) << 24) +
                                                 ( HexToInteger(line25.get(3) ) << 16) +
                                                 ( HexToInteger(line25.get(4) ) << 8) +
                                                 ( HexToInteger(line25.get(5) ) ) ) * 0.1;
        bmsData.accumulativeChargePower = ( ( HexToInteger(line25.get(6) ) << 24) +
                                            ( HexToInteger(line26.get(0) ) << 16) +
                                            ( HexToInteger(line26.get(1) ) << 8) +
                                            ( HexToInteger(line26.get(2) ) ) ) * 0.1;
        bmsData.accumulativeDischargePower = ( ( HexToInteger(line26.get(3) ) << 24) +
                                               ( HexToInteger(line26.get(4) ) << 16) +
                                               ( HexToInteger(line26.get(5) ) << 8) +
                                               ( HexToInteger(line26.get(6) ) ) ) * 0.1;
        bmsData.accumulativeOperatingTime = ( ( HexToInteger(line27.get(0) ) << 24) +
                                              ( HexToInteger(line27.get(1) ) << 16) +
                                              ( HexToInteger(line27.get(2) ) << 8) +
                                              ( HexToInteger(line27.get(3) ) ) );

        bmsData.driveMotorSpeed = ( ( HexToInteger(line28.get(0) ) << 8) + HexToInteger(line28.get(1)) ); // Could be also 2-3
        if (bmsData.driveMotorSpeed > 0x8000)
            bmsData.driveMotorSpeed = 0x10000 - bmsData.driveMotorSpeed;

        return true;
    }

    public boolean parseMessage2102(String rawData) {
        ParsedRawData data = new ParsedRawData(rawData);
        if (!data.isValid()) {
            return false;
        }

        final ArrayList<String> line21 = data.getData("21");
        final ArrayList<String> line22 = data.getData("22");
        final ArrayList<String> line23 = data.getData("23");
        final ArrayList<String> line24 = data.getData("24");
        final ArrayList<String> line25 = data.getData("25");

        // Battery Cell Voltage 01-32
        bmsData.batteryCellVoltage[0]  = HexToInteger(line21.get(0)) * 0.02;
        bmsData.batteryCellVoltage[1]  = HexToInteger(line21.get(1)) * 0.02;
        bmsData.batteryCellVoltage[2]  = HexToInteger(line21.get(2)) * 0.02;
        bmsData.batteryCellVoltage[3]  = HexToInteger(line21.get(3)) * 0.02;
        bmsData.batteryCellVoltage[4]  = HexToInteger(line21.get(4)) * 0.02;
        bmsData.batteryCellVoltage[5]  = HexToInteger(line21.get(5)) * 0.02;
        bmsData.batteryCellVoltage[6]  = HexToInteger(line21.get(6)) * 0.02;
        bmsData.batteryCellVoltage[7]  = HexToInteger(line22.get(0)) * 0.02;
        bmsData.batteryCellVoltage[8]  = HexToInteger(line22.get(1)) * 0.02;
        bmsData.batteryCellVoltage[9]  = HexToInteger(line22.get(2)) * 0.02;
        bmsData.batteryCellVoltage[10] = HexToInteger(line22.get(3)) * 0.02;
        bmsData.batteryCellVoltage[11] = HexToInteger(line22.get(4)) * 0.02;
        bmsData.batteryCellVoltage[12] = HexToInteger(line22.get(5)) * 0.02;
        bmsData.batteryCellVoltage[13] = HexToInteger(line22.get(6)) * 0.02;
        bmsData.batteryCellVoltage[14] = HexToInteger(line23.get(0)) * 0.02;
        bmsData.batteryCellVoltage[15] = HexToInteger(line23.get(1)) * 0.02;
        bmsData.batteryCellVoltage[16] = HexToInteger(line23.get(2)) * 0.02;
        bmsData.batteryCellVoltage[17] = HexToInteger(line23.get(3)) * 0.02;
        bmsData.batteryCellVoltage[18] = HexToInteger(line23.get(4)) * 0.02;
        bmsData.batteryCellVoltage[19] = HexToInteger(line23.get(5)) * 0.02;
        bmsData.batteryCellVoltage[20] = HexToInteger(line23.get(6)) * 0.02;
        bmsData.batteryCellVoltage[21] = HexToInteger(line24.get(0)) * 0.02;
        bmsData.batteryCellVoltage[22] = HexToInteger(line24.get(1)) * 0.02;
        bmsData.batteryCellVoltage[23] = HexToInteger(line24.get(2)) * 0.02;
        bmsData.batteryCellVoltage[24] = HexToInteger(line24.get(3)) * 0.02;
        bmsData.batteryCellVoltage[25] = HexToInteger(line24.get(4)) * 0.02;
        bmsData.batteryCellVoltage[26] = HexToInteger(line24.get(5)) * 0.02;
        bmsData.batteryCellVoltage[27] = HexToInteger(line24.get(6)) * 0.02;
        bmsData.batteryCellVoltage[28] = HexToInteger(line25.get(0)) * 0.02;
        bmsData.batteryCellVoltage[29] = HexToInteger(line25.get(1)) * 0.02;
        bmsData.batteryCellVoltage[30] = HexToInteger(line25.get(2)) * 0.02;
        bmsData.batteryCellVoltage[31] = HexToInteger(line25.get(3)) * 0.02;

        return true;
    }

    public boolean parseMessage2103(String rawData) {
        ParsedRawData data = new ParsedRawData(rawData);
        if (!data.isValid()) {
            return false;
        }

        final ArrayList<String> line21 = data.getData("21");
        final ArrayList<String> line22 = data.getData("22");
        final ArrayList<String> line23 = data.getData("23");
        final ArrayList<String> line24 = data.getData("24");
        final ArrayList<String> line25 = data.getData("25");

        // Battery Cell Voltage 33-64
        bmsData.batteryCellVoltage[32] = HexToInteger(line21.get(0)) * 0.02;
        bmsData.batteryCellVoltage[33] = HexToInteger(line21.get(1)) * 0.02;
        bmsData.batteryCellVoltage[34] = HexToInteger(line21.get(2)) * 0.02;
        bmsData.batteryCellVoltage[35] = HexToInteger(line21.get(3)) * 0.02;
        bmsData.batteryCellVoltage[36] = HexToInteger(line21.get(4)) * 0.02;
        bmsData.batteryCellVoltage[37] = HexToInteger(line21.get(5)) * 0.02;
        bmsData.batteryCellVoltage[38] = HexToInteger(line21.get(6)) * 0.02;
        bmsData.batteryCellVoltage[39] = HexToInteger(line22.get(0)) * 0.02;
        bmsData.batteryCellVoltage[40] = HexToInteger(line22.get(1)) * 0.02;
        bmsData.batteryCellVoltage[41] = HexToInteger(line22.get(2)) * 0.02;
        bmsData.batteryCellVoltage[42] = HexToInteger(line22.get(3)) * 0.02;
        bmsData.batteryCellVoltage[43] = HexToInteger(line22.get(4)) * 0.02;
        bmsData.batteryCellVoltage[44] = HexToInteger(line22.get(5)) * 0.02;
        bmsData.batteryCellVoltage[45] = HexToInteger(line22.get(6)) * 0.02;
        bmsData.batteryCellVoltage[46] = HexToInteger(line23.get(0)) * 0.02;
        bmsData.batteryCellVoltage[47] = HexToInteger(line23.get(1)) * 0.02;
        bmsData.batteryCellVoltage[48] = HexToInteger(line23.get(2)) * 0.02;
        bmsData.batteryCellVoltage[49] = HexToInteger(line23.get(3)) * 0.02;
        bmsData.batteryCellVoltage[50] = HexToInteger(line23.get(4)) * 0.02;
        bmsData.batteryCellVoltage[51] = HexToInteger(line23.get(5)) * 0.02;
        bmsData.batteryCellVoltage[52] = HexToInteger(line23.get(6)) * 0.02;
        bmsData.batteryCellVoltage[53] = HexToInteger(line24.get(0)) * 0.02;
        bmsData.batteryCellVoltage[54] = HexToInteger(line24.get(1)) * 0.02;
        bmsData.batteryCellVoltage[55] = HexToInteger(line24.get(2)) * 0.02;
        bmsData.batteryCellVoltage[56] = HexToInteger(line24.get(3)) * 0.02;
        bmsData.batteryCellVoltage[57] = HexToInteger(line24.get(4)) * 0.02;
        bmsData.batteryCellVoltage[58] = HexToInteger(line24.get(5)) * 0.02;
        bmsData.batteryCellVoltage[59] = HexToInteger(line24.get(6)) * 0.02;
        bmsData.batteryCellVoltage[60] = HexToInteger(line25.get(0)) * 0.02;
        bmsData.batteryCellVoltage[61] = HexToInteger(line25.get(1)) * 0.02;
        bmsData.batteryCellVoltage[62] = HexToInteger(line25.get(2)) * 0.02;
        bmsData.batteryCellVoltage[63] = HexToInteger(line25.get(3)) * 0.02;

        return true;
    }

    public boolean parseMessage2104(String rawData) {
        ParsedRawData data = new ParsedRawData(rawData);
        if (!data.isValid()) {
            return false;
        }

        final ArrayList<String> line21 = data.getData("21");
        final ArrayList<String> line22 = data.getData("22");
        final ArrayList<String> line23 = data.getData("23");
        final ArrayList<String> line24 = data.getData("24");
        final ArrayList<String> line25 = data.getData("25");

        // Battery Cell Voltage 65-96
        try {
            bmsData.batteryCellVoltage[64] = HexToInteger(line21.get(0)) * 0.02;
            bmsData.batteryCellVoltage[65] = HexToInteger(line21.get(1)) * 0.02;
            bmsData.batteryCellVoltage[66] = HexToInteger(line21.get(2)) * 0.02;
            bmsData.batteryCellVoltage[67] = HexToInteger(line21.get(3)) * 0.02;
            bmsData.batteryCellVoltage[68] = HexToInteger(line21.get(4)) * 0.02;
            bmsData.batteryCellVoltage[69] = HexToInteger(line21.get(5)) * 0.02;
            bmsData.batteryCellVoltage[70] = HexToInteger(line21.get(6)) * 0.02;
            bmsData.batteryCellVoltage[71] = HexToInteger(line22.get(0)) * 0.02;
            bmsData.batteryCellVoltage[72] = HexToInteger(line22.get(1)) * 0.02;
            bmsData.batteryCellVoltage[73] = HexToInteger(line22.get(2)) * 0.02;
            bmsData.batteryCellVoltage[74] = HexToInteger(line22.get(3)) * 0.02;
            bmsData.batteryCellVoltage[75] = HexToInteger(line22.get(4)) * 0.02;
            bmsData.batteryCellVoltage[76] = HexToInteger(line22.get(5)) * 0.02;
            bmsData.batteryCellVoltage[77] = HexToInteger(line22.get(6)) * 0.02;
            bmsData.batteryCellVoltage[78] = HexToInteger(line23.get(0)) * 0.02;
            bmsData.batteryCellVoltage[79] = HexToInteger(line23.get(1)) * 0.02;
            bmsData.batteryCellVoltage[80] = HexToInteger(line23.get(2)) * 0.02;
            bmsData.batteryCellVoltage[81] = HexToInteger(line23.get(3)) * 0.02;
            bmsData.batteryCellVoltage[82] = HexToInteger(line23.get(4)) * 0.02;
            bmsData.batteryCellVoltage[83] = HexToInteger(line23.get(5)) * 0.02;
            bmsData.batteryCellVoltage[84] = HexToInteger(line23.get(6)) * 0.02;
            bmsData.batteryCellVoltage[85] = HexToInteger(line24.get(0)) * 0.02;
            bmsData.batteryCellVoltage[86] = HexToInteger(line24.get(1)) * 0.02;
            bmsData.batteryCellVoltage[87] = HexToInteger(line24.get(2)) * 0.02;
            bmsData.batteryCellVoltage[88] = HexToInteger(line24.get(3)) * 0.02;
            bmsData.batteryCellVoltage[89] = HexToInteger(line24.get(4)) * 0.02;
            bmsData.batteryCellVoltage[90] = HexToInteger(line24.get(5)) * 0.02;
            bmsData.batteryCellVoltage[91] = HexToInteger(line24.get(6)) * 0.02;
            bmsData.batteryCellVoltage[92] = HexToInteger(line25.get(0)) * 0.02;
            bmsData.batteryCellVoltage[93] = HexToInteger(line25.get(1)) * 0.02;
            bmsData.batteryCellVoltage[94] = HexToInteger(line25.get(2)) * 0.02;
            bmsData.batteryCellVoltage[95] = HexToInteger(line25.get(3)) * 0.02;
        } catch (Exception e) {

        }
        return true;
    }

    public boolean parseMessage2105(String rawData) {
        ParsedRawData data = new ParsedRawData(rawData);
        if (!data.isValid()) {
            return false;
        }

        final ArrayList<String> line21 = data.getData("21");
        final ArrayList<String> line22 = data.getData("22");
        final ArrayList<String> line23 = data.getData("23");
        final ArrayList<String> line24 = data.getData("24");

        bmsData.batteryMaxTemperature = HexToInteger(line22.get(0));
        bmsData.batteryMinTemperature = HexToInteger(line21.get(6));
        bmsData.batteryInletTemperature = HexToInteger(line21.get(5));
        bmsData.airbagHwireDuty = HexToInteger(line23.get(4));
        bmsData.heat1Temperature = HexToInteger(line23.get(5));
        bmsData.heat2Temperature = HexToInteger(line23.get(6));
        if (line24 != null) {
            bmsData.maxDeterioration = ((HexToInteger(line24.get(0)) << 8) + HexToInteger(line24.get(1))) * 0.1;
            bmsData.maxDeteriorationCellNo = HexToInteger(line24.get(2));
            bmsData.minDeterioration = ((HexToInteger(line24.get(3)) << 8) + HexToInteger(line24.get(4))) * 0.1;
            bmsData.minDeteriorationCellNo = HexToInteger(line24.get(5));
            bmsData.stateOfChargeDisplay = HexToInteger(line24.get(6)) * 0.5;
        }
        return true;
    }

    private class ParsedRawData {
        private HashMap<String, ArrayList<String>> mMap = new HashMap<>();

        public ParsedRawData(String rawData) {
            // Split all strings on newlines
            final String [] lines;

            if (rawData == null)
                return;

            lines = rawData.split("\\r");

            // Parse all lines
            for (String s: lines ) {
                // Split the line into items. We assume that:
                // - Headers are turned on (AT H1)
                // - Spaces are turned on (AT S1)

                // Remove all whitespace characters but ' '
                s = s.replaceAll("[\\t\\n\\x0B\\f\\r]", "");

                // Split the data into items
                String[] items = s.split("\\s");
                if ( items.length != 9 )
                {
                    // We should have 9 elements here : header + 8 data bytes
                    continue;
                }

                // Get the ECU ID
                final String ecuId = items[0];
                if( !ecuId.equals(BMS_ECU) )
                {
                    // The string is not from the BMS ECU, skip
                    continue;
                }

                // Get the first data byte
                final String byte0 = items[1];

                // Get the message data (7 bytes)
                ArrayList<String> messageData = new ArrayList<>();
                for( int i = 2; i < items.length; ++i ) {
                    // Only keep 2 bytes data values (remove "<DATA ERROR" values)
                    String item = items[i];
                    if ( item.length() == 2 ) {
                        messageData.add(items[i]);
                    }
                }

                // Add the data to the map
                mMap.put(byte0, messageData);
            }
        }

        boolean isValid() {
            return !mMap.isEmpty();
        }

        public ArrayList<String> getData(String key) {
            return mMap.get(key);
        }
    }

    public static int HexToInteger(String hex) {
        return Integer.parseInt(hex, 16);
    }

}
