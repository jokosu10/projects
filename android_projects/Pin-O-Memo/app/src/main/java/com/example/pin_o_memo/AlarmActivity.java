package com.example.pin_o_memo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class AlarmActivity extends Activity{
	
	private String id = "2";
	int row_id;
	String title;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String lat = getIntent().getStringExtra("lat");
        
        String lng = getIntent().getStringExtra("lng");
        
        Uri uri = Uri.parse(LocationsContentProvider.CONTENT_URI + "/" + id);
		Cursor cursor = getContentResolver().query(uri, 
				new String[] {LocationsDB.FIELD_ROW_ID, LocationsDB.FIELD_TITLE},
				 "lat=? AND lng=?", new String[] {lat, lng}, null);
        
		if( cursor != null && cursor.moveToFirst() ){
			row_id = cursor.getInt(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_ROW_ID));
			title = cursor.getString(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_TITLE));
			cursor.close();
		}
		
        String notifTitle = "Reminder";
        String notifContent = title;
        
        Intent notificationIntent = new Intent(getApplicationContext(),NotificationView.class);
        
        notificationIntent.putExtra("content", notifContent);
        
        notificationIntent.setData(Uri.parse("tel:/"+ row_id));
        
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
        
        NotificationManager nManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setWhen(System.currentTimeMillis())
                .setContentText(notifContent)
                .setContentTitle(notifTitle)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setTicker(notifTitle)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
 
        /** Creating a notification from the notification builder */
        Notification notification = notificationBuilder.build();
 
        /** Sending the notification to system.
        * The first argument ensures that each notification is having a unique id
        * If two notifications share same notification id, then the last notification replaces the first notification
        * */
        nManager.notify(row_id, notification);
 
        /** Finishes the execution of this activity */
        finish();

	}
}
