package com.enyteam.abc.smartkitchen.services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.enyteam.abc.smartkitchen.R;
import com.enyteam.abc.smartkitchen.Storage.SmartSharedPreference;
import com.enyteam.abc.smartkitchen.activity.ContainerStatusActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by abc on 13-Jan-17.
 */

public class SmartFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM_SERVICE";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(!isApplicationRunning()) {
            if((new SmartSharedPreference()).getPreferedStoreKey(getApplicationContext())!=0) {
                //if a store is marked as a favorite  store
                try {
                    sendTextMessage(remoteMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                buildAndShowNotification(remoteMessage);
            }
        }
    }

    public boolean isApplicationRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(getResources().getString(R.string.app_package));
    }


    public void buildAndShowNotification(RemoteMessage message) {
        Intent intent = new Intent(this, ContainerStatusActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, R.integer.app_notification_id,
                intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.smart_logo_)
                .setContentTitle(message.getNotification().getTitle())
                .setContentText(message.getNotification().getBody())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(R.integer.app_notification_id);
        notificationManager.notify(R.integer.app_notification_id, notificationBuilder.build());
    }

    /*
        {order:[
              {"content":"Sugar","quantity":"2.5"},
              {"content":"Tea","quantity":"3"},
              ]
        }
     */
    public void sendTextMessage(RemoteMessage message) throws JSONException {
        String phoneNo =  (new SmartSharedPreference()).getPreferedStorePhone(getApplicationContext())+"";
        SmsManager manager = SmsManager.getDefault();
        StringBuilder order = new StringBuilder();
        JSONObject obj = new JSONObject(message.getData());
        JSONArray arr = obj.getJSONArray("order");
        for(int i=0; i< arr.length(); i++ ) {
            JSONObject objectInArray = arr.getJSONObject(i);
            order.append(objectInArray.get("content")+"  -   "+objectInArray.get("quantity")+"\n");
        }

        /*manager.sendTextMessage(phoneNo,null,order,null,null);*/

    }
}
