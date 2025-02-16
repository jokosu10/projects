package com.example.pin_o_memo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class ProximityActivity extends Activity{
	
	String notificationTitle;
	String notificationContent;
	String tickerMessage;
	
	private String id = "2";
	String title;
	int row_id;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
 
        super.onCreate(savedInstanceState);
 
        boolean proximity_entering = getIntent().getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);
 
        int lat = getIntent().getIntExtra("lat", 0);
        int lng = getIntent().getIntExtra("lng", 0);
        
        Uri uri = Uri.parse(LocationsContentProvider.CONTENT_URI + "/" + id);
		Cursor cursor = getContentResolver().query(uri, 
				new String[] {LocationsDB.FIELD_TITLE},
				 "lat=? AND lng=?", new String[] {String.valueOf(lat), String.valueOf(lng)}, null);
		
		if( cursor != null && cursor.moveToFirst() ){
			title = cursor.getString(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_TITLE));
			cursor.close();
		}
 
        String strLocation = Double.toString(lat)+","+Double.toString(lng);
 
        if(proximity_entering){
            Toast.makeText(getBaseContext(),"Entering a reminder"  ,Toast.LENGTH_LONG).show();
            notificationTitle = "Pin-o-Memo - Entry";
            notificationContent = title;
            tickerMessage = title;
        }else{
            Toast.makeText(getBaseContext(),"Exiting a reminder"  ,Toast.LENGTH_LONG).show();
            notificationTitle = "Pin-o-Memo - Exit";
            notificationContent = title;
            tickerMessage = title;
        }
 
        Intent notificationIntent = new Intent(getApplicationContext(),ProfilePage.class);
 
        /** Adding content to the notificationIntent, which will be displayed on
        * viewing the notification
        */
        notificationIntent.putExtra("content", notificationContent );
 
        /** This is needed to make this intent different from its previous intents */
        notificationIntent.setData(Uri.parse("tel:/"+ (int)System.currentTimeMillis()));
 
        /** Creating different tasks for each notification. See the flag Intent.FLAG_ACTIVITY_NEW_TASK */
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
 
        /** Getting the System service NotificationManager */
        NotificationManager nManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
 
        /** Configuring notification builder to create a notification */
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setWhen(System.currentTimeMillis())
                .setContentText(notificationContent)
                .setContentTitle(notificationTitle)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setTicker(tickerMessage)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
 
        /** Creating a notification from the notification builder */
        Notification notification = notificationBuilder.build();
 
        /** Sending the notification to system.
        * The first argument ensures that each notification is having a unique id
        * If two notifications share same notification id, then the last notification replaces the first notification
        * */
        nManager.notify((int)System.currentTimeMillis(), notification);
 
        /** Finishes the execution of this activity */
        finish();
    }
}
