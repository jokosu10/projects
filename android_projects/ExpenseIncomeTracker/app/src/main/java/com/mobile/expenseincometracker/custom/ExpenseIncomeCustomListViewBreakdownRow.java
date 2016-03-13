package com.mobile.expenseincometracker.custom;

import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import com.mobile.expenseincometracker.ExpensesActivity;
import com.mobile.expenseincometracker.R;
import com.mobile.expenseincometracker.custom.interfaces.ExpenseIncomeCustomListViewInterface;
import android.content.Context;
import com.mobile.expenseincometracker.model.ExpenseIncomeBreakdownModel;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExpenseIncomeCustomListViewBreakdownRow implements ExpenseIncomeCustomListViewInterface {
    
	private Context contxt;
    private ExpenseIncomeBreakdownModel eIB;
    private List<ExpenseIncomeBreakdownModel> eIBList;
    private LayoutInflater listViewLayoutInflater;
    
    public ExpenseIncomeCustomListViewBreakdownRow(Context c, ExpenseIncomeBreakdownModel eIB, List<ExpenseIncomeBreakdownModel> eIBList) {
        contxt = c;
        this.eIB = eIB;
        listViewLayoutInflater = (LayoutInflater) contxt.getSystemService("layout_inflater");
        this.eIBList = eIBList;
    }
    
    public View getView(View convertView, final int pos) {
    	
        ViewGroup viewGroup = null;
        viewGroup = (ViewGroup) listViewLayoutInflater.inflate(R.layout.expense_row_layout, viewGroup);
        TextView name = (TextView)viewGroup.findViewById(R.id.rowExpenseName);
        EditText price = (EditText)viewGroup.findViewById(R.id.rowExpensePrice);
        Button editBtn = (Button)viewGroup.findViewById(R.id.buttonExpenseEdit);
        Button deleteBtn = (Button)viewGroup.findViewById(R.id.buttonExpenseDelete);
        final ViewHolder holder = new ViewHolder(name, price, editBtn, deleteBtn);
        viewGroup.setTag(holder);
        ViewGroup view = viewGroup;
        holder.getName().setText(eIB.getIngredient());
        holder.getPrice().setText(eIB.getPrice());
        holder.getPrice().setOnFocusChangeListener(new View.OnFocusChangeListener() {
           
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    holder.getPrice().setText(holder.getPrice().getText().toString());
                }
            }
        });
        final String pToDeduct = holder.getPrice().getText().toString();
        holder.getDelete().setOnClickListener(new View.OnClickListener() {
            
           public void onClick(View v) {
                eIBList.remove(pos);
                ExpensesActivity.refreshUI_Expenses(pToDeduct, eIBList, "deduct");
            }
        });
        holder.getEdit().setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
            	
                Toast.makeText(contxt, "Saving entries as final", Toast.LENGTH_SHORT).show();
                holder.getPrice().setEnabled(false);
            }
        });
        return view;
    }
    
    public int getViewType() {
        return 0;
    }
    
    class ViewHolder {
        private Button delete;
        private Button edit;
        private TextView name;
        private EditText price;
        
        private ViewHolder(TextView expenseName, EditText expensePrice, Button edit, Button delete) {
            setName(expenseName);
            setPrice(expensePrice);
            setEdit(edit);
            setDelete(delete);
        }
        
        public TextView getName() {
            return name;
        }
        
        public void setName(TextView name) {
            this.name = name;
        }
        
        public EditText getPrice() {
            return price;
        }
        
        public void setPrice(EditText price) {
            this.price = price;
        }
        
        public Button getEdit() {
            return edit;
        }
        
        public void setEdit(Button edit) {
            this.edit = edit;
        }
        
        public Button getDelete() {
            return delete;
        }
        
        public void setDelete(Button delete) {
            this.delete = delete;
        }
    }
}
