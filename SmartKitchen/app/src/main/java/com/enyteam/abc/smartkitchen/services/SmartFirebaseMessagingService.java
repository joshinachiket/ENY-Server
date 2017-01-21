package com.enyteam.abc.smartkitchen.services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;


import com.enyteam.abc.smartkitchen.R;
import com.enyteam.abc.smartkitchen.Storage.JarPojo;
import com.enyteam.abc.smartkitchen.Storage.SmartSharedPreference;
import com.enyteam.abc.smartkitchen.activity.ContainerStatusActivity;
import com.enyteam.abc.smartkitchen.activity.RegistrationActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc on 13-Jan-17.
 */

public class SmartFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM_SERVICE";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendButtonPressedMessage(remoteMessage);
        if(!isApplicationRunning()) {
            if((new SmartSharedPreference()).getUID(getApplicationContext())!=null){
                Log.d("FIREBASE",(new SmartSharedPreference()).getPreferedStoreKey(getApplicationContext())+"");
                if((new SmartSharedPreference()).getPreferedStoreKey(getApplicationContext())!=0) {
                    //if a store is marked as a favorite  store
                    try {
                        placeOrder(remoteMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    buildAndShowNotification(remoteMessage);
                }
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
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, R.integer.app_notification_id,
                intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.smart_logo_)
                .setContentTitle(message.getData().get("title"))
                .setContentText(message.getData().get("body"))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(R.integer.app_notification_id);
        notificationManager.notify(R.integer.app_notification_id, notificationBuilder.build());
    }

    public void sendButtonPressedMessage(RemoteMessage remote) {
        SmsManager manager = SmsManager.getDefault();
        String phoneNo = remote.getData().get("button_user");
        String message = "Concerned Person Notified!";
        sendTextMessage(message,phoneNo);
        Log.d(TAG,"Message Sent to :"+phoneNo+"\n"+message);
    }

    public void placeOrder(RemoteMessage message) throws JSONException {
        String phoneNo =  (new SmartSharedPreference()).getPreferedStorePhone(getApplicationContext())+"";
        StringBuilder order = new StringBuilder();
        order.append("My Order \n");
        JSONObject info = new JSONObject(message.getData().get("info"));
        ArrayList<JarPojo> list = populateListView(info);
        for(int i=0 ; i<list.size(); i++) {
            if(list.get(i).currentQty < (list.get(i).maxJarQty/getResources().getInteger(R.integer.container_threshold_divider))) {
                order.append(i+". "+list.get(i).content+"     :     "+(list.get(i).maxJarQty-list.get(i).currentQty)+" lb\n");
            }
        }
        if(list.size()>0) {
            order.append("\n--"+(new SmartSharedPreference()).getDeliveryName(getApplicationContext()) + "\n");
            order.append((new SmartSharedPreference()).getDeliveryAddress(getApplicationContext()) + "\n");
            showNotificationForCommoditiesOrdered(list);
            sendTextMessage(order.toString(),phoneNo);
        }
    }

    public void sendTextMessage(String message, String to){
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(to, null, message.toString(), null, null);
    }

    public void showNotificationForCommoditiesOrdered(ArrayList<JarPojo> list) {
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        inboxStyle.setBigContentTitle("Commodities Order!");
        for(int i=0 ; i<list.size() && i<5; i++) {
            if(list.get(i).currentQty < (list.get(i).maxJarQty/getResources().getInteger(R.integer.container_threshold_divider)))
                inboxStyle.addLine(list.get(i).content+"   :    "+list.get(i).currentQty*2);
        }
        if(list.size()>4)
            inboxStyle.setSummaryText("+"+(list.size()-4)+" more");
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.smart_logo_noti)
                        .setContentTitle("ENY's Smart Kitchen")
                        .setContentText("Someone Ordered Grocery!.")
                        .setStyle(inboxStyle);

        NotificationManager notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }

    public ArrayList<JarPojo> populateListView(JSONObject input) throws JSONException {
        ArrayList<JarPojo> listData = new ArrayList<JarPojo>();
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
        return listData;
    }
}
