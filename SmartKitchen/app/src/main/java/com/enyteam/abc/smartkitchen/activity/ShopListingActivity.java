package com.enyteam.abc.smartkitchen.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.enyteam.abc.smartkitchen.R;
import com.enyteam.abc.smartkitchen.Storage.OrderPojo;
import com.enyteam.abc.smartkitchen.Storage.ShopListAdapter;
import com.enyteam.abc.smartkitchen.Storage.SmartSharedPreference;

import java.util.ArrayList;

public class ShopListingActivity extends AppCompatActivity {

    private ListView storeList;
    private ShopListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.smart_logo_action);
        setContentView(R.layout.activity_shop_listing);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shop_list_activity,menu);
        if ((new SmartSharedPreference()).getPreferedStoreKey(this)!=0)
        {
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(true);
        } else {
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                (new SmartSharedPreference()).clearPreferedStore(this);
                notifyAdapterChange();
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void init() {
        storeList = (ListView) findViewById(R.id.lv_shop_list);
        adapter = new ShopListAdapter(this);
        storeList.setAdapter(adapter);
        storeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,final View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ShopListingActivity.this);
                dialog.setTitle(R.string.confirm);
                dialog.setMessage("Confirm Order?");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendSMS(view);
                        dialog.cancel();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

        storeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ShopListingActivity.this);
                dialog.setTitle(R.string.confirm_preferrence_dialog_heading);
                dialog.setMessage("Confirm set the store as a prefered Store?");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        (new SmartSharedPreference()).
                                savePreferedStoreKey(getBaseContext(),
                                        Integer.parseInt(((TextView)view.findViewById(R.id.tv_shop_id)).getText().toString()));
                        (new SmartSharedPreference()).
                                savePreferedStorePhone(getBaseContext(),
                                        Long.parseLong(((TextView)view.findViewById(R.id.tv_phone_num)).getText().toString()));
                        dialog.cancel();
                        notifyAdapterChange();
                        invalidateOptionsMenu();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                return true;
            }
        });
    }

    public void notifyAdapterChange() {
        adapter.notifyDataSetChanged();
    }

    public void sendSMS(View view) {
        String name;
        String address;
        String phoneNo = ((TextView)view.findViewById(R.id.tv_phone_num)).getText().toString();
        StringBuilder message = new StringBuilder();
        message.append("My Order \n");
        for(OrderPojo obj: (ArrayList<OrderPojo>) getIntent().getSerializableExtra("Content")){
           message.append("\n"+obj.itemName +"  -  "+obj.itemQty+"\n");
        }
        if( ((ArrayList<OrderPojo>) getIntent().getSerializableExtra("Content")).size()>0){
            SmsManager manager = SmsManager.getDefault();
            message.append("\n--"+(new SmartSharedPreference()).getDeliveryName(getApplicationContext()) + "\n");
            message.append((new SmartSharedPreference()).getDeliveryAddress(getApplicationContext()) + "\n");
            manager.sendTextMessage(phoneNo,null,message.toString(),null,null);
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.message_send).toString()
                    ,Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
