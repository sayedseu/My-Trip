package com.example.mytrip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytrip.R;
import com.example.mytrip.data.model.Expense;
import com.example.mytrip.ui.expense.ExpenseViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import javax.inject.Inject;


public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenseList;
    private ExpenseViewModel expenseViewModel;

    @Inject
    public ExpenseAdapter() {
    }

    public void setData(List<Expense> expenseList, ExpenseViewModel expenseViewModel) {
        this.expenseList = expenseList;
        this.expenseViewModel = expenseViewModel;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        if (expenseList != null) {
            Expense expense = expenseList.get(position);
            holder.expenseName.setText(expense.getExpenseName());
            holder.expenseAmount.setText("\u09F3 " + expense.getExpenseAmount() + "/-");
            holder.expenseDate.setText(expense.getDate());
            holder.fab.setOnClickListener(click -> {
                expenseViewModel.deleteItem(expense.getId());
            });
        }
    }

    @Override
    public int getItemCount() {
        if (expenseList != null) return expenseList.size();
        else return 0;
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private TextView expenseName;
        private TextView expenseAmount;
        private TextView expenseDate;
        private FloatingActionButton fab;

        ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseName = itemView.findViewById(R.id.destination);
            expenseAmount = itemView.findViewById(R.id.budget);
            expenseDate = itemView.findViewById(R.id.date);
            fab = itemView.findViewById(R.id.deleteButton);
        }
    }
}
