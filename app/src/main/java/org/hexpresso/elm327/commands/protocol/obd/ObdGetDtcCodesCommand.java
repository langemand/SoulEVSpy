package org.hexpresso.elm327.commands.protocol.obd;

import org.hexpresso.elm327.commands.AbstractCommand;
import org.hexpresso.elm327.commands.filters.RegularExpressionResponseFilter;
import org.hexpresso.obd.ObdMessageData;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Henrik R. Scheel <henrik.scheel@spjeldager.dk> on 2019-04-08.
 */
public class ObdGetDtcCodesCommand extends AbstractCommand {
    Map<Integer, List<String>> mDtcCodes;

    public ObdGetDtcCodesCommand() {
        super("03");
        addResponseFilter(new RegularExpressionResponseFilter("^([0-9A-F]{3} .*)$"));
        withAutoProcessResponse(true);
        mDtcCodes = null;
    }

    public void getDTCCodes() {
        try {
            mDtcCodes = new HashMap<>();
            List<String> lines = getResponse().getLines();
            if (lines != null && !lines.isEmpty()) {
                Map<Integer, Integer> numCodes = new HashMap<>();
                for (String response : lines) {
                    if (response.length() > 5) {
                        int senderAddress = Integer.parseInt(response.substring(0, 3), 16);
                        int minLineLen = 12;
                        if (!mDtcCodes.containsKey(senderAddress)) {
                            mDtcCodes.put(senderAddress, new ArrayList<String>());
                            numCodes.put(senderAddress, Integer.parseInt(response.substring(10,12), 16));
                        } else {
                            minLineLen=6;
                        }
                        String hex = response.substring(minLineLen+1).replaceAll("[\\s\\t\\n\\x0B\\f\\r]", "");
                        while (hex.length() > 0 && mDtcCodes.get(senderAddress).size() < numCodes.get(senderAddress)) {
                            StringBuilder dtcCode = new StringBuilder();
                            int firstNibble = Integer.parseInt(hex.substring(0,1),16);
                            int firstChar = (firstNibble >> 2) & 0x03;
                            switch(firstChar) {
                                case (0) : dtcCode.append('P'); break;
                                case (1) : dtcCode.append('C'); break;
                                case (2) : dtcCode.append('B'); break;
                                default : dtcCode.append('U');
                            }
                            int ascii = (int)'0';
                            ascii += + (firstNibble) & 0x03;
                            char digit = (char)ascii;
                            dtcCode.append(digit);
                            dtcCode.append(hex.substring(1,4));
                            mDtcCodes.get(senderAddress).add(dtcCode.toString());
                            hex=hex.substring(4);
                        }
                    }
                }
            }
        }
        catch(Exception ex) {
            //
        }
    }

    public void doProcessResponse() {
        getDTCCodes();
        if (mDtcCodes != null) {
            for (Integer senderAddress : mDtcCodes.keySet()) {
                StringBuilder sb = new StringBuilder();
                SortedSet<String> dtcCodes = new TreeSet<String>(mDtcCodes.get(senderAddress));
                boolean isFirst = true;
                for (String code : dtcCodes) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        sb.append(",");
                    }
                    sb.append(code);
                }
                String key = "OBD.DtcCodes." + String.format("%03X", senderAddress);
                CurrentValuesSingleton.getInstance().set(key, sb.toString());
                skip(true);
            }
        }
    }
}
