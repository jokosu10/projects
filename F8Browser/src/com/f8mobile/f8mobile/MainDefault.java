package com.f8mobile.f8mobile;

import com.f8mobile.f8mobile.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class MainDefault extends Fragment {
	
	private View topMapButtonsLayout;	
	ImageView facilitcIMG;
	//public static View loader;
	
   @Override
   public View onCreateView(LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
	   //super.onCreate(savedInstanceState);
       //Inflate the layout for this fragment
       
	   //context = getActivity().getApplicationContext();
	   topMapButtonsLayout = inflater.inflate(R.layout.main_default,container,false);
	   //dialog = ProgressDialog.show(context, "Loading...", "Please wait...", true);
	   //loader = (ImageView)topMapButtonsLayout.findViewById(R.id.loader_id);
	   
	   //facilitcIMG = (ImageView)topMapButtonsLayout.findViewById(R.id.loadIMG);
	   //facilitcIMG.setImageResource(R.drawable.default1s);
	   
	   return topMapButtonsLayout;
	   
      //return inflater.inflate(
    	//	  R.layout.four_ads, container, false);
   }
   
   @Override
   public void onDestroyView() {
       super.onDestroyView();
       //findViewById(R.id.loader).setVisibility(View.GONE);
       topMapButtonsLayout = null; // now cleaning up!
   }
   
  
	
	
   
}