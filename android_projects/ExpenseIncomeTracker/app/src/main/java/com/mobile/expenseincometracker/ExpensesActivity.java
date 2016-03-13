package com.mobile.expenseincometracker;

import android.app.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.mobile.expenseincometracker.model.ExpenseIncomeBreakdownModel;
import com.mobile.expenseincometracker.model.ExpenseIncomeModel;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.os.Handler;

import com.mobile.expenseincometracker.custom.ExpenseIncomeBreakdownListViewAdapter;

import android.widget.ListView;

import com.mobile.expenseincometracker.database.ExpenseIncomeTrackerSqliteDatabaseHelper;

import java.util.List;

import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

public class ExpensesActivity extends Activity {
	
    private ExpenseIncomeBreakdownListViewAdapter breakdownAdapter; 
    private static ArrayList<ExpenseIncomeBreakdownModel> breakdownList;
    private ListView breakdownListView;
    private static double exp;
    private ExpenseIncomeTrackerSqliteDatabaseHelper expenseIncomeSqliteHelper;
    private static TextView expenseTv;
    private double inc;
    private String ingredient;
    private static Handler mHandler;
    private int totQty;
    private InputMethodManager imm;
    
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.breakdown_layout);
        expenseIncomeSqliteHelper = new ExpenseIncomeTrackerSqliteDatabaseHelper(getApplicationContext());
        breakdownList = new ArrayList<ExpenseIncomeBreakdownModel>();
        exp = 0;
        inc = 0.0;
        totQty = 0;
        inc = getIntent().getDoubleExtra("inc", 0.0);
        totQty = getIntent().getIntExtra("totQty", 0);
        final Spinner ingredientsSpinner = (Spinner)findViewById(R.id.spinnerIngredients);
        final String[] items = {"Flour",
        "Milk",
        "Sugar",
        "Eggs",
        "Chocolate",
        "Starch",
        "Syrup",
        "Salt",
        "Cocoa",
        "Butter",
        "Vanilla"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        ingredientsSpinner.setAdapter(adapter);
        ingredientsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            
        	@Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ingredient = parent.getSelectedItem().toString();
            }
            
        	@Override
            public void onNothingSelected(AdapterView<?> parent) {
                ingredient = items[0];
            }
        });
        
        breakdownListView = (ListView)findViewById(R.id.expenseListView);
        breakdownAdapter = new ExpenseIncomeBreakdownListViewAdapter(breakdownList, getApplicationContext());
        breakdownListView.setAdapter(breakdownAdapter);
        expenseTv = (TextView)findViewById(R.id.tvExpenseValue);
        final EditText priceEditText = (EditText)findViewById(R.id.priceEditText);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        Button expenseAddBtn = (Button)findViewById(R.id.addBtn);
        expenseAddBtn.setOnClickListener(new View.OnClickListener() {
            
        	@Override
            public void onClick(View v) {
                if(priceEditText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Cannot save. Missing price!", Toast.LENGTH_SHORT).show();
                    
                } else {
                	ExpenseIncomeBreakdownModel breakdown = new ExpenseIncomeBreakdownModel();
                    breakdown.setIngredient(ingredient);
                    breakdown.setPrice(priceEditText.getText().toString());
                    breakdownList.add(0, breakdown);
                    double e = Double.parseDouble(breakdown.getPrice());
                    exp = exp + e;
                    expenseTv.setText(String.valueOf(exp));
                    Message.obtain(mHandler, 2).sendToTarget();
                    priceEditText.setText("");
                    ingredientsSpinner.setSelection(0);
                    imm.hideSoftInputFromWindow(priceEditText.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
                
            }
        });
        Button expenseSaveBtn = (Button)findViewById(R.id.expenseSaveMainBtn);
        expenseSaveBtn.setOnClickListener(new View.OnClickListener() {
            
        	@Override
            public void onClick(View v) {
                if(exp > 0.0) {
                    Calendar cal = Calendar.getInstance();
                    String date = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(cal.getTime()).toString();
                    ExpenseIncomeModel ei = new ExpenseIncomeModel();
                    ei.setDate(date);
                    ei.setExpense(Double.valueOf(exp));
                    ei.setIncome(Double.valueOf((inc - exp)));
                    ei.setQuantity(Integer.valueOf(totQty));
                    expenseIncomeSqliteHelper.insertToTable(ei);
                    HashMap<String, Double> data = new HashMap<String, Double>();
                    data.put("income", ei.getIncome());
                    data.put("expenses", ei.getExpense());
                    Message.obtain(MainActivity.mHandler, 3, data).sendToTarget();
                    finish();
                    
                } else {
                	Toast.makeText(getApplicationContext(), "Cannot save expenses if less than or equal to 0", Toast.LENGTH_SHORT).show();
                }
                
            }
        });
        mHandler = new Handler() {
            
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 2:
                    {
                        ExpenseIncomeBreakdownListViewAdapter breakdownAdapter = new ExpenseIncomeBreakdownListViewAdapter(breakdownList, getApplicationContext());
                        breakdownListView.invalidate();
                        breakdownListView.setAdapter(breakdownAdapter);
                        breakdownAdapter.notifyDataSetChanged();
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
        breakdownAdapter = null;
        breakdownList = null;
        breakdownListView = null;
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        breakdownAdapter = null;
        breakdownList = null;
        breakdownListView = null;
    }
    
    @SuppressWarnings("unchecked")
	public static void refreshUI_Expenses(String priceValDeduct, List<?> o, String method) {
        breakdownList = (ArrayList<ExpenseIncomeBreakdownModel>) o;
        String newValRem = priceValDeduct.substring((priceValDeduct.indexOf("P") + 1), priceValDeduct.length());
        String oldValRem = expenseTv.getText().toString();
        oldValRem = oldValRem.substring((oldValRem.indexOf("P") + 1), oldValRem.length());
        if(method.equalsIgnoreCase("deduct")) {
            exp = (Double.parseDouble(oldValRem) - Double.parseDouble(newValRem));
            if(exp <= 0) {
                exp = 0;
            }
        }
        expenseTv.setText(String.valueOf(exp));
        Message.obtain(mHandler, 2).sendToTarget();
    }
}
