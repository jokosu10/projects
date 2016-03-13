package com.smsgt.mobile.application.supertsuper_v3.custom_interfaces.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.smsgt.mobile.application.supertsuper_v3.R;
import com.smsgt.mobile.application.supertsuper_v3.custom_interfaces.AchievementsInterface;
import com.smsgt.mobile.application.supertsuper_v3.database.model.AchievementsModel;

public class AchievementsRow implements AchievementsInterface {
	
	private final AchievementsModel achievementsModel;
	private LayoutInflater achievementsLayoutInflater;
	private Context contxt;
	
	public AchievementsRow(AchievementsModel achievementsModel, LayoutInflater layoutInflater, Context c) {
		this.achievementsModel = achievementsModel;
		this.achievementsLayoutInflater = layoutInflater;
		this.contxt = c;
	}
	
	@Override
	public View getView(View convertView, final int position) {
		
		final ViewHolder holder;
        View view;
        //we don't have a converView so we'll have to create a new one using ViewHolder pattern
        if (convertView == null) {
        	
            ViewGroup viewGroup = (ViewGroup) achievementsLayoutInflater.inflate(com.smsgt.mobile.application.supertsuper_v3.R.layout.achievements_rows, null);
            ImageView achievementsIcon = (ImageView) viewGroup.findViewById(R.id.achievementsRowImageView);
            TextView achievementsName = (TextView) viewGroup.findViewById(R.id.rowTextView2);
            TextView achievementsDescription = (TextView) viewGroup.findViewById(R.id.rowTextView3);
            
            holder = new ViewHolder(achievementsIcon, achievementsName, achievementsDescription);
            viewGroup.setTag(holder);
            view = viewGroup;
            
        } else {
            // get the holder back out
        	// check whether base on data, button should appear or not
            holder = (ViewHolder)convertView.getTag();
            view = convertView;
            
        }

        //actually setup the view
        AQuery androidAQuery=new AQuery(contxt);
        androidAQuery.id(holder.getAchievementsIcon()).image(achievementsModel.getAchievementImageName(), true, true, 200, R.drawable.achievement_icon,null, AQuery.FADE_IN); 
        
       //holder.getAchievementsIcon().setImageResource(R.drawable.achievement_icon);
		holder.getAchievementsName().setText(achievementsModel.getAchievementName());
		holder.getAchievementsDescription().setText(achievementsModel.getAchievementDescription());
        
        return view;
	}

	@Override
	public int getViewType() {
		return 0;
	}	
	
	
	private class ViewHolder {
        private ImageView achievementsIcon;
        private TextView achievementsName;
        private TextView achievementsDescription;

        private ViewHolder(ImageView t1, TextView t2, TextView t3) {
            setAchievementsIcon(t1);
            setAchievementsName(t2);
            setAchievementsDescription(t3);
        }

		public ImageView getAchievementsIcon() {
			return achievementsIcon;
		}

		public void setAchievementsIcon(ImageView achievementsIcon) {
			this.achievementsIcon = achievementsIcon;
		}

		public TextView getAchievementsName() {
			return achievementsName;
		}

		public void setAchievementsName(TextView achievementsName) {
			this.achievementsName = achievementsName;
		}

		public TextView getAchievementsDescription() {
			return achievementsDescription;
		}

		public void setAchievementsDescription(TextView achievementsDescription) {
			this.achievementsDescription = achievementsDescription;
		}
        
     
    }
}
