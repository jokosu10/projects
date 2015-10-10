package com.f8mobile.communitymapadmin.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import com.community_app_admin.mobile.R;

public class MemberDetailsFragment extends ListFragment {

    public void onActivityCreated(Bundle bundle) {
        String s;
        super.onActivityCreated(bundle);
        s = (String)getArguments().get("data");
        String s1;
        String s2;
        String s3;
        String s4;
        String s5;
        String s6;
        String s7;
        String s8;
        String s9;
        String s10;
        String s11;
        String s12;
        String s13;
        String s14;
        String s15;
        String s16;
        String s17;
        String s18;
        String s19;
        String s20;
        String s21;
        String s22;
        String s23;

        try {
            JSONObject obj = new JSONObject(s);
            s = (new StringBuilder("ID:  ")).append(((JSONObject) (obj)).getString("id")).toString();
            s1 = (new StringBuilder("NAME:  ")).append(((JSONObject) (obj)).getString("fname")).append(" ").append(((JSONObject) (obj)).getString("mi")).append(" ").append(((JSONObject) (obj)).getString("lname")).toString();
            s2 = (new StringBuilder("EMAIL:  ")).append(((JSONObject) (obj)).getString("email")).toString();
            s3 = (new StringBuilder("CONTACT:  ")).append(((JSONObject) (obj)).getString("contact")).toString();
            s4 = (new StringBuilder("REGISTRATION DATE:  ")).append(((JSONObject) (obj)).getString("reg_date")).toString();
            s5 = (new StringBuilder("STATUS:  ")).append(((JSONObject) (obj)).getString("status")).toString();
            s6 = (new StringBuilder("ADDRESS:  ")).append(((JSONObject) (obj)).getString("address")).toString();
            s7 = (new StringBuilder("PROVINCE:  ")).append(((JSONObject) (obj)).getString("province")).toString();
            s8 = (new StringBuilder("COUNTRY:  ")).append(((JSONObject) (obj)).getString("country")).toString();
            s9 = (new StringBuilder("COUNTRY CODE:  ")).append(((JSONObject) (obj)).getString("country_code")).toString();
            s10 = (new StringBuilder("BIRTHDAY:  ")).append(((JSONObject) (obj)).getString("birthdate")).toString();
            s11 = (new StringBuilder("GENDER:  ")).append(((JSONObject) (obj)).getString("sex")).toString();
            s12 = (new StringBuilder("HEIGHT:  ")).append(((JSONObject) (obj)).getString("height")).toString();
            s13 = (new StringBuilder("WEIGHT:  ")).append(((JSONObject) (obj)).getString("weight")).toString();
            s14 = (new StringBuilder("CIVIL STATUS:  ")).append(((JSONObject) (obj)).getString("civil_status")).toString();
            s15 = (new StringBuilder("CHILDREN:  ")).append(((JSONObject) (obj)).getString("children")).toString();
            s16 = (new StringBuilder("SPOUSE:  ")).append(((JSONObject) (obj)).getString("spouse")).toString();
            s17 = (new StringBuilder("RELIGION:  ")).append(((JSONObject) (obj)).getString("religion")).toString();
            s18 = (new StringBuilder("NATIONALITY:  ")).append(((JSONObject) (obj)).getString("nationality")).toString();
            s19 = (new StringBuilder("ARC No.:  ")).append(((JSONObject) (obj)).getString("arc_no")).toString();
            s20 = (new StringBuilder("LANDLINE:  ")).append(((JSONObject) (obj)).getString("landline")).toString();
            s21 = (new StringBuilder("CELL No.:  ")).append(((JSONObject) (obj)).getString("cell_no")).toString();
            s22 = (new StringBuilder("TIN:  ")).append(((JSONObject) (obj)).getString("tin")).toString();
			s23 = (new StringBuilder("REFERRED BY:   ")).append(((JSONObject) (obj)).getString("referred_by")).toString();
			String[] bundled = (new String[] {
		            s, s1, s2, s3, s4, s5, s6, s7, s8, s9, 
		            s10, s11, s12, s13, s14, s15, s16, s17, s18, s19, 
		            s20, s21, s22, s23
		        });
	        setListAdapter(new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, bundled)); 
	        
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
}
