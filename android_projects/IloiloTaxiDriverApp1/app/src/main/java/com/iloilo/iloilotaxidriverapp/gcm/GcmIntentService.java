package com.iloilo.iloilotaxidriverapp.gcm;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.iloilo.iloilotaxidriverapp.MainActivity;
import com.iloilo.iloilotaxidriverapp.R;
import com.iloilo.iloilotaxidriverapp.RequestorsActivity;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class GcmIntentService extends IntentService {

	public int NOTIFICATION_ID;
    private NotificationManager mNotificationManager;
	public static final String GCM_ACTION_REGISTRATION = "com.google.android.c2dm.intent.REGISTRATION";
	public static final String GCM_ACTION_RECEIVE = "com.google.android.c2dm.intent.RECEIVE";
	private static final String TAG = "GcmIntentService";
	
	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                
                if(GCM_ACTION_REGISTRATION.equalsIgnoreCase(intent.getAction())) {
                	// save to SharedPreferences
                	//saveAndRegisterToServer(intent);
                } else if(GCM_ACTION_RECEIVE.equalsIgnoreCase(intent.getAction())) {
                	 sendNotification(intent.getStringExtra("message"));
                }
                Log.e(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    private void sendNotification(String msg) {
    	
    	NOTIFICATION_ID = (int) System.currentTimeMillis();
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent requestorsActivityIntent = new Intent(this, RequestorsActivity.class);
        requestorsActivityIntent.putExtra("message", msg);
        
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
     // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(RequestorsActivity.class);
     // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(requestorsActivityIntent);
        PendingIntent resultPendingIntent =
             stackBuilder.getPendingIntent(
                 0,
                 PendingIntent.FLAG_UPDATE_CURRENT
             );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("New Notification")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
        .setLights(Color.YELLOW, 3000, 3000)
        .setContentText(msg);

        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }    
}
