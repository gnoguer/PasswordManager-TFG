package com.example.passwordmanager;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.passwordmanager.core.Crypter;
import com.example.passwordmanager.activites.passwords.PasswordsVaultActivity;
import com.example.passwordmanager.requests.URLs;
import com.example.passwordmanager.requests.VolleySingleton;
import com.example.passwordmanager.user.SharedPrefManager;
import com.example.passwordmanager.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class AlarmReceiver extends BroadcastReceiver {

    public static final String CHANNEL_1_ID = "channel1";

    private NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        int reqCode = intent.getExtras().getInt("code");

        if(reqCode == 0){

            int batteryPct = getBatteryPct(context);

            if(batteryPct > 70){
                checkLeaks(context);
            }

        }else if(reqCode == 1){
            sendExpirationNotification(context, intent);
        }
    }

    public int getBatteryPct(Context context){
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level * 100 / (float)scale;

        return (int) batteryPct;
    }


    public void checkLeaks(Context context){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_LEAKS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject obj = new JSONObject(response);
                            JSONArray leaksArray = obj.getJSONArray("leaked_passwords");

                            User user = SharedPrefManager.getInstance(context).getUser();

                            StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_SERVICES + "&userId=" + user.getId(),
                                    new Response.Listener<String>() {

                                        @Override
                                        public void onResponse(String response) {

                                            try {
                                                JSONObject obj = new JSONObject(response);
                                                 JSONArray servicesArray = obj.getJSONArray("passwords");
                                                 for (int i = 0; i< servicesArray.length(); i++){

                                                     JSONObject serviceJson = servicesArray.getJSONObject(i);
                                                     String decryptedPass = Crypter.getInstance(context).decrypt(serviceJson.getString("password"));

                                                     for(int j = 0; j < leaksArray.length(); j++){
                                                         if(decryptedPass.equals(leaksArray.get(j))){
                                                             sendLeakedPasswordNotification(context, serviceJson.getString("name"));
                                                         }
                                                     }
                                                 }


                                            } catch (JSONException | IOException | GeneralSecurityException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                            VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);

                        } catch (JSONException | GeneralSecurityException | IOException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void sendLeakedPasswordNotification(Context context, String name){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Your password has been leaked")
                .setContentText("Your password from " + name + " has been leaked")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);


        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(CHANNEL_1_ID);
        }

        mNotificationManager.notify(1, builder.build());
    }

    public void sendExpirationNotification(Context context, Intent intent){

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, PasswordsVaultActivity.class), 0);

        String serviceName = intent.getExtras().getString("serviceName");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Your password " + serviceName + " has expired")
                .setContentText("Go and change it")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent);

        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            builder.setChannelId(CHANNEL_1_ID);
        }

        mNotificationManager.notify(1, builder.build());
    }
}
