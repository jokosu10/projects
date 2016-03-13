package com.mobile.expenseincometracker;

import java.util.ArrayList;
import java.util.List;

import com.mobile.expenseincometracker.custom.ExpenseIncomeOrdersListViewAdapter;
import com.mobile.expenseincometracker.model.ExpenseIncomeOrdersModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class OrdersActivity extends Activity {
	
    private String cakeType;
    private static double inc;
    private static TextView incomeTv;
    private static Handler mHandler;
    private String orderSize;
    private ExpenseIncomeOrdersListViewAdapter ordersAdapter;
    private static ArrayList<ExpenseIncomeOrdersModel> ordersList;
    private ListView ordersListView;
    private static int totQty;
    private InputMethodManager imm;
    
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.orders_layout);
        ordersList = new ArrayList<ExpenseIncomeOrdersModel>();
        inc = 0;
        totQty = 0;
        final Spinner cakesTypeSpinner = (Spinner)findViewById(R.id.spinnerCakeType);
        final String[] items = {"Cheese Cake",
        "Yema Cake",
        "Brownies"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
       // adapter.setDropDownViewResource(0x1090009);
        cakesTypeSpinner.setAdapter(adapter);
        cakesTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cakeType = parent.getSelectedItem().toString();
            }
            
            public void onNothingSelected(AdapterView<?> parent) {
                cakeType = items[0];
            }
        });
        
        final EditText qtyEditText = (EditText)findViewById(R.id.editTextQuantityValue);
        final EditText priceEditText = (EditText)findViewById(R.id.editTextPriceValue);
        RadioGroup sizeRadioGroup = (RadioGroup)findViewById(R.id.myRadioGroup);
        final RadioButton smallRadioBtn = (RadioButton)findViewById(R.id.smallRadioBtn);
        final RadioButton mediumRadioBtn = (RadioButton)findViewById(R.id.mediumRadioBtn);
        final RadioButton largeRadioBtn = (RadioButton)findViewById(R.id.largeRadioBtn);
        incomeTv = (TextView)findViewById(R.id.tvIncomeValue);
        orderSize = "Small";
        sizeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
           
        	@Override
        	public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.smallRadioBtn) {
                    orderSize = smallRadioBtn.getText().toString();
                    return;
                }
                if(checkedId == R.id.mediumRadioBtn) {
                    orderSize = mediumRadioBtn.getText().toString();
                    return;
                }
                if(checkedId == R.id.largeRadioBtn) {
                    orderSize = largeRadioBtn.getText().toString();
                    return;
                }
                orderSize = "Small";
            }
        });

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        ordersListView = (ListView)findViewById(R.id.ordersListView);
        ordersAdapter = new ExpenseIncomeOrdersListViewAdapter(ordersList, getApplicationContext());
        ordersListView.setAdapter(ordersAdapter);
        Button ordersAddBtn = (Button)findViewById(R.id.addBtn);
        ordersAddBtn.setOnClickListener(new View.OnClickListener() {
            
        	@Override
            public void onClick(View v) {
        		
                if((qtyEditText.getText().toString().trim().isEmpty()) || (priceEditText.getText().toString().trim().isEmpty())) {
                    Toast.makeText(getApplicationContext(), "Cannot save. Missing quantity and/or price!", Toast.LENGTH_SHORT).show();
                } else {
                    ExpenseIncomeOrdersModel orders = new ExpenseIncomeOrdersModel();
                    orders.setCakeType(cakeType);
                    if(cakeType.equalsIgnoreCase("Cheese Cake")) {
                        orders.setPicId(1);
                    } else if(cakeType.equalsIgnoreCase("Yema Cake")) {
                        orders.setPicId(2);
                    } else if(cakeType.equalsIgnoreCase("Brownies")) {
                        orders.setPicId(3);
                    }
                    orders.setQuantity(qtyEditText.getText().toString());
                    double i = Double.parseDouble(orders.getQuantity()) * Double.parseDouble(priceEditText.getText().toString());
                    orders.setPrice(Double.toString(i));
                    orders.setSize(orderSize);
                    
                    ordersList.add(0, orders);
                    inc = inc + i;
                    incomeTv.setText(String.valueOf(inc));
                    totQty = ((totQty + Integer.parseInt(orders.getQuantity())));
                    Message.obtain(mHandler, 1).sendToTarget();
                    qtyEditText.setText("");
                    priceEditText.setText("");
                    cakesTypeSpinner.setSelection(0);
                    smallRadioBtn.setChecked(true);
                    mediumRadioBtn.setChecked(false);
                    largeRadioBtn.setChecked(false);
                    imm.hideSoftInputFromWindow(priceEditText.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }

            }
        });
        Button ordersSaveBtn = (Button)findViewById(R.id.ordersSaveMainBtn);
        ordersSaveBtn.setOnClickListener(new View.OnClickListener() {
            
        	@Override
            public void onClick(View v) {
                if((totQty <= 0) && ( inc <= 0)) {
                    Toast.makeText(getApplicationContext(), "Cannot save if Qty And/Or Capital is less than or equal to 0.", Toast.LENGTH_SHORT).show();
                   
                } else {
                	Intent expensesIntent = new Intent(OrdersActivity.this, ExpensesActivity.class);
                    expensesIntent.putExtra("inc", inc);
                    expensesIntent.putExtra("totQty", totQty);
                    startActivity(expensesIntent);
                    finish();
                }
                
            }
        });
        mHandler = new Handler() {
            
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 1:
                    {
                        ExpenseIncomeOrdersListViewAdapter ordersAdapter = new ExpenseIncomeOrdersListViewAdapter(ordersList, getApplicationContext());
                        ordersListView.invalidate();
                        ordersListView.setAdapter(ordersAdapter);
                        ordersAdapter.notifyDataSetChanged();
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
        ordersAdapter = null;
        ordersList = null;
        ordersListView = null;
    }
    
    public void onBackPressed() {
        super.onBackPressed();
        ordersAdapter = null;
        ordersList = null;
        ordersListView = null;
    }
    
    @SuppressWarnings("unchecked")
	public static void refreshUI_Orders(String priceValDeduct, String qtyValDeduct, List<?> o, String method, String p1, String q1) {
        Log.e("", "--" + qtyValDeduct + "--" + p1 + "--" + q1);
        ordersList = (ArrayList<ExpenseIncomeOrdersModel>) o;
        String newPriceValRem = priceValDeduct.substring((priceValDeduct.indexOf("P") + 1), priceValDeduct.length());
        String oldPriceValRem = incomeTv.getText().toString();
        oldPriceValRem = oldPriceValRem.substring((oldPriceValRem.indexOf("P") + 1), oldPriceValRem.length());
        
        if(method.equalsIgnoreCase("deduct")) {
            inc = (Double.parseDouble(oldPriceValRem) - Double.parseDouble(newPriceValRem));
            if(inc <= 0) {
                inc = 0;
            }
            totQty = (totQty - Integer.parseInt(qtyValDeduct));
            if(totQty <= 0) {
                totQty = 0;
            }
        } else if(!priceValDeduct.equalsIgnoreCase(p1)) {
            inc = (inc - Double.parseDouble(p1));
            if(inc <= 0) {
                inc = 0;
            } else if(!qtyValDeduct.equalsIgnoreCase(q1)) {
                totQty = (totQty - Integer.parseInt(q1));
                if(totQty <= 0) {
                    totQty = 0;
                } else {
                    totQty = (totQty + Integer.parseInt(qtyValDeduct));
                }
            } else {
                inc = (inc + Double.parseDouble(newPriceValRem));
            } 
        }
        incomeTv.setText(String.valueOf(inc));
        Message.obtain(mHandler, 1).sendToTarget();
    }
}

