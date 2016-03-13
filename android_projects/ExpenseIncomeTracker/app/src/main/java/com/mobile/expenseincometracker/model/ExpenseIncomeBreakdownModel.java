package com.mobile.expenseincometracker.model;

import java.io.Serializable;

public class ExpenseIncomeBreakdownModel implements Serializable {
	
	private String ingredient;
    private String price;
    private static final long serialVersionUID = 0x1L;
    
    public String getIngredient() {
        return ingredient;
    }
    
    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
    
    public String getPrice() {
        return price;
    }
    
    public void setPrice(String price) {
        this.price = price;
    }
} 
