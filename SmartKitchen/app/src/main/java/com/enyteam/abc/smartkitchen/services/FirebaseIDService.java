package com.enyteam.abc.smartkitchen.services;

import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.enyteam.abc.smartkitchen.R;
import com.enyteam.abc.smartkitchen.Storage.SmartSharedPreference;
import com.enyteam.abc.smartkitchen.network.HttpRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abc on 13-Jan-17.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("REGISTRATION_TOKEN", "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        SmartSharedPreference pref = new SmartSharedPreference();
        String uid = pref.getUID(getApplicationContext());
        if(uid!=null) {
            String url = getApplicationContext().getResources().getString(R.string.token_update_url);
            JsonObjectRequest req = null;
            try {
                req = createResponse(url,uid,token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new HttpRequest(this).addToRequestQueue(req);
        }

    }

    public JsonObjectRequest createResponse(String url, String uid, String token) throws JSONException {
        JSONObject input = new JSONObject();
        input.put("uid",uid);
        input.put("token",token);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, input,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Not needed as only new token is to be updated
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        return request;
    }
}
