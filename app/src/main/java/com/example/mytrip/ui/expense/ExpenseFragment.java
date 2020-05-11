package com.example.mytrip.ui.expense;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytrip.R;
import com.example.mytrip.adapter.ExpenseAdapter;
import com.example.mytrip.app.ViewModelProviderFactory;
import com.example.mytrip.data.model.Expense;
import com.example.mytrip.databinding.FragmentExpenseBinding;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class ExpenseFragment extends DaggerFragment {
    private static final String TAG = "sayed";
    @Inject
    ViewModelProviderFactory providerFactory;
    @Inject
    ExpenseAdapter adapter;
    private ExpenseViewModel expenseViewModel;
    private FragmentExpenseBinding binding;
    private RecyclerView recyclerView;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExpenseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        expenseViewModel = new ViewModelProvider(this, providerFactory).get(ExpenseViewModel.class);
        subscribeObserver();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navController.popBackStack();
                    }
                });
        binding.floatingActionButton.setOnClickListener(clicked -> {
            openDialog();
        });
    }

    private void subscribeObserver() {
        expenseViewModel.observeExpenseList().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        binding.progressBar.setVisibility(View.VISIBLE);
                        break;
                    case ERROR:
                        binding.progressBar.setVisibility(View.GONE);
                        binding.emptyText.setVisibility(View.VISIBLE);
                        showSnackBar("Something went wrong. Please try again latter.");
                        break;
                    case SUCCESS:
                        if (resource.data != null) {
                            if (!resource.data.isEmpty()) {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.emptyText.setVisibility(View.GONE);
                                adapter.setData(resource.data, expenseViewModel);
                            } else {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.emptyText.setVisibility(View.VISIBLE);
                                adapter.setData(null, expenseViewModel);
                            }
                        } else binding.emptyText.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        expenseViewModel.observeAddingResult().removeObservers(getViewLifecycleOwner());
        expenseViewModel.observeAddingResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        break;
                    case ERROR:
                        showSnackBar("Something went wrong. Please try again latter.");
                        expenseViewModel.clearObserver();
                        break;
                    case SUCCESS:
                        assert (resource.data != null);
                        if (resource.data) {
                            showSnackBar("Adding successful.");
                            expenseViewModel.clearObserver();
                        }
                        break;
                }
            }
        });

        expenseViewModel.observeDeletingResult().removeObservers(getViewLifecycleOwner());
        expenseViewModel.observeDeletingResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        break;
                    case ERROR:
                        showSnackBar("Something went wrong. Please try again latter.");
                        expenseViewModel.clearObserver();
                        break;
                    case SUCCESS:
                        assert (resource.data != null);
                        if (resource.data) {
                            showSnackBar("Deleting successful.");
                            expenseViewModel.clearObserver();
                        }
                        break;
                }
            }
        });
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.expense_dialog, null);
        final EditText expenseNameET = view.findViewById(R.id.destination);
        final EditText expenseAmountET = view.findViewById(R.id.budget);
        builder.setView(view)
                .setTitle(R.string.expense_entry)
                .setPositiveButton("Add", (dialogInterface, i) -> {
                    String expenseName = expenseNameET.getText().toString();
                    String expenseAmount = expenseAmountET.getText().toString();
                    String id = UUID.randomUUID().toString();
                    String date = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
                    Expense expense = new Expense(id, expenseName, expenseAmount, date);
                    expenseViewModel.addExpense(expense);
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                })
                .setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showSnackBar(String message) {
        Snackbar.make(this.requireView(), message, Snackbar.LENGTH_SHORT).show();
    }
}
