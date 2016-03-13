package com.f8mobile.f8mobile;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.f8mobile.community_app.mobile.asynctask.CommunityAppAsyncTaskHttpCall;
import com.f8mobile.community_app.mobile.asynctask.CommunityAppGeocodingAsyncTask;
import com.f8mobile.community_app.mobile.custom.PlaceHolder;
import com.f8mobile.community_app.mobile.custom.PopupDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

public class CommunityFragment extends Fragment {
	
	private GoogleMap map;
	private LatLng currPosLatLng;
	private ProgressDialog progressDialog;
	private Button menuBtn;
	private boolean viewNearbyMode = false, searchMode = false;
	View rootView;
	View floatingMenuButtonLayout;
	ViewGroup viewGroup;
	Bundle bundle = new Bundle();
	private Handler mHandler;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
	   
	   	rootView = inflater.inflate(R.layout.community_fragment, container, false);
	   	
	   	viewGroup = container;
       
	   	map = ((MapFragment) getFragmentManager().findFragmentById(R.id.communityMap)).getMap();	
	   	//System.out.println("MAP: " + map);
		
		progressDialog = new ProgressDialog(getActivity());	
		
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
	    
	    map.setOnMyLocationChangeListener(myLocationChangeListener);
	    
	    if(map.getMyLocation() != null) {
	    	LatLng l = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
	    	map.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 14f));
	    } else {
	    	String lat = PreferenceConnector.readString(getActivity(),
					PreferenceConnector.LOCATION_LAT, null);
	    	String lon = PreferenceConnector.readString(getActivity(),
					PreferenceConnector.LOCATION_LONG, null);
	    	currPosLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
	    	map.animateCamera(CameraUpdateFactory.newLatLngZoom(currPosLatLng, 14f));
	    }
	    
	   floatingMenuButtonLayout = getActivity().getWindow().getLayoutInflater().inflate(R.layout.floating_menu, null);
	   
       int width = getActivity().getWindow().getAttributes().width, height = getActivity().getWindow().getAttributes().height;
       LayoutParams params = new LayoutParams(width, height);
       getActivity().getWindow().addContentView(floatingMenuButtonLayout, params);
       
       mHandler = new Handler() {
           public void handleMessage(Message msg) {
               switch (msg.what) {
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
    							Toast.makeText(getActivity().getApplicationContext(), "No nearby members found", 5000).show();
    						}
    					} catch (JSONException e) {
    						e.printStackTrace();
    						Toast.makeText(getActivity().getApplicationContext(), "No nearby members found", 5000).show();
    					}
               		} else {
               			Toast.makeText(getActivity().getApplicationContext(), "No nearby members found", 5000).show();
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
    							Toast.makeText(getActivity().getApplicationContext(), "No nearby members' establishment found", 5000).show();
    						}
    					} catch (JSONException e) {
    						e.printStackTrace();
    						Toast.makeText(getActivity().getApplicationContext(), "No nearby members' establishment found", 5000).show();
    					}
               		} else {
               			Toast.makeText(getActivity().getApplicationContext(), "No nearby members' establishment found", 5000).show();
               		}
               		break;
               		
               	case 5: 
               		String saveSuccess = (String) msg.obj;
               		
    			    if(saveSuccess.trim().equalsIgnoreCase("true")) {
						Toast.makeText(getActivity().getApplicationContext(), "Saved successfully", 5000).show();
					} else {
						Toast.makeText(getActivity().getApplicationContext(), "Saved failed..", 5000).show();
					}
    			   
               		break;
               }
           }
       };  
               		
       menuBtn = (Button) floatingMenuButtonLayout.findViewById(R.id.menuBtn);
       
       menuBtn.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				
				final View floatingMenuPrompt = getActivity().getWindow().getLayoutInflater().inflate(R.layout.floating_menu_inner_layout, null);
				final PopupWindow floatingMenuPopupWindow = new PopupWindow(floatingMenuPrompt, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
				
				final Button plotOnMapButton = (Button) floatingMenuPrompt.findViewById(R.id.cat_button1);
				plotOnMapButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						floatingMenuPopupWindow.dismiss();
						floatingMenuPrompt.setVisibility(View.GONE);						
						
						searchMode = false;
						viewNearbyMode = false;
						
						final View savePrompt = getActivity().getWindow().getLayoutInflater().inflate(R.layout.plot_current_location_layout, null);
						saveLocation(1, savePrompt, currPosLatLng);
							
					}
				});
				
				final Button searchButton = (Button) floatingMenuPrompt.findViewById(R.id.cat_button2);
				searchButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						floatingMenuPopupWindow.dismiss();
						floatingMenuPrompt.setVisibility(View.GONE);	
						
						final View searchPrompt = getActivity().getWindow().getLayoutInflater().inflate(R.layout.search_location_layout, null);
						final PopupDialog searchPopupDialog = new PopupDialog(getActivity());
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
										LatLng l = new CommunityAppGeocodingAsyncTask(getActivity().getApplicationContext(), progressDialog).execute(new String[] { locationToSearch } ).get();
										if(l != null) {
											map.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15f));	
											searchMode = true;
											viewNearbyMode = false;
										} else {
											Toast.makeText(getActivity().getApplicationContext(), "No results found", 5000).show();
											
										}
										
									} catch (InterruptedException | ExecutionException e) {
										e.printStackTrace();
										Toast.makeText(getActivity().getApplicationContext(), "An error occurred..", 5000).show();
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
					
					new AlertDialog.Builder(getActivity())
			        .setIcon(android.R.drawable.ic_dialog_info)
			        .setTitle("Save Location")
			        .setMessage("Do you want to save this location ?")
			        .setPositiveButton("YES", new DialogInterface.OnClickListener() {

			            @Override
			            public void onClick(DialogInterface dialog, int which) {
			            	final View savePrompt = getActivity().getWindow().getLayoutInflater().inflate(R.layout.plot_current_location_layout, null);
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
						
						Intent tabIntent = new Intent(getActivity().getApplicationContext(), UserStoredDetails.class);
						tabIntent.putExtra("details", arg0.getSnippet());
						tabIntent.putExtra("type", arg0.getTitle());
						startActivity(tabIntent);
						                			
					}
				}
			
				return true;
			}
		});
      
      return rootView;
   }
   
   public void saveLocation(final int methodType, final View savePrompt, final LatLng param) {
		
		final PopupWindow savePopupWindow = new PopupWindow(savePrompt, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
		
		final EditText saveCurrentLocationTitle = (EditText) savePrompt.findViewById(R.id.plotCurrentLocationTitleEditText);
		final EditText saveCurrentLocationDescription = (EditText) savePrompt.findViewById(R.id.plotCurrentLocationDescriptionEditText);
		
		Button saveBtn = (Button) savePrompt.findViewById(R.id.plotCurrentLocationSaveBtn);
		saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(getActivity().getApplicationContext(),"Saving to database.." , Toast.LENGTH_SHORT).show();
				
				// call CommunityAppAsyncTaskHttpCall class here passing longlat as arguments
				// show progress dialog
				
				String[] params = new String[2];
				params[0] = "method=" + methodType;
				params[1] = "POST";
				
				String clientId = PreferenceConnector.readString(getActivity().getApplicationContext(),
						PreferenceConnector.CLIENT_ID, null);
				
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
   
   // checks if there's internet connection on device
	public boolean isInternetAvailable() {
		
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); 
            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
    }	
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        floatingMenuButtonLayout.setVisibility(View.GONE);
    }
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		try {
			 MapFragment f = (MapFragment) getFragmentManager().findFragmentById(R.id.communityMap);
			    if (f != null) {
			    	getFragmentManager().beginTransaction().remove(f).commit();
			    } 
		} catch(Exception e) {
			e.printStackTrace();
		}
	     
	   
	}
}
