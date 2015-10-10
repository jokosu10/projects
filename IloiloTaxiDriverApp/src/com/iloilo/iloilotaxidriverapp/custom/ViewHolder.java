package com.iloilo.iloilotaxidriverapp.custom;

import android.widget.TextView;

class ViewHolder {
	
    private TextView addressTv;
    
    public ViewHolder(TextView t) {
        addressTv = t;
    }
    
    public TextView getAddressTv() {
        return addressTv;
    }
    
    public void setAddressTv(TextView addressTv) {
        this.addressTv = addressTv;
    }
}
