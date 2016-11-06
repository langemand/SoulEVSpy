package org.hexpresso.soulevspy.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hexpresso.soulevspy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-01.
 */
public class BatteryFragment extends ListFragment {

    private List<ListViewItem> mItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle(R.string.action_battery);

        Double soc = getArguments().getDouble("SOC");

        mItems.add(new ListViewItem("SOC", soc.toString()));

        // initialize and set the list adapter
        setListAdapter(new ListViewAdapter(getActivity(), mItems));

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_battery, container, false);
//    }
}
