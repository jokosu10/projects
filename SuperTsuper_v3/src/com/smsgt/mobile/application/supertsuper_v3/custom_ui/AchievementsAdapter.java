package com.smsgt.mobile.application.supertsuper_v3.custom_ui;

import java.util.ArrayList;
import java.util.List;

import com.smsgt.mobile.application.supertsuper_v3.custom_interfaces.AchievementsInterface;
import com.smsgt.mobile.application.supertsuper_v3.custom_interfaces.impl.AchievementsRow;
import com.smsgt.mobile.application.supertsuper_v3.database.model.AchievementsModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class AchievementsAdapter extends BaseAdapter {
	
	final List<AchievementsInterface> achievementsRowInterface;

    public AchievementsAdapter(List<AchievementsModel> achievements, Context ctx) {
    	
        achievementsRowInterface = new ArrayList<AchievementsInterface>(); //member variable
        for (AchievementsModel achievement : achievements) {
            achievementsRowInterface.add(new AchievementsRow(achievement, LayoutInflater.from(ctx), ctx));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return achievementsRowInterface.get(position).getViewType();
    }

    @Override
    public int getCount() {
        return achievementsRowInterface.size();
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
        return achievementsRowInterface.get(position).getView(convertView, position);
    }
 
}
