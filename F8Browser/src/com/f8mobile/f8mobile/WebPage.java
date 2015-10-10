package com.f8mobile.f8mobile;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnKeyListener;
import android.view.KeyEvent;


@SuppressLint("SetJavaScriptEnabled") 
public class WebPage extends Fragment {
	
	static WebView browser;
	
	Button bGo;
	Button bRefresh;
	Button bBack;
	Button bForward;
	private View topMapButtonsLayout;
	static EditText urlbox;  
	static String url;
	String currentUrl;
	String previousUrl;
	
	/*public static WebPage newInstance() {
		WebPage fragment = new WebPage();
        return fragment;
    }*/
	
   @Override
   public View onCreateView(LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
	   
	   int urlID = this.getArguments().getInt("id");
	   
	   String URLfromAds = this.getArguments().getString("url");
	   //System.out.println("URL: " + URLfromAds);
	   
	   if (URLfromAds == null){
		   if (urlID == 1){
			   url = "http://www.facebook.com";
		   }
		   else if (urlID == 6){
			   url = "http://www.skype.com";
		   }
		   else if (urlID == 2){
			   url = "http://www.twitter.com";
		   }
		   else if (urlID == 4){
			   url = "http://www.youtube.com";
		   }
		   else if (urlID == 3){
			   url = "http://www.google.com";
		   }
		   else if (urlID == 8){
			   url = "http://www.f8mobile.net";
		   }
		   else if (urlID == 9){
			   url = "http://www.typhoon2000.com";
		   }
	   }
	   else{
		   url = URLfromAds;
	   }
	   
       Log.d("WID", String.valueOf(urlID));
       //Log.d("RID", rid);
       //String video= "<table bgcolor=\"#666666\"><tr><td><iframe width=\"300\" height=\"260\"     frameborder=\"0\" id=\"player\" type=\"text/html\"src=\"http://www.youtube.com/embed/iiLepwjBhZE?enablejsapi=1&origin=example.com\"></iframe></td></tr><tr><td><iframe width=\"300\" height=\"260\" frameborder=\"0\" id=\"player\" type=\"text/html\"src=\"http://www.youtube.com/embed/lBMMTeuJ_UQ?enablejsapi=1&origin=example.com\"></iframe></td></tr><tr><td><iframe width=\"300\" height=\"260\" frameborder=\"0\" id=\"player\" type=\"text/html\"src=\"http://www.youtube.com/embed/BZMkY3y7nM0?enablejsapi=1&origin=example.com\"></iframe></td></tr><tr><td></table>"; 
       
       
       topMapButtonsLayout = inflater.inflate(R.layout.webpage,container,false);
       //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	   
	   browser = (WebView)topMapButtonsLayout.findViewById(R.id.webView1);
	   
	   urlbox = (EditText)topMapButtonsLayout.findViewById(R.id.urlBox);
       //urlbox.setFocusableInTouchMode(true);
       //urlbox.requestFocus();  
	   
	   browser.clearCache(true);
	   //WebSettings setting =browser.getSettings();
	   browser.setWebViewClient(new MyBrowser());
	   browser.setWebChromeClient(new WebChromeClient());	
	   browser.getSettings().setLoadsImagesAutomatically(true);
	   browser.getSettings().setJavaScriptEnabled(true);
	   browser.getSettings().setUseWideViewPort(true);
	   browser.getSettings().setLoadWithOverviewMode(true);
	   browser.getSettings().setPluginState(PluginState.ON);
	   //browser.getSettings().setPluginsEnabled(true);
	   //browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	   //browser.loadData(video,"text/html","UTF-8");
	   //System.out.println(url);
	   browser.loadUrl(url);
	   urlbox.setText(url);	   
	   
	   bGo = (Button)topMapButtonsLayout.findViewById(R.id.btnGo);
	   bBack = (Button)topMapButtonsLayout.findViewById(R.id.btnBack);
	   bForward = (Button)topMapButtonsLayout.findViewById(R.id.btnForward);
	   bRefresh = (Button)topMapButtonsLayout.findViewById(R.id.btnRefresh);
	   
	   bGo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				previousUrl = browser.getUrl();
				url = urlbox.getText().toString();
            	if (url.length() < 4)
            	{
            		url = "www.google.com/search?q="+url;
            	}
            	if (url.substring(0, 4).equals("http")){
            		
            	}else{
            		url = "http://" + url;
            	}
            	browser.setWebViewClient(new MyBrowser());
         	   	browser.setWebChromeClient(new WebChromeClient());	
         	   	browser.getSettings().setLoadsImagesAutomatically(true);
         	   	browser.getSettings().setJavaScriptEnabled(true);
         	   	browser.getSettings().setUseWideViewPort(true);
         	   	browser.getSettings().setLoadWithOverviewMode(true);
         	   	browser.loadUrl(url);
         	   	urlbox.setText(url);
			}
		});
	   
	   bBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (browser.canGoBack()) {
					browser.goBack();
					urlbox.setText(previousUrl);
					//browser.getOriginalUrl();
					//browser.get
				}
			}
		});
	   
	   bRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				browser.reload();
				urlbox.setText(browser.getUrl());
			}
		});
	   
	   bForward.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (browser.canGoForward()) {
					browser.goForward();
					urlbox.setText(browser.getUrl());
					
				}
			}
		});
	   
	   
	   urlbox.setOnKeyListener(new OnKeyListener() {
	        public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
	            //If the keyevent is a key-down event on the "enter" button
	            if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
	                //...
	                // Perform your action on key press here
	                // ...  
	            	
	            	previousUrl = browser.getUrl();
	            	url = urlbox.getText().toString();
	            	
	            	if (url.length() < 4)
	            	{
	            		url = "www.google.com/search?q="+url;
	            	}
	            	if (url.substring(0, 4).equals("http")){
	            		
	            	}else{
	            		url = "http://" + url;
	            	}
	            	browser.setWebViewClient(new MyBrowser());
	         	   	browser.setWebChromeClient(new WebChromeClient());	
	         	   	browser.getSettings().setLoadsImagesAutomatically(true);
	         	   	browser.getSettings().setJavaScriptEnabled(true);
	         	   	browser.getSettings().setUseWideViewPort(true);
	         	   	browser.getSettings().setLoadWithOverviewMode(true);
	         	   	browser.loadUrl(url);
	         	   	urlbox.setText(url);
	         	   	
	         	   	
	         	   	
	                return true;
	            }  
	            return false;
	        }
	});
	   
	   return topMapButtonsLayout;
	   
   }
   
   @Override
   public void onDestroyView() {
       super.onDestroyView();
       topMapButtonsLayout = null; // now cleaning up!
   }
   
   private class MyBrowser extends WebViewClient {
	      @Override
	      public boolean shouldOverrideUrlLoading(WebView view, String urlLoad) {
	         //url = urlLoad;
	    	 previousUrl = browser.getUrl();
	    	 if (!urlLoad.equals("about:blank")){
	    		 view.loadUrl(urlLoad);
	    		 urlbox.setText(urlLoad); 
	    		 
	    	 }
	    	 //System.out.println("WebView Goes Here!" + urlLoad);
	         return true;
	      }
	   }
   
  
   public boolean canGoBack() {
       return WebPage.browser != null && WebPage.browser.canGoBack();
   } 

   public void goBack() {
       if(WebPage.browser != null) {
           WebPage.browser.goBack(); 
           
       }  
   }

}