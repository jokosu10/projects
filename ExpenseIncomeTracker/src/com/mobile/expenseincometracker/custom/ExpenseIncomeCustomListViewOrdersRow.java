package com.mobile.expenseincometracker.custom;

import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import com.mobile.expenseincometracker.OrdersActivity;
import com.mobile.expenseincometracker.R;
import com.mobile.expenseincometracker.custom.interfaces.ExpenseIncomeCustomListViewInterface;
import android.content.Context;
import com.mobile.expenseincometracker.model.ExpenseIncomeOrdersModel;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExpenseIncomeCustomListViewOrdersRow implements ExpenseIncomeCustomListViewInterface {
    
	private Context contxt;
    private String currValQty;
    private ExpenseIncomeOrdersModel eIO;
    private List<ExpenseIncomeOrdersModel> eIOList;
    private LayoutInflater listViewLayoutInflater;
    private String currValPrice = "";
    
    public ExpenseIncomeCustomListViewOrdersRow(Context c, ExpenseIncomeOrdersModel eIO, List<ExpenseIncomeOrdersModel> eIOList) {
        this.contxt = c;
        this.eIO = eIO;
        this.eIOList = eIOList;
        this.listViewLayoutInflater = (LayoutInflater) contxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public View getView(View convertView, final int pos) {
        ViewGroup viewGroup = null;
        viewGroup = (ViewGroup) listViewLayoutInflater.inflate(R.layout.orders_row_layout, viewGroup);
        TextView pic = (TextView)viewGroup.findViewById(R.id.textViewOrderPic);
        TextView name = (TextView)viewGroup.findViewById(R.id.textViewOrderName);
        EditText qty = (EditText)viewGroup.findViewById(R.id.editTextOrderQty);
        EditText size = (EditText)viewGroup.findViewById(R.id.editTextOrderSize);
        EditText price = (EditText)viewGroup.findViewById(R.id.editTextOrderPrice);
        Button editBtn = (Button)viewGroup.findViewById(R.id.ordersEditBtn);
        Button deleteBtn = (Button)viewGroup.findViewById(R.id.ordersDeleteBtn);
        final ViewHolder holder = new ViewHolder(pic, name, qty, size, price, editBtn, deleteBtn);
        viewGroup.setTag(holder);
        ViewGroup view = viewGroup;
        if(eIO.getPicId() == 1) {
            holder.getPic().setBackground(contxt.getResources().getDrawable(R.drawable.cheese_cake));
        } else if(eIO.getPicId() == 2) {
            holder.getPic().setBackground(contxt.getResources().getDrawable(R.drawable.yema_cake));
        } else if(eIO.getPicId() == 3) {
            holder.getPic().setBackground(contxt.getResources().getDrawable(R.drawable.brownies_cake));
        }
        holder.getName().setText(eIO.getCakeType());
        holder.getQty().setText(eIO.getQuantity());
        holder.getSize().setText(eIO.getSize());
        holder.getPrice().setText(eIO.getPrice());
        holder.getPrice().getText().toString();
        holder.getPrice().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    currValPrice = holder.getPrice().getText().toString();
                    holder.getPrice().setText(currValPrice);
                }
            }
        });
        holder.getQty().getText().toString();
        holder.getQty().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            
        	public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    currValQty = holder.getQty().getText().toString();
                    holder.getQty().setText(currValQty);
                }
            }
        });
        holder.getSize().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    holder.getSize().setText(holder.getSize().getText().toString());
                }
            }
        });
        holder.getEdit().setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                Toast.makeText(contxt, "Saving entries as final", Toast.LENGTH_SHORT).show();
                holder.getName().setEnabled(false);
                holder.getPic().setEnabled(false);
                holder.getPrice().setEnabled(false);
                holder.getQty().setEnabled(false);
                holder.getSize().setEnabled(false);
            }
        });
        holder.getDelete().setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                eIOList.remove(pos);
                currValPrice = holder.getPrice().getText().toString();
                currValQty = holder.getQty().getText().toString();
                OrdersActivity.refreshUI_Orders(currValPrice, currValQty, eIOList, "deduct", "", "");
            }
        });
        return view;
    }
    
    public int getViewType() {
        return 0x0;
    }
    
    class ViewHolder {
        private Button delete;
        private Button edit;
        private TextView name;
        private TextView pic;
        private EditText price;
        private EditText qty;
        private EditText size;
        
        private ViewHolder(TextView pic, TextView orderName, EditText orderQty, EditText orderSize, EditText orderPrice, Button edit, Button delete) {
            setPic(pic);
            setName(orderName);
            setQty(orderQty);
            setSize(orderSize);
            setPrice(orderPrice);
            setEdit(edit);
            setDelete(delete);
        }
        
        public TextView getPic() {
            return pic;
        }
        
        public void setPic(TextView pic) {
            this.pic = pic;
        }
        
        public TextView getName() {
            return name;
        }
        
        public void setName(TextView name) {
            this.name = name;
        }
        
        public EditText getQty() {
            return qty;
        }
        
        public void setQty(EditText qty) {
            this.qty = qty;
        }
        
        public EditText getSize() {
            return size;
        }
        
        public void setSize(EditText size) {
            this.size = size;
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
