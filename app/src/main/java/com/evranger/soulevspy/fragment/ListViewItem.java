package com.evranger.soulevspy.fragment;

class ListViewItem {
    public final String title;
    public final String value;
    public final String buttonText;

    public ListViewItem(String title, String value) {
        this.title = title;
        this.value = value;
        this.buttonText = null;
    }

    public ListViewItem(String title, String value, String buttonText) {
        this.title = title;
        this.value = value;
        this.buttonText = buttonText;
    }
}
