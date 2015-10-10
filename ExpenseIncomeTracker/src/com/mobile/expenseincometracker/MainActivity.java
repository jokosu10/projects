package com.mobile.expenseincometracker;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	   private TextView expenseTextViewValueMain;
	    private Button historyBtn;
	    private TextView incomeTextViewValueMain;
	    public static Handler mHandler;
	    private Button numOrdersBtn;
	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main_layout);
	        numOrdersBtn = (Button)findViewById(R.id.ordersBtn);
	        historyBtn = (Button)findViewById(R.id.historyBtn);
	        incomeTextViewValueMain = (TextView)findViewById(R.id.textViewIncomeValue);
	        expenseTextViewValueMain = (TextView)findViewById(R.id.textViewExpenseValue);
	        numOrdersBtn.setOnClickListener(new View.OnClickListener() {
	           
	        	@Override
	            public void onClick(View v) {
	                Intent ordersIntent = new Intent(MainActivity.this, OrdersActivity.class);
	                startActivity(ordersIntent);
	            }
	        });
	        
	        historyBtn.setOnClickListener(new View.OnClickListener()  {
	            
	        	@Override
	            public void onClick(View v) {
	                Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
	                startActivity(historyIntent);
	            }
	        });
	        mHandler = new Handler() {
	            
	            @SuppressWarnings("unchecked")
				public void handleMessage(Message msg) {
	                switch(msg.what) {
	                    case 3:
	                    {
	                        HashMap<String, Double> d = (HashMap<String, Double>) msg.obj;
	                        incomeTextViewValueMain.setText(String.valueOf(d.get("income")));
	                        expenseTextViewValueMain.setText(String.valueOf(d.get("expenses")));
	                        break;
	                    }
	                }
	            }
	        };
	    }
	    
	    @Override
	    protected void onPause() {
	        super.onPause();
	    }
	    
	    @Override
	    protected void onResume() {
	        super.onResume();
	    }
	    
	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	    }
}
