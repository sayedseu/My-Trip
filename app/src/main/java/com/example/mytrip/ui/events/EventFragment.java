package com.example.mytrip.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mytrip.R;
import com.example.mytrip.adapter.EventsAdapter;
import com.example.mytrip.app.ViewModelProviderFactory;
import com.example.mytrip.databinding.FragmentEventBinding;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class EventFragment extends DaggerFragment {
    @Inject
    ViewModelProviderFactory providerFactory;
    @Inject
    EventsAdapter adapter;
    private FragmentEventBinding binding;
    private EventViewModel eventViewModel;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventViewModel = new ViewModelProvider(this, providerFactory).get(EventViewModel.class);
        navController = Navigation.findNavController(view);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        subscribeObserver();
        binding.floatingActionButton.setOnClickListener(click -> {
            navController.navigate(R.id.action_eventFragment_to_newEventFragment);
        });
    }

    private void subscribeObserver() {
        eventViewModel.retrieveEventList().observe(getViewLifecycleOwner(), resource -> {
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
                                adapter.setData(resource.data, eventViewModel);
                            } else {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.emptyText.setVisibility(View.VISIBLE);
                                adapter.setData(null, eventViewModel);
                            }
                        } else binding.emptyText.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        eventViewModel.observeDeletingResult().removeObservers(getViewLifecycleOwner());
        eventViewModel.observeDeletingResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        break;
                    case ERROR:
                        showSnackBar("Something went wrong. Please try again latter.");
                        eventViewModel.clearObserver();
                        break;
                    case SUCCESS:
                        assert (resource.data != null);
                        if (resource.data) {
                            showSnackBar("Deleting successful.");
                            eventViewModel.clearObserver();
                        }
                        break;
                }
            }
        });
    }

    private void showSnackBar(String message) {
        Snackbar.make(this.requireView(), message, Snackbar.LENGTH_SHORT).show();
    }
}
