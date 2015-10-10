package com.f8mobile.f8mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.f8mobile.community_app.mobile.asynctask.CommunityAppAsyncTaskHttpCall;
import com.f8mobile.community_app.mobile.database.SqliteDatabaseHelper;
import com.f8mobile.community_app.mobile.fragment.ChatFragment;
import com.f8mobile.community_app.mobile.fragment.MemberDetailsFragment;
import com.f8mobile.community_app.mobile.fragment.StoreDetailsFragment;
import com.f8mobile.community_app.mobile.model.UserDataModel;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class UserStoredDetails extends Activity {
	
	private String userData, viewType, message;
	private ActionBar.Tab chatTab, membersTab, storeTab;
	private ChatFragment chatFragment;
	private MemberDetailsFragment memberDetailsFragment;
	private StoreDetailsFragment storeDetailsFragment;
	private Handler mHandler;
	private ProgressDialog progressDialog;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.user_store_details_layout);
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.gravity = Gravity.RIGHT | Gravity.TOP;
		params.width = 950;
		getWindow().setAttributes(params);
		
		progressDialog = new ProgressDialog(getApplicationContext());
        userData = getIntent().getStringExtra("details");
	    viewType = getIntent().getStringExtra("type");
	    
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                	case 6:
                		/*String res = (String) msg.obj;
                		if(!TextUtils.isEmpty(res)) {
    						try {
								JSONArray jsonArray = new JSONArray(res);
								if(jsonArray != null && jsonArray.length() > 0) {
	    							for(int i = 0; i < jsonArray.length(); i++) {
	    								userData = jsonArray.getJSONObject(i).toString();
	    							}
	    						}
							} catch (JSONException e) {
								e.printStackTrace();
							}
    					}*/
                		break;
                }
            }
        };    
	    
	    
	    if(userData == null && TextUtils.isEmpty(userData)) { // will only happen if opened from gcm push
	    	message = getIntent().getStringExtra("sender_id");
	    	if(message != null && !TextUtils.isEmpty(message)) {
	    		// call community http asynctask, get user id from message
	    		// check first if present in database
	    		SqliteDatabaseHelper sqliteDatabaseHelper = new SqliteDatabaseHelper(getApplicationContext());
	    		UserDataModel userDataModel = sqliteDatabaseHelper.returnUserData(message);
	    		
	    		if(userDataModel != null ) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("id", userDataModel.getId());
					map.put("fname", userDataModel.getFname());
					map.put("lname", userDataModel.getLname());
					map.put("contact", userDataModel.getContact());
					map.put("address", userDataModel.getAddress());
					map.put("country", userDataModel.getCountry());
					map.put("gender", userDataModel.getSex());
					map.put("cell_no", userDataModel.getCellNo());
					map.put("gcm_registration_id", userDataModel.getGcmRegId());
					
					JSONObject j = new JSONObject(map);
					userData = j.toString();
					
	    		} else {
	    			
		    		String[] p = new String[2];
		    		p[0] = "method=12";
		    		p[1] = "POST";
		    		
		    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				    nameValuePairs.add(new BasicNameValuePair("id", message));
				    
				    try {
						String res = new CommunityAppAsyncTaskHttpCall(progressDialog, nameValuePairs, mHandler, 6).execute(p).get();
						if(!TextUtils.isEmpty(res)) {
							try {
								JSONArray jsonArray = new JSONArray(res);
								if(jsonArray != null && jsonArray.length() > 0) {
	    							for(int i = 0; i < jsonArray.length(); i++) {
	    								userData = jsonArray.getJSONObject(i).toString();
	    							}
	    							
	    							JSONObject j = new JSONObject(userData);
	    							UserDataModel u = new UserDataModel();
	    							u.setId(j.getString("id"));
	    							u.setFname(j.getString("fname"));
	    							u.setLname(j.getString("lname"));
	    							u.setCellNo(j.getString("cell_no"));
	    							u.setGcmRegId(j.getString("gcm_registration_id"));
	    							sqliteDatabaseHelper.insertToUsersTable(u);
	    						}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
	    		}					
	    	}    	
	    } 
	    
	    Bundle arguments = new Bundle();
	    arguments.putString("data", userData);
    	
	    Bundle argumentsChat = new Bundle();
	    argumentsChat.putString("data", userData);
	    argumentsChat.putString("gcm_registration_id_this_device", PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.GCM_REG_ID, ""));
	    
	    if(message != null && !TextUtils.isEmpty(message)) {
	    	 argumentsChat.putString("chat", message);
	    }
	     
		ActionBar actionBar = getActionBar();
        // Screen handling while hiding ActionBar icon.
        actionBar.setDisplayShowHomeEnabled(false);
        // Screen handling while hiding Actionbar title.
        actionBar.setDisplayShowTitleEnabled(false);
        // Creating ActionBar tabs.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
        // Setting custom tab icons.
        chatTab = actionBar.newTab().setText("CHAT");
        
        chatFragment = new ChatFragment();
        chatFragment.setArguments(argumentsChat);
               
        if(viewType == null || TextUtils.isEmpty(viewType)) {
	    	viewType = "nearbyMembers";
	    }
        
        // Setting tab listeners.
        chatTab.setTabListener(new TabListener(chatFragment));
        // Adding tabs to the ActionBar.
        actionBar.addTab(chatTab);
        
        if(viewType.equalsIgnoreCase("nearbyMembers")) {
            memberDetailsFragment = new MemberDetailsFragment();
            memberDetailsFragment.setArguments(arguments);
            membersTab = actionBar.newTab().setText("MEMBER DETAILS");
        	membersTab.setTabListener(new TabListener(memberDetailsFragment));
        	actionBar.addTab(membersTab);
        } else {
        	storeDetailsFragment = new StoreDetailsFragment();
        	storeDetailsFragment.setArguments(arguments);
        	storeTab = actionBar.newTab().setText("STORE DETAILS");
        	storeTab.setTabListener(new TabListener(storeDetailsFragment));
        	actionBar.addTab(storeTab);
        } 
        
        actionBar.selectTab(chatTab);
    }	
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(getWindow().getDecorView()
                .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
}

class TabListener implements ActionBar.TabListener {

	private Fragment fragment;
	
	// The contructor.
	public TabListener(Fragment fragment) {
		this.fragment = fragment;
	}

	// When a tab is tapped, the FragmentTransaction replaces
	// the content of our main layout with the specified fragment;
	// that's why we declared an id for the main layout.
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft.replace(R.id.mainLayout, fragment);
	}

	// When a tab is unselected, we have to hide it from the user's view. 
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(fragment);
	}

	// Nothing special here. Fragments already did the job.
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		
	}
}