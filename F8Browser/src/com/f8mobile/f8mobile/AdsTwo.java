package com.f8mobile.f8mobile;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AdsTwo extends Fragment {
	
	private View topMapButtonsLayout;
	
	ImageView ads_normal;
	ImageView ads_fast;
	
	Bundle bundle = new Bundle(); 
	
   @SuppressWarnings("deprecation")
@Override
   public View onCreateView(LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
      
	   topMapButtonsLayout = inflater.inflate(R.layout.two_ads,container,false);
	     
	   ads_normal = (ImageView)topMapButtonsLayout.findViewById(R.id.two_normal);
	   ads_fast = (ImageView)topMapButtonsLayout.findViewById(R.id.two_fast);
	   //ads_normal = (ImageView)topMapButtonsLayout.findViewById(R.id.four_normal);
	   //ads_fast = (ImageView)topMapButtonsLayout.findViewById(R.id.four_fast);
	   
	   //ads_steady.setBackgroundResource(R.drawable.nike);
	   //ads_slow.setBackgroundResource(R.drawable.nike);
	   //ads_normal.setBackgroundResource(R.drawable.nike);
	   //ads_fast.setBackgroundResource(R.drawable.nike);
	
	   //System.out.println(MainActivity.myImageListTwo.size());
	   
	   //ads_normal.setBackgroundDrawable(MainActivity.myImageListFour.get(0));
	   //ads_fast.setBackgroundDrawable(MainActivity.myImageListFour.get(1));
	   
	   
	   //ads_normal.setImageResource(R.drawable.coke);
	   //ads_fast.setImageResource(R.drawable.dunhill);
	   //normal_animation();
       //fast_animation();
	   System.out.println("Ads Two Goes");
	   try{
		   ads_normal.setBackgroundDrawable(MainActivity.myImageListTwo.get(0));
		   //System.out.println("LINKTWO: " + String.valueOf(MainActivity.myImageLinkTwo.get(0)));
		   if (!MainActivity.myImageLinkTwo.get(0).toString().equals("-")){
			   ads_normal.setOnClickListener(new OnClickListener() {
					
				   @Override
				   public void onClick(View v) {
					   // TODO Auto-generated method stub
					   
					   FragmentManager fm = getActivity().getFragmentManager();
					   //FragmentTransaction fragmentTransaction = fm.beginTransaction();
					   String currentFragment = fm.findFragmentById(R.id.second_fragment).toString();
					   
					   System.out.println("IMAGE ADS: WEB GOES HERE");
					   String url = MainActivity.myImageLinkTwo.get(0).toString();
					   
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
			   if (!MainActivity.myContactTwo.get(0).toString().equals("-")){
				   
				   ads_normal.setOnClickListener(new OnClickListener() {
						
					   @Override
					   public void onClick(View v) {
						   // TODO Auto-generated method stub
						   
						   AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
					          builder1.setMessage("You can contact us at \n" + MainActivity.myContactTwo.get(0) + "\n");
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
		   
		   ads_fast.setBackgroundDrawable(MainActivity.myImageListTwo.get(1));
		   if (!MainActivity.myImageLinkTwo.get(1).toString().equals("-")){
			   ads_fast.setOnClickListener(new OnClickListener() {
					
				   @Override
				   public void onClick(View v) {
					   // TODO Auto-generated method stub
					   
					   FragmentManager fm = getActivity().getFragmentManager();
					   //FragmentTransaction fragmentTransaction = fm.beginTransaction();
					   String currentFragment = fm.findFragmentById(R.id.second_fragment).toString();
					   
					   System.out.println("IMAGE ADS: WEB GOES HERE");
					   String url = MainActivity.myImageLinkTwo.get(1).toString();
					   
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
			   if (!MainActivity.myContactTwo.get(1).toString().equals("-")){
				   
				   ads_normal.setOnClickListener(new OnClickListener() {
						
					   @Override
					   public void onClick(View v) {
						   // TODO Auto-generated method stub
						   
						   AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
					          builder1.setMessage("You can contact us at \n" + MainActivity.myContactTwo.get(1) + "\n");
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
		   System.out.println("Error on AdsTwo");

	   }
	   
	   
	   
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
	    animation.addFrame(MainActivity.myImageListTwo.get(0), 20000);
	    //animation.addFrame(MainActivity.myImageListTwo.get(2), 20000);
	    //animation.addFrame(MainActivity.myImageListTwo.get(4), 20000);
	    animation.setOneShot(false);

	    //ImageView imageAnim =  (ImageView) findViewById(R.id.img);
	    ads_normal.setBackgroundDrawable(animation);
		
	    animation.start();
	}
	
	@SuppressWarnings("deprecation")
	public void fast_animation(){
		AnimationDrawable animation = new AnimationDrawable();
	    animation.addFrame(MainActivity.myImageListTwo.get(1), 20000);
	    //animation.addFrame(MainActivity.myImageListTwo.get(3), 10000);
	   // animation.addFrame(MainActivity.myImageListTwo.get(5), 10000);
	    //animation.addFrame(MainActivity.myImageListTwo.get(6), 10000);
	    //animation.addFrame(MainActivity.myImageListTwo.get(7), 10000);
	    //animation.addFrame(MainActivity.myImageListTwo.get(8), 10000);
	    //animation.setOneShot(false);

	    //ImageView imageAnim =  (ImageView) findViewById(R.id.img);
	    ads_fast.setBackgroundDrawable(animation);
		
	    animation.start();
	}
   
	public void FragmentsWeb(String x){
		bundle.putString("url", x );
	    Fragment fr1 = new WebPage();
	    fr1.setArguments(bundle);
	    FragmentManager fm = getFragmentManager();
	    FragmentTransaction fragmentTransaction = fm.beginTransaction();
	    fragmentTransaction.replace(R.id.second_fragment, fr1);
	    try{
			fragmentTransaction.commit();
		}catch(Exception e){
			
		}
	}
}