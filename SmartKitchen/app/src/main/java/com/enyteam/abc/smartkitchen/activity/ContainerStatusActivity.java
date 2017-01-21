package com.enyteam.abc.smartkitchen.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.enyteam.abc.smartkitchen.R;
import com.enyteam.abc.smartkitchen.Storage.JarPojo;
import com.enyteam.abc.smartkitchen.Storage.ContainerStatusListAdapter;
import com.enyteam.abc.smartkitchen.Storage.SmartSharedPreference;
import com.enyteam.abc.smartkitchen.network.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContainerStatusActivity extends AppCompatActivity {

    private static final String TAG = "SMART KITCHEN";
    public static String UID;
    private static boolean activityVisible;

    private HttpRequest requestQueue;
    private View noDeviceRegisteredView;
    private View progressView;
    private View detailContainerView;
    private View orderAndRefreshView;
    private View registerContainerView;
    private View serverErrorView;

    private ListView containerDetailList;
    private ContainerStatusListAdapter listAdapter;

    private Button refreshButton;
    private Button orderButton;
    private Button registerContainerButton;

    private ArrayList<JarPojo> listData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_status);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.smart_logo_action);
        init();
        requestContainerStatus();
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    private void setActivityVisible(boolean v){
        activityVisible = v;
    }

    @Override
    protected void onStart() {
        super.onStart();
        setActivityVisible(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setActivityVisible(false);
    }

    private void init() {
        requestQueue = new HttpRequest(this);
        UID = (new SmartSharedPreference()).getUID(getApplicationContext()).toString();

        noDeviceRegisteredView= findViewById(R.id.no_device_registered_placehold);
        progressView= findViewById(R.id.progress_container);
        detailContainerView=findViewById(R.id.container_detail_view);
        orderAndRefreshView=findViewById(R.id.order_and_refresh_button_view);
        registerContainerView=findViewById(R.id.container_register_view);
        serverErrorView = findViewById(R.id.error_placehold);

        containerDetailList= (ListView) findViewById(R.id.container_info);
        listData = new ArrayList<JarPojo>();
        listAdapter = new ContainerStatusListAdapter(listData,this);
        containerDetailList.setAdapter(listAdapter);
        containerDetailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox box = (CheckBox) view.findViewById(R.id.order_bt);
                if(!box.isChecked()){
                    listData.get(position).toOrder = true;
                    box.setChecked(true);
                } else {
                    listData.get(position).toOrder = false;
                    box.setChecked(false);
                }
                System.out.println(listData);
            }
        });

        refreshButton= (Button) findViewById(R.id.bt_refresh);
        orderButton= (Button) findViewById(R.id.bt_order);
        registerContainerButton= (Button) findViewById(R.id.bt_register_device);

        /** Button click listners **/
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestContainerStatus();
            }
        });
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(JarPojo obj: listData) {
                    if(obj.toOrder){
                        orderSelectedStuffs();
                        break;
                    } else {
                        Toast.makeText(getApplicationContext(),"Select Commodity to order!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        registerContainerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewContainer();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_register:
                registerNewContainer();
                return true;
            case R.id.menu_delete:
                try {
                    deleteContainer();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.logout:
                    logoutUser();
                return true;
            case R.id.menu_deregisterUser:
                    deregisterUser();
                    deregisterNeura();
                return true;
            case R.id.menu_neura_activate:
                    //changeNeuraConfiguration();
                launchNeuraApplication();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchNeuraApplication() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getResources().getString(R.string.neura_activity));
        if (launchIntent != null) {
            startActivity(launchIntent);//null pointer check in case package name was not found
        } else {
            Toast.makeText(ContainerStatusActivity.this,"Cannot find Neura!",Toast.LENGTH_SHORT).show();
        }
    }

    public void setInitialActivityState() {
        noDeviceRegisteredView.setVisibility(View.INVISIBLE);
        progressView.setVisibility(View.VISIBLE);
        detailContainerView.setVisibility(View.INVISIBLE);
        orderAndRefreshView.setVisibility(View.INVISIBLE);
        registerContainerView.setVisibility(View.INVISIBLE);
        serverErrorView.setVisibility(View.INVISIBLE);
    }

    public void setRegisterContainerView() {
        noDeviceRegisteredView.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.INVISIBLE);
        detailContainerView.setVisibility(View.INVISIBLE);
        orderAndRefreshView.setVisibility(View.INVISIBLE);
        registerContainerView.setVisibility(View.VISIBLE);
        serverErrorView.setVisibility(View.INVISIBLE);
    }

    public void setServerErrorView() {
        noDeviceRegisteredView.setVisibility(View.INVISIBLE);
        progressView.setVisibility(View.INVISIBLE);
        detailContainerView.setVisibility(View.INVISIBLE);
        orderAndRefreshView.setVisibility(View.VISIBLE);
        registerContainerView.setVisibility(View.INVISIBLE);
        serverErrorView.setVisibility(View.VISIBLE);
    }

    public void setOrderAndRefreshView() {
        noDeviceRegisteredView.setVisibility(View.INVISIBLE);
        progressView.setVisibility(View.INVISIBLE);
        detailContainerView.setVisibility(View.VISIBLE);
        orderAndRefreshView.setVisibility(View.VISIBLE);
        registerContainerView.setVisibility(View.INVISIBLE);
        serverErrorView.setVisibility(View.INVISIBLE);

    }

    public void logoutUser() {
        String url = getResources().getString(R.string.logout_user_url).toString();
        Log.d(TAG,"ContainerStatusActivity - "+url);

        JSONObject obj = new JSONObject();
        try {
            obj.put("Content-Type", "application/json; charset=utf-8");
            obj.put("uid",(new SmartSharedPreference()).getUID(getApplicationContext()));
            obj.put("username",(new SmartSharedPreference()).getUname(getApplicationContext()));
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getInt("statusCode") == getResources().getInteger(R.integer.registration_successful)) {
                                    Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                                    (new SmartSharedPreference()).clearSharedPreference(getApplicationContext());
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Log.d(TAG, "Error: Status code unsuccessful" );
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                setServerErrorView();
                                Toast.makeText(getApplicationContext(),"Logout Failed!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error: " + error.getMessage());

                    Toast.makeText(getApplicationContext(),"Server Error!",Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.addToRequestQueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void  deregisterUser(){
        String url = getResources().getString(R.string.deregister_user_url).toString();
        Log.d(TAG,"ContainerStatusActivity - "+url);

        JSONObject obj = new JSONObject();
        try {
            obj.put("uid",(new SmartSharedPreference()).getUID(getApplicationContext()));
            obj.put("username",(new SmartSharedPreference()).getUname(getApplicationContext()));
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,(JSONObject) null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getInt("statusCode") == getResources().getInteger(R.integer.registration_successful)) {
                                    Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                                    (new SmartSharedPreference()).clearUID(getApplicationContext());
                                    startActivity(intent);
                                    finish();
                                } else{
                                    Log.d(TAG, "Error: Status code unsuccessful" );
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),"Deregistration Failed!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),"Server Error!",Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.addToRequestQueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestContainerStatus() {
        setInitialActivityState();
        String url = getResources().getString(R.string.container_status_url).toString()+
                "/"+(new SmartSharedPreference()).getUID(this);
        Log.d(TAG,"ContainerStatusActivity - "+url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,(JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseDataAndSetView(response);
                        Log.d(TAG,response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                setServerErrorView();
            }
        });
        requestQueue.addToRequestQueue(request);
    }

    public JSONObject getStatusRequestObj() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("uid",(new SmartSharedPreference()).getUID(getApplicationContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public void parseDataAndSetView(JSONObject response) {
        try {
            int containerCount = response.getInt("count");
            if(containerCount==0) {
                setRegisterContainerView();
            } else{
                populateListView(response);
                setOrderAndRefreshView();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            setServerErrorView();
        }
    }

    public void populateListView(JSONObject input) throws JSONException {
        listData.clear();
        JSONArray arr = input.getJSONArray("containers");
        for( int i=0 ; i<arr.length();i++) {
            JarPojo obj = new JarPojo();
            obj.jarId = arr.getJSONObject(i).get("tagId").toString();
            obj.content = arr.getJSONObject(i).get("content_desc").toString();
            obj.currentQty = arr.getJSONObject(i).getDouble("cur_qty");
            obj.maxJarQty = arr.getJSONObject(i).getDouble("max_qty");
            obj.toOrder = false;
            listData.add(obj);
        }
        listAdapter.notifyDataSetChanged();
    }

    public void registerNewContainer() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.register_container_dialog);
        dialog.setTitle(R.string.register_container_dialog_title);
        dialog.setCanceledOnTouchOutside(false);
        final EditText content = (EditText) dialog.findViewById(R.id.dig_content_name_tv);
        final EditText weight = (EditText) dialog.findViewById(R.id.dig_max_qty_tv);
        final EditText tagId = (EditText) dialog.findViewById(R.id.dig_id_tv);
        Button register = (Button) dialog.findViewById(R.id.bt_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                setInitialActivityState();
                makeContainerRegistrationRequest(tagId.getText().toString(),
                                                content.getText().toString(),
                                                Double.parseDouble(weight.getText().toString()));
            }
        });
        Button cancel = (Button) dialog.findViewById(R.id.bt_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void deleteContainer() throws JSONException {
        JSONArray arr = new JSONArray();
        for(JarPojo obj: listData) {
            if(obj.toOrder==true) {
                JSONObject jobj = new JSONObject();
                jobj.put("tagId",obj.jarId);
                arr.put(jobj);
            }
        }
        if(arr.length()>0) {
            JSONObject main = new JSONObject();
            main.put("uid",UID);
            main.put("containers", arr);
            Log.d(TAG,main.toString());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    getResources().getString(R.string.delete_container_url).toString(),
                    main,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            requestContainerStatus();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error: " + error.getMessage());
                }
            });
            requestQueue.addToRequestQueue(request);
        } else {
            Toast.makeText(getApplicationContext(),"Nothing to Delete",Toast.LENGTH_LONG).show();
        }
    }

    public void makeContainerRegistrationRequest(String tagId, String content, double weight) {
        JSONObject input = new JSONObject();
        try {
            input.put("uid",UID);
            input.put("tagId", tagId);
            input.put("content_desc", content);
            input.put("max_qty",weight);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                getResources().getString(R.string.register_container_url).toString(),
                input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        requestContainerStatus();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
             }
        });
        requestQueue.addToRequestQueue(request);
    }

    public void orderSelectedStuffs(){
        Intent intent = new Intent(this,OrderActivity.class);
        intent.putExtra("Content",listData);
        startActivity(intent);
    }

    public void changeNeuraConfiguration() {
        Intent intent = new Intent(this,NeuraSettingActivity.class);
        startActivity(intent);
    }

    public void  deregisterNeura() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction("com.enyteam.DEREGISTER_ENY");
        sendBroadcast(intent); 
    }
}
