package com.enyteam.abc.smartkitchen.Storage;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.enyteam.abc.smartkitchen.R;

import java.util.ArrayList;

/**
 * Created by abc on 16-Jan-17.
 */

public class OrderListAdapter extends BaseAdapter{

    private ArrayList<OrderPojo> list;
    private Context context;

    public OrderListAdapter(ArrayList<OrderPojo> l, Context c) {
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
        final ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.order_list_view,parent,false);
            holder.contentName = (TextView) convertView.findViewById(R.id.content_name_tv);
            holder.contentQty = (EditText) convertView.findViewById(R.id.et_qty);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.ref = position;
        holder.contentName.setText(list.get(position).itemName);
        holder.contentQty.setText(list.get(position).itemQty.toString());
        holder.contentQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0)
                 list.get(holder.ref).itemQty = Double.parseDouble(s.toString());
                else
                    list.get(holder.ref).itemQty = 0d;
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView contentName;
        EditText contentQty;
        int ref;
    }
}
