package com.ce411.project.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ce411.project.MainActivity;
import com.ce411.project.R;
import com.ce411.project.model.Config;
import com.ce411.project.model.SessionManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseMessageService extends FirebaseMessagingService {
    static String TAG = "TTT";
    private NotificationCompat.Builder notBuilder;
    private static final int MY_NOTIFICATION_ID = 12345;
    private static final int MY_REQUEST_CODE = 100;
//    Config config = new Config();
//    private String url= config.getUrl() + "api/user/token/";
//    SessionManager sessionManager = new SessionManager(this);

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        this.notBuilder = new NotificationCompat.Builder(this);
        this.notBuilder.setAutoCancel(true);

        Log.d(TAG,"đã nhận thông báo");
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getData().get("CheckIn"));
        String ticker = "";
        String title = "";
        String text = "";
        if(remoteMessage.getData().get("CheckIn").equals("1")){
            title = "Check In";
            ticker = "Check In";
            text = "Bạn đã điểm danh thành công";
        }
        sendNotification(R.mipmap.ic_launcher,ticker,title,text);
        //Calling method to generate notification
        //sendNotification(remoteMessage.getNotification().getBody());
    }
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
       // sendRegistrationToServer(token);
    }
    public void sendNotification(int icon,String Ticker,String title,String text)  {
        this.notBuilder.setSmallIcon(icon);
        this.notBuilder.setTicker(Ticker);
        this.notBuilder.setWhen(System.currentTimeMillis()+ 10* 1000);
        this.notBuilder.setContentTitle(title);
        this.notBuilder.setContentText(text);
        this.notBuilder.setDefaults(Notification.DEFAULT_SOUND);
        // Tạo một Intent
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, MY_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        this.notBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationService  =
                (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification =  notBuilder.build();
        notificationService.notify(MY_NOTIFICATION_ID, notification);

    }
//    public void sendRegistrationToServer(String token){
//        final RequestQueue queue = Volley.newRequestQueue(this);
//        try{
//            JSONObject data = new JSONObject();
//            data.put("token",token);
//            url = url + sessionManager.getUsername();
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, data, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        String res = response.getString("status");
//                        if(res.equals("success")){
//                            Log.d("send Token","done");
//                        }else Log.d("send Token","fail");
//
//                    } catch (JSONException e){
//                        e.printStackTrace();
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
//                }
//            });
//            queue.add(jsonObjectRequest);
//        }catch (JSONException e){
//            e.printStackTrace();
//        }
//    }
}



