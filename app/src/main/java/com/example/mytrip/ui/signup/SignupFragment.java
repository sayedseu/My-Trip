package com.example.mytrip.ui.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mytrip.app.ViewModelProviderFactory;
import com.example.mytrip.databinding.FragmentSignupBinding;
import com.example.mytrip.utils.NetworkInfo;
import com.example.mytrip.utils.Validator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class SignupFragment extends DaggerFragment {
    private static final String TAG = "sayed";
    @Inject
    ViewModelProviderFactory providerFactory;
    @Inject
    FirebaseAuth firebaseAuth;
    private SignupViewModel signupViewModel;
    private FragmentSignupBinding binding;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        signupViewModel = new ViewModelProvider(requireActivity(), providerFactory).get(SignupViewModel.class);
        subscribeObserver();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navController.popBackStack();
                    }
                });

        binding.signupToLoginButton.setOnClickListener(clicked -> {
            navController.popBackStack();
        });

        binding.signupButton.setOnClickListener(clicked -> {
            if (isValid()) {
                if (NetworkInfo.hasNetwork(requireContext())) {
                    signupViewModel.signup(
                            binding.signupName.getText().toString(),
                            binding.signupEmail.getText().toString(),
                            binding.signupPassword.getText().toString()
                    );
                    binding.signupButton.setEnabled(false);
                } else showSnackBar("No internet connection.");
            }
        });
    }

    private void subscribeObserver() {
        signupViewModel.observeSignupResult().removeObservers(getViewLifecycleOwner());
        signupViewModel.observeSignupResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        binding.signupProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case ERROR:
                        binding.signupButton.setEnabled(true);
                        binding.signupProgressBar.setVisibility(View.GONE);
                        signupViewModel.clearObserver();
                        showSnackBar("Something went wrong. Please try again latter");
                        break;
                    case SUCCESS:
                        binding.signupButton.setEnabled(true);
                        binding.signupProgressBar.setVisibility(View.GONE);
                        if (resource.data) {
                            signupViewModel.clearObserver();
                            navController.popBackStack();
                        }
                        break;
                }
            }
        });
    }

    private boolean isValid() {
        boolean flag = true;
        if (!Validator.isNamedValid(binding.signupName.getText().toString())) {
            flag = false;
            binding.signupName.setError("Invalid");
        }
        if (!Validator.isUserEmailValid(binding.signupEmail.getText().toString())) {
            flag = false;
            binding.signupEmail.setError("Invalid Email");
        }
        if (!Validator.isPasswordValid(binding.signupPassword.getText().toString())) {
            flag = false;
            binding.signupPassword.setError("Length must be < 5");
        }
        return flag;
    }

    private void showSnackBar(String message) {
        Snackbar.make(this.requireView(), message, Snackbar.LENGTH_SHORT).show();
    }

}
