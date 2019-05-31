package com.evranger.soulevspy.fragment;

import com.evranger.soulevspy.advisor.ChargeLocation;

class ListLocationItem extends ListViewItem {
    public ChargeLocation mLocation;

    public ListLocationItem(String title, String value, ChargeLocation location) {
        super(title, value);
        mLocation = location;
    }
}
