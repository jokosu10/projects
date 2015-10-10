package com.mobile.expenseincometracker.model;

import java.io.Serializable;

public class ExpenseIncomeOrdersModel implements Serializable {
	
	private String cakeType;
    private int picId;
    private String price;
    private String quantity;
    private static final long serialVersionUID = 0x1L;
    private String size;
    
    public int getPicId() {
        return picId;
    }
    
    public void setPicId(int picId) {
        this.picId = picId;
    }
    
    public String getCakeType() {
        return cakeType;
    }
    
    public void setCakeType(String cakeType) {
        this.cakeType = cakeType;
    }
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public String getQuantity() {
        return quantity;
    }
    
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    
    public String getPrice() {
        return price;
    }
    
    public void setPrice(String price) {
        this.price = price;
    }
}
