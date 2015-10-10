package com.f8mobile.community_app.mobile.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.f8mobile.community_app.mobile.asynctask.CommunityAppAsyncTaskHttpCall;
import com.f8mobile.community_app.mobile.database.SqliteDatabaseHelper;
import com.f8mobile.community_app.mobile.model.UserDataModel;
import com.f8mobile.f8mobile.PreferenceConnector;
import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class SyncService extends IntentService {
	
	private SqliteDatabaseHelper sqliteDatabaseHelper;
	
	public SyncService() {
       super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("SyncService", "About to execute SyncServiceTask"); 
        
        sqliteDatabaseHelper = new SqliteDatabaseHelper(this);
        
		PreferenceConnector.writeBoolean(this, PreferenceConnector.SYNC_FINISHED, false);
		PreferenceConnector.writeBoolean(this, PreferenceConnector.SYNC_RUNNING, true);
		
		String maxId = sqliteDatabaseHelper.getMaxId();
 	   
 	    String[] params = new String[2];
        params[0] = "method=14";
        params[1] = "POST";
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("max_id", maxId));
       
        String jsonReturn;
		try {
			jsonReturn = new CommunityAppAsyncTaskHttpCall(nameValuePairs).execute(params).get();
			Log.e("SyncService", "finished asynctask");
	        if(!TextUtils.isEmpty(jsonReturn) && jsonReturn != null && !jsonReturn.equalsIgnoreCase("null")) {
				// create UserDataModel per json iteration
				try {
					JSONArray jArr = new JSONArray(jsonReturn);
					if(jArr != null && jArr.length() > 0) {
						for(int i = 0; i < jArr.length(); i++) {
							
							JSONObject j = jArr.getJSONObject(i);
							if(j != null) {
									UserDataModel user = new UserDataModel();
									user.setId(j.getString("id"));
									user.setFname(j.getString("fname"));
									user.setLname(j.getString("lname"));
									user.setCellNo(j.getString("cell_no"));
									user.setGcmRegId(j.getString("gcm_registration_id"));
									Log.e("SyncService", "saving to database");
									sqliteDatabaseHelper.insertToUsersTable(user);										
								}
							}
							PreferenceConnector.writeBoolean(getApplicationContext(), PreferenceConnector.SYNC_FINISHED, true);
				    		PreferenceConnector.writeBoolean(getApplicationContext(), PreferenceConnector.SYNC_RUNNING, false);
				    		PreferenceConnector.writeBoolean(getApplicationContext(), PreferenceConnector.ALARM_FOR_SYNC_IS_SET, true);	
							
						}
					} catch (JSONException e) {
						Log.e("","", e);
						PreferenceConnector.writeBoolean(getApplicationContext(), PreferenceConnector.SYNC_FINISHED, false);
						PreferenceConnector.writeBoolean(getApplicationContext(), PreferenceConnector.SYNC_RUNNING, false);
						PreferenceConnector.writeBoolean(getApplicationContext(), PreferenceConnector.ALARM_FOR_SYNC_IS_SET, false);
					}
				}
		} catch (InterruptedException e1) {
			Log.e("","", e1);
			PreferenceConnector.writeBoolean(getApplicationContext(), PreferenceConnector.SYNC_FINISHED, false);
			PreferenceConnector.writeBoolean(getApplicationContext(), PreferenceConnector.SYNC_RUNNING, false);
			PreferenceConnector.writeBoolean(getApplicationContext(), PreferenceConnector.ALARM_FOR_SYNC_IS_SET, false);
		} catch (ExecutionException e1) {
			Log.e("","", e1);
			PreferenceConnector.writeBoolean(getApplicationContext(), PreferenceConnector.SYNC_FINISHED, false);
			PreferenceConnector.writeBoolean(getApplicationContext(), PreferenceConnector.SYNC_RUNNING, false);
			PreferenceConnector.writeBoolean(getApplicationContext(), PreferenceConnector.ALARM_FOR_SYNC_IS_SET, false);
		}
    }    
}
