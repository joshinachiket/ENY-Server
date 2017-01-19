package com.enyteam.abc.smartkitchen.network;


import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
;

/**
 * Created by abc on 13-Jan-17.
 */

public class HttpRequest {

    private RequestQueue queue = null;
    private Context context;

    public HttpRequest(Context context) {
        this.context = context;
        if(queue==null) {
            queue = Volley.newRequestQueue(context);
        }
    }

    public <T> void addToRequestQueue(Request<T> req) {
        queue.add(req);
    }
}
