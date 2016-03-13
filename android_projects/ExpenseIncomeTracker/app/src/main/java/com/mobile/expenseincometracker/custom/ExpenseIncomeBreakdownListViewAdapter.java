package com.mobile.expenseincometracker.custom;

import android.widget.BaseAdapter;
import java.util.List;
import com.mobile.expenseincometracker.custom.interfaces.ExpenseIncomeCustomListViewInterface;
import android.content.Context;
import com.mobile.expenseincometracker.model.ExpenseIncomeBreakdownModel;
import java.util.ArrayList;
import android.view.View;
import android.view.ViewGroup;

public class ExpenseIncomeBreakdownListViewAdapter extends BaseAdapter {
	
    final List<ExpenseIncomeCustomListViewInterface> expenseIncomeRowInterface;
    
    public ExpenseIncomeBreakdownListViewAdapter(List<ExpenseIncomeBreakdownModel> breakdown, Context ctx) {
        expenseIncomeRowInterface = new ArrayList<ExpenseIncomeCustomListViewInterface>();
        for(ExpenseIncomeBreakdownModel brkdwn : breakdown) {
        	if(brkdwn.getIngredient() != null) {
                expenseIncomeRowInterface.add(new ExpenseIncomeCustomListViewBreakdownRow(ctx, brkdwn, breakdown));
            }
        }
    }
    
    public int getItemViewType(int position) {
        return expenseIncomeRowInterface.get(position).getViewType();
    }
    
    public int getCount() {
        return expenseIncomeRowInterface.size();
    }
    
    public Object getItem(int position) {
        return Integer.valueOf(position);
    }
    
    public long getItemId(int position) {
        return (long)position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        return expenseIncomeRowInterface.get(position).getView(convertView, position);
    }
}
