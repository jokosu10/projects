package com.smsgt.mobile.application.supertsuper_v3.activities;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mapquest.android.maps.DefaultItemizedOverlay;
import com.mapquest.android.maps.GeoPoint;
import com.mapquest.android.maps.ItemizedOverlay;
import com.mapquest.android.maps.MapView;
import com.mapquest.android.maps.MyLocationOverlay;
import com.mapquest.android.maps.OverlayItem;
import com.smsgt.mobile.application.supertsuper_v3.R;
import com.smsgt.mobile.application.supertsuper_v3.asynctask_classes.AsyncTaskStrongloopCommonHttpCall;
import com.smsgt.mobile.application.supertsuper_v3.asynctask_classes.GetAllTrafficProfilesFromStrongloop;
import com.smsgt.mobile.application.supertsuper_v3.asynctask_classes.MapViewLoadTrafficProfileAsync;
import com.smsgt.mobile.application.supertsuper_v3.asynctask_classes.SendTravelDataOnStrongloop;
import com.smsgt.mobile.application.supertsuper_v3.asynctask_classes.SendTsuperUserProfileToStrongloop;
import com.smsgt.mobile.application.supertsuper_v3.asynctask_classes.TrafficProfileTimer;
import com.smsgt.mobile.application.supertsuper_v3.custom_ui.RewardsAdapter;
import com.smsgt.mobile.application.supertsuper_v3.custom_ui.TrafficProfileDialogFragment;
import com.smsgt.mobile.application.supertsuper_v3.database.model.CategoriesAndReportModel;
import com.smsgt.mobile.application.supertsuper_v3.database.model.RewardsModel;
import com.smsgt.mobile.application.supertsuper_v3.database.model.TsuperTrafficProfileModel;
import com.smsgt.mobile.application.supertsuper_v3.database.model.TsuperUserProfileModel;
import com.smsgt.mobile.application.supertsuper_v3.util_helpers.BitmapScalerAndResizer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class DriveActivity extends FragmentActivity {
	
	private MapView mapView;
	private ProgressDialog progressDialog;
//	private AnnotationView annotationView;
	private MyLocationOverlay myLocationOverlay;
	private Location updatedLocation;
    private LocationListener locListener;
    private LocationManager locManager;
    private SensorManager sensorManager;
    private Sensor sensorGravity, sensorMagnetometer;
    private SensorEventListener sensorEventListener;
    
    private static Handler mHandler;
    private static TextView currentTripDistanceTextView, totalTripDistanceTextView, totalPointsTextView,currentAverageSpeedTextView;
    private static TextView totalDistanceTextView, averageSpeedTextView, totalCurrentTripMeterTextView, slidingDrawerTotalPointsTextView, slidingDraweruserProfileName;
    
    private float[] mLastGravity = new float[3],  mLastMagnetometer = new float[3];
    private float[] mR = new float[9], mR2 = new float[9], mOrientation = new float[3];
    
    private String bestProvider;
    private Button categoriesBtn, reportsBtn, goBackToCurrentLocBtn;
    private View searchPrompt;
 
    private double speed = 0, averageSpeed = 0.0;
    
    //for average calculation
    private double[] forAverage;
    private int counter , arrayCount;
    private float totalDistance = 0f, currentTripTotalDistance = 0f, distanceTo = 0f;

    static final float ALPHA = 0.25f; // for low-pass filtering of sensor values
    
    private TsuperUserProfileModel tsuperUserProfile;
    private BitmapScalerAndResizer bitmapScalerAndResizer;
    private RewardsAdapter rewardsAdapter;
    private ListView contentListView;
    
    private ArrayList<RewardsModel> listRewardsModels;
    private ArrayList<CategoriesAndReportModel> overlayList = new ArrayList<CategoriesAndReportModel>();
    private ArrayList<TsuperTrafficProfileModel> tsuperTrafficProfileList = new ArrayList<TsuperTrafficProfileModel>();
    private String tsuperId; 
    
    private String travelInfoJSONResponse = "";
    private JSONArray travelInfoJArray;
    private boolean isGpsEnabled = false;
	
    private Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08"));
	private long timestamp;
	private Timer trafficProfileTimer = new Timer();
    
    @SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
		
        try {
        	
        	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            setContentView(R.layout.drive_activity); 
            
            tsuperUserProfile = (TsuperUserProfileModel) getIntent().getSerializableExtra("tsuperUserProfile");
            tsuperId = tsuperUserProfile.getUserId();
            Log.e("", "DriveActivity tsuperId: " + tsuperId);
            
            bitmapScalerAndResizer = new BitmapScalerAndResizer();                   	
    		mapView = (MapView) findViewById(R.id.driveActivityMap);
    		//annotationView = new AnnotationView(mapView);		
            locManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
            sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            sensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            

            
    		myLocationOverlay = new MyLocationOverlay(this, mapView);
    		progressDialog = new ProgressDialog(DriveActivity.this);
    		  		
    		isGpsEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    		if(!isGpsEnabled) {
    			Log.e("", "Checking for Network");
    			isGpsEnabled = locManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
    			if(!isGpsEnabled) {
        			Log.e("", "Checking for Passive");
        			isGpsEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    			}
    		}
    		
    		if(isGpsEnabled) {	
    			
    			try {	
    		        this.updatedLocation = null;
    		        this.forAverage = new double[5];
    		        this.counter = 1;
    		        this.arrayCount = 0;				
    				
    				myLocationOverlay.enableMyLocation();
    				myLocationOverlay.setFollowing(true);
    				myLocationOverlay.shouldAnimate(false);
    				myLocationOverlay.setBeaconAnimationOutlinePaint(null);
    				myLocationOverlay.setBeaconAnimationFillPaint(null);
    				
    				myLocationOverlay.runOnFirstFix(new Runnable() {
    					
    					@Override
    					public void run() {
    						addCarMarker();
    						mapView.getOverlays().add(myLocationOverlay);
    						mapView.getController().animateTo(new GeoPoint(myLocationOverlay.getMyLocation().getLatitudeE6(),myLocationOverlay.getMyLocation().getLongitudeE6()));
    						mapView.getController().setZoom(20);
    						mapView.setClickable(true);
    						mapView.getController().setCenter(myLocationOverlay.getMyLocation());
    						
    					}
    				});
    				
//    				annotationView.setClickable(true);
//    				annotationView.setOnClickListener(new OnClickListener() {
//    					
//    					@Override
//    					public void onClick(View v) {
//    						AnnotationView showedAnnotation = (AnnotationView) v;
//    						showedAnnotation.hide();
//    					}
//    				});
    				
    		        final Criteria criteria = new Criteria();
    		        criteria.setAccuracy(Criteria.ACCURACY_FINE);
    		        criteria.setSpeedRequired(true);
    		        criteria.setAltitudeRequired(true);
    		        criteria.setBearingRequired(true);
    		        criteria.setCostAllowed(true);
    		        criteria.setPowerRequirement(Criteria.POWER_LOW);
    		       
    		        locListener = addLocationListener();
    		        sensorEventListener = addSensorEventListener();
    		            		        
    		        bestProvider = locManager.getBestProvider(criteria, true);
    		        Log.e("onCreate -> ", " provider: " + bestProvider);
    		            
    		        // request location updates every 20 sec and every 1 meters
    		        locManager.requestLocationUpdates(bestProvider, 20000, 1, locListener);
    		        
    		        final View inflatedDrawerLayout = getWindow().getLayoutInflater().inflate(R.layout.sliding_drawer, null);
    		        final View topMapButtonsLayout = getWindow().getLayoutInflater().inflate(R.layout.top_map_transparent_buttons, null);
    		        
    		        int width = getWindow().getAttributes().width, height = getWindow().getAttributes().height;
    		        LayoutParams params = new LayoutParams(width, height);
    		        getWindow().addContentView(topMapButtonsLayout, params);
    		        getWindow().addContentView(inflatedDrawerLayout, params);		        		       		       
    		        
    		        final ImageButton searchLocationButton = (ImageButton) topMapButtonsLayout.findViewById(R.id.searchBtn);
    		        searchLocationButton.setOnClickListener(new OnClickListener() {
    					
    					@Override
    					public void onClick(View v) {
    						
    						searchLocationButton.setVisibility(View.INVISIBLE);
    						searchPrompt = getWindow().getLayoutInflater().inflate(R.layout.search_location_layout, null);
    						final PopupWindow searchPopupWindow = new PopupWindow(searchPrompt, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
    						
    						final EditText searchLocationText = (EditText) searchPrompt.findViewById(R.id.searchLocationText);						
    						searchLocationText.setOnKeyListener(new OnKeyListener() {
    							
    							@Override
    							public boolean onKey(View v, int keyCode, KeyEvent event) {
    								
    								if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && !searchLocationText.getText().toString().trim().isEmpty()) {
    									new ConnectToMapQuestWebService().execute(searchLocationText.getText().toString());		
    									return true;
    								}
    								
    								return false; 
    							}
    						});
    						
    						searchPopupWindow.setOutsideTouchable(true);
    						searchPopupWindow.setFocusable(true);
    						searchPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    						
    						searchPopupWindow.setOnDismissListener(new OnDismissListener() {
    							
    							@Override
    							public void onDismiss() {
    								searchLocationText.setVisibility(View.GONE);
    								searchPrompt.setVisibility(View.GONE);
    								searchPopupWindow.dismiss();
    								searchLocationButton.setVisibility(View.VISIBLE);
    							}
    						});
    						
    						if(Build.VERSION.SDK_INT < 11 ) {
    							searchPopupWindow.showAtLocation(searchPrompt, Gravity.TOP|Gravity.RIGHT, 0, 39);
    						} else {
    							searchPopupWindow.showAsDropDown(searchPrompt);
        						
    						}
    					}
    				});
    		        
    		        
    		        categoriesBtn = (Button) topMapButtonsLayout.findViewById(R.id.categoriesBtn);  
    		        categoriesBtn.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						
    						final View categoriesPrompt = getWindow().getLayoutInflater().inflate(R.layout.categories_prompt_layout, null);
    						final PopupWindow categoryPopupWindow = new PopupWindow(categoriesPrompt, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
    						
    						// ***** gas button ********** //
    						final Button gasButton = (Button) categoriesPrompt.findViewById(R.id.cat_button1);
    						gasButton.setOnClickListener(new OnClickListener() {
    							@Override
    							public void onClick(View v) {
    								categoryPopupWindow.dismiss();
    								showPopUpWindow(1);
    							}
    						});
    						
    						// ***** end gas button ********** //
    						
    						// ***** atm button ********** //
    						final Button atmButton = (Button) categoriesPrompt.findViewById(R.id.cat_button2);
    						atmButton.setOnClickListener(new OnClickListener() {
    							@Override
    							public void onClick(View v) {
    								categoryPopupWindow.dismiss();
    								showPopUpWindow(2);
    							}
    						});
    						
    						// ***** end atm button ********** //						
    						
    						// ***** hospital button ********** //
    						final Button hospitalButton = (Button) categoriesPrompt.findViewById(R.id.cat_button3);
    						hospitalButton.setOnClickListener(new OnClickListener() {
    							@Override
    							public void onClick(View v) {
    								categoryPopupWindow.dismiss();
    								showPopUpWindow(3);
    							}
    						});
    						
    						// ***** end hospital button ********** //
    						
    						// ***** fastfood button ********** //
    						final Button fastFoodButton = (Button) categoriesPrompt.findViewById(R.id.cat_button4);
    						fastFoodButton.setOnClickListener(new OnClickListener() {
    							@Override
    							public void onClick(View v) {
    								categoryPopupWindow.dismiss();
    								showPopUpWindow(4);
    							}
    						});
    						
    						// ***** end fastfood button ********** //
    						
    						// ***** airport button ********** //
    						final Button airportButton = (Button) categoriesPrompt.findViewById(R.id.cat_button5);
    						airportButton.setOnClickListener(new OnClickListener() {
    							@Override
    							public void onClick(View v) {
    								categoryPopupWindow.dismiss();
    								showPopUpWindow(5);
    							}
    						});
    						
    						// ***** end airport button ********** //						

    						// ***** loading/unloading button ********** //
    						final Button loadUnloadButton = (Button) categoriesPrompt.findViewById(R.id.cat_button6);
    						loadUnloadButton.setOnClickListener(new OnClickListener() {
    							@Override
    							public void onClick(View v) {
    								categoryPopupWindow.dismiss();
    								showPopUpWindow(6);
    							}
    						});
    						
    						// ***** end loading/unloading button ********** //	
    						
    						categoryPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    						categoryPopupWindow.setOutsideTouchable(true);
    						categoryPopupWindow.setFocusable(true);
    						categoryPopupWindow.showAtLocation(categoriesBtn, Gravity.NO_GRAVITY, 0, 0);
    						
    						categoryPopupWindow.setOnDismissListener(new OnDismissListener() {
    							
    							@Override
    							public void onDismiss() {
    								categoriesPrompt.setVisibility(View.GONE);
    								categoryPopupWindow.dismiss();
    							}
    						});
    						
    					}
    				});
    		        
    		        reportsBtn = (Button) topMapButtonsLayout.findViewById(R.id.reportBtn);
    		        reportsBtn.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						
    						final View reportsPrompt = getWindow().getLayoutInflater().inflate(R.layout.reports_prompt_layout, null);
    						final PopupWindow reportPopupWindow = new PopupWindow(reportsPrompt, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
    						
    						// ***** accident button ********** //
    						final Button accidentButton = (Button) reportsPrompt.findViewById(R.id.report_button1);
    						accidentButton.setOnClickListener(new OnClickListener() {
    							@Override
    							public void onClick(View v) {
    								reportPopupWindow.dismiss();
    								showPopUpWindow(7);
    							}
    						});
    						
    						// ***** end accident button ********** //
    						
    						// ***** heavytraffic button ********** //
    						final Button heavytrafficButton = (Button) reportsPrompt.findViewById(R.id.report_button2);
    						heavytrafficButton.setOnClickListener(new OnClickListener() {
    							@Override
    							public void onClick(View v) {
    								reportPopupWindow.dismiss();
    								showPopUpWindow(8);
    							}
    						});
    						
    						// ***** end heavytraffic button ********** //						
    						
    						// ***** trafficlight button ********** //
    						final Button trafficlightButton = (Button) reportsPrompt.findViewById(R.id.report_button3);
    						trafficlightButton.setOnClickListener(new OnClickListener() {
    							@Override
    							public void onClick(View v) {
    								reportPopupWindow.dismiss();
    								showPopUpWindow(9);
    							}
    						});
    						
    						// ***** end trafficlight button ********** //						
    						
    						// ***** policegraft button ********** //
    						final Button policegraftButton = (Button) reportsPrompt.findViewById(R.id.report_button4);
    						policegraftButton.setOnClickListener(new OnClickListener() {
    							@Override
    							public void onClick(View v) {
    								reportPopupWindow.dismiss();
    								showPopUpWindow(10);
    							}
    						});
    						
    						// ***** end policegraft button ********** //						
    						
    						// ***** roadblock button ********** //
    						final Button roadblockButton = (Button) reportsPrompt.findViewById(R.id.report_button5);
    						roadblockButton.setOnClickListener(new OnClickListener() {
    							@Override
    							public void onClick(View v) {
    								reportPopupWindow.dismiss();
    								showPopUpWindow(11);
    							}
    						});
    						
    						// ***** end roadblock button ********** //						
    						
    						// ***** accident6 button ********** //
    						final Button noswervingButton = (Button) reportsPrompt.findViewById(R.id.report_button6);
    						noswervingButton.setOnClickListener(new OnClickListener() {
    							@Override
    							public void onClick(View v) {
    								reportPopupWindow.dismiss();
    								showPopUpWindow(12);
    							}
    						});
    						
    						// ***** end noswerving button ********** //						
    						
    						reportPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    						reportPopupWindow.setOutsideTouchable(true);
    						reportPopupWindow.setFocusable(true);
    						reportPopupWindow.showAtLocation(reportsBtn, Gravity.NO_GRAVITY, 0, 0);
    						
    						reportPopupWindow.setOnDismissListener(new OnDismissListener() {
    							
    							@Override
    							public void onDismiss() {
    								reportsPrompt.setVisibility(View.GONE);
    								reportPopupWindow.dismiss();
    							}
    						});
    						
    					}
    				});
    		        
    		        goBackToCurrentLocBtn = (Button) topMapButtonsLayout.findViewById(R.id.crossHairBtn);
    		        goBackToCurrentLocBtn.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						
    						GeoPoint homeLocGeopoint = myLocationOverlay.getMyLocation();
    						if(homeLocGeopoint != null) {
    							mapView.getController().animateTo(homeLocGeopoint);
    							mapView.getController().setCenter(myLocationOverlay.getMyLocation());
    							mapView.invalidate();
    							myLocationOverlay.setFollowing(true);
    						} else {
    							Toast.makeText(DriveActivity.this, "home location cannot be found", Toast.LENGTH_SHORT);
    						}
    					}
    				}); 
    		        		        
    		        ImageButton endDriveButton = (ImageButton) inflatedDrawerLayout.findViewById(R.id.endDriveBtn);
    		        endDriveButton.setOnClickListener(new OnClickListener() {
    					
    					@Override
    					public void onClick(View v) {
    		
    						final View endDrive_prompt = getWindow().getLayoutInflater().inflate(R.layout.end_drive_prompt_layout,null, true);
    						final Dialog dialog = new Dialog(DriveActivity.this, R.style.DialogCustomTheme);
    						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    						
    						// *** END DRIVE final *** //updatedUserProfileIntent
    						Button endDriveOk = (Button) endDrive_prompt.findViewById(R.id.end_drive_ok_btn);
    						endDriveOk.setOnClickListener(new OnClickListener() {
    							@Override
    							public void onClick(View v) {
    								endDrive_prompt.setVisibility(View.GONE);
    								dialog.dismiss();
    								mapView.clearTilesInMemory();
    								Intent updatedUserProfileIntent = DriveActivity.this.getIntent();
    								tsuperUserProfile.setAverageMovingSpeed("0");
    								
    								updatedUserProfileIntent.putExtra("tsuperUserProfile", tsuperUserProfile);
    								setResult(RESULT_OK,updatedUserProfileIntent); 
    								
    								// to update 
    								new SendTsuperUserProfileToStrongloop(tsuperUserProfile, getApplicationContext()).execute("UserRegistrations/" + tsuperUserProfile.getUserId(), "PUT");
    								finish();	
    											
    							}
    						});				

    						currentTripDistanceTextView = (TextView) endDrive_prompt.findViewById(R.id.total_trip_with_icon_textview);
    						totalTripDistanceTextView = (TextView) endDrive_prompt.findViewById(R.id.total_distance_with_icon_textview);
    						totalPointsTextView = (TextView) endDrive_prompt.findViewById(R.id.total_points_with_icon_textview);
    			
    						currentTripDistanceTextView.setText("  " + String.valueOf((int)Math.round(Float.parseFloat(tsuperUserProfile.getCurrDistance()))) + " km");
    						totalTripDistanceTextView.setText("  " + String.valueOf((int)Math.round(Float.parseFloat(tsuperUserProfile.getTotalDistance()))) + " km");
    						totalPointsTextView.setText("  " + tsuperUserProfile.getTotalPoints() + " pts");
    						
    						WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();  
    						lp.dimAmount=0.90f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
    						dialog.getWindow().setAttributes(lp);
    						dialog.getWindow().setLayout(365, 300);
    						dialog.setContentView(endDrive_prompt);
    						dialog.getWindow().setBackgroundDrawable(new BitmapDrawable());
    						dialog.setCanceledOnTouchOutside(false);
    						dialog.show();						
    						
    					}
    				});
    		         		       
    		        currentAverageSpeedTextView = (TextView) inflatedDrawerLayout.findViewById(R.id.slide_handle_average_current_speed);
    		        averageSpeedTextView = (TextView) inflatedDrawerLayout.findViewById(R.id.slide_handle_kph_value); 
    		        totalDistanceTextView = (TextView) inflatedDrawerLayout.findViewById(R.id.slide_handle_total_distance_textview);	
    		        
    		        totalCurrentTripMeterTextView = (TextView) inflatedDrawerLayout.findViewById(R.id.slide_handle_trip_meter_textview);
    		        
    		        slidingDrawerTotalPointsTextView = (TextView) inflatedDrawerLayout.findViewById(R.id.slidingContentPointsTextView);
    		        slidingDraweruserProfileName = (TextView) inflatedDrawerLayout.findViewById(R.id.slidingContentNameTextView);
    			  
    		        slidingDraweruserProfileName.setText(tsuperUserProfile.getFirstName() + " " + tsuperUserProfile.getLastName()); // value should be from Strongloop backend
    			    slidingDrawerTotalPointsTextView.setText(String.format("%06d", Integer.parseInt(tsuperUserProfile.getTotalPoints()))); // value should be from Strongloop backend

    		        contentListView = (ListView) inflatedDrawerLayout.findViewById(R.id.content_listView);
    		        listRewardsModels = (ArrayList<RewardsModel>) getIntent().getSerializableExtra("rewardsList");
    				   
    		        rewardsAdapter = new RewardsAdapter(listRewardsModels, getApplicationContext(), tsuperUserProfile, "DriveActivity");
    		    	contentListView.setAdapter(rewardsAdapter);

    		        mHandler = new Handler() {
    		            public void handleMessage(Message msg) {
    		                switch (msg.what) {
    		                    case 1:
    		                    	DriveActivity.this.setData((Location) msg.obj);
    		                        break;
    		                        
    		                    case 2: 
    		                    		float rotate = (Float) msg.obj;
    		                    		rotate =  (float) (( 180 * rotate ) / Math.PI );
    		                    		
    		                            tsuperUserProfile.setTravelBearing(Float.toString(rotate));
    	                    			mapView.getController().setMapRotation((rotate));
    		                    		mapView.invalidate();
    		                    		break;
    		                    		
    		                    case 3: 
    		                    		Log.e("", "notifying changes on adapter");
    		                    		tsuperUserProfile = (TsuperUserProfileModel) msg.obj;
    		                    		//rewardsAdapter = new RewardsAdapter(rs.getRewards(), DriveActivity.this, tsuperUserProfile, "DriveActivity");
    		                    		rewardsAdapter = new RewardsAdapter(listRewardsModels, getApplicationContext(), tsuperUserProfile, "DriveActivity");
    		                    		contentListView.invalidate();
    		                    		contentListView.setAdapter(rewardsAdapter);
    		                    	 	break; 
    		                    	 	
    		                    case 4: tsuperTrafficProfileList = (ArrayList<TsuperTrafficProfileModel>) msg.obj;
    		                    		if(tsuperTrafficProfileList != null && tsuperTrafficProfileList.size() > 0) {
    		                    			showTrafficProfiles(tsuperTrafficProfileList);
    		                    		} 
    		                    		
    		                    		break;
    		                }
    		            }
    		        };
    		        
    		        tsuperTrafficProfileList = (ArrayList<TsuperTrafficProfileModel>) getIntent().getSerializableExtra("trafficProfileList");
    		        if(tsuperTrafficProfileList != null && tsuperTrafficProfileList.size() > 0) {
    		        	showTrafficProfiles(tsuperTrafficProfileList);
    		        } 
		    
    		        trafficProfileTimer.schedule(new TrafficProfileTimer(new GetAllTrafficProfilesFromStrongloop(mHandler, "DriveActivity")), 900000, 900000); // calls every 15 minutes
    		        
    			} catch (Exception e) {
    				Log.e(this.getLocalClassName(),"Exception on onCreate", e);
    			}
    			
    		} else {
    			alertbox("GPS Status!");
    		}       
        } catch(Exception e) {
        	Log.e(this.getLocalClassName(), "Exception on onCreate", e);
        }    
	}
	   
	protected void extractTravelInfo(JSONObject j) {
		
		CategoriesAndReportModel cARM = new CategoriesAndReportModel();

		try {
			cARM.setDescription(j.getString("description"));
			cARM.setLat(j.getString("latitude"));
			cARM.setLon(j.getString("longitude"));
			cARM.setName(j.getString("name"));
			cARM.setDrawableId(0);
			cARM.setPostedBy(j.getString("userId"));
			cARM.setPostedDate(new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(Long.parseLong(j.getString("timestamp"))));
		} catch (NumberFormatException e) {
			Log.e("", "", e);
		} catch (JSONException e) {
			Log.e("", "", e);
		}
		overlayList.add(cARM);
	}

	@Override
	protected void onResume() {
		try {
			myLocationOverlay.enableMyLocation();
			myLocationOverlay.setFollowing(true);
			myLocationOverlay.shouldAnimate(false);
			myLocationOverlay.setBeaconAnimationOutlinePaint(null);	
			myLocationOverlay.setBeaconAnimationFillPaint(null);
			locManager.requestLocationUpdates(bestProvider, 20000, 1, locListener);
			trafficProfileTimer = new Timer();
			trafficProfileTimer.schedule(new TrafficProfileTimer(new GetAllTrafficProfilesFromStrongloop(mHandler, "DriveActivity")), 900000, 900000); // calls every 15 minutes
			super.onResume();
				
		} catch (Exception e) {
			Log.e(this.getLocalClassName(), "Exception on onResume", e);
		}

		sensorManager.registerListener(sensorEventListener,
		        sensorGravity,
		        SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(sensorEventListener,
		        sensorMagnetometer,
		        SensorManager.SENSOR_DELAY_NORMAL);	
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		try {
			myLocationOverlay.disableMyLocation();
			locManager.removeUpdates(locListener);
			sensorManager.unregisterListener(sensorEventListener);
			trafficProfileTimer.cancel();
		} catch(Exception e) {
			Log.e(this.getLocalClassName(), "Exception on onPause", e);
		}
		
	}
		
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onDestroy() {
		try {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			mapView.getOverlays().clear();
			//annotationView = null;
			mapView.destroy();
			mapView = null;
			myLocationOverlay.destroy();
			sensorGravity = null;
			sensorMagnetometer = null;
			locManager.removeUpdates(locListener);
			locListener = null;
			locManager = null;
			sensorManager.unregisterListener(sensorEventListener);
			sensorEventListener = null;
			sensorManager = null;
			trafficProfileTimer.cancel();
			trafficProfileTimer.purge();
			super.onDestroy();
			
		} catch(Exception e) {
			Log.e(this.getLocalClassName(), "Exception on onDestroy", e);
		}

	}
	
	private LocationListener addLocationListener() {
		
		 LocationListener locListener = new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) { }
			
			@Override
			public void onProviderEnabled(String provider) { }
			
			@Override
			public void onProviderDisabled(String provider) { }
			
			@Override
			public void onLocationChanged(Location location) {
					Message.obtain(mHandler, 1, location).sendToTarget();		
			}
		};        
		return locListener;
	}

	private SensorEventListener addSensorEventListener() {
		SensorEventListener sensorEventListener = new SensorEventListener() {
			
			@Override
			public void onSensorChanged(SensorEvent event) {
			
				if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
			        mLastGravity = lowpassFilter(event.values.clone(), mLastGravity);
			    }

			    if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			        mLastMagnetometer = lowpassFilter(event.values.clone(), mLastMagnetometer);
			    }

			    if (sensorGravity != null && sensorMagnetometer != null) {
			    	boolean success = SensorManager.getRotationMatrix(mR, mR2, mLastGravity, mLastMagnetometer);
			    	
			        if (success) {
			        	
		                switch (getWindowManager().getDefaultDisplay()
		                        .getRotation()) {
		                case Surface.ROTATION_0:
		                    SensorManager.remapCoordinateSystem(mR,
		                            SensorManager.AXIS_X, SensorManager.AXIS_Y,
		                            mR2);
		                    break;
		                case Surface.ROTATION_90:
		                    SensorManager.remapCoordinateSystem(mR,
		                            SensorManager.AXIS_Y,
		                            SensorManager.AXIS_MINUS_X,
		                            mR2);
		                    break;
		                case Surface.ROTATION_180:
		                    SensorManager.remapCoordinateSystem(mR,
		                            SensorManager.AXIS_MINUS_X,
		                            SensorManager.AXIS_MINUS_Y,
		                            mR2);
		                    break;
		                case Surface.ROTATION_270:
		                    SensorManager.remapCoordinateSystem(mR,
		                            SensorManager.AXIS_MINUS_Y,
		                            SensorManager.AXIS_X, mR2);
		                    break;
		                }
			        	
		                SensorManager.getOrientation(mR2,
		                        mOrientation);

	                    //SensorManager.remapCoordinateSystem(mR,
	                    //        SensorManager.AXIS_X, SensorManager.AXIS_Z,
	                    //        mR2);
	                    //SensorManager.getOrientation(mR2, mOrientation);
			            double azimuth = mOrientation[0];
			            float azimuthFloat = (float) azimuth;
			           //   double pitch = 180 * orientation[1] / Math.PI;
			           //   double roll = 180 * orientation[2] / Math.PI;
			            Message.obtain(mHandler, 2, azimuthFloat).sendToTarget();
			        }
			    }   		
			}
			
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}
		};
		return sensorEventListener;
	}

	
    protected float[] lowpassFilter(float[] input, float[] output) {
    	
    	if ( output == null ) return input;

        for ( int i=0; i < input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
	}

	private void setData(Location location) {
		
    	try {   	
    		
    		if(updatedLocation == null) {
    			updatedLocation = location;
    		} 
    			
    		distanceTo = location.distanceTo(updatedLocation);
			
    		// todo: interchange curr & prev distances upon location update
    		
			currentTripTotalDistance = (int)Math.round((currentTripTotalDistance) + (distanceTo/1000)); // in kilometers
			totalDistance = (int) Math.round(currentTripTotalDistance + Float.parseFloat(tsuperUserProfile.getTotalDistance())); // in kilometers
			
			speed = (DriveActivity.this.distanceTo / Math.pow(((location.getTime() / 1000) - (updatedLocation.getTime() / 1000)), 2)) * 3.6 ; // in kph    	        
			
			Log.e("Location data: ","distanceTo: " + DriveActivity.this.distanceTo + "\nCurrentTripTotal: " + (int)Math.round(DriveActivity.this.currentTripTotalDistance) + "\nTotalTripDistance: " + (int)Math.round(DriveActivity.this.totalDistance) + "\nSpeed: " + DriveActivity.this.speed
					+ "\ntsuperUserProfile.getCurrDistance(): " + tsuperUserProfile.getCurrDistance() + "\ntsuperUserProfile.getPrevDistance(): " + tsuperUserProfile.getPrevDistance() + "\ntsuperUserProfile.getTotalDistance(): " + tsuperUserProfile.getTotalDistance());
	
			updatedLocation = location;
			forAverage[arrayCount] = (int)Math.round(speed);
	    	
			double sum = 0;
	        for (int i = 0; i < arrayCount; i++) {
	             sum = sum + forAverage[i];
	        }
	        
	        averageSpeed = (int)Math.round((Integer.parseInt(tsuperUserProfile.getAverageMovingSpeed()) + sum) / counter);
	        tsuperUserProfile.setAverageMovingSpeed(Integer.toString((int)averageSpeed));
	        
	        Log.e("","Counter: " + DriveActivity.this.counter + ", ArrayCount: " + DriveActivity.this.arrayCount + ", ForAverageLength: " + DriveActivity.this.forAverage.length + ", Sum: " + sum + ", Ave. Speed: "+ DriveActivity.this.averageSpeed);
	     
			if((int)Math.round(Float.parseFloat(tsuperUserProfile.getAverageMovingSpeed())) >= 0 && (int)Math.round(Float.parseFloat(tsuperUserProfile.getAverageMovingSpeed())) < 35) {
				currentAverageSpeedTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.red));
			} else if((int)Math.round(Float.parseFloat(tsuperUserProfile.getAverageMovingSpeed())) >= 35 && (int)Math.round(Float.parseFloat(tsuperUserProfile.getAverageMovingSpeed())) < 50) {
				currentAverageSpeedTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.yellow));
			} else if((int)Math.round(Float.parseFloat(tsuperUserProfile.getAverageMovingSpeed())) >= 50 ) {
				currentAverageSpeedTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.green));
			}
			
	        tsuperUserProfile.setCurrDistance(Float.toString(currentTripTotalDistance));
	        tsuperUserProfile.setTotalDistance(Float.toString(totalDistance));
	        
	        int pointDivisibleByTen = (int) Math.round(Float.parseFloat(tsuperUserProfile.getCurrDistance())) / 10;
	        
	        Log.e("", "points current: " + Integer.toString(Integer.parseInt(tsuperUserProfile.getTotalPoints()) + pointDivisibleByTen));
	        
	        tsuperUserProfile.setTotalPoints(Integer.toString(Integer.parseInt(tsuperUserProfile.getTotalPoints()) + pointDivisibleByTen));
	        tsuperUserProfile.setPrevDistance(tsuperUserProfile.getCurrDistance());
	        
	        
	        totalDistanceTextView.setText(String.format("%06d", (int)Math.round(Float.parseFloat(tsuperUserProfile.getTotalDistance()))) + " km");
	        averageSpeedTextView.setText(String.format("%02d", (int)Math.round(Float.parseFloat(tsuperUserProfile.getAverageMovingSpeed()))));
	        totalCurrentTripMeterTextView.setText(String.format("%06d", (int)Math.round(Float.parseFloat(tsuperUserProfile.getCurrDistance()))) + " km"); 

			if (this.arrayCount == 4) {
			    this.arrayCount = 0;
			    this.counter = 1;
			} else {
				 this.arrayCount++;
				 this.counter++;
			}
			  
		    slidingDraweruserProfileName.setText(tsuperUserProfile.getFirstName() + " " + tsuperUserProfile.getLastName()); // value should be from Strongloop backend
		    slidingDrawerTotalPointsTextView.setText(String.format("%06d", Integer.parseInt(tsuperUserProfile.getTotalPoints()))); // value should be from Strongloop backend
		    
		    if(Integer.parseInt(tsuperUserProfile.getTotalPoints()) % 10 == 0 || Integer.parseInt(tsuperUserProfile.getTotalPoints()) >= 10) {
		    	Message.obtain(mHandler, 3, tsuperUserProfile).sendToTarget();
		    }
		    
			myLocationOverlay.onLocationChanged(location);
			mapView.getController().setCenter(myLocationOverlay.getMyLocation());
			myLocationOverlay.setFollowing(true);
			mapView.invalidate();
				
			timestamp = (cal.getTimeInMillis() / 1000);
			
			String[] params = new String[8];
			params[0] = "TravelInfos";
			params[1] = "POST";
			params[2] = Double.toString(myLocationOverlay.getMyLocation().getLatitude());
			params[3] = Double.toString(myLocationOverlay.getMyLocation().getLongitude());
			params[4] = "Travel";
			params[5] = "N/A";
			params[6] = "N/A";
			params[7] = String.valueOf(timestamp);
			
			new SendTravelDataOnStrongloop(tsuperUserProfile, getApplicationContext()).execute(params);
	
			// change trafficProfile current/average speed overlay value
			showTrafficProfiles(tsuperTrafficProfileList);
				
		} catch(Exception ex) {	
			
			Log.e(this.getLocalClassName(), "Exception on setData", ex);
		}
    }
      	
	/* add car marker overlay on current map */
	private void addCarMarker() {
		
		Drawable icon = getResources().getDrawable(R.drawable.caricon);
		myLocationOverlay.setMarker(icon, 0);	
	}
	/* end add car marker */
	
	/* activate GPS if not enabled */
	 private void alertbox(String title) {  
		 final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
		           .setCancelable(false)
		           .setTitle(title)
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		               public void onClick(final DialogInterface dialog, final int id) {
		                   startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
		               }
		           })
		           .setNegativeButton("No", new DialogInterface.OnClickListener() {
		               public void onClick(final DialogInterface dialog, final int id) {
		                    dialog.cancel();
		               }
		           });
		    final AlertDialog alert = builder.create();
		    alert.show();
	} 
	/* end GPS activation */ 
	
	/* get result from GPS activation */ 
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	     super.onActivityResult(requestCode, resultCode, data);
	     if (resultCode == 1) {
	        switch (requestCode) {
	           case 1: onCreate(data.getExtras()); break; 
	         }
	      } 
	  }	
	 /* end get result */
	 
	 /* show popup window for every category/report button clicked */
	 private void showPopUpWindow(final int viewType) {
		 
		 	int resourceLayout = 1, resourceButtonShow = 1, resourceButtonPost = 1;
		 	switch(viewType) {
		 	case 1: resourceLayout = R.layout.categories_inner_category_gas_prompt_layout; 
		 			resourceButtonShow = R.id.cat_inner_button1_show; 
		 			resourceButtonPost = R.id.cat_inner_button1_post; break;
		 			
		 	case 2: resourceLayout = R.layout.categories_inner_category_atm_prompt_layout; 
 					resourceButtonShow = R.id.cat_inner_button2_show; 
 					resourceButtonPost = R.id.cat_inner_button2_post; break;
 					
		 	case 3: resourceLayout = R.layout.categories_inner_category_hospital_prompt_layout; 
 					resourceButtonShow = R.id.cat_inner_button3_show; 
 					resourceButtonPost = R.id.cat_inner_button3_post; break;
 					
		 	case 4: resourceLayout = R.layout.categories_inner_category_fastfood_prompt_layout; 
 					resourceButtonShow = R.id.cat_inner_button4_show; 
 					resourceButtonPost = R.id.cat_inner_button4_post; break;
 					
		 	case 5: resourceLayout = R.layout.categories_inner_category_airport_prompt_layout; 
 					resourceButtonShow = R.id.cat_inner_button5_show; 
 					resourceButtonPost = R.id.cat_inner_button5_post; break;
 					
		 	case 6: resourceLayout = R.layout.categories_inner_category_loading_unloading_prompt_layout; 
 					resourceButtonShow = R.id.cat_inner_button6_show; 
 					resourceButtonPost = R.id.cat_inner_button6_post; break;
 					
		 	case 7: resourceLayout = R.layout.reports_inner_category_accident_prompt_layout; 
 					resourceButtonShow = R.id.rep_inner_button1_show; 
 					resourceButtonPost = R.id.rep_inner_button1_post; break;
 					
		 	case 8: resourceLayout = R.layout.reports_inner_category_heavytraffic_prompt_layout; 
					resourceButtonShow = R.id.rep_inner_button2_show; 
					resourceButtonPost = R.id.rep_inner_button2_post; break;
					
		 	case 9: resourceLayout = R.layout.reports_inner_category_trafficlight_prompt_layout; 
					resourceButtonShow = R.id.rep_inner_button3_show; 
					resourceButtonPost = R.id.rep_inner_button3_post; break;
					
		 	case 10: resourceLayout = R.layout.reports_inner_category_policegraft_prompt_layout; 
					 resourceButtonShow = R.id.rep_inner_button4_show; 
					 resourceButtonPost = R.id.rep_inner_button4_post; break;
					
		 	case 11: resourceLayout = R.layout.reports_inner_category_roadblock_prompt_layout; 
					 resourceButtonShow = R.id.rep_inner_button5_show; 
					 resourceButtonPost = R.id.rep_inner_button5_post; break;
					 
		 	case 12: resourceLayout = R.layout.reports_inner_category_noswerving_prompt_layout; 
					 resourceButtonShow = R.id.rep_inner_button6_show; 
					 resourceButtonPost = R.id.rep_inner_button6_post; break;
					 
		 	default: resourceLayout = R.layout.categories_inner_category_gas_prompt_layout; 
 					 resourceButtonShow = R.id.cat_inner_button1_show; 
 					 resourceButtonPost = R.id.cat_inner_button1_post; break;
		 	}
		 
		 
			View popUpView = getLayoutInflater().inflate(resourceLayout,null);
			final Dialog dialog = new Dialog(DriveActivity.this, R.style.DialogCustomTheme);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			
			// *** SHOW ON MAP *** //
			Button showOnMap = (Button) popUpView.findViewById(resourceButtonShow);
			showOnMap.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					showAllOverlaysWithSameCategory(viewType);
				}
			});
			
			// *** POST ON MAP *** //
			Button postOnMap = (Button) popUpView.findViewById(resourceButtonPost);
			postOnMap.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();	
					putOverlayOnMap(viewType);
				}
			});
				
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();  
			lp.dimAmount=0.90f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
			dialog.getWindow().setAttributes(lp);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
			dialog.getWindow().setLayout(365, 325);
			dialog.setContentView(popUpView);
			dialog.show();
	
	 }
	 /* end show popup window */
	 
	 // this method should connect to Strongloop
	 // to show all overlays within 5km radius
	 // for a specific category
	 private void showAllOverlaysWithSameCategory(int viewType) {
		 
		 String query = "";
		 switch(viewType) {
			 case 1: query = "Gas"; break;
			 case 2: query = "ATM"; break;
			 case 3: query = "Hospital"; break;
			 case 4: query = "Restaurant"; break;
			 case 5: query = "Airport"; break;
			 case 6: query = "Vehicle"; break;
			 case 7: query = "Accident"; break;
			 case 8: query = "Heavy"; break;
			 case 9: query = "Light"; break;
			 case 10: query = "Police"; break;
			 case 11: query = "Blocked"; break;
			 case 12: query = "Slippery"; break;
			 default: query = "Gas"; break;
		 }
		 String[] params = new String[2];
		 params[0] = "TravelInfos?filter[where][name][like]=" + query;
		 params[1] = "GET";
		 
		 try {
			 
         	travelInfoJSONResponse = new AsyncTaskStrongloopCommonHttpCall(progressDialog).execute(params).get();
         	if(!travelInfoJSONResponse.trim().isEmpty()) {
         		try {
						travelInfoJArray = new JSONArray(travelInfoJSONResponse);
					} catch (JSONException e) {
						e.printStackTrace();
					}
         	
         		for(int index = 0; index < travelInfoJArray.length(); index++) {
         			try {
							String indexString = travelInfoJArray.getString(index);
							JSONObject jObj = new JSONObject(indexString);									
							extractTravelInfo(jObj);				
						} catch (JSONException e) {
							e.printStackTrace();
						}	
         		} 	                		
         	}	 
		 } catch (Exception x) {
			 Log.e("", "", x);
		 }

		 if(overlayList.size() > 0 && overlayList != null && !overlayList.isEmpty()) {
			 for(final CategoriesAndReportModel l : overlayList) { 
				 
				    int drawableId = viewType;
					switch(drawableId) {
						 case 1: drawableId = R.drawable.cat_gasstation; break;
						 case 2: drawableId = R.drawable.cat_atm; break;
						 case 3: drawableId = R.drawable.cat_hospital; break;
						 case 4: drawableId = R.drawable.cat_restaurant; break;
						 case 5: drawableId = R.drawable.cat_airport; break;
						 case 6: drawableId = R.drawable.cat_waitingshed; break;
						 case 7: drawableId = R.drawable.rep_accident; break;
						 case 8: drawableId = R.drawable.rep_traffic; break;
						 case 9: drawableId = R.drawable.rep_3; break;
						 case 10: drawableId = R.drawable.rep_traffic_enforcer; break;
						 case 11: drawableId = R.drawable.rep_roadclosed; break;
						 case 12: drawableId = R.drawable.rep_6; break;
						 default: drawableId = R.drawable.cat_gasstation; break;
					 }
				 
					Drawable markerOverlay = getResources().getDrawable(drawableId);
					final DefaultItemizedOverlay overlay = new DefaultItemizedOverlay(markerOverlay);
					
					OverlayItem overlayItem = new OverlayItem(new GeoPoint(Double.parseDouble(l.getLat()), Double.parseDouble(l.getLon())), "", "");    	
					overlay.addItem(overlayItem);
					
					overlay.setTapListener(new ItemizedOverlay.OverlayTapListener() {
						@Override
						public void onTap(GeoPoint pt, MapView mapView) {
							// when tapped, show the annotation for the overlayItem
							int lastTouchedIndex = overlay.getLastFocusedIndex();
							if(lastTouchedIndex >- 1){
								//OverlayItem tapped = overlay.getItem(lastTouchedIndex);
								
								final View categoriesAndReportsPopUpView = getWindow().getLayoutInflater().inflate(R.layout.categories_reports_custom_annotationview_layout, null);
	    						final PopupWindow categoriesAndReportsPopUpWindow = new PopupWindow(categoriesAndReportsPopUpView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
	    						
	    						TextView categoriesAndReportNameTextView = (TextView) categoriesAndReportsPopUpView.findViewById(R.id.categories_reports_custom_annotation_view_nameTextView);
	    						TextView categoriesAndReportPostedByTextView = (TextView) categoriesAndReportsPopUpView.findViewById(R.id.categories_reports_custom_annotation_view_postedByTextView);
	    						TextView categoriesAndReportPostedDateTextView = (TextView) categoriesAndReportsPopUpView.findViewById(R.id.categories_reports_custom_annotation_view_postedDateTextView);
	    						TextView categoriesAndReportDescriptionTextView = (TextView) categoriesAndReportsPopUpView.findViewById(R.id.categories_reports_custom_annotation_view_descriptionTextView);
	    						
	    						categoriesAndReportNameTextView.setText("" + l.getName());
	    						categoriesAndReportPostedByTextView.setText("Posted By: " + l.getPostedBy());
	    						categoriesAndReportPostedDateTextView.setText("Posting Date: " + l.getPostedDate());
	    						categoriesAndReportDescriptionTextView.setText("Description: " + l.getDescription());
	    						
	    						categoriesAndReportsPopUpWindow.setBackgroundDrawable(new BitmapDrawable());
	    						categoriesAndReportsPopUpWindow.setOutsideTouchable(true);
	    						categoriesAndReportsPopUpWindow.setFocusable(true);
	    						categoriesAndReportsPopUpWindow.showAtLocation(mapView, Gravity.TOP, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    						
	    						categoriesAndReportsPopUpWindow.setOnDismissListener(new OnDismissListener() {
	    							
	    							@Override
	    							public void onDismiss() {
	    								categoriesAndReportsPopUpView.setVisibility(View.GONE);
	    								categoriesAndReportsPopUpWindow.dismiss();
	    							}
	    						});
								//annotationView.showAnnotationView(tapped);
							}
						}
					});
					
			    	mapView.getOverlays().add(overlay);
			 }
		    mapView.invalidate();
		    mapView.getController().setZoom(16);
		    overlayList.clear();
		 } else {
			 Toast.makeText(getApplicationContext(), "Nothing found for the selected item", Toast.LENGTH_SHORT).show();
		 }
		 
	}

	 
	/* put overlay on Map per "Post on Map" button clicked */
	 private void putOverlayOnMap(int overlayType) {
		 
		 int drawableId = 1;
		 String overlayDescription = "";
		 
		 switch(overlayType) {
			 case 1: drawableId = R.drawable.cat_gasstation; overlayDescription = "Gas Station"; break;
			 case 2: drawableId = R.drawable.cat_atm; overlayDescription = "ATM Station"; break;
			 case 3: drawableId = R.drawable.cat_hospital; overlayDescription = "Hospital"; break;
			 case 4: drawableId = R.drawable.cat_restaurant; overlayDescription = "FastFood Restaurant"; break;
			 case 5: drawableId = R.drawable.cat_airport; overlayDescription = "Airport Terminal"; break;
			 case 6: drawableId = R.drawable.cat_waitingshed; overlayDescription = "Vehicle Loading/Unloading Station"; break;
			 case 7: drawableId = R.drawable.rep_accident; overlayDescription = "Road Accident"; break;
			 case 8: drawableId = R.drawable.rep_traffic; overlayDescription = "Heavy Traffic"; break;
			 case 9: drawableId = R.drawable.rep_3; overlayDescription = "Traffic Light"; break;
			 case 10: drawableId = R.drawable.rep_traffic_enforcer; overlayDescription = "Police Blockade"; break;
			 case 11: drawableId = R.drawable.rep_roadclosed; overlayDescription = "Road Blocked"; break;
			 case 12: drawableId = R.drawable.rep_6; overlayDescription = "Slippery Road"; break;
			 default: drawableId = R.drawable.cat_gasstation; overlayDescription = "Gas Station"; break;
		 }
		 
		Drawable markerOverlay = getResources().getDrawable(drawableId);
		final DefaultItemizedOverlay overlay = new DefaultItemizedOverlay(markerOverlay);
		
		OverlayItem overlayItem = new OverlayItem(new GeoPoint(myLocationOverlay.getMyLocation()), "", overlayDescription);    	
		overlay.addItem(overlayItem);
		
		final View categoriesAndReportsPopUpView = getWindow().getLayoutInflater().inflate(R.layout.categories_reports_custom_annotationview_layout, null);
		final PopupWindow categoriesAndReportsPopUpWindow = new PopupWindow(categoriesAndReportsPopUpView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
		
		TextView categoriesAndReportNameTextView = (TextView) categoriesAndReportsPopUpView.findViewById(R.id.categories_reports_custom_annotation_view_nameTextView);
		TextView categoriesAndReportPostedByTextView = (TextView) categoriesAndReportsPopUpView.findViewById(R.id.categories_reports_custom_annotation_view_postedByTextView);
		TextView categoriesAndReportPostedDateTextView = (TextView) categoriesAndReportsPopUpView.findViewById(R.id.categories_reports_custom_annotation_view_postedDateTextView);
		TextView categoriesAndReportDescriptionTextView = (TextView) categoriesAndReportsPopUpView.findViewById(R.id.categories_reports_custom_annotation_view_descriptionTextView);
		
		categoriesAndReportNameTextView.setText(overlayDescription);
		categoriesAndReportPostedByTextView.setText("Posted By: " + tsuperUserProfile.getUserName());
		categoriesAndReportPostedDateTextView.setText("Posting Date: " + new SimpleDateFormat("MM/dd/yyyy hh:mm aa").format(Long.parseLong(String.valueOf(timestamp))));
		categoriesAndReportDescriptionTextView.setText("Description: " + overlayDescription);
		
		overlay.setTapListener(new ItemizedOverlay.OverlayTapListener() {
			@Override
			public void onTap(GeoPoint pt, MapView mapView) {
				// when tapped, show the annotation for the overlayItem
				// int lastTouchedIndex = overlay.getLastFocusedIndex();
				// if(lastTouchedIndex >- 1){
				
					categoriesAndReportsPopUpWindow.setBackgroundDrawable(new BitmapDrawable());
					categoriesAndReportsPopUpWindow.setOutsideTouchable(true);
					categoriesAndReportsPopUpWindow.setFocusable(true);
					categoriesAndReportsPopUpWindow.showAtLocation(mapView, Gravity.TOP, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					
					categoriesAndReportsPopUpWindow.setOnDismissListener(new OnDismissListener() {
						
						@Override
						public void onDismiss() {
							categoriesAndReportsPopUpView.setVisibility(View.GONE);
							categoriesAndReportsPopUpWindow.dismiss();
						}
					});
					
//					OverlayItem tapped = overlay.getItem(lastTouchedIndex);
//					annotationView.showAnnotationView(tapped);
			//	}
			}
		});
		
    	mapView.getOverlays().add(overlay);
    	mapView.invalidate(); 
    	
    	timestamp = (cal.getTimeInMillis() / 1000);
    	
    	// send to Strongloop server
		String[] params = new String[8];
		params[0] = "TravelInfos";
		params[1] = "POST";
		params[2] = Double.toString(myLocationOverlay.getMyLocation().getLatitude());
		params[3] = Double.toString(myLocationOverlay.getMyLocation().getLongitude());
		params[4] = (overlayType < 7) ? "Establishment" : "TrafficReport";
		params[5] = overlayDescription;
		params[6] = overlayDescription;
		params[7] = String.valueOf(timestamp);
		new SendTravelDataOnStrongloop(tsuperUserProfile, getApplicationContext()).execute(params);
    
	 }
	 /* end put overlay on Map */
	 
	 private void showTrafficProfiles(ArrayList<TsuperTrafficProfileModel> listTrafficProfileModels) {
		 
		 /* todo: create connection to strongloop backend
		  * 	: query all traffic profiles available within user radius (10 km)
		  * 	: check if current average speed falls on green, red or yellow then set as drawable marker
		  */ 
		 
		List<DefaultItemizedOverlay> overlayList = new ArrayList<DefaultItemizedOverlay>(); 
		Toast.makeText(getApplicationContext(), "updating map with traffic profiles", Toast.LENGTH_SHORT).show(); 
		
		Bitmap trafficBitmap = null; 
		int averageMovingSpeed = (int) Math.round(Float.parseFloat(tsuperUserProfile.getAverageMovingSpeed()));
		if(averageMovingSpeed <= 0) {
			averageMovingSpeed = (int) Math.round(this.averageSpeed);
		}
		
		for(TsuperTrafficProfileModel dtp : listTrafficProfileModels) {
			
			View trafficProfileView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.traffic_profile_custom_overlay_item, null);
			//ImageView trafficProfileImageView = (ImageView) trafficProfileView.findViewById(R.id.traffic_profile_image_overlay);
			LinearLayout trafficProfileImageView = (LinearLayout) trafficProfileView.findViewById(R.id.traffic_profile_image_overlay);
			
			TextView trafficProfileCurrentText = (TextView) trafficProfileView.findViewById(R.id.traffic_profile_custom_overlay_item_currentspeed);
			TextView trafficProfileAverageText = (TextView) trafficProfileView.findViewById(R.id.traffic_profile_custom_overlay_item_averagespeed);
			
			int ave = (int) Math.round(Float.parseFloat(dtp.getTrafficProfileHistorical())); // value should be from Strongloop
			
			trafficProfileCurrentText.setText(String.format("%02d",averageMovingSpeed));
			trafficProfileAverageText.setText(String.format("%02d",ave));
			
			Drawable trafficProfileMarkerOverlay = null;
			trafficBitmap=null;

			if(averageMovingSpeed >= 0 && averageMovingSpeed < 35 ) {
				// set marker color to red
				//trafficProfileImageView.setImageDrawable(getResources().getDrawable(R.drawable.traffic_profile_red));
				trafficProfileImageView.setBackground(getResources().getDrawable(R.drawable.traffic_profile_red));
				//trafficProfileImageView.setBackground(getResources().getDrawable(R.drawable.traffic_profile_red));
				trafficBitmap = bitmapScalerAndResizer.createDrawableFromView(DriveActivity.this, trafficProfileView);
				trafficProfileMarkerOverlay = new BitmapDrawable(getResources(),trafficBitmap);
				
			} else if (averageMovingSpeed >= 35 && averageMovingSpeed < 49) {
				// set marker color to yellow
				//trafficProfileImageView.setImageDrawable(getResources().getDrawable(R.drawable.traffic_profile_yel));
				trafficProfileImageView.setBackground(getResources().getDrawable(R.drawable.traffic_profile_yel));
				trafficBitmap = bitmapScalerAndResizer.createDrawableFromView(DriveActivity.this, trafficProfileView);
				trafficProfileMarkerOverlay = new BitmapDrawable(getResources(),trafficBitmap);
				
			} else if (averageMovingSpeed >= 50) {
				// set marker color to green
				//trafficProfileImageView.setImageDrawable(getResources().getDrawable(R.drawable.traffic_profile_green));
				trafficProfileImageView.setBackground(getResources().getDrawable(R.drawable.traffic_profile_green));
				trafficBitmap = bitmapScalerAndResizer.createDrawableFromView(DriveActivity.this, trafficProfileView);
				trafficProfileMarkerOverlay = new BitmapDrawable(getResources(),trafficBitmap);
			
			} 
			
			final DefaultItemizedOverlay overlay = new DefaultItemizedOverlay(trafficProfileMarkerOverlay);
			GeoPoint geopoint = new GeoPoint(Double.valueOf(dtp.getLat()), Double.valueOf(dtp.getLon()));
			final String polygonTagName = dtp.getTrafficProfilePolygonTag();
			final OverlayItem overlayItem = new OverlayItem(geopoint, "", "");    	
			overlay.addItem(overlayItem);
			
			overlay.setTapListener(new ItemizedOverlay.OverlayTapListener() {
				@Override
				public void onTap(GeoPoint pt, MapView mapView) {
				    
					//int lastTouchedIndex = overlay.getLastFocusedIndex();
					//if(lastTouchedIndex >- 1){
						final TrafficProfileDialogFragment trafficProfileDialog = new TrafficProfileDialogFragment(polygonTagName);
						trafficProfileDialog.show(getSupportFragmentManager(),"");
					//}
						
				}
			});
				
			overlay.setKey(polygonTagName);
			overlayList.add(overlay);
		}
		
		trafficBitmap.recycle();
		
		// call MapViewLoadTrafficProfileAsync here
		// pass mapview, and the overlay list as parameters
		new MapViewLoadTrafficProfileAsync(mapView, overlayList, getApplicationContext()).execute();
		
	 }
	 
	/* async thread for "Search" functionality */
	 private class ConnectToMapQuestWebService extends AsyncTask<String, Void, String[]>{

		 	protected void onPreExecute() {
		 		
		 		progressDialog.setTitle("");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		 		progressDialog.setCanceledOnTouchOutside(false);
		 		progressDialog.setMessage("Loading... Please wait");
		 		progressDialog.show();
		 	}
		 
		 	@Override
			protected void onPostExecute(String[] result) {
				
		 		if(progressDialog.isShowing()) {
		 			progressDialog.dismiss();
		 			
		 		}
				double lat = Float.parseFloat(result[0]);
				double lng = Float.parseFloat(result[1]);
				mapView.getController().animateTo(new GeoPoint(lat,lng));
				mapView.getController().setZoom(20);
				mapView.setClickable(true);
				mapView.invalidate();
			}
			
			@Override
			protected String[] doInBackground(String... params) {
				
				HttpResponse response = null;
			    String[] ret = new String[2];
			    String responseText = null, lat = "", lng = "";
		        try {
		            HttpClient client = new DefaultHttpClient();
		            HttpGet request = new HttpGet();
		            request.setURI(new URI("http://www.mapquestapi.com/geocoding/v1/address?key=Fmjtd%7Cluur2l6rll%2Cag%3Do5-9012qf&location="+ Uri.encode(params[0])));
		            response = client.execute(request);
		   
		        	if(!response.getEntity().equals(null)) {
		        		 responseText = EntityUtils.toString(response.getEntity());
		                 JSONArray j = new JSONObject(responseText).getJSONArray("results")
		                 				.getJSONObject(0).getJSONArray("locations");
		                 int i = 0;
		                 if(j != null) {
		                 	if(j.length() > i) {
		                     	while(i < j.length()) {
		                     		JSONObject k = (JSONObject) j.get(i);
		                     		JSONObject l = k.getJSONObject("latLng");
		                     		lat = l.getString("lat");
		                     		lng = l.getString("lng");
		                     		Log.e("", "INDEX: " + i + " , LAT: " + lat + " , LNG: " + lng);
		                     		i++;
		                     	}
		                     }
		                 }
		        	}	        	
				    ret[0] = lat;
				    ret[1] = lng;	
		                 
		        } catch (URISyntaxException e) {
		        	Log.e("URISyntaxException", e + "");
		        } catch (ClientProtocolException e) {
		        	Log.e("ClientProtocolException", e + "");
		        } catch (IOException e) {
		        	Log.e("IO Exception", e + "");
		        } catch (JSONException e) {
					Log.e("JSONException", e + "");
				}
		        
		        Log.e("responseText",responseText);
				return ret;
			}	 
	 }
	 /* end async thread */

	 public static void refreshSliderUIData(TsuperUserProfileModel t) {
		 
		 slidingDrawerTotalPointsTextView.setText(String.format("%06d", Integer.parseInt(t.getTotalPoints())));
		 Message.obtain(mHandler, 3, t).sendToTarget();	 
	 }	 
}