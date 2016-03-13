package com.f8mobile.f8mobile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class LoadCentral extends Fragment {
	
	private View LoadCentralLayout;
	Bundle bundle = new Bundle(); 
	Spinner load;
	EditText mobile;
	Button submit;
	
	private ProgressDialog pd;
	initTask task;
	JSONObject json = null;
	
	//*****PARAMS*****
	String username;
	String password;
	String defaultUsername;
	String mobileNo;
	String productName;
	String product;
	String token;
	String default_user;
	String default_pass;
	String new_user;
	String new_pass;
	
	String resoponseCode;
   	String responseMsg;
	
   @Override
   public View onCreateView(LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
        
	   LoadCentralLayout = inflater.inflate(R.layout.loadcentral,container,false);
	   
	   load = (Spinner)LoadCentralLayout.findViewById(R.id.load);
	   //load.setEnabled(false);
	   
	   mobile = (EditText)LoadCentralLayout.findViewById(R.id.mobile);
	   //mobile.setEnabled(false);
	   
	   submit = (Button)LoadCentralLayout.findViewById(R.id.btnSubmit);
	   submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				//verySoon();
				username = PreferenceConnector.readString(getActivity(),
						PreferenceConnector.USERNAME, null);
				password =PreferenceConnector.readString(getActivity(),
						PreferenceConnector.PASSWORD, null);
				defaultUsername = PreferenceConnector.readString(getActivity(),
						PreferenceConnector.DEF_USER, null);
				mobileNo = mobile.getText().toString(); 
				productName = load.getSelectedItem().toString();
				TypedArray product_codes = getResources().obtainTypedArray(R.array.productsCode);
				product = product_codes.getString((int) load.getSelectedItemId());
				try {
					token = AeSimpleSHA1.SHA1(username+password+defaultUsername+mobileNo+product);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println(username);
				System.out.println(password);
				System.out.println(defaultUsername);
				System.out.println(mobileNo);
				System.out.println(product);
				System.out.println(token);
				System.out.println("http://f8mobile.info/f8m/api/topup.php?username="+defaultUsername+"&mobile="+mobileNo+"&product="+product+"&token="+token);
			
				veryfyLoad();
			}
		});
	   //verySoon();
	 
	   return LoadCentralLayout;
   }
   
   public static class AeSimpleSHA1 {
	    private static String convertToHex(byte[] data) {
	        StringBuilder buf = new StringBuilder();
	        for (byte b : data) {
	            int halfbyte = (b >>> 4) & 0x0F;
	            int two_halfs = 0;
	            do {
	                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
	                halfbyte = b & 0x0F;
	            } while (two_halfs++ < 1);
	        }
	        return buf.toString();
	    }

	    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
	        MessageDigest md = MessageDigest.getInstance("SHA-1");
	        md.update(text.getBytes("iso-8859-1"), 0, text.length());
	        byte[] sha1hash = md.digest();
	        return convertToHex(sha1hash);
	    }
	}
     
   public void veryfyLoad(){
	   AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
       builder1.setMessage("Load Verification!\nProduct Name: " + productName + "\nProduct Code: " + product + "\nMobile #: " + mobileNo);
       builder1.setCancelable(false);
       builder1.setPositiveButton("Verify",
               new DialogInterface.OnClickListener() {
           @Override
			public void onClick(DialogInterface dialog, int id) {
        	   task = new initTask();
        	   task.execute();
               dialog.cancel();  
           }
       });
       builder1.setNegativeButton("Cancel",
    		   new DialogInterface.OnClickListener() {
           		@Override
           		public void onClick(DialogInterface dialog, int id) {
           			dialog.cancel();  
           }
       });
       AlertDialog alert11 = builder1.create();
       alert11.show();
   }
   
   /*public void productSpinner(){
	   ArrayList<String> productCodes = new ArrayList<String>();
	   
	   ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
       (getActivity(), android.R.layout.simple_spinner_item,productCodes);
        
   		dataAdapter.setDropDownViewResource
       (android.R.layout.simple_spinner_dropdown_item);
        
   		load.setAdapter(dataAdapter);
   }*/
   
   public class Pair
	{
		String response_id;
		String response_message;
		String claim_code;
	}

	// ------- start of Async Task -------- //
	private class initTask extends AsyncTask <String, Void, Pair> 
	{
	   
	   @Override
		protected Pair doInBackground(String... params) 
	   {
		  
		   ParseJSON();
			   
		   System.out.println("Do Background");
		   
	
	   		Pair p = new Pair();
	   		//p.response_id = resoponseCode;
	        //p.response_message = response_count;
	        //p.response_count = response_count;
		    return p;
	   }
	   
	   @Override
	   protected void onPostExecute(Pair p) 
	   {
		   if (pd.isShowing()) {
	           pd.dismiss();
	           
	           Toast.makeText(getActivity(), responseMsg, Toast.LENGTH_LONG).show();
       	
	           System.out.println("Post Execute");
	       }
	   	}
  
  		@Override
  		protected void onPreExecute() 
  		{
	   		pd = new ProgressDialog(getActivity());
	   		pd.setMessage("Sending Load Request...");
      		pd.show();
  		}

	}
	public void ParseJSON(){
	   	json = null;
	   	String str = "";
	   	HttpResponse response;
	   	HttpClient myClient = new DefaultHttpClient();
	   	HttpPost myConnection = new HttpPost("http://f8mobile.info/f8m/api/topup.php?username="+defaultUsername+"&mobile="+mobileNo+"&product="+product+"&token="+token);
   
	   	System.out.println("Start JSON Parsing");  
	   	try {  
	   		response = myClient.execute(myConnection);
	   		str = EntityUtils.toString(response.getEntity(), "UTF-8");
	   		JSONObject json1 = new JSONObject(str);
	   		resoponseCode = json1.get("status").toString();
	       	responseMsg = json1.get("message").toString();
	       	//String responseData = json1.get("ResponseData").toString();
	       	
	       	
	       	
	       	System.out.println("ResponseCode:" + resoponseCode);
	       	System.out.println("ResponseMsg:" + responseMsg);
	       	
	       
		} catch (ClientProtocolException e) {
		   	e.printStackTrace();
		} catch (IOException e) {
		   	e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	   	if (responseMsg == null){
       		responseMsg = "Request not success!";
       	}
	}
   
   @Override
   public void onDestroyView() {
       super.onDestroyView();
       LoadCentralLayout = null; // now cleaning up!
   }
 
  
  
}