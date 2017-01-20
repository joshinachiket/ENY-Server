package com.enyteam.abc.smartkitchen.Storage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.enyteam.abc.smartkitchen.R;

import java.util.ArrayList;

/**
 * Created by abc on 14-Jan-17.
 */

public class ContainerStatusListAdapter extends BaseAdapter{

    private ArrayList<JarPojo> list;
    private Context context;

    public ContainerStatusListAdapter(ArrayList<JarPojo> l, Context c) {
        list = l;
        context = c;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.container_status_view,parent,false);
        TextView contentDesc = (TextView) view.findViewById(R.id.content_name_tv);
        TextView currentQty = (TextView) view.findViewById(R.id.current_qty_tv);
        TextView maxQty = (TextView) view.findViewById(R.id.max_qty_tv);
        JarPojo obj = list.get(position);
        contentDesc.setText(obj.content);
        currentQty.setText(obj.currentQty+" lb");
        maxQty.setText(obj.maxJarQty+" lb");
        return view;
    }
}
