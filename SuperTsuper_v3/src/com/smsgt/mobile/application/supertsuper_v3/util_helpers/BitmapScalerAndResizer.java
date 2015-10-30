package com.smsgt.mobile.application.supertsuper_v3.util_helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class BitmapScalerAndResizer {
	
	 /* create a Bitmap from a relativelayout */
	 public Bitmap createDrawableFromView(Context context, View view) {
			DisplayMetrics displayMetrics = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			view.setLayoutParams(new LayoutParams(android.support.v4.view.ViewPager.LayoutParams.WRAP_CONTENT, android.support.v4.view.ViewPager.LayoutParams.WRAP_CONTENT));
			//view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
			view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
			//view.buildDrawingCache();
			Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
			
			Canvas canvas = new Canvas(bitmap);
			view.draw(canvas);
			return bitmap;
		} 
	 /* end create Bitmap */	 

	 public Drawable createBitmapFromDrawable(Context context, int path) {
		    Options options = new BitmapFactory.Options();
		    options.inScaled = false;
		    options.inJustDecodeBounds = true;
		    options.inSampleSize = 8;
		    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), path , options);
		    
	      // Create blank bitmap of equal size
	        Bitmap canvasBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
	        canvasBitmap.eraseColor(0x00000000);

	      // Create canvas
	        Canvas canvas = new Canvas(canvasBitmap);
	      //Draw bitmap onto canvas using matrix
	        canvas.drawBitmap(bitmap, null, null);
		    return new BitmapDrawable(context.getResources(), bitmap);
	 }
	 
}
