package com.example.mytrip.data.model;

public class Expense {
    private String id;
    private String expenseName;
    private String expenseAmount;
    private String date;

    public Expense() {
    }

    public Expense(String id, String expenseName, String expenseAmount, String date) {
        this.id = id;
        this.expenseName = expenseName;
        this.expenseAmount = expenseAmount;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }

    public String getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(String expenseAmount) {
        this.expenseAmount = expenseAmount;
    }
}
