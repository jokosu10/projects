package com.smsgt.mobile.application.supertsuper_v3.custom_ui;

import java.util.ArrayList;
import java.util.List;

import com.smsgt.mobile.application.supertsuper_v3.custom_interfaces.RewardsInterface;
import com.smsgt.mobile.application.supertsuper_v3.custom_interfaces.impl.RewardsRow;
import com.smsgt.mobile.application.supertsuper_v3.database.model.RewardsModel;
import com.smsgt.mobile.application.supertsuper_v3.database.model.TsuperUserProfileModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class RewardsAdapter extends BaseAdapter {
	 
	final List<RewardsInterface> rewardsRowInterface;

    public RewardsAdapter(List<RewardsModel> rewards, Context ctx, TsuperUserProfileModel t, String caller) {
    	
        rewardsRowInterface = new ArrayList<RewardsInterface>(); //member variable
        for (RewardsModel reward : rewards) {
            if (reward.getRewardsImageId() != null) {
                rewardsRowInterface.add(new RewardsRow(reward, LayoutInflater.from(ctx), t, caller, ctx));
            } 
        }
    }

    @Override
    public int getItemViewType(int position) {
        return rewardsRowInterface.get(position).getViewType();
    }

    @Override
    public int getCount() {
        return rewardsRowInterface.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return rewardsRowInterface.get(position).getView(convertView, position);
    }
}
