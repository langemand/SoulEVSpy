package org.hexpresso.obd;

import java.util.ArrayList;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-12.
 */
public class ObdMessageData {
    private ArrayList<String>  mData              = null;
    private String             mRawData           = null;
    private String             mMessageIdentifier = null;

    public ObdMessageData(String rawData) {
        // Remove all whitespace characters but ' '
        rawData = rawData.replaceAll("[\\t\\n\\x0B\\f\\r]", "");

        // Split the data into items
        String[] data = rawData.split("\\s");
        if ( data.length == 0 )
        {
            return;
        }

        // Handle cheap ELM327v2.1 clones, returning 00 00 06 53 XX... instead of 653 XX...
        if (data.length > 4 && data[2].length() == 2 && data[3].length() == 2
                && data[0].contentEquals("00") && data[1].contentEquals("00")
                && data[2].substring(0,1).contentEquals("0")) {
            String[] newdata = new String[data.length - 3];
            newdata[0] = data[2].substring(1) + data[3];
            for (int i = 4; i < data.length; ++i) {
                newdata[i-3] = data[i];
            }
            data = newdata;
        }

            // Get the message identifier (first value)
        mMessageIdentifier = data[0];

        // Get the message data (other values)
        mData = new ArrayList<>();
        for( int i = 1; i < data.length; ++i ) {
            // Only keep 2 bytes data values (remove "<DATA ERROR" values)
            String item = data[i];
            if ( item.length() == 2 ) {
                mData.add(data[i]);
            }
        }

        mRawData = rawData;
/*
        if (!rawData.matches("([0-9A-F])+")) {
            throw new NonNumericResponseException(rawData);
        }
*/
    }

    public String getMessageIdentifier() {
        return mMessageIdentifier;
    }

    public String getRawData() {
        return mRawData;
    }

    public ArrayList<String> getData() {
        return mData;
    }

    public int getSize()
    {
        return mData.size();
    }

    public int getDataByte(int index) {
        return Integer.parseInt(mData.get(index), 16);
    }
}
