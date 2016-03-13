package com.f8mobile.f8mobile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.f8mobile.fragment.imageutils.ImageLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;    
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

@SuppressLint({ "NewApi", "InflateParams" }) public class MainActivity extends Activity {
	
	//***LOG OUT FUNCTION*///
	private ProgressDialog pd;
	initTaskLogOut taskOut;
	
	int idle = 0;
	int refresh = 0;
	Random rnd = new Random();  
	
	String status;
	boolean alertOn = false;
	
	Bundle bundle = new Bundle(); 
	Fragment fr = new AdsOne(); 
	
	View homeButtonLayout;
	Button reportsBtn; 
	
	Drawable defaultImg;
	
	// ***** HOME BUTTON LIST VIEW
	ListView list;
	  String[] web = {
			  "MLM",
			  "facebook",
			  "twitter",
			  "google",
			  "youtube",
			  "tv1",
			  "skype",
			  "stream",
			  "f8mobile",
			  "typhoon",
			  "chat",
			  "chat_members"
	  } ;
	  Integer[] imageId = {
			  R.drawable.f8,
			  R.drawable.facebook,
			  R.drawable.twitter,
			  R.drawable.google,
			  R.drawable.youtube, 
			  R.drawable.tv1,
			  R.drawable.skype,
			  R.drawable.stream,
			  R.drawable.f8_android,
			  R.drawable.typhoon,
			  R.drawable.community_users_icon,
			  R.drawable.chat_icon
			  
	  };
	
	
	String xml;
	XMLParser parser;
	String claim_code;
	String response_id;
	String response_site;
	String response_contact;
	String response_result;
	String response_count;
	String response_message;
	String response_total;
	String transaction_record_id;
	float response_earnings;
	String response_client;
	final String KEY_RESPONSE_HEADER = "F8MobileXMLRequest"; // parent node
	final String KEY_RESPONSE_RESULT = "Result";
	final String KEY_RESPONSE_COUNT = "count";
	final String KEY_RESPONSE_TOTAL = "totalprice";
	final String KEY_RESPONSE_DATA = "image";
	final String KEY_RESPONSE_SITE = "site";
	final String KEY_RESPONSE_CONTACT = "contact";
	//final String KEY_RESPONSE_MESSAGE = "ResponseMsg";
	
	final String KEY_RESPONSE_EARNINGS = "earnings";
	final String KEY_REPOSNSE_CLIENT = "client_id";
	
	// ******* GLOBAL VARIABLES FOR IMAGES FROM WEB TO BE CACHE
	private ImageLoader imgLoader;
	private View topMapButtonsLayout;
	Drawable mybmp;
	public static ArrayList<Drawable> myImageListFour;
	public static ArrayList<Drawable> myImageListTwo;
	public static ArrayList<Drawable> myImageListOne;
	
	public static ArrayList<String> myImageLinkFour;
	public static ArrayList<String> myImageLinkTwo;
	public static ArrayList<String> myImageLinkOne;
	
	public static ArrayList<String> myContactFour;
	public static ArrayList<String> myContactTwo;
	public static ArrayList<String> myContactOne;
	
	// ******* GLOBAL VARIABLES FOR SHARED PREFERENCE ---- GET USER DETAILS
	String profile_client;
	String profile_gcm;
	String profile_name;
	String profile_serial;
	String profile_gender;
	String profile_age;
	String profile_c_status;
	String profile_address;
	String profile_country;
	String profile_province;
	String profile_town;
	String profile_longtitude;
	String profile_latitude;
	String profile_earnings = "0";  
	String profile_username;
	int day;
	
	float earnings = 0;
    BigDecimal result; 
	int tag = 0;
	int tagView = 0;
	int earnTag = 0;
	
	String category;
	String cat;
	
	Timer timer;
	initTask task;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      setContentView(R.layout.activity_main); 
      
      defaultImg = getResources().getDrawable(R.drawable.f8_android);
      
      Fragment fr = new MainDefault();	
	    FragmentManager fm = getFragmentManager();
	    FragmentTransaction fragmentTransaction = fm.beginTransaction();
	    fragmentTransaction.replace(R.id.fragment_place, fr);
	    try{
	    	fragmentTransaction.commit();
	    }
	    catch(Exception e){
	    	  
	    }
	    
	    PreferenceConnector.writeString(this, PreferenceConnector.IDLESTATUS,
				String.valueOf(idle));
     
      // ******** DISPLAY HOME BUTTON FUNCTION
      homeButton(); 
      
      // ******** GET USER DETAILS FROM PREFERENCE
      getUserDetailsFromPrefs();
      Log.d("clientid", profile_client);
      Log.d("name", profile_name);
      Log.d("serial", profile_serial);
      Log.d("gender", profile_gender);
      Log.d("age", profile_age);
      Log.d("civil status", profile_c_status);
      Log.d("address", profile_address);
      Log.d("profile_earnings", profile_earnings);
      Log.d("location", profile_country + " - " + profile_latitude + " - " + profile_longtitude + " - " + profile_province + " - " + profile_town);
      
      
      
      earnings = Float.parseFloat(profile_earnings);
      //Toast.makeText(MainActivity.this, "Current Earnings: " +earnings, Toast.LENGTH_SHORT).show();
     
      mainProcess();
		    
      activeCheck();
		    
      checkPlayServices(); // checks for valid google play services; necessary for GCM to work
        
	}  
	
	public void refreshIdle(){
		idle = 0;
		PreferenceConnector.writeString(this, PreferenceConnector.IDLESTATUS,
				String.valueOf(idle));
	}
	
	
	public void mainProcess(){
		System.out.println("Main Process Started!");
		
		final Handler handler = new Handler();
		timer = new Timer();
		    TimerTask doAsynchronousTask = new TimerTask() {       
		        @Override
		        public void run() {
		        	
		            handler.post(new Runnable() {
		                @Override
						public void run() { 
		                   try {   
		                	   
		                	   System.out.println("TIMER STARTS!");
	                		   
		                	   alertConnection();
		                	   
		                	   if (status.equals("1") || status.equals("2"))
		                	   {
		                		   	
		                		   idle = Integer.valueOf(PreferenceConnector.readString(MainActivity.this,
		                					PreferenceConnector.IDLESTATUS, null));
		                		   
		                		   //System.out.println("Idle value: " + idle);
		                		   
		                		   	if(idle == 0){
		                		   		System.out.println("Start Task!");
		                		   		task = new initTask();
		                		   		task.execute();
		                		   	}
		                		   	
		                		
		                       }
		                	   else{
		                		  // do nothing
		                	   }
		                   }
		                   catch (Exception e) {
		                        // TODO Auto-generated catch block
		                   }
		                }
		            });
		        }  
		    };
		    timer.schedule(doAsynchronousTask, 0, 20000); 
	}
	
	public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);       
        return bd;
	}
	
	@SuppressLint("NewApi") public void FragmentsView(int x){
		
		if (x == 1){
			fr = new AdsOne();
		}
		else if (x == 2){
			fr = new AdsTwo();
		}
		else if (x == 3){ 
			fr = new AdsFour(); 
		}
		else if (x == 4){
			fr = new AdsInactive();
		}
		else if (x == 5){
			fr = new MainDefault();
		}
		FragmentManager fm = getFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_place, fr);
		try{
			fragmentTransaction.commit();
		}catch(Exception e){
			
		}
	}
	
	public void FragmentsWeb(int x){
		bundle.putInt("id", x );
	    Fragment fr1 = new WebPage();
	    fr1.setArguments(bundle);
	    FragmentManager fm = getFragmentManager();
	    FragmentTransaction fragmentTransaction = fm.beginTransaction();
	    fragmentTransaction.replace(R.id.second_fragment, fr1);
	    try{
			fragmentTransaction.commit();
		}catch(Exception e){
			
		}
	}
	
	public void FragmentsStream(int x){
		bundle.putInt("id", x );
		bundle.putString("package", getPackageName() );
	    VideoTV fr1 = new VideoTV();
	    fr1.setArguments(bundle);
	    FragmentManager fm = getFragmentManager();
	    FragmentTransaction fragmentTransaction = fm.beginTransaction();
	    fragmentTransaction.replace(R.id.second_fragment, fr1);
	    try{
			fragmentTransaction.commit();
		}catch(Exception e){
			
		}
	}
	
	
	
	@SuppressLint({ "RtlHardcoded", "InflateParams" }) 
	public void homeButton(){
		FragmentsWeb(3);
		homeButtonLayout = getLayoutInflater().inflate(R.layout.home_button,null);
		int width = getWindow().getAttributes().width;
		int height = getWindow().getAttributes().height;
		LayoutParams params = new LayoutParams(width, height);
		getWindow().addContentView(homeButtonLayout, params);
		
		reportsBtn = (Button) homeButtonLayout.findViewById(R.id.reportBtn);
		reportsBtn.getBackground().setAlpha(163);
        reportsBtn.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				
			    final View appsMenu = getLayoutInflater().inflate(R.layout.app_buttons,null);
				final PopupWindow reportPopupWindow = new PopupWindow(appsMenu, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.FILL_PARENT, true);   	                  
				          
				TextView earning_view = (TextView)appsMenu.findViewById(R.id.earnings_app);
				
				if(result == null ) {
					result = new BigDecimal("0.00");
				}
				
				
				
				earning_view.setText("P"+String.valueOf(result));
				System.out.println(result);
				
				CustomList adapter = new CustomList(MainActivity.this, web, imageId);
				    list=(ListView)appsMenu.findViewById(R.id.list);
				    list.setAdapter(adapter);
				    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			                @Override
			                public void onItemClick(AdapterView<?> parent, View view,
			                                        int position, long id) {
			                	reportPopupWindow.dismiss();
			                	if (position==5 || position==7){
			                		FragmentsStream(position);
			                	}
			                	else if(position==0){
			                		//Toast.makeText(MainActivity.this, "You Clicked at " +position + "\nCommutity Categories", Toast.LENGTH_SHORT).show();
			                		subMenuButton(0);
			                		homeButtonLayout.setAlpha(0);
			                		reportPopupWindow.dismiss();
			                	}
			                	else if (position==10){
			                		//bundle.putInt("id", x );
			                	    Fragment fr1 = new CommunityFragment();
			                	    //fr1.setArguments(bundle);
			                	    FragmentManager fm = getFragmentManager();
			                	    FragmentTransaction fragmentTransaction = fm.beginTransaction();
			                	    fragmentTransaction.replace(R.id.second_fragment, fr1);
			                		fragmentTransaction.commit();
			                	} else if (position == 11) {
			                		Fragment chatOnlineMembersFragment = new ChatOnlineMembersFragment();
			                	    FragmentManager fm = getFragmentManager();
			                	    FragmentTransaction fragmentTransaction = fm.beginTransaction();
			                	    fragmentTransaction.replace(R.id.second_fragment, chatOnlineMembersFragment);
			                		fragmentTransaction.commit();
			                	}
			                	else{
			                		FragmentsWeb(position);
			                	}
			                	
			                	
			                    //Toast.makeText(MainActivity.this, "You Clicked at " +position, Toast.LENGTH_SHORT).show();
			                }
			            });
				    
				    
					
					//Toast.makeText(getApplicationContext(), listSize + "\n" + listLast + "\n" + listStart , Toast.LENGTH_SHORT).show(); 
				    
				    Button btnUp = (Button)appsMenu.findViewById(R.id.btnUp);
					btnUp.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							
							int listStart = list.getFirstVisiblePosition();
							
							//Toast.makeText(getApplicationContext(), String.valueOf(listStart), Toast.LENGTH_SHORT).show(); 
							if (listStart != 0){
								list.smoothScrollToPosition(listStart-1);
							}
							else{
								list.smoothScrollToPosition(0);
							}
						}
					});
					
					Button btnDown = (Button)appsMenu.findViewById(R.id.btnDown);
					btnDown.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							int listSize = list.getCount();
							int listLast = list.getLastVisiblePosition();
							
							if (listLast != listSize - 1){
								list.smoothScrollToPosition(listLast+1);
							}
							else{
								list.smoothScrollToPosition(listSize-1);
							}
							//Toast.makeText(getApplicationContext(), "Button Down Clicked!", Toast.LENGTH_SHORT).show(); 
						}
					});
					
				reportPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);  
				reportPopupWindow.setBackgroundDrawable(new BitmapDrawable());
				reportPopupWindow.setOutsideTouchable(true);
				reportPopupWindow.setFocusable(true);
				//reportPopupWindow.setContentView(linearLayout);
				reportPopupWindow.showAtLocation(appsMenu, Gravity.RIGHT, 0, 0);
				reportPopupWindow.showAsDropDown(reportsBtn, 0, 0);
				
			}
		});	
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("RtlHardcoded")
	public void subMenuButton(int x){
		final View subMenu = getLayoutInflater().inflate(R.layout.sub_menu,null);
		final PopupWindow reportPopupWindow = new PopupWindow(subMenu, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);   	                  
			
		TextView txtCategoryName = (TextView)subMenu.findViewById(R.id.txtMainCategory);
		txtCategoryName.setText(web[x] + " " + "Menu");
		
		ImageView btn1 = (ImageView)subMenu.findViewById(R.id.btnImage1);
		btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				LoadCentral fr1 = new LoadCentral();
        	    FragmentManager fm = getFragmentManager();
        	    FragmentTransaction fragmentTransaction = fm.beginTransaction();
        	    fragmentTransaction.replace(R.id.second_fragment, fr1);
        	    try{
        			fragmentTransaction.commit();
        		}catch(Exception e){
        			
        		}
        	    reportPopupWindow.dismiss();
			}
		});	 
		
		
		reportPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);  
		reportPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		reportPopupWindow.setOutsideTouchable(true);
		reportPopupWindow.setFocusable(true);
		//reportPopupWindow.setContentView(linearLayout);
		reportPopupWindow.showAtLocation(subMenu, Gravity.RIGHT, 0, 0);
		reportPopupWindow.showAsDropDown(list, 0, 0);
		
		reportPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // Do your action
            	//setContentView(R.layout.login);
            	homeButtonLayout.setAlpha(100);
            	//loginBG.setBackgroundResource(R.drawable.bg);
                //loginLayout.setAlpha(100);
            }
        });
	}
	
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
        	System.out.println("START BACKGROUND");
        	try{
        		doBackGround();
        		if (tag == 2){
        			if (earnTag == 4){
        				earnings = earningUpdate(response_total);
        			}
        			else{
        				earnTag = earnTag + 1;
        			}
        		}
        	}
        	catch(Exception e){
        		
        	}

        	Pair p = new Pair();
	        //p.response_id = earn;
	        p.response_message = response_count;
	        //p.response_count = response_count;
		    return p;
        }
        
        @Override
        protected void onPostExecute(Pair p) 
        {
      		// ****** UPDATE EARNINGS
        	//System.out.println("result"+ p.response_id);
        	if (tag == 2){
        		/*if (tagView == 4){
        			FragmentsView(Integer.parseInt(category));
        		}
        		else{
        			tagView = tagView + 1;
        		}*/
        		FragmentsView(Integer.parseInt(category));
        		//earnings = Float.parseFloat(p.response_id);
  		   		result=round(earnings,2);
  		   		System.out.println("result"+ result);
        	}
        	else{
        		tag = tag + 1;
  			   	result=round(earnings,2);
  			   	
  			  fr = new MainDefault();	
  			  FragmentManager fm = getFragmentManager();
  			  FragmentTransaction fragmentTransaction = fm.beginTransaction();
  			  fragmentTransaction.replace(R.id.fragment_place, fr);
  			  try{
  				  fragmentTransaction.commit();
  			  }
  			  catch(Exception e){
  				  
  			  }
        	}
        	
        }
        
        @Override
        protected void onPreExecute() 
        {
            //pre execute logic
            //super.onPreExecute();
        	//dialog = ProgressDialog.show(Claim.this, "Validating your claim code" , "Please wait ... ", true);
        }

    }
    
    public void doBackGround(){
    	//ImageLoader.clearCache();
    		
    		//Toast.makeText(getBaseContext(), "background", Toast.LENGTH_LONG).show();
		// ****** GET ACTIVE ADS TO IDENTIFY CATEGORY
		   	boolean a = fr.toString().contains("AdsOne");
		   	boolean b = fr.toString().contains("AdsTwo");
		   	boolean c = fr.toString().contains("AdsFour");
		   	boolean d = fr.toString().contains("MainDefault");
		   	boolean ee = fr.toString().contains("AdsInactive");
		   	
		   	if (a || d || ee){  
		   		category = "3";
		   		cat = "1";
		   	}
		   	else if (b){
		   		category = "1"; 
		   		cat = "2";
		   	}
		   	else if (c){
		   		category = "2"; 
		   		cat = "3";
		   	}
		   	
		   	if (tag == 0){
		   		cat = "3";
		   	}
		   	else if (tag == 1){
		   		cat = "2";
		   	}
		   	
		   	//System.out.println(tag);
		   	//Toast.makeText(getBaseContext(), tag, Toast.LENGTH_LONG).show();
		 
		   	// ****** GET IMAGE URLS via APIs
		   	profile_gender = profile_gender.replace(" ", "%20");
		   	if (profile_username == null){
		   		profile_username = "";
		   	}
		   	String Url_search = ""; 
		   	if (profile_country.equals("Philippines"))
		   	{
		   		Url_search = "http://f8mobile.net/account/index.php/webservice_api/get_data?gender="+profile_gender+"&category="+cat+"&age="+profile_age+"&location="+profile_country+"&province="+profile_province+"&city="+profile_town+"&day="+day+"&username="+profile_username;
		   		//Url_search = "http://f8mobile.net/account/index.php/webservice_api/get_data?gender="+profile_gender+"&category="+cat+"&age="+profile_age+"&location=Philippines&province=Isabela&city=BenitoSoliven&day="+day+"&username="+profile_username;
		   	}
		   	else{
		   		Url_search = "http://f8mobile.net/account/index.php/webservice_api/get_data?gender="+profile_gender+"&category="+cat+"&age="+profile_age+"&location="+profile_country+"&day="+day+"&username="+profile_username;
		   	}
		   	
		   	//Url_search = "http://f8mobile.net/account/index.php/webservice_api/get_data?gender="+profile_gender+"&category="+cat+"&age="+profile_age+"&location=Philippines&province=Nenel&city=All&day="+day+"&username="+profile_username;
		   	
		   	//String Url_search = "http://f8mobile.net/account/index.php/webservice_api/get_data?gender="+profile_gender+"&category="+cat+"&age="+profile_age+"&location=Philippines&day="+day;
		   	final String URL = (Url_search); 
		   	System.out.println(Url_search);
		
			parser = new XMLParser();
			xml = parser.getXmlFromUrl(URL); // getting XML
			Document doc = parser.getDomElement(xml); // getting DOM element
			
			NodeList nl = doc.getElementsByTagName(KEY_RESPONSE_HEADER);
			Element e = (Element) nl.item(0);
			
			// ******** GET THE RESULT OF API
			response_result = parser.getValue(e, KEY_RESPONSE_RESULT);
			System.out.println("RESULT OF XML: " + response_result);
			//String noimg_url = "http://f8mobile.net/images/ads/two/1416473103_377.jpg";
			String noimg_url = "http://f8mobile.net/images/ads/f8_android.png";
			
			// ******** GET TOTAL COUNT OF IMAGE TO SET ON IMAGE VIEW 			
			response_count = parser.getValue(e, KEY_RESPONSE_COUNT);
			response_total = parser.getValue(e, KEY_RESPONSE_TOTAL);

			String[] arr = new String[1];
			String[] arr_site = new String[1];
			String[] arr_contact = new String[1];
 			if (cat.equals("3")){
 				arr = new String[5];
 				arr_site = new String[5];
 				arr_contact = new String[5];
 				myImageListFour = new ArrayList<Drawable>();
 				myImageLinkFour = new ArrayList<String>();
 				myContactFour = new ArrayList<String>();
 				//myImageList = myImageListFour;
 			}
 			else if (cat.equals("2")){
 				arr = new String[3];
 				arr_site = new String[3];
 				arr_contact = new String[3];
 				myImageListTwo = new ArrayList<Drawable>();
 				myImageLinkTwo = new ArrayList<String>();
 				myContactTwo = new ArrayList<String>();
 				//myImageList = myImageListTwo;
 			}
 			else if (cat.equals("1")){
 				arr = new String[2];
 				arr_site = new String[2];
 				arr_contact = new String[2];
 				myImageListOne = new ArrayList<Drawable>();
 				myImageLinkOne = new ArrayList<String>();
 				myContactOne = new ArrayList<String>();
 				//myImageList = myImageListOne;
 			}
			
			if (response_result.equals("0001")){
	 			
	 			System.out.println(arr.length);
	 			
	 			for (int number = 1; number < arr.length; number++) { 
	 				String image_link = KEY_RESPONSE_DATA;
	 				String site_link = KEY_RESPONSE_SITE;
	 				String contact_link = KEY_RESPONSE_CONTACT;
	 				//System.out.println("IMAGE: " + image_link + number);
	 				
	 				arr[number] = image_link + number; 
	 				arr_site[number] = site_link + number;
	 				arr_contact[number] = contact_link + number;
	 				
	 				
	 				if (number <= Integer.parseInt(response_count)){
	 					// ********* GET IMAGES
	 					response_id = parser.getValue(e, arr[number]);
	 					response_site = parser.getValue(e, arr_site[number]);
	 					response_contact = parser.getValue(e, arr_contact[number]);
	 					
	 					if (response_site.equals("")){
	 						response_site = "-";
	 					}
	 					
	 					if (cat.equals("3")){
	 						//System.out.println("SITE: " + response_site);
	 						myImageListFour.add(imageLoader(response_id));
	 						myImageLinkFour.add(response_site);
	 						myContactFour.add(response_contact);
	 					}
	 					else if (cat.equals("2")){
	 						//System.out.println("SITE: " + response_site);
	 						myImageListTwo.add(imageLoader(response_id));
	 						myImageLinkTwo.add(response_site);
	 						myContactTwo.add(response_contact);
	 					}
	 					else if (cat.equals("1")){
	 						//System.out.println("SITE ONE: " + response_site);
	 						myImageListOne.add(imageLoader(response_id));
	 						myImageLinkOne.add(response_site);
	 						myContactOne.add(response_contact);
	 						//System.out.println("ARRAY SITE: " + myImageLinkOne);
	 						
	 					}
	 					System.out.println("IMAGE: " + response_id);
	 				}
	 				else{
	 					//response_site = "-";
	 					if (cat.equals("3")){
	 						//Drawable defaultImg = getResources().getDrawable(R.drawable.f8_android);
	 						//myImageListFour.add(defaultImg);
	 						myImageListFour.add(imageLoader(noimg_url));
	 						myImageLinkFour.add("-");
	 						myContactFour.add("-");
	 					}
	 					else if (cat.equals("2")){
	 						//Drawable defaultImg = getResources().getDrawable(R.drawable.f8_android);
	 						//myImageListTwo.add(defaultImg);
	 						myImageListTwo.add(imageLoader(noimg_url));
	 						myImageLinkTwo.add("-");
	 						myContactTwo.add("-");
	 					}
	 					else if (cat.equals("1")){
	 						//Drawable defaultImg = getResources().getDrawable(R.drawable.f8_android);
	 						//myImageListOne.add(defaultImg);
	 						myImageListOne.add(imageLoader(noimg_url));
	 						myImageLinkOne.add("-");
	 						myContactOne.add("-");
	 					}
	 				}
	 				//System.out.println(number + arr[number]);// + ": " + response_id);
	 		    }
			}
			else{
				response_total = "0";
				if (cat.equals("3")){
					/*Drawable defaultImg = getResources().getDrawable(R.drawable.f8_android);
					myImageListFour.add(defaultImg);
					myImageListFour.add(defaultImg);
					myImageListFour.add(defaultImg);
					myImageListFour.add(defaultImg);
					myImageListFour.add(defaultImg);*/
					myImageListFour.add(imageLoader(noimg_url));
					myImageListFour.add(imageLoader(noimg_url));
					myImageListFour.add(imageLoader(noimg_url));
					myImageListFour.add(imageLoader(noimg_url));
					myImageListFour.add(imageLoader(noimg_url));
					
					myImageLinkFour.add("-");
					myImageLinkFour.add("-");
					myImageLinkFour.add("-");
					myImageLinkFour.add("-");
					myImageLinkFour.add("-");
					
					myContactFour.add("-");
					myContactFour.add("-");
					myContactFour.add("-");
					myContactFour.add("-");
					myContactFour.add("-");
				}
				else if (cat.equals("2")){
					/*Drawable defaultImg = getResources().getDrawable(R.drawable.f8_android);
					myImageListTwo.add(defaultImg);
					myImageListTwo.add(defaultImg);
					myImageListTwo.add(defaultImg);*/
					myImageListTwo.add(imageLoader(noimg_url));
					myImageListTwo.add(imageLoader(noimg_url));
					myImageListTwo.add(imageLoader(noimg_url));
					
					myImageLinkTwo.add("-");
					myImageLinkTwo.add("-");
					myImageLinkTwo.add("-");
					
					myContactTwo.add("-");
					myContactTwo.add("-");
					myContactTwo.add("-");
				}
				else if (cat.equals("1")){
					/*Drawable defaultImg = getResources().getDrawable(R.drawable.f8_android);
					myImageListOne.add(defaultImg);
					myImageListOne.add(defaultImg);*/
					myImageListOne.add(imageLoader(noimg_url));
					myImageListOne.add(imageLoader(noimg_url));
					
					myImageLinkOne.add("-");
					myImageLinkOne.add("-");
					
					myContactOne.add("-");
					myContactOne.add("-");
				}
			}	
    }
    
    public float earningUpdate(String price){
    	float previous_earnings = response_earnings;
    	profile_longtitude = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.LOCATION_LONG, null);
    	
    	if (profile_longtitude == null)
    	{
    		profile_longtitude = "0";
    	}
    	
    	// ******* GET NAME OF USER
    	profile_latitude = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.LOCATION_LAT, null);
    	
    	if (profile_latitude == null)
    	{
    		profile_latitude = "0";
    	}
    	System.out.println("LONG: " + profile_longtitude + "\nLAT: " + profile_latitude);
    	try{
    		String earningURL = "http://f8mobile.net/web_api/index.php/webservice_api/add_earnings?client_id="+profile_client+"&earnings="+price+"&longitude="+profile_longtitude+"&latitude="+profile_latitude;
	   		final String eURL = (earningURL);
	   		
	   		parser = new XMLParser();
			xml = parser.getXmlFromUrl(eURL); // getting XML
			Document doc = parser.getDomElement(xml); // getting DOM element
			
			NodeList nl = doc.getElementsByTagName(KEY_RESPONSE_HEADER);
			Element e = (Element) nl.item(0);
			response_result = parser.getValue(e, KEY_RESPONSE_RESULT);
			response_client = parser.getValue(e, KEY_REPOSNSE_CLIENT);
			response_earnings = Float.parseFloat(parser.getValue(e, KEY_RESPONSE_EARNINGS));
			System.out.println("category: " +cat);
			System.out.println("price: " +price);
			System.out.println("earnings: " + response_earnings);
			System.out.println("http://f8mobile.net/web_api/index.php/webservice_api/add_earnings?client_id="+profile_client+"&earnings="+price+"&longtitude="+profile_longtitude+"&latitude="+profile_latitude);
    	}
    	catch(Exception e){
    		response_earnings = previous_earnings;
    	}
    	return response_earnings;
    }
   
    
    @SuppressLint("InflateParams") 
    public Drawable imageLoader(String URL){
    	topMapButtonsLayout = getLayoutInflater().inflate(R.layout.home_button,null);
	     
 	   	ImageView ads_steady = (ImageView)topMapButtonsLayout.findViewById(R.id.four_steady);
    	
 	   	imgLoader = new ImageLoader(this);
		imgLoader.DisplayImage(URL, ads_steady);
		
		//Bitmap bitmap=ImageLoader.memoryCache.get(URL);
		   
		mybmp =  new BitmapDrawable(getResources(),ImageLoader.bitmap);
		
		return mybmp;
    }
	
    public void getUserDetailsFromPrefs(){
    	
    	// ******* GET GCM REG ID
    	profile_gcm = PreferenceConnector.readString(MainActivity.this,
    			PreferenceConnector.GCM_REG_ID, null);
    	// ******* GET USERNAME
    	profile_username = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.USERNAME, null);
    	
    	// ******* GET CLIENT ID
    	profile_client = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.CLIENT_ID, null);
    	
    	// ******* GET NAME OF USER
    	profile_name = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.NAME, null);
    	
    	// ******* GET NAME OF USER
    	profile_serial = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.SERIAL, null);
    	
    	// ******* GET NAME OF USER
    	profile_gender = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.GENDER, null);
    	
    	// ******* GET NAME OF USER
    	profile_age = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.AGE, null);
    	
    	// ******* GET NAME OF USER
    	profile_c_status = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.CIVIL_STATUS, null);
    	
    	// ******* GET NAME OF USER
    	profile_address = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.ADDRESS, null);
    	
    	// ******* GET NAME OF USER
    	profile_country = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.LOCATION, null);
    	
    	if(profile_country == null)
  	  	{
    		profile_country = profile_address.replace(" ", "");
  	  	}
    	
    	
    	profile_province = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.PROVINCE, null);
    	
    	if(profile_province == null)
  	  	{
  	   		profile_province = "";
  	  	}
        /*else if (profile_province.equals("MetroManila"))
        {
      	  	profile_province = "NCR";
        }*/
    	
    	profile_town = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.TOWN, null);
    	
    	if(profile_town == null)
  	  	{
    		profile_town = "";
  	  	}
    	
     	// ******* GET NAME OF USER
    	profile_longtitude = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.LOCATION_LONG, null);
    	
    	if (profile_longtitude == null)
    	{
    		profile_longtitude = "";
    	}
    	
    	// ******* GET NAME OF USER
    	profile_latitude = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.LOCATION_LAT, null);
    	
    	if (profile_latitude == null)
    	{
    		profile_latitude = "";
    	}
    	
    	profile_earnings = PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.EARNINGS, null);
    	
    	Calendar calendar = Calendar.getInstance();
    	day = calendar.get(Calendar.DAY_OF_WEEK);
    }
    
    public void alertConnection(){
		//LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
	    //boolean statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	      
	      status = NetworkUtil.getConnectivityStatusString(this);
	      
	      if (status.equals("3")){
	    	  if (!alertOn){
	    		  alertOn = true;
		    	  //Toast.makeText(getBaseContext(), "Internet Connections or GPS is OFF!!", Toast.LENGTH_LONG).show();
		    	  
		    	  AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		          builder1.setMessage("Please Turn on Internet Connection.\n");
		          builder1.setCancelable(false);
		          builder1.setPositiveButton("Try Again",
		                  new DialogInterface.OnClickListener() {
		              @Override
					public void onClick(DialogInterface dialog, int id) {
		            	  alertOn = false;
		            	  alertConnection();
		            	  
		            	  //finish();
		                  dialog.cancel();  
		              }
		          });
		          AlertDialog alert11 = builder1.create();
		          alert11.show();
	    	  }
	    	  
	      }
	      else{  
	    	  alertOn = false;
	      }
	}
    
    @Override
    protected void onPause() {
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	checkPlayServices();
    }
    
    @Override 
    protected void onDestroy() {
    	super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
   
	private static long back_pressed;
	
	@Override
	public void onBackPressed() {
		
	    if (WebPage.browser != null && WebPage.browser.canGoBack()) {
	        WebPage.browser.goBack();
	        WebPage.urlbox.setText(WebPage.url);
	    } else {  
	        // The back key event only counts if we execute super.onBackPressed();
	    	if (back_pressed + 2000 > System.currentTimeMillis()){
	    		
	    		taskOut = new initTaskLogOut();
	    		taskOut.execute();
	    		//super.onBackPressed();
	    	}
	        else {
	        	Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
	        }
	    	back_pressed = System.currentTimeMillis();
	    } 
	}
	
	@Override
    protected void onStop() 
    {
        super.onStop();
        Log.d("Home", "Home Button Pressed!");
        taskOut = new initTaskLogOut();
        taskOut.execute();
        // insert here your instructions
    }
	
	Timer timer1;
	int secs = 0;
	
	@Override
	public void onUserInteraction(){
		idle = Integer.valueOf(PreferenceConnector.readString(MainActivity.this,
				PreferenceConnector.IDLESTATUS, null));
		if (idle == 1){
			PreferenceConnector.writeString(this, PreferenceConnector.IDLESTATUS,"0");
			
			FragmentsView(5);
		}
		timer1.cancel();
		secs = 0;
		activeCheck();
	}

	public void activeCheck(){
		//System.out.println(secs);
		final Handler handler = new Handler();
		timer1 = new Timer();
		TimerTask doAsynchronousTask = new TimerTask() {       
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() { 
						secs = secs + 1000;
						if (secs == 600000){
							alertIdle();
						}  
					}
				});
			}  
		};
		timer1.schedule(doAsynchronousTask, 0, 1000);    
	}
	  
	public void alertIdle(){
		System.out.println("idle");
		task.cancel(true);
		PreferenceConnector.writeString(this, PreferenceConnector.IDLESTATUS,"1");
		//
		timer1.cancel();
		FragmentsView(4);
	}
	
	public class PairOut
	{
		String response_id;
		String response_message;
		String claim_code;
	}
	
   // ------- start of Async Task -------- //
   private class initTaskLogOut extends AsyncTask <String, Void, PairOut> 
   {
	   
       @Override
		protected PairOut doInBackground(String... params) 
       {
    	   	ParseLogOut();
    	   	System.out.println("Do Background");
    	   
       		PairOut p = new PairOut();
	        //p.response_id = earn;
	        //p.response_message = response_count;
	        //p.response_count = response_count;
		    return p;
       }
       
       @Override
       protected void onPostExecute(PairOut p) 
       {
    	   if (pd.isShowing()) {
               pd.dismiss();
              // MainActivity.super.onBackPressed(); 
               MainActivity.this.finish();
               //MainActivity.super.onStop();
               System.out.println("Post Execute");
           }
       }
       
       @Override
       protected void onPreExecute() 
       {
    	   pd = new ProgressDialog(MainActivity.this);
    	   pd.setMessage("Logging Out...");
           pd.show();
       }

   }
   
   public void ParseLogOut(){
	   try{
   		String url_version = "http://f8mobile.net/web_api/index.php/webservice_api/logout?client_id="+profile_client;
		   	final String URL = (url_version);
		   	//System.out.println("http://f8mobile.net/account/index.php/webservice_api/get_data?gender="+profile_gender+"&category="+cat+"&age="+profile_age+"&location=Philippines&day="+day);
		
			parser = new XMLParser();
			xml = parser.getXmlFromUrl(URL); // getting XML
			Document doc = parser.getDomElement(xml); // getting DOM element
			
			NodeList nl = doc.getElementsByTagName(KEY_RESPONSE_HEADER);
			Element e = (Element) nl.item(0);
			
			String logout;
			logout = parser.getValue(e, KEY_RESPONSE_RESULT);	
   	}
   	catch(Exception e){
   		
   	}
   }
	
   private boolean checkPlayServices() {
	    boolean result = false;
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    9000).show();
	        } else {
	            Log.e("MainActivity", "This device is not supported.");
	            finish();
	        }
	        return result;
	    }
	    result = true;
	    
	    Log.e("", "checkPlayServices: " + result);
	    return result;
	} 
}

