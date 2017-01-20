package com.enyteam.abc.smartkitchen.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.enyteam.abc.smartkitchen.R;
import com.enyteam.abc.smartkitchen.Storage.SmartSharedPreference;
import com.enyteam.abc.smartkitchen.network.HttpRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {

    final String TAG = "SMART KITCHEN";
    private EditText name;
    private EditText address;
    private EditText userName;
    private EditText passWord;
    private EditText enyToken;
    private Button login;
    private Button register;
    private View progress;
    private View login_container;
    private TextView login_register_tag;

    private View customerDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initialize();
    }

    private void initialize() {
        name = (EditText) findViewById(R.id.et_prefered_name);
        address = (EditText) findViewById(R.id.et_preferred_address);
        userName = (EditText) findViewById(R.id.et_username);
        passWord = (EditText) findViewById(R.id.et_password);
        enyToken = (EditText) findViewById(R.id.et_eny_reg_key);
        customerDetail = findViewById(R.id.customer_info_view);
        login = (Button) findViewById(R.id.bt_login);
        register = (Button) findViewById(R.id.bt_register);
        progress = findViewById(R.id.progress_container);
        login_container = findViewById(R.id.login_container);
        login_register_tag = (TextView) findViewById(R.id.login_register_text);
        login_register_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(v.getTag().toString().equals("login")){
                   login.setVisibility(View.INVISIBLE);
                   register.setVisibility(View.VISIBLE);
                   customerDetail.setVisibility(View.VISIBLE);
                   v.setTag("register");
                   ((TextView)v).setText("Login as an existing Customer.");
               } else {
                   login.setVisibility(View.VISIBLE);
                   register.setVisibility(View.INVISIBLE);
                   customerDetail.setVisibility(View.GONE);
                   v.setTag("login");
                   ((TextView)v).setText("Register as a new Customer.");
               }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userName.getText().toString().trim().length()>0 && passWord.getText().toString().trim().length()>0 &&
                        name.getText().toString().trim().length()>0 && address.getText().toString().trim().length()>0 &&
                        enyToken.getText().toString().trim().length()>0) {
                    login_container.setVisibility(View.INVISIBLE);
                    progress.setVisibility(View.VISIBLE);
                    register();
                }

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userName.getText().toString().trim().length()>0 && passWord.getText().toString().trim().length()>0) {
                    login_container.setVisibility(View.INVISIBLE);
                    progress.setVisibility(View.VISIBLE);
                    login();
                }
            }
        });
    }

    private void register()  {
        String url = getResources().getString(R.string.device_registration_url).toString();
        try {
            JSONObject input = new JSONObject();
            String firebaseToken = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, firebaseToken);
            input.put("Content-Type", "application/json; charset=utf-8");
            input.put("name",name.getText().toString());
            input.put("address",address.getText().toString());
            storeCustomerDetails(name.getText().toString(),address.getText().toString());
            input.put("username", userName.getText().toString());
            input.put("password", passWord.getText().toString());
            input.put("device_token",firebaseToken);
            input.put("eny_token",enyToken.getText().toString());

            JsonObjectRequest req = createRegistrationRequestObject(url,input);
            new HttpRequest(this).addToRequestQueue(req);

        }catch (Exception e) {
            Log.e(TAG,"RegistrationActivity - Error Registering Customer");
        }
    }

    private void login() {
        String url = getResources().getString(R.string.device_login_url).toString();
        try {
            JSONObject input = new JSONObject();
            input.put("Content-Type", "application/json; charset=utf-8");
            input.put("username", userName.getText().toString());
            input.put("password", passWord.getText().toString());

            JsonObjectRequest req = loginRequest(url,input);
            new HttpRequest(this).addToRequestQueue(req);

        }catch (Exception e) {
            Log.e(TAG,"RegistrationActivity - Error Login Customer");
        }
    }

    public JsonObjectRequest createRegistrationRequestObject(String url, JSONObject input) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            storeUID(response.getString("uid"));
                            //storeCustomerDetails(response.getString("name"),response.getString("address"));
                            progress.setVisibility(View.INVISIBLE);
                            login_container.setVisibility(View.VISIBLE);
                            login.setVisibility(View.VISIBLE);
                            register.setVisibility(View.INVISIBLE);
                            customerDetail.setVisibility(View.GONE);
                            login_register_tag.setTag("login");
                            login_register_tag.setText("Register as a new Customer.");
                            Toast.makeText(getApplicationContext(),"Registration Successful!",Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.INVISIBLE);
                            login_container.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(),"Server Error!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                progress.setVisibility(View.INVISIBLE);
                login_container.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Registration Failed!",Toast.LENGTH_SHORT).show();
            }
        });
        return request;
    }

    public JsonObjectRequest loginRequest(String url, JSONObject input) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("statusCode") == getResources().getInteger(R.integer.login_successful)) {
                                storeUID(response.getString("uid"));
                                storeCustomerDetails(response.getString("name"),response.getString("address"));
                                startStatusActivity();
                            } else {
                                Toast.makeText(getApplicationContext(),"Login Failed!",Toast.LENGTH_SHORT).show();
                                progress.setVisibility(View.INVISIBLE);
                                login_container.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.INVISIBLE);
                            login_container.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(),"Server Error!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                progress.setVisibility(View.INVISIBLE);
                login_container.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Login Failed!",Toast.LENGTH_SHORT).show();
            }
        });
        return request;
    }

    public void storeUID(String uid) {
        SmartSharedPreference pref = new SmartSharedPreference();
        pref.saveUID(this,uid);
    }

    public void storeCustomerDetails(String name, String address) {
        SmartSharedPreference pref = new SmartSharedPreference();
        pref.saveCustomerDetails(this,name,address);
    }

    public void startStatusActivity(){
        Intent intent = new Intent(this,ContainerStatusActivity.class);
        startActivity(intent);
        finish();
    }
}
