package com.evranger.soulevspy.fragment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.evranger.soulevspy.R;

import java.util.List;

class ListViewAdapter extends ArrayAdapter<ListViewItem> {

    View.OnClickListener mButtonOnClickListener;

    public ListViewAdapter(Context context, List<ListViewItem> items) {
        super(context, R.layout.listview_item, items);
        mButtonOnClickListener = null;
    }

    public ListViewAdapter(Context context, List<ListViewItem> items, @Nullable View.OnClickListener l) {
        super(context, R.layout.listview_item, items);
        mButtonOnClickListener = l;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_item, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvValue = (TextView) convertView.findViewById(R.id.tvValue);
            viewHolder.tvButton = (ImageButton) convertView.findViewById(R.id.tvButton);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        try {
            ListViewItem item = getItem(position);
            viewHolder.tvTitle.setText(item.title);
            viewHolder.tvValue.setText(item.value);
            if (item.buttonText != null) {
//                viewHolder.tvButton.setText(item.buttonText);
                viewHolder.tvButton.setTag(position);
                viewHolder.tvButton.setOnClickListener(mButtonOnClickListener);
                viewHolder.tvButton.setVisibility(View.VISIBLE);
//                viewHolder.tvButton.setWidth(48);
            } else {
                viewHolder.tvButton.setVisibility(View.GONE);
                viewHolder.tvButton.setOnClickListener(null);
//                viewHolder.tvButton = null;
            }
        } catch (Exception ex) {
            // No such item...
            int i = 0;
        }

        return convertView;
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     *
     * @see http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
     */
    private static class ViewHolder {
        TextView tvTitle;
        TextView tvValue;
        ImageButton tvButton;
    }
}
