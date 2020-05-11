package com.example.mytrip.ui.moment;

import android.os.Bundle;
import android.util.Log;
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
import com.example.mytrip.adapter.MomentAdapter;
import com.example.mytrip.app.ViewModelProviderFactory;
import com.example.mytrip.databinding.FragmentMomentBinding;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class MomentFragment extends DaggerFragment {
    @Inject
    ViewModelProviderFactory providerFactory;
    @Inject
    MomentAdapter adapter;
    private MomentViewModel mViewModel;
    private FragmentMomentBinding binding;
    private NavController navController;

    public static MomentFragment newInstance() {
        return new MomentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMomentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setDrawingCacheEnabled(true);
        binding.recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this, providerFactory).get(MomentViewModel.class);
        subscribeObserver();

        binding.floatingActionButton.setOnClickListener(click -> {
            navController.navigate(R.id.action_momentFragment_to_newMomentFragment);
        });
    }

    private void subscribeObserver() {
        mViewModel.observeMomentList().observe(getViewLifecycleOwner(), listResource -> {
            if (listResource != null) {
                switch (listResource.status) {
                    case LOADING:
                        binding.progressBar.setVisibility(View.VISIBLE);
                        break;
                    case ERROR:
                        binding.progressBar.setVisibility(View.GONE);
                        binding.emptyText.setVisibility(View.VISIBLE);
                        break;
                    case SUCCESS:
                        binding.progressBar.setVisibility(View.GONE);
                        if (listResource.data.isEmpty()) {
                            adapter.setData(null);
                            binding.emptyText.setVisibility(View.VISIBLE);
                            Log.i("sayed", "subscribeObserver: empty");
                        } else {
                            Log.i("sayed", "subscribeObserver: not empty");
                            binding.emptyText.setVisibility(View.GONE);
                            adapter.setData(listResource.data);
                        }
                        break;
                }
            }
        });
    }

}
