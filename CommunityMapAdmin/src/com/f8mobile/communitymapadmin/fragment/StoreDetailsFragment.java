package com.f8mobile.communitymapadmin.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONObject;

import com.community_app_admin.mobile.R;

public class StoreDetailsFragment extends Fragment {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        String data = (String) getArguments().get("data");
        View v = layoutinflater.inflate(R.layout.store_details_layout, viewgroup, false);
        try
        {
            JSONObject j = new JSONObject(data);
            ((TextView) v.findViewById(R.id.textTitleValue)).setText(j.getString("title"));
            ((TextView) v.findViewById(R.id.textDescriptionValue)).setText(j.getString("description"));
            ((TextView) v.findViewById(R.id.textLatitudeValue)).setText(j.getString("latitude"));
            ((TextView) v.findViewById(R.id.textLongitudeValue)).setText(j.getString("longitude"));
        } catch (Exception e) {
            e.printStackTrace();
            return v;
        }
        return v;
    }
}
