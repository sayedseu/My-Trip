package com.example.mytrip.ui.events;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mytrip.app.ViewModelProviderFactory;
import com.example.mytrip.data.model.Event;
import com.example.mytrip.databinding.FragmentNewEventBinding;
import com.example.mytrip.utils.NetworkInfo;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class NewEventFragment extends DaggerFragment {
    private final Calendar calendar = Calendar.getInstance();
    @Inject
    ViewModelProviderFactory providerFactory;
    private FragmentNewEventBinding binding;
    private NavController navController;
    private NewEventViewModel newEventViewModel;
    private String fromDate = "", toDate = "";
    private int day = calendar.get(Calendar.DAY_OF_MONTH);
    private int month = calendar.get(Calendar.MONTH);
    private int year = calendar.get(Calendar.YEAR);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newEventViewModel = new ViewModelProvider(this, providerFactory).get(NewEventViewModel.class);
        navController = Navigation.findNavController(view);
        subscribeObserver();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
        binding.fromDateTV.setText("From: " + currentDate);
        binding.toDateTV.setText("To: " + currentDate);
        binding.saveEventBT.setOnClickListener(click -> {
            if (isValid()) {
                if (NetworkInfo.hasNetwork(requireContext())) {
                    Event event = new Event(
                            UUID.randomUUID().toString(),
                            binding.destinationET.getText().toString(),
                            binding.budgetET.getText().toString(),
                            fromDate,
                            toDate
                    );
                    newEventViewModel.saveEvent(event);
                    binding.saveEventBT.setEnabled(false);
                } else showSnackBar("No internet connection.");
            }
        });
        binding.fromDateBT.setOnClickListener(click -> {
            DatePickerDialog dialog = new DatePickerDialog(requireContext(), (datePicker, year, month, day) -> {
                fromDate = " " + day + "/" + month + "/" + year;
                binding.fromDateTV.setText("From: " + fromDate);
            }, year, month, day);
            dialog.show();
        });
        binding.toDateBT.setOnClickListener(click -> {
            DatePickerDialog dialog = new DatePickerDialog(requireContext(), (datePicker, year, month, day) -> {
                toDate = " " + day + "/" + month + "/" + year;
                binding.toDateTV.setText("To: " + toDate);
            }, year, month, day);
            dialog.show();
        });
    }

    private void subscribeObserver() {
        newEventViewModel.observeSavingResult().removeObservers(getViewLifecycleOwner());
        newEventViewModel.observeSavingResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        binding.progressBar.setVisibility(View.VISIBLE);
                        break;
                    case ERROR:
                        binding.progressBar.setVisibility(View.GONE);
                        showSnackBar("Something went wrong. Please try again later.");
                        binding.saveEventBT.setEnabled(true);
                        newEventViewModel.clearObserver();
                        break;
                    case SUCCESS:
                        binding.progressBar.setVisibility(View.GONE);
                        binding.saveEventBT.setEnabled(true);
                        newEventViewModel.clearObserver();
                        assert (resource.data != null);
                        if (resource.data) {
                            navController.popBackStack();
                        }
                        break;
                }
            }
        });
    }

    private boolean isValid() {
        boolean flag = true;
        if (fromDate.isEmpty() || toDate.isEmpty()) {
            flag = false;
            showSnackBar("Please select a date.");
        }
        if (binding.destinationET.getText().toString().isEmpty()) {
            flag = false;
            binding.destinationET.setError("Required");
        }
        if (binding.budgetET.getText().toString().isEmpty()) {
            flag = false;
            binding.budgetET.setError("Required");
        }
        return flag;
    }

    private void showSnackBar(String message) {
        Snackbar.make(this.requireView(), message, Snackbar.LENGTH_SHORT).show();
    }
}
