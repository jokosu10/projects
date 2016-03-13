package com.f8mobile.f8mobile;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AdsOne extends Fragment {
	
	private View topMapButtonsLayout;
	Drawable ad;
	ImageView ads_normal;
	
	Bundle bundle = new Bundle(); 
	
   @SuppressWarnings("deprecation")
@Override
   public View onCreateView(LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
        
	   
	   topMapButtonsLayout = inflater.inflate(R.layout.one_ads,container,false);
	     
	   ads_normal = (ImageView)topMapButtonsLayout.findViewById(R.id.one);
	   //ads_normal.setBackgroundResource(R.drawable.nike);
	   //ad = ;
	   
	   //ads_normal.setImageResource(R.drawable.samsung_1s);
	  
	   //ads_normal.setBackgroundDrawable(MainActivity.myImageListFour.get(0));
	   
	   System.out.println("Ads One Goes");
	   try{
		   ads_normal.setBackgroundDrawable(MainActivity.myImageListOne.get(0));
		   //System.out.println("LINKONE: " + String.valueOf(MainActivity.myImageLinkOne.get(0)));
		   if (!MainActivity.myImageLinkOne.get(0).toString().equals("-")){
			   ads_normal.setOnClickListener(new OnClickListener() {
					
				   @Override
				   public void onClick(View v) {
					   // TODO Auto-generated method stub
					   
					   FragmentManager fm = getActivity().getFragmentManager();
					   //FragmentTransaction fragmentTransaction = fm.beginTransaction();
					   String currentFragment = fm.findFragmentById(R.id.second_fragment).toString();
					   
					   System.out.println("IMAGE ADS: WEB GOES HERE");
					   String url = MainActivity.myImageLinkOne.get(0).toString();
					   
					   if (currentFragment.contains("Web"))
					   {
						   System.out.println("currentfragment: " + currentFragment);
						   //FragmentsWeb(url);
						   WebPage.browser.loadUrl(url);
						   WebPage.urlbox.setText(url);
					   }
					   else{
						   FragmentsWeb(url);
					   }  
				   }
			   });
		   }
		   else{
			   if (!MainActivity.myContactOne.get(0).toString().equals("-")){
				   
				   ads_normal.setOnClickListener(new OnClickListener() {
						
					   @Override
					   public void onClick(View v) {
						   // TODO Auto-generated method stub
						   
						   AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
					          builder1.setMessage("You can contact us at \n" + MainActivity.myContactOne.get(0) + "\n");
					          builder1.setCancelable(false);
					          builder1.setPositiveButton("Exit",
					                  new DialogInterface.OnClickListener() {
					              @Override
								public void onClick(DialogInterface dialog, int id) {
					            	  
					            	  //finish();
					                  dialog.cancel();  
					              }
					          });
					          AlertDialog alert11 = builder1.create();
					          alert11.show();
					   }
				   });
				 
			   }
		   }
	   }
	   catch(Exception e){
		   System.out.println("Error on AdsOne");
	   }
	   
	   
	   
	   //normal_animation();
	  
	   
	   return topMapButtonsLayout;
	   
   }
   
   @Override
   public void onDestroyView() {
       super.onDestroyView();
       topMapButtonsLayout = null; // now cleaning up!
   }
   
   @SuppressWarnings("deprecation")
public void normal_animation(){
		AnimationDrawable animation = new AnimationDrawable();
	    animation.addFrame(MainActivity.myImageListOne.get(0), 60000);
	    //animation.addFrame(MainActivity.myImageListOne.get(1), 20000);
	    //animation.addFrame(MainActivity.myImageListOne.get(2), 20000);
	    
	    animation.setOneShot(false);

	    //ImageView imageAnim =  (ImageView) findViewById(R.id.img);
	    ads_normal.setBackgroundDrawable(animation);
		
	    animation.start();
	}
  
   public void FragmentsWeb(String x){
		bundle.putString("url", x );
	    Fragment fr1 = new WebPage();
	    fr1.setArguments(bundle);
	    FragmentManager fm = getFragmentManager();
	    FragmentTransaction fragmentTransaction = fm.beginTransaction();
	    //fragmentTransaction.
	    fragmentTransaction.replace(R.id.second_fragment, fr1);
	    try{
			fragmentTransaction.commit();
		}catch(Exception e){
			
		}
	}
   
   
}