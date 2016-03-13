package com.iloilo.iloilotaxidriverapp.custom;

import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.content.Context;
import com.iloilo.iloilotaxidriverapp.model.HireRequestModel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HireRequestAdapter extends BaseAdapter {
    
	ArrayList<HireRequestInterface> hireRequestInterfaceList = new ArrayList<HireRequestInterface>();
    
    public HireRequestAdapter(ArrayList<HireRequestModel> hireRequestModelList, Context ctx) {
        
        for(HireRequestModel h : hireRequestModelList) {
        	 hireRequestInterfaceList.add(new HireRequestRow(h, LayoutInflater.from(ctx), ctx));
        }
       
    }
    
    public int getItemViewType(int position) {
        return hireRequestInterfaceList.get(position).getViewType();
    }
    
    public int getCount() {
        return hireRequestInterfaceList.size();
    }
    
    public Object getItem(int position) {
        return Integer.valueOf(position);
    }
    
    public long getItemId(int position) {
        return (long)position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        return hireRequestInterfaceList.get(position).getView(convertView, position);
    }
}
