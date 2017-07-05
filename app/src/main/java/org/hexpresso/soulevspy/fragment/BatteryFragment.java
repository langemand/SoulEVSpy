package org.hexpresso.soulevspy.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.util.SortedList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hexpresso.obd.ObdMessageFilter;
import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.obd.StateOfChargePreciseMessageFilter;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-01.
 */
public class BatteryFragment extends ListFragment implements ObdMessageFilter.ObdMessageFilterListener {

    private List<ListViewItem> mItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle(R.string.action_battery);

        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        Map<String, Object> battVals = vals.find("battery.");
        SortedSet<String> keyset = new TreeSet<String>(battVals.keySet());

        for (String key : keyset) {
            mItems.add(new ListViewItem(key, new String(battVals.get(key).toString())));
        }
        // initialize and set the list adapter
        setListAdapter(new ListViewAdapter(getActivity(), mItems));

    }

    public void onMessageReceived(ObdMessageFilter messageFilter) {
//        StateOfChargePreciseMessageFilter soc = (StateOfChargePreciseMessageFilter) messageFilter;
//        if (soc == null)
//            return;

//        mItems.add(new ListViewItem("Precise SOC", new Double(soc.getSOC()).toString()));
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        Map<String, Object> battVals = vals.find("battery.");
        SortedSet<String> keyset = new TreeSet<String>(battVals.keySet());
        for (String key : keyset) {
            mItems.add(new ListViewItem(key, new String(battVals.get(key).toString())));
        }
        // initialize and set the list adapter
//        setListAdapter(new ListViewAdapter(getActivity(), mItems));

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_battery, container, false);
//    }
}
