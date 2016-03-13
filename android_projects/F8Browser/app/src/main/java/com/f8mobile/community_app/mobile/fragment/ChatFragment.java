package com.f8mobile.community_app.mobile.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.f8mobile.community_app.mobile.asynctask.CommunityAppAsyncTaskHttpCall;
import com.f8mobile.community_app.mobile.custom.ChatArrayAdapter;
import com.f8mobile.community_app.mobile.database.SqliteDatabaseHelper;
import com.f8mobile.community_app.mobile.model.ChatMessage;
import com.f8mobile.community_app.mobile.model.UserMessages;
import com.f8mobile.f8mobile.PreferenceConnector;
import com.f8mobile.f8mobile.R;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatFragment extends Fragment {
	
	private JSONObject jObj;
	private String messageFromIntent, gcmRegIdThisDevice, gcmRegIdFromClickedUser, recipientId;
	private ChatArrayAdapter chatArrayAdapter;
	private ProgressDialog progressDialog;
	private ListView lv;
	private BroadcastReceiver broadcastReceiver;
	private TextView chatTo;
	private boolean broadcastReceiverIsRegistered = false;
	private SqliteDatabaseHelper sqliteDatabaseHelper;
	private String sender, user;
	private EditText chat;
	private Handler mHandler;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.chat_layout, container, false);
        
    	try {
        	
    		progressDialog = new ProgressDialog(getActivity());
           	sqliteDatabaseHelper = new SqliteDatabaseHelper(getActivity());
           	
			sender = PreferenceConnector.readString(getActivity(), PreferenceConnector.CLIENT_ID, "");
			user = PreferenceConnector.readString(getActivity(), PreferenceConnector.NAME, "");
           	
            lv = (ListView) v.findViewById(R.id.listView1);
        	chatArrayAdapter = new ChatArrayAdapter(getActivity(), R.layout.activity_chat_singlemessage);
    		lv.setAdapter(chatArrayAdapter);
    		lv.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	        lv.setAdapter(chatArrayAdapter);
	        lv.setDivider(null);
	        
	        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
	            @Override
	            public void onChanged() {
	                super.onChanged();
	                lv.setSelection(chatArrayAdapter.getCount() - 1);
	            }
	        });
    			        
	        chatArrayAdapter.notifyDataSetChanged();
	        chatTo = (TextView) v.findViewById(R.id.textViewChatTo);
	               
        	Bundle b = getArguments();
        	String s = (String) b.get("data");
        	
        	Log.e("ChatFragment", "data: " + s);
        	if(!TextUtils.isEmpty(s)) {
        		jObj = new JSONObject(s);
            	gcmRegIdThisDevice = (String) b.get("gcm_registration_id_this_device");
            	gcmRegIdFromClickedUser = jObj.getString("gcm_registration_id");
            	recipientId = jObj.getString("id");
            	chatTo.setText(jObj.getString("fname") + " " + jObj.getString("lname"));
            	
        	} else {
        		messageFromIntent = (String) b.getString("chat");
        		if(messageFromIntent != null && !TextUtils.isEmpty(messageFromIntent)) {
        			recipientId = messageFromIntent.substring(messageFromIntent.indexOf("[") + 1, messageFromIntent.indexOf("]") - 1);
        		}
        	}	        
	        
	        broadcastReceiver = new BroadcastReceiver() {
	            @Override
	            public void onReceive(Context context, Intent intent) {
	                Log.e("ChatFragment", "onReceive: " + intent.getStringExtra("chat_message"));
	                
	                Calendar cal = Calendar.getInstance();
	                cal.setTimeInMillis(Long.valueOf(intent.getStringExtra("timestamp")) * 1000);
	                String tstamp = new SimpleDateFormat("EEE yyyy-MM-dd HH:mm").format(cal.getTimeInMillis()).toString();
	                Log.e("ChatFragment", "timestamp intent: " + intent.getStringExtra("timestamp") + ", formatted tstamp: " + tstamp);
	                
	                chatArrayAdapter.add(new ChatMessage(true, intent.getStringExtra("chat_message"), tstamp));
	                chatArrayAdapter.notifyDataSetChanged();
	                
	                // insert to device database
	                UserMessages u = new UserMessages();
	                u.setMessage(intent.getStringExtra("chat_message"));
	                u.setUser(intent.getStringExtra("user"));
	                u.setSenderId(intent.getStringExtra("sender_id"));
	                u.setRecipientId(intent.getStringExtra("recipient_id"));
	                u.setTimestamp(String.valueOf(cal.getTimeInMillis()));
	                u.setRegIdReceiver(intent.getStringExtra("gcmRegIdTo"));
	                u.setRegIdSender(intent.getStringExtra("gcmRegIdFrom"));
	                
	                sqliteDatabaseHelper.insertToMessagesTable(u);
					
	            }
	        }; 
	        
	        getActivity().getApplicationContext().registerReceiver(broadcastReceiver, new IntentFilter("com.google.android.c2dm.intent.RECEIVE"));
        	broadcastReceiverIsRegistered = true;        	
        	
        	chat = (EditText) v.findViewById(R.id.editTextChatBox);
	        final Button sendChat = (Button) v.findViewById(R.id.buttonSendChat);
	        
	        sendChat.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					if(!TextUtils.isEmpty(chat.getText().toString())) {
						
						String[] p = new String[2];
						p[0] = "method=9";
						p[1] = "POST";
												
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					    nameValuePairs.add(new BasicNameValuePair("sender_id", sender));
					    nameValuePairs.add(new BasicNameValuePair("user", user));
					    nameValuePairs.add(new BasicNameValuePair("recipient_id", recipientId));
					    nameValuePairs.add(new BasicNameValuePair("message", chat.getText().toString()));
					    nameValuePairs.add(new BasicNameValuePair("registration_id_from", gcmRegIdThisDevice));
					    nameValuePairs.add(new BasicNameValuePair("registration_id_to", gcmRegIdFromClickedUser));
					    
					    new CommunityAppAsyncTaskHttpCall(nameValuePairs, null, 0).execute(p);

					    Calendar c = Calendar.getInstance();
					    String timestamp = String.valueOf(c.getTimeInMillis());	
					    
					    String tstamp = new SimpleDateFormat("EEE yyyy-MM-dd HH:mm").format(c.getTimeInMillis()).toString();
		                Log.e("ChatFragment", "timestamp onclick calendar: " + c.getTimeInMillis() + ", formatted tstamp: " + tstamp);
		                
					    UserMessages uMessage = new UserMessages();
					    uMessage.setSenderId(sender);
					    uMessage.setRecipientId(recipientId);
					    uMessage.setUser(user);
					    uMessage.setMessage(chat.getText().toString());
					    uMessage.setTimestamp(timestamp);
					    uMessage.setRegIdSender(gcmRegIdThisDevice);
					    uMessage.setRegIdReceiver(gcmRegIdFromClickedUser);

					    sqliteDatabaseHelper.insertToMessagesTable(uMessage);
					    
					    chatArrayAdapter.add(new ChatMessage(false, chat.getText().toString(), tstamp));
					    chat.setText("");
							 
					  } else {
						Toast.makeText(getActivity().getApplicationContext(), "No chat message to send", Toast.LENGTH_SHORT).show();
					}
				}
			});
	        
	        mHandler = new Handler() {
	            public void handleMessage(Message msg) {
	                switch (msg.what) {
	                	case 1: 
	                		String result = (String) msg.obj;
	        	        	if(result != null && !TextUtils.isEmpty(result)) {
	        	        		
								try {
									JSONArray jArr = new JSONArray(result);
		        	            	if(jArr != null && jArr.length() > 0) {
		        	            		// parse messages here
		        	            		// check if sender id is same with user.getUserId()
		        	            		for(int i = 0; i < jArr.length(); i++) {
		        	            			
		        	            			JSONObject j = jArr.getJSONObject(i);
		        	            			if(j != null) {
		        	            				String sender = j.getString("sender_id");
		        	            				
		        	            				Calendar cal = Calendar.getInstance();
		        	         	                cal.setTimeInMillis(Long.valueOf(j.getString("timestamp")) * 1000);
		        	         	                String tstamp = new SimpleDateFormat("EEE yyyy-MM-dd HH:mm").format(cal.getTimeInMillis()).toString();
		        	         	                Log.e("ChatFragment", "timestamp json: " + j.getString("timestamp") + ", formatted tstamp: " + tstamp);
		        	         	                
		        	            				
		        	            				if(sender.equalsIgnoreCase(PreferenceConnector.readString(getActivity(), PreferenceConnector.CLIENT_ID, ""))) {
		        	            					chatArrayAdapter.add(new ChatMessage(false, j.getString("message"), tstamp));
		        	            				} else {
		        	            					chatArrayAdapter.add(new ChatMessage(true, j.getString("message"), tstamp));
		        	            				}
		        	            				
		        	            				 UserMessages u = new UserMessages();
		        	         	                 u.setMessage(j.getString("message"));
		        	         	                 //u.setUser(user"));
		        	         	                 u.setSenderId(j.getString("sender_id"));
		        	         	                 u.setRecipientId(j.getString("recipient_id"));
		        	         	                 u.setTimestamp(String.valueOf(cal.getTimeInMillis()));
		        	         	                 u.setRegIdReceiver(j.getString("gcmRegIdTo"));
		        	         	                 u.setRegIdSender(j.getString("gcmRegIdFrom"));
		        	         	                
		        	         	                sqliteDatabaseHelper.insertToMessagesTable(u);
		        	            				
		        	            			}
		        	            		}
		        	            		chatArrayAdapter.notifyDataSetChanged();
		        	            	}
								} catch (JSONException e) {
									Log.e("ChatFragment", "", e);
									Toast.makeText(getActivity(), "No chat messages to show", Toast.LENGTH_SHORT).show();
								} 
	        	        	} ;       	
	                		break;
	                }
	            }
	        };    
	        
	        
        	// load first all messages between this 2 user from device database
	        ArrayList<UserMessages> uMList = sqliteDatabaseHelper.returnMessagesWithThisUser(sender, recipientId);
	        if(uMList != null && uMList.size() > 0) {
	        	for(UserMessages u : uMList) {
	        		
	        		Calendar cal = Calendar.getInstance();
 	                cal.setTimeInMillis(Long.valueOf(u.getTimestamp()));
 	                String tstamp = new SimpleDateFormat("EEE yyyy-MM-dd HH:mm").format(cal.getTimeInMillis()).toString();
 	                
	        		if(u.getSenderId().equalsIgnoreCase(sender)) { 
	        			chatArrayAdapter.add(new ChatMessage(false, u.getMessage(), tstamp));
    				} else {
    					chatArrayAdapter.add(new ChatMessage(true, u.getMessage(), tstamp));
    				}
	        		
	        		Log.e("ChatFragment", "timestamp database: " + cal.getTimeInMillis() + ", formatted tstamp: " + tstamp);
	        	}	
	        } else {
	        	// no messages from database
	        	// call API from server database
	        	Log.e("", "Fetching messages from remote server: ");
	        	String[] messagesParam = new String[2];
	        	messagesParam[0] = "method=10";
	        	messagesParam[1] = "POST";
	        	
	        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			    nameValuePairs.add(new BasicNameValuePair("sender_id", PreferenceConnector.readString(getActivity(), PreferenceConnector.CLIENT_ID, "")));
			    nameValuePairs.add(new BasicNameValuePair("recipient_id", recipientId));

			    new CommunityAppAsyncTaskHttpCall(progressDialog, nameValuePairs, mHandler, 1).execute(messagesParam);	        	
	        }  
        } catch (JSONException e) {
			e.printStackTrace();
		} 
        
        return v;
    }
    
    @Override
    public void onDetach() {
    	super.onDetach();
    	try {
    		if(broadcastReceiverIsRegistered) {
        		getActivity().unregisterReceiver(broadcastReceiver);
        		broadcastReceiverIsRegistered = false;
        	}
    		
    		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    		imm.hideSoftInputFromWindow(chat.getWindowToken(), 0);
    		
    	} catch(Exception e) {
    		// do nothing
    	}
    		
    }
}
