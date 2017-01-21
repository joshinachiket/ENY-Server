package com.enyteam.abc.smartkitchen.Storage;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.enyteam.abc.smartkitchen.R;

import java.util.ArrayList;

/**
 * Created by abc on 16-Jan-17.
 */

public class ShopListAdapter extends BaseAdapter {

    ArrayList<StorePojo> list;
    Context context;
    SmartSharedPreference pref;

    public ShopListAdapter(Context context) {
        this.context = context;
        pref = new SmartSharedPreference();
        list = new ArrayList<StorePojo>();
        list.add(new StorePojo(1001,"WalMart","777 Story Rd, San Jose, CA",6692655212l));
        list.add(new StorePojo(1002,"Safeway","100 S Second St, San Jose, CA 95113",6692655211l));
        list.add(new StorePojo(1003,"Costco","2201 Senter Rd, San Jose, CA",6692655211l));
        list.add(new StorePojo(1004,"Whole Foods"," 777 The Alameda, San Jose, CA 95126",6692655211l));
        list.add(new StorePojo(1005,"WalMart","777 Story Rd, San Jose, CA",6692655212l));
        list.add(new StorePojo(1006,"Safeway","100 S Second St, San Jose, CA 95113",6692655211l));
        list.add(new StorePojo(1007,"Costco","2201 Senter Rd, San Jose, CA",6692655211l));
        list.add(new StorePojo(1008,"Whole Foods"," 777 The Alameda, San Jose, CA 95126",6692655211l));
        list.add(new StorePojo(1009,"WalMart","777 Story Rd, San Jose, CA",6692655211l));
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

    private class ViewHolder {
        TextView shopId;
        TextView shopName;
        TextView shopAddr;
        TextView shopPhone;
        ImageView preferred;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.shop_list_view,parent,false);

            holder.shopId = (TextView) convertView.findViewById(R.id.tv_shop_id);
            holder.shopName = (TextView) convertView.findViewById(R.id.tv_shop_name);
            holder.shopAddr = (TextView) convertView.findViewById(R.id.tv_address);
            holder.shopPhone = (TextView) convertView.findViewById(R.id.tv_phone_num);
            holder.preferred = (ImageView) convertView.findViewById(R.id.preferred);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.shopId.setText(list.get(position).storeId+"");
        holder.shopName.setText(list.get(position).storeName);
        holder.shopAddr.setText(list.get(position).storeAdd);
        holder.shopPhone.setText(list.get(position).storePhone+"");
        if(pref.getPreferedStoreKey(context)==list.get(position).storeId) {
            holder.preferred.setVisibility(View.VISIBLE);
        } else {
            holder.preferred.setVisibility(View.INVISIBLE);
        }

        return  convertView;
    }
}
