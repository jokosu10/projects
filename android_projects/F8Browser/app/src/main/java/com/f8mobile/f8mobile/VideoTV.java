package com.f8mobile.f8mobile;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

@SuppressLint("NewApi") public class VideoTV extends Fragment {
	
	private View videoView;
	
	VideoView videoview;
	Uri uri;
	
   @Override
   public View onCreateView(LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {
      
	   int urlID = this.getArguments().getInt("id");
	   String packageName = this.getArguments().getString("package");
	   
	   videoView = inflater.inflate(R.layout.videotv,container,false);
	   
	   videoview = (VideoView)videoView.findViewById(R.id.stream);
	   
	   if (urlID == 5){
		   //uri = Uri.parse("android.resource://"+packageName+"/"+R.raw.destruct);
		   uri = Uri.parse("http://www.f8mobile.net/mediafiles/news1.mp4");
	   }
	   else if (urlID == 7){
		   uri = Uri.parse("http://www.f8mobile.net/mediafiles/cnn.mp4");
	   }
	     
         
		videoview.setVideoURI(uri);
		videoview.start();  
		     
		videoview.setOnPreparedListener(new OnPreparedListener() {
			@Override
		    public void onPrepared(MediaPlayer mp) {
		    	
				videoview.start();
		        mp.setLooping(true);
		       
		    }
		});
	
	   return videoView;
	   
   }
   
   @Override
   public void onDestroyView() {  
       super.onDestroyView();
       videoView = null; // now cleaning up!
   }

}