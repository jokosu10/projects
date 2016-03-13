package com.example.pin_o_memo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;

public class MyAlarmService extends Service{
	private NotificationManager mManager;
	private String id = "2";
	String title;
	int row_id;
	 
    @Override
    public IBinder onBind(Intent arg0)
    {
       // TODO Auto-generated method stub
        return null;
    }
 
    @Override
    public void onCreate()
    {
       // TODO Auto-generated method stub 
       super.onCreate();
    }
 
   @SuppressWarnings("static-access")
   @Override
   public void onStart(Intent intent, int startId)
   {
       super.onStart(intent, startId);
      
       String lat = intent.getStringExtra("lat");
       String lng = intent.getStringExtra("lng");
       
       Uri uri = Uri.parse(LocationsContentProvider.CONTENT_URI + "/" + id);
		Cursor cursor = getContentResolver().query(uri, 
				new String[] {LocationsDB.FIELD_ROW_ID, LocationsDB.FIELD_TITLE},
				 "lat=? AND lng=?", new String[] {lat, lng}, null);
		
		if( cursor != null && cursor.moveToFirst() ){
			row_id = cursor.getInt(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_ROW_ID));
			title = cursor.getString(cursor.getColumnIndexOrThrow(LocationsDB.FIELD_TITLE));
			cursor.close();
		}
       
       mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
       Intent intent1 = new Intent(this.getApplicationContext(),ProfilePage.class);
     
       Notification notification = new Notification(R.drawable.marker, title, System.currentTimeMillis());
       intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
 
       PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(),0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
       notification.flags |= Notification.FLAG_AUTO_CANCEL;
       notification.setLatestEventInfo(this.getApplicationContext(), "Pin-o-Memo", title, pendingNotificationIntent);
       
       mManager.notify(row_id, notification);
    }
 
    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
