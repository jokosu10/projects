package com.f8mobile.f8mobile;

import java.io.File;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;


public class AdsFour extends Fragment {
	
	private View topMapButtonsLayout;
	
	Drawable mybmp;	
	
	ImageView ads_steady;
	ImageView ads_slow;
	ImageView ads_normal;
	ImageView ads_fast;
	
	Bundle bundle = new Bundle(); 
	
	
   @SuppressWarnings("deprecation")
@Override
   public View onCreateView(LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
	   //super.onCreate(savedInstanceState);
       //Inflate the layout for this fragment
       
	   topMapButtonsLayout = inflater.inflate(R.layout.four_ads,container,false);
	     
	   ads_steady = (ImageView)topMapButtonsLayout.findViewById(R.id.four_steady);
	   ads_slow = (ImageView)topMapButtonsLayout.findViewById(R.id.four_slow);
	   ads_normal = (ImageView)topMapButtonsLayout.findViewById(R.id.four_normal);
	   ads_fast = (ImageView)topMapButtonsLayout.findViewById(R.id.four_fast);
	  
	   //ads_steady.setBackgroundDrawable(MainActivity.myImageListFour.get(0));
	   //ads_slow.setBackgroundDrawable(MainActivity.myImageListFour.get(1));
	   //ads_normal.setBackgroundDrawable(MainActivity.myImageListFour.get(2));
	   //ads_fast.setBackgroundDrawable(MainActivity.myImageListFour.get(3));
	   
	   System.out.println("Ads Four Goes");
	   
	   try{
		   //ads_steady.setBackgroundResource(R.drawable.f8_android);
		   ads_steady.setBackgroundDrawable(MainActivity.myImageListFour.get(0));//(mybmp);
		   //System.out.println("LINKFOUR: " + MainActivity.myImageLinkFour.get(0));
		   if (!MainActivity.myImageLinkFour.get(0).toString().equals("-")){
			   ads_steady.setOnClickListener(new OnClickListener() {  
					
				   @Override
				   public void onClick(View v) {
					   // TODO Auto-generated method stub
					   
					   FragmentManager fm = getActivity().getFragmentManager();
					   //FragmentTransaction fragmentTransaction = fm.beginTransaction();
					   String currentFragment = fm.findFragmentById(R.id.second_fragment).toString();
					   
					   System.out.println("IMAGE ADS: WEB GOES HERE");
					   String url = MainActivity.myImageLinkFour.get(0).toString();
					   
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
			   if (!MainActivity.myContactFour.get(0).toString().equals("-")){
				   
				   ads_normal.setOnClickListener(new OnClickListener() {
						
					   @Override
					   public void onClick(View v) {
						   // TODO Auto-generated method stub
						   
						   AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
					          builder1.setMessage("You can contact us at \n" + MainActivity.myContactFour.get(0) + "\n");
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
		   
		   ads_slow.setBackgroundDrawable(MainActivity.myImageListFour.get(1));
		   if (!MainActivity.myImageLinkFour.get(1).toString().equals("-")){
			   ads_slow.setOnClickListener(new OnClickListener() {
					
				   @Override
				   public void onClick(View v) {
					   // TODO Auto-generated method stub
					   
					   FragmentManager fm = getActivity().getFragmentManager();
					   //FragmentTransaction fragmentTransaction = fm.beginTransaction();
					   String currentFragment = fm.findFragmentById(R.id.second_fragment).toString();
					   
					   System.out.println("IMAGE ADS: WEB GOES HERE");
					   String url = MainActivity.myImageLinkFour.get(1).toString();
					   
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
			   if (!MainActivity.myContactFour.get(1).toString().equals("-")){
				   
				   ads_normal.setOnClickListener(new OnClickListener() {
						
					   @Override
					   public void onClick(View v) {
						   // TODO Auto-generated method stub
						   
						   AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
					          builder1.setMessage("You can contact us at \n" + MainActivity.myContactFour.get(1) + "\n");
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
		   
		   ads_normal.setBackgroundDrawable(MainActivity.myImageListFour.get(2));
		   if (!MainActivity.myImageLinkFour.get(2).toString().equals("-")){
			   ads_normal.setOnClickListener(new OnClickListener() {
					
				   @Override
				   public void onClick(View v) {
					   // TODO Auto-generated method stub
					   
					   FragmentManager fm = getActivity().getFragmentManager();
					   //FragmentTransaction fragmentTransaction = fm.beginTransaction();
					   String currentFragment = fm.findFragmentById(R.id.second_fragment).toString();
					   
					   System.out.println("IMAGE ADS: WEB GOES HERE");
					   String url = MainActivity.myImageLinkFour.get(2).toString();
					   
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
			   if (!MainActivity.myContactFour.get(2).toString().equals("-")){
				   
				   ads_normal.setOnClickListener(new OnClickListener() {
						
					   @Override
					   public void onClick(View v) {
						   // TODO Auto-generated method stub
						   
						   AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
					          builder1.setMessage("You can contact us at \n" + MainActivity.myContactFour.get(2) + "\n");
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
		   
		   ads_fast.setBackgroundDrawable(MainActivity.myImageListFour.get(3));
		   if (!MainActivity.myImageLinkFour.get(3).toString().equals("-")){
			   ads_fast.setOnClickListener(new OnClickListener() {
					
				   @Override
				   public void onClick(View v) {
					   // TODO Auto-generated method stub
					   
					   FragmentManager fm = getActivity().getFragmentManager();
					   //FragmentTransaction fragmentTransaction = fm.beginTransaction();
					   String currentFragment = fm.findFragmentById(R.id.second_fragment).toString();
					   
					   System.out.println("IMAGE ADS: WEB GOES HERE");
					   String url = MainActivity.myImageLinkFour.get(3).toString();
					   
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
			   if (!MainActivity.myContactFour.get(3).toString().equals("-")){
				   
				   ads_normal.setOnClickListener(new OnClickListener() {
						
					   @Override
					   public void onClick(View v) {
						   // TODO Auto-generated method stub
						   
						   AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
					          builder1.setMessage("You can contact us at \n" + MainActivity.myContactFour.get(3) + "\n");
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
		   //slow_animation();
		   //normal_animation();
		   //fast_animation();
	   }
	   catch(Exception e){
		   System.out.println("Error on AdsFour");
	   }
      
	   return topMapButtonsLayout;
	   
   }
   
   @Override
   public void onDestroyView() {
       super.onDestroyView();
       topMapButtonsLayout = null; // now cleaning up!
   }
   
   @SuppressWarnings("deprecation")
public void slow_animation(){
		AnimationDrawable animation = new AnimationDrawable();
	    animation.addFrame(MainActivity.myImageListFour.get(1), 30000);
	    animation.addFrame(MainActivity.myImageListFour.get(4), 30000);
	    animation.setOneShot(false);

	    //ImageView imageAnim =  (ImageView) findViewById(R.id.img);
	    ads_slow.setBackgroundDrawable(animation);
		
	    animation.start();
	}
	
	
	
	@SuppressWarnings("deprecation")
	public void normal_animation(){
		AnimationDrawable animation = new AnimationDrawable();
	    animation.addFrame(MainActivity.myImageListFour.get(2), 20000);
	    animation.addFrame(MainActivity.myImageListFour.get(5), 20000);
	    animation.addFrame(MainActivity.myImageListFour.get(7), 20000);
	    animation.setOneShot(false);

	    //ImageView imageAnim =  (ImageView) findViewById(R.id.img);
	    ads_normal.setBackgroundDrawable(animation);
		
	    animation.start();
	}
	
	@SuppressWarnings("deprecation")
	public void fast_animation(){
		AnimationDrawable animation = new AnimationDrawable();
	    animation.addFrame(MainActivity.myImageListFour.get(3), 10000);
	    animation.addFrame(MainActivity.myImageListFour.get(6), 10000);
	    animation.addFrame(MainActivity.myImageListFour.get(8), 10000);
	    animation.addFrame(MainActivity.myImageListFour.get(9), 10000);
	    animation.addFrame(MainActivity.myImageListFour.get(10), 10000);
	    animation.addFrame(MainActivity.myImageListFour.get(11), 10000);
	    animation.setOneShot(false);

	    //ImageView imageAnim =  (ImageView) findViewById(R.id.img);
	    ads_fast.setBackgroundDrawable(animation);
		
	    animation.start();
	}

	public File getCacheDir() {
		// TODO Auto-generated method stub
		return null;
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