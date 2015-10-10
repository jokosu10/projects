package com.f8mobile.community_app.mobile.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import com.f8mobile.f8mobile.R;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StoreDetailsFragment extends Fragment {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	
    	Bundle b = getArguments();
    	String s = (String) b.get("data");
    	View v = inflater.inflate(R.layout.store_details_layout, container, false);
    		
    	try {
			
			JSONObject j = new JSONObject(s);
			TextView tv = (TextView) v.findViewById(R.id.textTitleValue);
		    tv.setText(j.getString("title"));
			
		    TextView tv2 = (TextView) v.findViewById(R.id.textDescriptionValue);
		    tv2.setText(j.getString("description"));
		    
		    TextView tv3 = (TextView) v.findViewById(R.id.textLatitudeValue);
		    tv3.setText(j.getString("latitude"));
		    
		    TextView tv4 = (TextView) v.findViewById(R.id.textLongitudeValue);
		    tv4.setText(j.getString("longitude"));
		    
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	return v;
    }
}
