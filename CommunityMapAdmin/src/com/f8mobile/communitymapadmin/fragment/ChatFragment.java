package com.f8mobile.communitymapadmin.fragment;

import com.community_app_admin.mobile.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChatFragment extends Fragment {

   public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle) {
        View v = layoutinflater.inflate(R.layout.chat_layout, viewgroup, false);
        TextView tv = (TextView) v.findViewById(R.id.textChat);
        tv.setText(getTag());
        return v;
    }
}
