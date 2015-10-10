package com.f8mobile.f8mobile;

import java.io.File;

import com.f8mobile.f8mobile.R;
import com.f8mobile.f8mobile.MainActivity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class AdsInactive extends Fragment {
	
	private View topMapButtonsLayout;
	Button refresh;
	MainActivity mActivity = new MainActivity();
	
	@Override
	public View onCreateView(LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
	   //super.onCreate(savedInstanceState);
       //Inflate the layout for this fragment
		  
		
       
	   topMapButtonsLayout = inflater.inflate(R.layout.inactive_ads,container,false);
	   /*refresh = (Button)topMapButtonsLayout.findViewById(R.id.btnRefresh);
	   refresh.setOnClickListener(new OnClickListener() {
		
		   @Override
		   public void onClick(View v) {
			   System.out.println("refresh clicked!");
			   
			   
			    
			   mActivity.refreshIdle();
			   
		   }
		});*/
	  
      
	   return topMapButtonsLayout;
	   
   }
   
   @Override
   public void onDestroyView() {
       super.onDestroyView();
       topMapButtonsLayout = null; // now cleaning up!
   }
   
	public File getCacheDir() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
   
}