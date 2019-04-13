package org.hexpresso.elm327.commands.protocol.obd;

import org.hexpresso.elm327.commands.AbstractCommand;
import org.hexpresso.elm327.commands.filters.RegularExpressionResponseFilter;
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
    List<String> mDtcCodes;


    public ObdGetDtcCodesCommand(String service) {
        super("03");
        addResponseFilter(new RegularExpressionResponseFilter("^([0-9A-F]{3} .*)$"));
        withAutoProcessResponse(true);
        mDtcCodes = new ArrayList<String>();
    }

    public void getDTCCodes() {
        List<String> lines = getResponse().getLines();
        if (lines != null && !lines.isEmpty()) {
            for (String response : lines) {
                String dtcCode1 = new String();
                int senderAddress = Integer.parseInt(response.substring(0,3), 16);
                String hex = response.substring(13).replace(" ", "");
//                long binary = Long.parseLong(hex, 16);
//                Integer pid = 32;
//                while (binary > 0) {
//                    if ((binary & 1) == 1) {
//                        supportedPIDS.add(pid);
//                    }
//                    --pid;
//                    binary = binary>>1;
//
//                }
//                mSupportedPIDS.put(senderAddress, supportedPIDS);
            }
        }
    }

//    public void doProcessResponse() {
//        getSupportedPIDs();
//        if (mSupportedPIDS != null) {
//            for (Integer senderAddress : mSupportedPIDS.keySet()) {
//                StringBuilder sb = new StringBuilder();
//                SortedSet<Integer> supportedPIDS = new TreeSet<Integer>(mSupportedPIDS.get(senderAddress));
//                boolean isFirst = true;
//                for (Integer pid : supportedPIDS) {
//                    if (isFirst) {
//                        isFirst = false;
//                    } else {
//                        sb.append(",");
//                    }
//                    sb.append(String.format("%02X", pid));
//                }
//                String key = "OBD.SupportedPids." + super.mCommand.substring(0,2) + "." + String.format("%03X", senderAddress);
//                CurrentValuesSingleton.getInstance().set(key, sb.toString());
//            }
//        }
//    }
}
