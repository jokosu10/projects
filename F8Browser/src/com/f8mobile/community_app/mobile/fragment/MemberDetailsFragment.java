package com.f8mobile.community_app.mobile.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import com.f8mobile.f8mobile.R;

import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MemberDetailsFragment extends Fragment {

	  private ListView listView;
	
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	      
		    View view = inflater.inflate(R.layout.member_details_layout, container, false);
	      
	        Bundle b = getArguments();
			String[] values = null;
			try {
				String s = (String) b.get("data");
				Log.e("","s: " + s);
				if(!s.equalsIgnoreCase("null") && !TextUtils.isEmpty(s)) {
					JSONObject j = new JSONObject(s);
				    values = new String[] { 
				//	"ID:  " + j.getString("id"),
				    "NAME:  " + j.getString("fname") + " " + j.getString("lname"),
				//	"EMAIL:  " + j.getString("email"),
				//	"CONTACT:  " + j.getString("contact"),
				//	"REGISTRATION DATE:  " + j.getString("reg_date"),
				//	"STATUS:  " + j.getString("status"),
				//	"ADDRESS:  " + j.getString("address"),
				//	"PROVINCE:  " + j.getString("province"),
				//	"COUNTRY:  " + j.getString("country"),
				//	"COUNTRY CODE:  " + j.getString("country_code"),
				//	"BIRTHDAY:  " + j.getString("birthdate"),
				//	"GENDER:  " + j.getString("sex"),
				//	"HEIGHT:  " + j.getString("height"),
				//	"WEIGHT:  " + j.getString("weight"),
				//	"CIVIL STATUS:  " + j.getString("civil_status"),
				//	"CHILDREN:  " + j.getString("children"),
				//	"SPOUSE:  " + j.getString("spouse"),
				//	"RELIGION:  " + j.getString("religion"),
				//	"NATIONALITY:  " + j.getString("nationality"),
				//	"ARC No.:  " + j.getString("arc_no"),
				//	"LANDLINE:  " + j.getString("landline"),
				//	"CELL No.:  " + j.getString("cell_no")
				//	"TIN:  " + j.getString("tin"),
				//	"REFERRED BY:   " + j.getString("referred_by")
				    };
				} else {
					values = new String[] {
							"NAME: Not available"
						}; 
				}

			} catch (JSONException e) {
				e.printStackTrace();
				values = new String[] {
						"NAME: Not available"
					}; 
				}
				
			  createListView(view, values);
			  // Inflate the layout for this fragment
	      return view;
	  }

	  private void createListView(View view, String[] values) {
	      listView = (ListView) view.findViewById(R.id.memberDetailsListView);
	      //Set option as Multiple Choice. So that user can able to select more the one option from list
	      listView.setAdapter(new ArrayAdapter<String>(getActivity(),
	      android.R.layout.simple_list_item_1, values));
	      listView.setDivider(null);
	  }	  
	  
	  
	  
}
