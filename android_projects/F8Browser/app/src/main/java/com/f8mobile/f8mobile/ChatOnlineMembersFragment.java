package com.f8mobile.f8mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import com.f8mobile.community_app.mobile.custom.PlaceHolder;
import com.f8mobile.community_app.mobile.database.SqliteDatabaseHelper;
import com.f8mobile.community_app.mobile.fragment.ChatFragment;
import com.f8mobile.community_app.mobile.model.UserDataModel;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChatOnlineMembersFragment extends Fragment {

	private View view;
	private ViewGroup viewGroup;
	private ArrayList<String> membersOnline = new ArrayList<String>();
	private AutoCompleteTextView membersAutoCompleteTextView;
	private Button searchOnlineMemberBtn;
	private SqliteDatabaseHelper sqliteDatabaseHelper;
	private UserDataModel userDataModel;
	private Handler mHandler;
	private ProgressDialog progressDialog;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		viewGroup = container;
		view = inflater.inflate(R.layout.chat_online_members_list, viewGroup, false);
		
		sqliteDatabaseHelper = new SqliteDatabaseHelper(getActivity());
		
		progressDialog = new ProgressDialog(getActivity());
		
		mHandler = new Handler() {
			
			public void handleMessage(Message msg) {
				switch(msg.what) {
				  case 888: /* do nothing */
					  break;
				}
			}
		};
		
		membersOnline = sqliteDatabaseHelper.returnAllUsers();
		
		Log.e("ChatOnlineMembersFragment","membersOnline size: " + membersOnline.size());
		
		boolean isSyncRunning = PreferenceConnector.readBoolean(getActivity(), PreferenceConnector.SYNC_RUNNING, false);
		boolean isSyncFinished = PreferenceConnector.readBoolean(getActivity(), PreferenceConnector.SYNC_FINISHED,  true);
		
		if(membersOnline.size() > 0 ) {
			if(!isSyncFinished && isSyncRunning) {
				Toast.makeText(getActivity(), "Sorry for the inconvenience but chat is currently unavailable until sync is finished. Please try again later. ", Toast.LENGTH_SHORT).show();
				
				Bundle bundle = new Bundle();
				bundle.putInt("id", 3);
				Fragment fr1 = new WebPage();
			    fr1.setArguments(bundle);
			    FragmentManager fm = getFragmentManager();
			    FragmentTransaction fragmentTransaction = fm.beginTransaction();
			    fragmentTransaction.replace(R.id.second_fragment, fr1);
			    fragmentTransaction.commit();
			} 
		} else {
			
	    	  Thread thread = new Thread() {
	    		    @Override
	    		    public void run() {
	    		    	  PreferenceConnector.writeBoolean(getActivity(), PreferenceConnector.SYNC_RUNNING, true);
	    		    	  PreferenceConnector.writeBoolean(getActivity(), PreferenceConnector.SYNC_FINISHED, false);
	    		    	  SqliteDatabaseHelper sqliteDatabaseHelper = new SqliteDatabaseHelper(getActivity());
	    		    	  BufferedReader reader = null;
	    		    	  try {
	    		    	      reader = new BufferedReader(
	    		    	          new InputStreamReader(getActivity().getAssets().open("initial_members_list.csv")) , 1024);

	    		    	      // do reading, usually loop until end of file reading  
	    		    	      String mLine = "";
	    		    	      while((mLine = reader.readLine()) != null) {
	    		    	    	  
	    		    	    	  String[] fields = mLine.split("\",\"", -1);
	    		    	    	  UserDataModel userDataModel = new UserDataModel();
	    		    	    	  userDataModel.setId(fields[0].replaceAll("\"", ""));
	    		    	    	  userDataModel.setFname(fields[1].replaceAll("\"", ""));
	    		    	    	  userDataModel.setLname(fields[2].replaceAll("\"", ""));
	    		    	    	  userDataModel.setCellNo(fields[3].replaceAll("\"", ""));
	    		    	    	  userDataModel.setGcmRegId(fields[4].replaceAll("\"", ""));
	    						  sqliteDatabaseHelper.insertToUsersTable(userDataModel);
	    		    	    	 
	    		    	      }
	    		    	  } catch (IOException e) {
	    		    		  Log.e("ChatOnlineMembersFragment","", e);
	    		    	  } finally {
	    		    	      if (reader != null) {
	    		    	           try {
	    		    	               reader.close();
	    		    	           } catch (IOException e) {
	    		    	              Log.e("ChatOnlineMembersFragment","", e);
	    		    	           }
	    		    	      }
	        		    	  PreferenceConnector.writeBoolean(getActivity(), PreferenceConnector.SYNC_RUNNING, false);
	        		    	  PreferenceConnector.writeBoolean(getActivity(), PreferenceConnector.SYNC_FINISHED, true);
	    		    	  }
	    		    }
	    		};
	    		thread.start();
	    		Toast.makeText(getActivity(), "Sorry for the inconvenience but chat is currently unavailable until sync is finished. Please try again later. ", Toast.LENGTH_SHORT).show();
				
				Bundle bundle = new Bundle();
				bundle.putInt("id", 3);
				Fragment fr1 = new WebPage();
			    fr1.setArguments(bundle);
			    FragmentManager fm = getFragmentManager();
			    FragmentTransaction fragmentTransaction = fm.beginTransaction();
			    fragmentTransaction.replace(R.id.second_fragment, fr1);
			    fragmentTransaction.commit();
		}
		
		membersAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextViewMemberName);
		membersAutoCompleteTextView.setAdapter(new PlaceHolder(getActivity().getApplicationContext(), R.layout.list_location_auto_complete, 2, membersOnline));
		
		membersAutoCompleteTextView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {
				
				membersAutoCompleteTextView.setText((String) parent.getItemAtPosition(position));
				
				// get data from database (?) base on id
				String idTemp = membersAutoCompleteTextView.getText().toString();
				String uId = idTemp.substring(idTemp.indexOf("[") + 1, idTemp.indexOf("]") - 1);
				userDataModel = sqliteDatabaseHelper.returnUserData(uId);
				
			}
		});
				
		searchOnlineMemberBtn = (Button) view.findViewById(R.id.searchMemberBtn);
		searchOnlineMemberBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// call ChatFragment, pass userids as arguments
				// to get previous messages
				if(TextUtils.isEmpty(membersAutoCompleteTextView.getText().toString())) {
					Toast.makeText(getActivity().getApplicationContext(), "No member to chat with", Toast.LENGTH_SHORT).show();
				} else {
					
					if(userDataModel == null) {
						Toast.makeText(getActivity(), "Unable to find user data for this user. ", Toast.LENGTH_SHORT).show();
					} else {
						Bundle argumentsChat = new Bundle();
						
						if(TextUtils.isEmpty(userDataModel.getGcmRegId()) || userDataModel.getGcmRegId().equalsIgnoreCase("null") 
								|| userDataModel.getGcmRegId() == null) {
							// call server, check if updated details
							
							String[] p = new String[2];
							p[0] = "method=12";
							p[1] = "POST";
													
							List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						    nameValuePairs.add(new BasicNameValuePair("id", userDataModel.getId()));
						    
						    try {
								String res = new CommunityAppAsyncTaskHttpCall(progressDialog, nameValuePairs, mHandler, 888).execute(p).get();
								if(!TextUtils.isEmpty(res)) {
									try {
										String userData = "";
										JSONArray jsonArray = new JSONArray(res);
										if(jsonArray != null && jsonArray.length() > 0) {
			    							for(int i = 0; i < jsonArray.length(); i++) {
			    								userData = jsonArray.getJSONObject(i).toString();
			    							}
			    							
			    							JSONObject j = new JSONObject(userData);
			    							if(j.getString("gcm_registration_id") == null || 
			    									TextUtils.isEmpty(j.getString("gcm_registration_id")) || 
			    									j.getString("gcm_registration_id").equalsIgnoreCase("null")) {
			    								
			    								Toast.makeText(getActivity(), userDataModel.getFname() + " " + userDataModel.getLname() + " is not registered to chat so he/she will not received your chat message. Please try another user.", Toast.LENGTH_SHORT).show();
			    								
			    							} else {
			    								sqliteDatabaseHelper.updateGcmIdOfThisUser(j.getString("id"), j.getString("gcm_registration_id"));
			    								
			    								userDataModel.setGcmRegId(j.getString("gcm_registration_id"));
			    								
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
			    								
			    								JSONObject j2 = new JSONObject(map);
			    								
			    							    argumentsChat.putString("data", j2.toString());
			    							    argumentsChat.putString("gcm_registration_id_this_device", PreferenceConnector.readString(getActivity().getApplicationContext(), PreferenceConnector.GCM_REG_ID, ""));

			    						        ChatFragment chatFragment = new ChatFragment();
			    						        chatFragment.setArguments(argumentsChat);
			    						        
			    				        	    FragmentManager fm = getFragmentManager();
			    				        	    FragmentTransaction fragmentTransaction = fm.beginTransaction();
			    				        	    fragmentTransaction.replace(R.id.second_fragment, chatFragment);
			    				        		fragmentTransaction.commit();
			    								
			    							}
			    						}
									} catch (JSONException e) {
										Log.e("ChatOnlineMembersFragment", "",e);
									}
								}
							} catch (InterruptedException e) {
								Log.e("ChatOnlineMembersFragment", "",e);
							} catch (ExecutionException e) {
								Log.e("ChatOnlineMembersFragment", "",e);
							}
							
						} else {
							
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
							
						    argumentsChat.putString("data", j.toString());
						    argumentsChat.putString("gcm_registration_id_this_device", PreferenceConnector.readString(getActivity().getApplicationContext(), PreferenceConnector.GCM_REG_ID, ""));

					        ChatFragment chatFragment = new ChatFragment();
					        chatFragment.setArguments(argumentsChat);
					        
			        	    FragmentManager fm = getFragmentManager();
			        	    FragmentTransaction fragmentTransaction = fm.beginTransaction();
			        	    fragmentTransaction.replace(R.id.second_fragment, chatFragment);
			        		fragmentTransaction.commit();
						}
					}
				}   
			}
		});
		
		return view;
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
    }
	
	@Override
	public void onDestroyView() {
	    super.onDestroyView();  
	}	
	
}
