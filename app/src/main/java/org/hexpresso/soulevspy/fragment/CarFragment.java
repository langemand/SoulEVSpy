package org.hexpresso.soulevspy.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

import org.hexpresso.soulevspy.R;
import org.hexpresso.soulevspy.util.KiaVinParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-07.
 */
public class CarFragment extends ListFragment {

    private List<ListViewItem> mItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle(R.string.action_car_information);

        String vinStr = getArguments().getString("VIN");

        KiaVinParser vin = new KiaVinParser(getContext(), vinStr); //"KNDJX3AEXG7123456");

        mItems.add(new ListViewItem("Vehicle Identification Number", vin.getVIN()));
        mItems.add(new ListViewItem("Brand", vin.getBrand()));
        mItems.add(new ListViewItem("Model", vin.getModel()));
        mItems.add(new ListViewItem("Trim", vin.getTrim()));
        mItems.add(new ListViewItem("Engine", vin.getEngine()));
        mItems.add(new ListViewItem("Year", vin.getYear()));
        mItems.add(new ListViewItem("Sequential Number", vin.getSequentialNumber()));
        mItems.add(new ListViewItem("Production Plant", vin.getProductionPlant()));

        // initialize and set the list adapter
        setListAdapter(new ListViewAdapter(getActivity(), mItems));
    }
/*
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        ListViewItem item = mItems.get(position);

        // do something
        Toast.makeText(getActivity(), item.title, Toast.LENGTH_SHORT).show();
    }
*/
}

