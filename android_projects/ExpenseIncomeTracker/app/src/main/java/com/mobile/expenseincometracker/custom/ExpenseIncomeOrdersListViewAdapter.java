package com.mobile.expenseincometracker.custom;

import android.widget.BaseAdapter;
import java.util.List;
import com.mobile.expenseincometracker.custom.interfaces.ExpenseIncomeCustomListViewInterface;
import android.content.Context;
import com.mobile.expenseincometracker.model.ExpenseIncomeOrdersModel;
import java.util.ArrayList;
import android.view.View;
import android.view.ViewGroup;

public class ExpenseIncomeOrdersListViewAdapter extends BaseAdapter {
    List<ExpenseIncomeCustomListViewInterface> expenseIncomeRowInterface = null;
    
    public ExpenseIncomeOrdersListViewAdapter(List<ExpenseIncomeOrdersModel> orders, Context ctx) {
        expenseIncomeRowInterface = new ArrayList<ExpenseIncomeCustomListViewInterface>();
        for(ExpenseIncomeOrdersModel order: orders) {
        	if(order.getCakeType() != null) {
                expenseIncomeRowInterface.add(new ExpenseIncomeCustomListViewOrdersRow(ctx, order, orders));
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
