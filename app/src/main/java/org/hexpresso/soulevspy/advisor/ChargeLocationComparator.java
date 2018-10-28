package org.hexpresso.soulevspy.advisor;

import java.util.Comparator;

public class ChargeLocationComparator implements Comparator<ChargeLocation> {
    @Override
    public int compare(ChargeLocation c1, ChargeLocation c2) {
        if(c1.get_distFromLookupPos() > c2.get_distFromLookupPos()){
            return 1;
        } else {
            return -1;
        }
    }
}
