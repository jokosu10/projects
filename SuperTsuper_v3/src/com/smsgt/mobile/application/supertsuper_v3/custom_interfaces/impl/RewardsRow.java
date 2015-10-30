package com.smsgt.mobile.application.supertsuper_v3.custom_interfaces.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.smsgt.mobile.application.supertsuper_v3.R;
import com.smsgt.mobile.application.supertsuper_v3.activities.DriveActivity;
import com.smsgt.mobile.application.supertsuper_v3.activities.UserRewardsActivity;
import com.smsgt.mobile.application.supertsuper_v3.asynctask_classes.SendTsuperUserProfileToStrongloop;
import com.smsgt.mobile.application.supertsuper_v3.custom_interfaces.RewardsInterface;
import com.smsgt.mobile.application.supertsuper_v3.database.model.RewardsModel;
import com.smsgt.mobile.application.supertsuper_v3.database.model.TsuperUserProfileModel;


public class RewardsRow implements RewardsInterface {

	private final RewardsModel rewardsModel;
	private LayoutInflater rewardsLayoutInflater;
	private boolean isButtonClicked = false;
	private TsuperUserProfileModel tsuperUserProfileModel;
	private String caller;
	private Context contxt;
	
	public RewardsRow(RewardsModel rewardsModel, LayoutInflater layoutInflater, TsuperUserProfileModel t, String caller, Context c) {
		this.rewardsModel = rewardsModel;
		this.rewardsLayoutInflater = layoutInflater;
		this.tsuperUserProfileModel = t;
		this.caller = caller;
		this.contxt = c;
	}
	
	@Override
	public View getView(View convertView, final int position) {
		
		final ViewHolder holder;
        View view;
        //we don't have a converView so we'll have to create a new one using ViewHolder pattern
        if (convertView == null) {
        	
            ViewGroup viewGroup = (ViewGroup) rewardsLayoutInflater.inflate(com.smsgt.mobile.application.supertsuper_v3.R.layout.sliding_drawer_rewards_row, null);
            ImageView rewardsImage = (ImageView) viewGroup.findViewById(R.id.slidingDrawerRewardsImage);
            TextView rewardsPoints = (TextView) viewGroup.findViewById(R.id.slidingDrawerRewardPoints);
            ProgressBar rewardsProgressBar = (ProgressBar) viewGroup.findViewById(R.id.slidingDrawerRewardsProgressBar);
            Button claimRewardsButton = (Button) viewGroup.findViewById(R.id.slidingDrawerClaimPromoButton);
            holder = new ViewHolder(rewardsImage, rewardsPoints, rewardsProgressBar, claimRewardsButton);
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
        androidAQuery.id(holder.getImageView()).image(rewardsModel.getRewardImageName(), true, true, 300, R.drawable.reward_default,null, AQuery.FADE_IN);        
		holder.getTextView().setText(tsuperUserProfileModel.getTotalPoints() + " / "+ rewardsModel.getRewardsItemEquivalentPoints()  + " pts");
		holder.getProgressBar().setMax(Integer.parseInt(rewardsModel.getRewardsItemEquivalentPoints()));
	    holder.getProgressBar().setProgress(Integer.parseInt(tsuperUserProfileModel.getTotalPoints()));
       
        if(Integer.parseInt(tsuperUserProfileModel.getTotalPoints()) >= Integer.parseInt(rewardsModel.getRewardsItemEquivalentPoints())) {
        	
        	if(!isButtonClicked) {
        		
            	holder.getTextView().setVisibility(View.INVISIBLE);
            	holder.getProgressBar().setVisibility(View.INVISIBLE);
            	holder.getButton().setVisibility(View.VISIBLE);
                holder.getButton().setOnClickListener(new OnClickListener() {
    				
     				@Override
     				public void onClick(View v) {
 
     					isButtonClicked = true;
     					Toast.makeText(v.getContext(), "Rewards Claimed: ", Toast.LENGTH_SHORT).show();
     					
     					// to do:
     					// * update user total points, subtracting the item equivalent points to user total points
     					Integer updatedPoints = Integer.parseInt(tsuperUserProfileModel.getTotalPoints()) - Integer.parseInt(rewardsModel.getRewardsItemEquivalentPoints());
     					
     					tsuperUserProfileModel.setTotalPoints(Integer.toString(updatedPoints));
     					if(caller.equalsIgnoreCase("DriveActivity")) {
     						DriveActivity.refreshSliderUIData(tsuperUserProfileModel);
     						new SendTsuperUserProfileToStrongloop(tsuperUserProfileModel, v.getContext()).execute("UserRegistrations", "PUT");
     					} else if (caller.equalsIgnoreCase("UserRewardsActivity")) {
     						UserRewardsActivity.refreshListViewUIData(tsuperUserProfileModel);
     						new SendTsuperUserProfileToStrongloop(tsuperUserProfileModel, v.getContext()).execute("UserRegistrations", "PUT");
     					}
     					
     					holder.getTextView().setText(updatedPoints + " / "+ rewardsModel.getRewardsItemEquivalentPoints()  + " pts");
     		            holder.getProgressBar().setMax(Integer.parseInt(rewardsModel.getRewardsItemEquivalentPoints()));
     		            holder.getProgressBar().setProgress(updatedPoints);
     		            //holder.getProgressBar().setProgressDrawable();
     		            holder.getTextView().setVisibility(View.VISIBLE);
     		        	holder.getProgressBar().setVisibility(View.VISIBLE);
     		        	holder.getButton().setVisibility(View.INVISIBLE);
     		        			        	
     				}
     			});
        	} 	
        } else {
      
            holder.getTextView().setText(tsuperUserProfileModel.getTotalPoints() + " / "+ rewardsModel.getRewardsItemEquivalentPoints()  + " pts");
            holder.getProgressBar().setMax(Integer.parseInt(rewardsModel.getRewardsItemEquivalentPoints()));
            holder.getProgressBar().setProgress(Integer.parseInt(tsuperUserProfileModel.getTotalPoints()));
            holder.getTextView().setVisibility(View.VISIBLE);
        	holder.getProgressBar().setVisibility(View.VISIBLE);
            holder.getButton().setVisibility(View.INVISIBLE);
         
        }
       
        return view;
	}

	@Override
	public int getViewType() {
		return 0;
	}
	
    private class ViewHolder {
        private ImageView imageView;
        private TextView titleView;
        private ProgressBar progressBar;
        private Button claimRewardsBtn;

        private ViewHolder(ImageView imageView, TextView titleView, ProgressBar progressBar, Button claimRewardsButton) {
            setImageView(imageView);
            setTextView(titleView);
            setProgressBar(progressBar);
            setButton(claimRewardsButton);
        }
        
        public void setImageView(ImageView v) {
        	this.imageView = v;
        }
        
        public ImageView getImageView() {
        	return imageView;
        }
        
        public void setTextView(TextView v) {
        	this.titleView = v;
        }
        
        public TextView getTextView() {
        	return titleView;
        }
        
        public void setProgressBar(ProgressBar v) {
        	this.progressBar = v;
        }
        
        public ProgressBar getProgressBar() {
        	return progressBar;
        }
        
        public void setButton(Button v) {
        	this.claimRewardsBtn = v;
        }
        
        public Button getButton() {
        	return claimRewardsBtn;
        }   
    }	
}
