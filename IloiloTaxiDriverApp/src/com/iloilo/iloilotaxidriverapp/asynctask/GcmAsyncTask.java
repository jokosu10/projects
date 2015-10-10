package com.iloilo.iloilotaxidriverapp.asynctask;

import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.SharedPreferences;

import java.io.IOException;

import android.util.Log;

public class GcmAsyncTask extends AsyncTask<Void, Void, String> {
	
    private static final String GCM_SENDER_ID = "239846199688";
    private GoogleCloudMessaging gcm;
    private SharedPreferences sharedPreferences;
    
    public GcmAsyncTask(GoogleCloudMessaging g, SharedPreferences s) {
        gcm = g;
        sharedPreferences = s;
    }
    
    protected String doInBackground(Void... params) {
        String r = "";
        try {
            Log.e("", "Registering device");
            r = gcm.register(new String[] {GCM_SENDER_ID});
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("IloiloTaxi_GCM_RegistrationId", r);
            editor.commit();
            Log.e("", r);
            return r;
        } catch(IOException ex) {
            Log.e("", ex.getMessage(), ex);
        }
        return r;
    }
}
