package com.f8mobile.f8mobile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class CheckUpdate {
	public void check(String AppID,Context C) {
		try {
		final String FAppID =AppID;
		final Context FC=C;  
		String Html =GetHtml("https://play.google.com/store/apps/details?id="+AppID);
		Log.d("URl", "https://play.google.com/store/apps/details?id="+AppID);
		Pattern pattern = Pattern.compile("softwareVersion\">[^<]*</d");
		Log.d("Pattern: ", pattern.toString());
		Matcher matcher = pattern.matcher(Html);
		//Log.d("matcher: ", matcher.toString());
		matcher.find();
		
		//Log.d("matcher: ", matcher.find().toString());
		String MarketVersionName = matcher.group(0).substring(matcher.group(0).indexOf(">")+1, matcher.group(0).indexOf("<"));
		Log.d("matcher: ", matcher.toString());
		Log.d("VersionAvailble", MarketVersionName);
		String ExistingVersionName = C.getPackageManager().getPackageInfo(C.getPackageName(), 0 ).versionName;
		Log.d("VersionActual", ExistingVersionName);
		if(!MarketVersionName.equals(ExistingVersionName)){
	        AlertDialog.Builder alertDialog = new AlertDialog.Builder(C);
			alertDialog.setTitle("Update Availble");
	        alertDialog.setMessage("You are using "+ExistingVersionName+" version\n"+MarketVersionName+" version is availble\nDo you which to download it?");
	        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	try {            		
					    FC.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+FAppID)));
						} catch (android.content.ActivityNotFoundException anfe) {
						FC.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id"+FAppID)));
					}
	            }
	        });
	        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {	   
	            }
	        });
	        alertDialog.show();
		}		
		} catch (Exception e) {}
	}

	String GetHtml(String url1) {
		String str = "";
		try {
			URL url = new URL(url1);
			URLConnection spoof = url.openConnection();
			spoof.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
			BufferedReader in = new BufferedReader(new InputStreamReader(spoof.getInputStream()));
			String strLine = "";
			// Loop through every line in the source
			while ((strLine = in.readLine()) != null) {
				str = str + strLine;
			}
		} catch (Exception e) {
		}
		return str;
	}
}