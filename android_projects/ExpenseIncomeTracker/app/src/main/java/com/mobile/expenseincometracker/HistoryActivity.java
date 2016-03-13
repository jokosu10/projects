package com.mobile.expenseincometracker;

import android.app.Activity;

import com.mobile.expenseincometracker.database.ExpenseIncomeTrackerSqliteDatabaseHelper;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import java.util.ArrayList;
import com.mobile.expenseincometracker.model.ExpenseIncomeModel;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

public class HistoryActivity extends Activity {
	
    private ExpenseIncomeTrackerSqliteDatabaseHelper expenseIncomeSqliteHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        expenseIncomeSqliteHelper = new ExpenseIncomeTrackerSqliteDatabaseHelper(getApplicationContext());
        TableLayout tblLayout = (TableLayout) findViewById(R.id.historyTableLayout);
        
        ArrayList<ExpenseIncomeModel> eiList = expenseIncomeSqliteHelper.getHistoricalData();
        
        TableRow tRowHeader = new TableRow(this);
        tRowHeader.setBackground(getResources().getDrawable(R.drawable.cellborder));
        TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f);  
        params.topMargin = 1;
        params.leftMargin = 2;
        params.bottomMargin = 1;
        params.rightMargin = 2;
        
        TextView tRowDate = new TextView(this);
        tRowDate.setText("    Date    ");
        tRowDate.setTextColor(Color.BLUE);
        tRowDate.setTextSize(12);
        tRowDate.setTypeface(Typeface.DEFAULT_BOLD);
        tRowDate.setGravity(Gravity.CENTER);
        tRowDate.setPadding(6, 3, 3, 6);
        tRowDate.setBackground(getResources().getDrawable(R.drawable.valuecellborder));
        tRowDate.setLayoutParams(params);
        tRowHeader.addView(tRowDate);
        
        TextView tRowQty = new TextView(this);
        tRowQty.setText("  Quantity  ");
        tRowQty.setTextColor(Color.BLUE);
        tRowQty.setTextSize(12);
        tRowQty.setTypeface(Typeface.DEFAULT_BOLD);
        tRowQty.setGravity(Gravity.CENTER);
        tRowQty.setPadding(6, 3, 3, 6);
        tRowQty.setBackground(getResources().getDrawable(R.drawable.valuecellborder));
        tRowQty.setLayoutParams(params);
        tRowHeader.addView(tRowQty);
        
        TextView tRowExpense = new TextView(this);
        tRowExpense.setText("  Expenses  ");
        tRowExpense.setTextColor(Color.BLUE);
        tRowExpense.setTextSize(12);
        tRowExpense.setTypeface(Typeface.DEFAULT_BOLD);
        tRowExpense.setGravity(Gravity.CENTER);
        tRowExpense.setPadding(6, 3, 3, 6);
        tRowExpense.setBackground(getResources().getDrawable(R.drawable.valuecellborder));
        tRowExpense.setLayoutParams(params);
        tRowHeader.addView(tRowExpense);
        
        TextView tRowIncome = new TextView(this);
        tRowIncome.setText("   Income   ");
        tRowIncome.setTextColor(Color.BLUE);
        tRowIncome.setTextSize(12);
        tRowIncome.setTypeface(Typeface.DEFAULT_BOLD);
        tRowIncome.setGravity(Gravity.CENTER);
        tRowIncome.setPadding(6, 3, 3, 6);
        tRowIncome.setBackground(getResources().getDrawable(R.drawable.valuecellborder));
        tRowIncome.setLayoutParams(params);
        tRowHeader.addView(tRowIncome);
        
        tblLayout.addView(tRowHeader);
           
        if((eiList != null) && (eiList.size() > 0)) {
            	
            for(ExpenseIncomeModel ei : eiList) {
                Log.e("", ei.getDate() + ", " + ei.getQuantity() + ", " + ei.getExpense() + ", " + ei.getIncome());
                TableRow tRow = new TableRow(this);
                tRow.setBackground(getResources().getDrawable(R.drawable.cellborder));
                TextView tRowDateVal = new TextView(this);
                tRowDateVal.setText(ei.getDate() + "  ");
                tRowDateVal.setTextColor(Color.BLUE);
                tRowDateVal.setTextSize(12);
                tRowDateVal.setTypeface(Typeface.DEFAULT_BOLD);
                tRowDateVal.setGravity(Gravity.CENTER);
                tRowDateVal.setPadding(6, 3, 3, 6);
                tRowDateVal.setBackground(getResources().getDrawable(R.drawable.valuecellborder));
                tRowDateVal.setLayoutParams(params);
                tRow.addView(tRowDateVal);
                
                TextView tRowQtyVal = new TextView(this);
                tRowQtyVal.setText(ei.getQuantity() + " ");
                tRowQtyVal.setTextColor(Color.BLUE);
                tRowQtyVal.setTextSize(12);
                tRowQtyVal.setTypeface(Typeface.DEFAULT_BOLD);
                tRowQtyVal.setGravity(Gravity.CENTER);
                tRowQtyVal.setPadding(6, 3, 3, 6);
                tRowQtyVal.setBackground(getResources().getDrawable(R.drawable.valuecellborder));
                tRowQtyVal.setLayoutParams(params);
                tRow.addView(tRowQtyVal);
                
                TextView tRowExpenseVal = new TextView(this);
                tRowExpenseVal.setText(ei.getExpense() + " ");
                tRowExpenseVal.setTextColor(Color.BLUE);
                tRowExpenseVal.setTextSize(12);
                tRowExpenseVal.setTypeface(Typeface.DEFAULT_BOLD);
                tRowExpenseVal.setGravity(Gravity.CENTER);
                tRowExpenseVal.setPadding(6, 3, 3, 6);
                tRowExpenseVal.setBackground(getResources().getDrawable(R.drawable.valuecellborder));
                tRowExpenseVal.setLayoutParams(params);
                tRow.addView(tRowExpenseVal);
                
                TextView tRowIncomeVal = new TextView(this);
                tRowIncomeVal.setText(ei.getIncome() + " ");
                tRowIncomeVal.setTextColor(Color.BLUE);
                tRowIncomeVal.setTextSize(12);
                tRowIncomeVal.setTypeface(Typeface.DEFAULT_BOLD);
                tRowIncomeVal.setGravity(Gravity.CENTER);
                tRowIncomeVal.setPadding(6, 3, 3, 6);
                tRowIncomeVal.setBackground(getResources().getDrawable(R.drawable.valuecellborder));
                tRowIncomeVal.setLayoutParams(params);
                tRow.addView(tRowIncomeVal);
                tblLayout.addView(tRow);
            }
            

        }
    }
}
