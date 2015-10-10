package com.f8mobile.f8mobile;

import android.os.Bundle;
import android.view.WindowManager;
import android.view.Window;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class MainActivityRedirect extends Activity{
	
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      setContentView(R.layout.login);
     
      context = this;
      
      Intent intent = new Intent(MainActivityRedirect.this, MainActivity.class);
      MainActivityRedirect.this.startActivity(intent);
      
	}  
	
	
}

