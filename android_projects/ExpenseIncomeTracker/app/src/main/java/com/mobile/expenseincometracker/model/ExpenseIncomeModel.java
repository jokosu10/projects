package com.mobile.expenseincometracker.model;

import java.io.Serializable;

public class ExpenseIncomeModel implements Serializable {

    private String date;
    private Double expense;
    private Double income;
    private Integer quantity;
    private static final long serialVersionUID = 0x1L;
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Double getExpense() {
        return expense;
    }
    
    public void setExpense(Double expense) {
        this.expense = expense;
    }
    
    public Double getIncome() {
        return income;
    }
    
    public void setIncome(Double income) {
        this.income = income;
    }
}
