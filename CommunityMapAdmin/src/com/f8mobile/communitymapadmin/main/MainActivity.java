package com.f8mobile.communitymapadmin.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

import com.community_app_admin.mobile.R;
import com.f8mobile.communitymapadmin.asynctask.CommunityAppAsyncTaskHttpCall;
import com.f8mobile.communitymapadmin.asynctask.CommunityAppGeocodingAsyncTask;
import com.f8mobile.communitymapadmin.custom_views.PlaceHolder;
import com.f8mobile.communitymapadmin.custom_views.PopupDialog;
import com.f8mobile.communitymapadmin.model.Users;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    private ArrayList countries;
    private LatLng currPosLatLng;
    private boolean currentCoordinatesBeenCaught;
    private String currentUserId;
    private GoogleMap map;
    private Button menuBtn;
    private TextView onlineMembersTextViewCount;
    private ProgressDialog progressDialog;
    private Button searchByCountryBtn;
    private boolean searchMode;
    private ArrayList userModelList;
    private boolean viewNearbyMode;
    private Handler mHandler;

    public MainActivity()
    {
        currentUserId = "admin";
        viewNearbyMode = false;
        searchMode = false;
        currentCoordinatesBeenCaught = false;
        countries = new ArrayList();
    }

    public String generateRandom() {
        double d = Math.random();
        Log.e("", (new StringBuilder("generateRandom() => ")).append(d).toString());
        return String.valueOf(d);
    }

    public boolean isInternetAvailable() {
        boolean flag;
        try {
            flag = InetAddress.getByName("google.com").equals("");
        } catch (Exception exception) {
            return false;
        }
        return !flag;
    }

    protected void onCreate(Bundle bundle) {
    	
        super.onCreate(bundle);
        getWindow().setFlags(1024, 1024);
        requestWindowFeature(1);
        setContentView(R.layout.activity_main);
        
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();	
        
        progressDialog = new ProgressDialog(getApplicationContext());
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setCompassEnabled(false);

		map.setMyLocationEnabled(true);
	    map.getUiSettings().setZoomControlsEnabled(false);
	    map.getUiSettings().setCompassEnabled(false);
	     
	    GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
	        @Override
	        public void onMyLocationChange (Location location) {
	           LatLng loc = new LatLng (location.getLatitude(), location.getLongitude());
	           currPosLatLng = loc;
	        }
	    };
        
        new CommunityAppAsyncTaskHttpCall(progressDialog, mHandler, 1).execute(new String[] { "method=6", "GET" });
        
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                	case 1: 
                		String countryList = (String) msg.obj;
                		if(countryList.trim().isEmpty()) {
                			Toast.makeText(getApplicationContext(), "Failed to get country list", Toast.LENGTH_SHORT).show();
                		} else {
                			
                		}
                		break;
                	case 3: 
                		String jsonResponse = (String) msg.obj;
                		if(jsonResponse != null && !TextUtils.isEmpty(jsonResponse)) {
     					try {
     						JSONArray jArray = new JSONArray(jsonResponse);
     						if(jArray != null && jArray.length() > 0) {
     							
     							viewNearbyMode = true;
     							searchMode = false;
     							
     							map.animateCamera(CameraUpdateFactory.newLatLngZoom(currPosLatLng, 13f));
     							
     							for(int i = 0; i < jArray.length(); i++) {
     								JSONObject j = jArray.getJSONObject(i);
     								
     								LatLng locations = new LatLng(Double.parseDouble(j.getString("latitude")), Double.parseDouble(j.getString("longitude")));
     								 map.addMarker(new MarkerOptions().title("nearbyMembers")
     						           .position(locations).snippet(j.toString()));			 
     							}
     						} else {
     							Toast.makeText(getApplicationContext(), "No nearby members found", 5000).show();
     						}
     					} catch (JSONException e) {
     						e.printStackTrace();
     						Toast.makeText(getApplicationContext(), "No nearby members found", 5000).show();
     					}
                		} else {
                			Toast.makeText(getApplicationContext(), "No nearby members found", 5000).show();
                		}
                		break;
                		
                	case 4: 
                		String jsonResponse2 = (String) msg.obj;
                		if(jsonResponse2 != null && !TextUtils.isEmpty(jsonResponse2)) {
     					try {
     						JSONArray jArray = new JSONArray(jsonResponse2);
     						if(jArray != null && jArray.length() > 0) {
     							
     							viewNearbyMode = true;
     							searchMode = false;
     							
     							map.animateCamera(CameraUpdateFactory.newLatLngZoom(currPosLatLng, 13f));
     							
     							for(int i = 0; i < jArray.length(); i++) {
     								JSONObject j = jArray.getJSONObject(i);
     								
     								LatLng locations = new LatLng(Double.parseDouble(j.getString("latitude")), Double.parseDouble(j.getString("longitude")));
     								 map.addMarker(new MarkerOptions().title("nearbyEstablishments")
     						           .position(locations).snippet(j.toString()));	 
     							}
     						} else {
     							Toast.makeText(getApplicationContext(), "No nearby members' establishment found", 5000).show();
     						}
     					} catch (JSONException e) {
     						e.printStackTrace();
     						Toast.makeText(getApplicationContext(), "No nearby members' establishment found", 5000).show();
     					}
                		} else {
                			Toast.makeText(getApplicationContext(), "No nearby members' establishment found", 5000).show();
                		}
                		break;
                		
                	case 5: 
                		String saveSuccess = (String) msg.obj;
                		
     			    if(saveSuccess.trim().equalsIgnoreCase("true")) {
 						Toast.makeText(getApplicationContext(), "Saved successfully", 5000).show();
 					} else {
 						Toast.makeText(getApplicationContext(), "Saved failed..", 5000).show();
 					}
     			   
                		break;
                }
            }
        };          
        
        menuBtn = (Button) findViewById(R.id.menuBtn);
        
        menuBtn.setOnClickListener(new OnClickListener() {
 			
 			@SuppressWarnings("deprecation")
 			@Override
 			public void onClick(View v) {
 				
 				final View floatingMenuPrompt = getWindow().getLayoutInflater().inflate(R.layout.floating_menu_inner_layout, null);
 				final PopupWindow floatingMenuPopupWindow = new PopupWindow(floatingMenuPrompt, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
 				
 				final Button plotOnMapButton = (Button) floatingMenuPrompt.findViewById(R.id.cat_button1);
 				plotOnMapButton.setOnClickListener(new OnClickListener() {
 					@Override
 					public void onClick(View v) {
 						
 						floatingMenuPopupWindow.dismiss();
 						floatingMenuPrompt.setVisibility(View.GONE);						
 						
 						searchMode = false;
 						viewNearbyMode = false;
 						
 						final View savePrompt = getWindow().getLayoutInflater().inflate(R.layout.plot_current_location_layout, null);
 						saveLocation(1, savePrompt, currPosLatLng);
 							
 					}
 				});
 				
 				final Button searchButton = (Button) floatingMenuPrompt.findViewById(R.id.cat_button2);
 				searchButton.setOnClickListener(new OnClickListener() {
 					@Override
 					public void onClick(View v) {
 						
 						floatingMenuPopupWindow.dismiss();
 						floatingMenuPrompt.setVisibility(View.GONE);	
 						
 						final View searchPrompt = getWindow().getLayoutInflater().inflate(R.layout.search_location_layout, null);
 						final PopupDialog searchPopupDialog = new PopupDialog(getApplicationContext());
 						searchPopupDialog.setContentView(searchPrompt);
 						
 						final AutoCompleteTextView searchAutoComplete = (AutoCompleteTextView) searchPrompt.findViewById(R.id.autoCompleteTextViewLocation);
 						searchAutoComplete.setAdapter(new PlaceHolder(searchPrompt.getContext(), R.layout.list_location_auto_complete, 1));
 						
 						searchAutoComplete.setOnItemClickListener(new OnItemClickListener() {

 							@Override
 							public void onItemClick(AdapterView<?> parent,
 									View view, int position, long id) {
 								
 								searchAutoComplete.setText((String) parent.getItemAtPosition(position));
 							}
 						});
 						
 						Button triggerSearchBtn = (Button) searchPrompt.findViewById(R.id.searchBtn);
 						triggerSearchBtn.setOnClickListener(new OnClickListener() {
 							
 							@Override
 							public void onClick(View v) {
 								
 								String locationToSearch = searchAutoComplete.getText().toString();
 								
 								if(!locationToSearch.trim().isEmpty()) {
 									
 									searchAutoComplete.setText("");
 									map.clear();
 									try {
 										LatLng l = new CommunityAppGeocodingAsyncTask(getApplicationContext(), progressDialog).execute(new String[] { locationToSearch } ).get();
 										if(l != null) {
 											map.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15f));	
 											searchMode = true;
 											viewNearbyMode = false;
 										} else {
 											Toast.makeText(getApplicationContext(), "No results found", 5000).show();
 											
 										}
 										
 									} catch (InterruptedException | ExecutionException e) {
 										e.printStackTrace();
 										Toast.makeText(getApplicationContext(), "An error occurred..", 5000).show();
 									}
 								}
 								
 								searchPopupDialog.dismiss();
 								searchPrompt.setVisibility(View.GONE);
 							}
 						});
 						
 						Button clearSearchBtn = (Button) searchPrompt.findViewById(R.id.clearBtn);
 						clearSearchBtn.setOnClickListener(new OnClickListener() {
 							
 							@Override
 							public void onClick(View v) {
 								searchAutoComplete.setText("");
 							}
 						});
 						
 						searchPopupDialog.show();
 					}
 				});
 				
 				final Button showNearbyMembersButton = (Button) floatingMenuPrompt.findViewById(R.id.cat_button3);
 				showNearbyMembersButton.setOnClickListener(new OnClickListener() {
 					
 					@Override
 					public void onClick(View v) {
 						
 						map.clear();
 						String[] params = new String[2];
 						
 						params[0] = "method=3&latitude=" + currPosLatLng.latitude + "&longitude=" + currPosLatLng.longitude;
 						params[1] = "GET";
 						
 					    new CommunityAppAsyncTaskHttpCall(progressDialog, mHandler, 3).execute(params);							
 						
 						floatingMenuPopupWindow.dismiss();
 						floatingMenuPrompt.setVisibility(View.GONE);
 						
 					}
 				});
 				
 				
 				final Button showNearbyEstablishmentsButton = (Button) floatingMenuPrompt.findViewById(R.id.cat_button4);
 				showNearbyEstablishmentsButton.setOnClickListener(new OnClickListener() {
 					
 					@Override
 					public void onClick(View v) {
 						
 						// call CommunityAppAsyncTaskHttpCall class here passing curr longlat as arguments
 						// show progress dialog
 						// clear the map first
 						map.clear();
 						
 						String[] params = new String[2];
 						
 						params[0] = "method=2&latitude=" + currPosLatLng.latitude + "&longitude=" + currPosLatLng.longitude;
 						params[1] = "GET";
 						
 					    new CommunityAppAsyncTaskHttpCall(progressDialog, mHandler, 4).execute(params);
 						
 						floatingMenuPopupWindow.dismiss();
 						floatingMenuPrompt.setVisibility(View.GONE);
 					}
 				});
 				
 				
 				floatingMenuPopupWindow.setBackgroundDrawable(new BitmapDrawable());
 				floatingMenuPopupWindow.setOutsideTouchable(true);
 				floatingMenuPopupWindow.setFocusable(true);
 				floatingMenuPopupWindow.showAsDropDown(menuBtn, 85, -60);
 				
 				floatingMenuPopupWindow.setOnDismissListener(new OnDismissListener() {
 					
 					@Override
 					public void onDismiss() {
 						floatingMenuPopupWindow.dismiss();
 						floatingMenuPrompt.setVisibility(View.GONE);
 						
 					}
 				});
 				
 			}
 		});
        
        
        
        onlineMembersTextViewCount = (TextView) findViewById(0x7f050008);
        searchByCountryBtn = (Button) findViewById(0x7f05000a);
        searchByCountryBtn.setOnClickListener(new android.view.View.OnClickListener() {

        public void onClick(View view) {
        	
                view = getWindow().getLayoutInflater().inflate(0x7f030007, null);
                PopupDialog popupdialog = new PopupDialog(MainActivity.this);
                popupdialog.setContentView(view);
                final AutoCompleteTextView searchCountryAutoComplete = (AutoCompleteTextView)popupdialog.findViewById(0x7f05001f);
                searchCountryAutoComplete.setAdapter(new PlaceHolder(view.getContext(), 0x7f030004, 2, countries));
                searchCountryAutoComplete.setOnItemClickListener(popupdialog. new android.widget.AdapterView.OnItemClickListener() {

                    final _cls3 this$1;
                    private final AutoCompleteTextView val$searchCountryAutoComplete;
                    private final PopupDialog val$searchPopupDialog;

                    public void onItemClick(AdapterView adapterview, View view, int i, long l)
                    {
                        ArrayList arraylist;
                        searchCountryAutoComplete.setText((String)adapterview.getItemAtPosition(i));
                        adapterview = (new StringBuilder("method=7&country=")).append(Uri.encode((String)adapterview.getItemAtPosition(i))).toString();
                        map.clear();
                        userModelList = new ArrayList();
                        arraylist = new ArrayList();
                        adapterview = (String)(new CommunityAppAsyncTaskHttpCall(progressDialog)).execute(new String[] {
                            adapterview, "GET"
                        }).get();
                        if (adapterview.equalsIgnoreCase("null") || adapterview.trim().isEmpty())
                        {
                            Toast.makeText(getApplicationContext(), "No members found", 5000).show();
                            searchCountryAutoComplete.setText("");
                            searchPopupDialog.dismiss();
                            onlineMembersTextViewCount.setText("     0");
                            return;
                        }
                        JSONArray jsonarray = new JSONArray(adapterview);
                        
                        viewNearbyMode = true;
                        searchMode = false;
                        i = 0;
                        if (i >= jsonarray.length())
                        {
                            if (userModelList.size() > 0)
                            {
                                double d = Double.parseDouble(((UserModel)userModelList.get(userModelList.size() - 1)).getLatitude());
                                double d1 = Double.parseDouble(((UserModel)userModelList.get(userModelList.size() - 1)).getLongitude());
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(d, d1), 5F));
                            }
                            onlineMembersTextViewCount.setText((new StringBuilder("     ")).append(userModelList.size()).toString());
                            searchCountryAutoComplete.setText("");
                            searchPopupDialog.dismiss();
                            return;
                        }
       
                        adapterview.printStackTrace();
                        searchCountryAutoComplete.setText("");
                        searchPopupDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "An error occurred..", 5000).show();
                        return;
                        
                        String s;
                        JSONObject jsonobject;
                        jsonobject = jsonarray.getJSONObject(i);
                        adapterview = jsonobject.getString("latitude");
                        s = jsonobject.getString("longitude");
                        if (adapterview.equalsIgnoreCase("null") || s.equalsIgnoreCase("null") || adapterview == null || s == null)
                        {
                            break; /* Loop/switch isn't completed */
                        }
                        if (adapterview.trim().isEmpty() || s.trim().isEmpty())
                        {
                            break; /* Loop/switch isn't completed */
                        }
                        Object obj;
                        obj = adapterview;
                        view = s;

                        obj = arraylist.iterator();
                        view = s;

                        obj = adapterview;
                        adapterview = new LatLng(Double.parseDouble(((String) (obj))), Double.parseDouble(view));
                        map.addMarker((new MarkerOptions()).title("nearbyMembers").position(adapterview).snippet(jsonobject.toString()));
                        adapterview = new UserModel();
                        adapterview.setUserId(jsonobject.getString("id"));
                        adapterview.setLatitude(((String) (obj)));
                        adapterview.setLongitude(view);
                        userModelList.add(adapterview);
                        arraylist.add((new StringBuilder(String.valueOf(obj))).append(",").append(view).toString());
                        Log.e("", (new StringBuilder("user: ")).append(adapterview.getUserId()).append(", coordinates: ").append(adapterview.getLatitude()).append(" , ").append(adapterview.getLongitude()).toString());
                        break; /* Loop/switch isn't completed */
_L8:
                        if (((String)((Iterator) (obj)).next()).equalsIgnoreCase((new StringBuilder(String.valueOf(adapterview))).append(",").append(view).toString()))
                        {
                            adapterview = Double.toString(Double.parseDouble(adapterview) + Double.parseDouble(generateRandom()));
                            view = Double.toString(Double.parseDouble(view) + Double.parseDouble(generateRandom()));
                        }
                        
                        try
                        {
                            Toast.makeText(getApplicationContext(), "No members found", 5000).show();
                            searchCountryAutoComplete.setText("");
                            searchPopupDialog.dismiss();
                            onlineMembersTextViewCount.setText("     0");
                            return;
                        }
                        // Misplaced declaration of an exception variable
                        catch (AdapterView adapterview) { }
                        // Misplaced declaration of an exception variable
                        catch (AdapterView adapterview) { }
                        // Misplaced declaration of an exception variable
                        catch (AdapterView adapterview) { }
                    }

            
            {
                this$1 = final__pcls3;
                searchCountryAutoComplete = autocompletetextview;
                searchPopupDialog = PopupDialog.this;
                super();
            }
                });
                
                
                searchCountryAutoComplete.setOnKeyListener(popupdialog. new android.view.View.OnKeyListener() {

                    final _cls3 this$1;
                    private final AutoCompleteTextView val$searchCountryAutoComplete;
                    private final PopupDialog val$searchPopupDialog;

                    public boolean onKey(View view, int i, KeyEvent keyevent)
                    {
                        if (keyevent.getAction() != 0 || i != 66 || searchCountryAutoComplete.getText().toString().trim().isEmpty()) goto _L2; else goto _L1
_L1:
                        ArrayList arraylist;
                        view = (new StringBuilder("method=7&country=")).append(Uri.encode(searchCountryAutoComplete.getText().toString())).toString();
                        map.clear();
                        userModelList = new ArrayList();
                        arraylist = new ArrayList();
                        view = (String)(new CommunityAppAsyncTaskHttpCall(progressDialog)).execute(new String[] {
                            view, "GET"
                        }).get();
                        if (!view.equalsIgnoreCase("null") && !view.trim().isEmpty()) goto _L4; else goto _L3
_L3:
                        Toast.makeText(getApplicationContext(), "No members found", 5000).show();
                        searchCountryAutoComplete.setText("");
                        searchPopupDialog.dismiss();
                        onlineMembersTextViewCount.setText("     0");
                          goto _L5
_L4:
                        JSONArray jsonarray = new JSONArray(view);
                        if (jsonarray == null) goto _L7; else goto _L6
_L6:
                        if (jsonarray.length() <= 0) goto _L7; else goto _L8
_L8:
                        viewNearbyMode = true;
                        searchMode = false;
                        i = 0;
_L22:
                        if (i < jsonarray.length()) goto _L10; else goto _L9
_L9:
                        if (userModelList.size() > 0)
                        {
                            double d = Double.parseDouble(((UserModel)userModelList.get(userModelList.size() - 1)).getLatitude());
                            double d1 = Double.parseDouble(((UserModel)userModelList.get(userModelList.size() - 1)).getLongitude());
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(d, d1), 5F));
                        }
                        onlineMembersTextViewCount.setText((new StringBuilder("     ")).append(userModelList.size()).toString());
                        searchCountryAutoComplete.setText("");
                        searchPopupDialog.dismiss();

                        view.printStackTrace();
                        searchCountryAutoComplete.setText("");
                        searchPopupDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "An error occurred..", 5000).show();

                        String s;
                        JSONObject jsonobject;
                        jsonobject = jsonarray.getJSONObject(i);
                        view = jsonobject.getString("latitude");
                        s = jsonobject.getString("longitude");
                       
                        Object obj;
                        obj = view;
                        keyevent = s;
      
                        obj = arraylist.iterator();
                        keyevent = s;
                       
                        obj = view;
                        view = new LatLng(Double.parseDouble(((String) (obj))), Double.parseDouble(keyevent));
                        map.addMarker((new MarkerOptions()).title("nearbyMembers").position(view).snippet(jsonobject.toString()));
                        view = new UserModel();
                        view.setUserId(jsonobject.getString("id"));
                        view.setLatitude(((String) (obj)));
                        view.setLongitude(keyevent);
                        userModelList.add(view);
                        arraylist.add((new StringBuilder(String.valueOf(obj))).append(",").append(keyevent).toString());
                        Log.e("", (new StringBuilder("user: ")).append(view.getUserId()).append(", coordinates: ").append(view.getLatitude()).append(" , ").append(view.getLongitude()).toString());
        
                        if (!((String)((Iterator) (obj)).next()).equalsIgnoreCase((new StringBuilder(String.valueOf(view))).append(",").append(keyevent).toString())) goto _L19; else goto _L18

                        view = Double.toString(Double.parseDouble(view) + Double.parseDouble(generateRandom()));
                        keyevent = Double.toString(Double.parseDouble(keyevent) + Double.parseDouble(generateRandom()));
                        Toast.makeText(getApplicationContext(), "No members found", 5000).show();
                        searchCountryAutoComplete.setText("");
                        searchPopupDialog.dismiss();
                        onlineMembersTextViewCount.setText("     0");
                        
                        return false;
                       
                        return true;

                        i++;
                       
                    }

            
            {
                this$1 = final__pcls3;
                searchCountryAutoComplete = autocompletetextview;
                searchPopupDialog = PopupDialog.this;
                super();
            }
                });
                popupdialog.showAtLocation(5, 100);
            }


            
            {
                this$0 = MainActivity.this;
                super();
            }
        });

        
        map.setOnMapClickListener(new OnMapClickListener() {
			
 			@Override
 			public void onMapClick(LatLng arg0) {
 				
 				if(arg0.latitude != currPosLatLng.latitude && arg0.longitude != currPosLatLng.longitude) {
 					
 					if(searchMode) { // only allow addition of markers on map if "SEARCH LOCATION" button was clicked
 						map.addMarker(new MarkerOptions().position(arg0));
 					} 
 				} 
 				
 			}
 		});
        
        map.setOnMarkerClickListener(new OnMarkerClickListener() {
			
 			@Override
 			public boolean onMarkerClick(final Marker arg0) {
 				
 				if(searchMode) { // only allow saving of marker's location on map if "SEARCH LOCATION" button was clicked
 					
 					new AlertDialog.Builder(getApplicationContext())
 			        .setIcon(android.R.drawable.ic_dialog_info)
 			        .setTitle("Save Location")
 			        .setMessage("Do you want to save this location ?")
 			        .setPositiveButton("YES", new DialogInterface.OnClickListener() {

 			            @Override
 			            public void onClick(DialogInterface dialog, int which) {
 			            	final View savePrompt = getWindow().getLayoutInflater().inflate(R.layout.plot_current_location_layout, null);
 			            	saveLocation(4, savePrompt, arg0.getPosition());
 			            }
 			        })
 			        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
 						
 						@Override
 						public void onClick(DialogInterface dialog, int which) {
 							arg0.remove();
 						}
 					})
 			        .show();
 				} else {
 					
 					if(viewNearbyMode) {
 						// should show details of establishment
 						// call UserStoreDetailsActivity to show chat and store front
 						
 						System.out.println("details, " +  arg0.getSnippet());
 						System.out.println("type, " + arg0.getTitle());
 						
 						Intent tabIntent = new Intent(getApplicationContext(), Users.class);
 						tabIntent.putExtra("details", arg0.getSnippet());
 						tabIntent.putExtra("type", arg0.getTitle());
 						startActivity(tabIntent);
 						                			
 					}
 				}
 			
 				return true;
 			}
 		});
        
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressDialog.dismiss();
    }

    
    public void saveLocation(final int methodType, final View savePrompt, final LatLng param) {
		
 		final PopupWindow savePopupWindow = new PopupWindow(savePrompt, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
 		
 		final EditText saveCurrentLocationTitle = (EditText) savePrompt.findViewById(R.id.plotCurrentLocationTitleEditText);
 		final EditText saveCurrentLocationDescription = (EditText) savePrompt.findViewById(R.id.plotCurrentLocationDescriptionEditText);
 		
 		Button saveBtn = (Button) savePrompt.findViewById(R.id.plotCurrentLocationSaveBtn);
 		saveBtn.setOnClickListener(new OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 				
 				Toast.makeText(getApplicationContext(),"Saving to database.." , Toast.LENGTH_SHORT).show();
 				
 				// call CommunityAppAsyncTaskHttpCall class here passing longlat as arguments
 				// show progress dialog
 				
 				String[] params = new String[2];
 				params[0] = "method=" + methodType;
 				params[1] = "POST";
 				
 				String clientId = "1";
 				
 				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
 			    nameValuePairs.add(new BasicNameValuePair("user_id", clientId));
 			    nameValuePairs.add(new BasicNameValuePair("title", saveCurrentLocationTitle.getText().toString()));
 			    nameValuePairs.add(new BasicNameValuePair("description", saveCurrentLocationDescription.getText().toString()));
 			    nameValuePairs.add(new BasicNameValuePair("latitude", "" + param.latitude));
 			    nameValuePairs.add(new BasicNameValuePair("longitude", "" + param.longitude));
 				
 				new CommunityAppAsyncTaskHttpCall(progressDialog, nameValuePairs, mHandler, 5).execute(params);
 					
 			    savePopupWindow.dismiss();
 			    savePrompt.setVisibility(View.GONE);
 			}
 		});
 		
 		
 		Button cancelSaveBtn = (Button) savePrompt.findViewById(R.id.plotCurrentLocationCancelBtn);
 		cancelSaveBtn.setOnClickListener(new OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 				
 				savePopupWindow.dismiss();
 				savePrompt.setVisibility(View.GONE);
 				
 			}
 		});
 		  
 		savePopupWindow.setOutsideTouchable(false);
 		savePopupWindow.setFocusable(true);
 		savePopupWindow.showAtLocation(savePrompt, Gravity.CENTER, 0, 0);
 	}
}
