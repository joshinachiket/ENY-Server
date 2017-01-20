package com.enyteam.abc.smartkitchen.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.enyteam.abc.smartkitchen.R;
import com.enyteam.abc.smartkitchen.Storage.JarPojo;
import com.enyteam.abc.smartkitchen.Storage.OrderPojo;
import com.enyteam.abc.smartkitchen.Storage.OrderListAdapter;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    private ArrayList<OrderPojo> orderData;
    private ListView orderList;
    private Button nextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.smart_logo_action);
    }

    @Override
    protected void onStart() {
        super.onStart();
        orderData = new ArrayList<OrderPojo>();
        for(JarPojo obj: (ArrayList<JarPojo>) getIntent().getSerializableExtra("Content")){
            if(obj.toOrder) {
                orderData.add(new OrderPojo(obj.content,obj.maxJarQty));
            }
        }
        init();
    }

    private void init() {
        orderList = (ListView) findViewById(R.id.order_info);
        nextButton = (Button) findViewById(R.id.bt_next);
        orderList.setAdapter(new OrderListAdapter(orderData,this));
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStoreListInfo();
            }
        });
    }

    public void getStoreListInfo() {
        Intent intent = new Intent(this,ShopListingActivity.class);
        intent.putExtra("Content",orderData);
        startActivity(intent);
    }
}
