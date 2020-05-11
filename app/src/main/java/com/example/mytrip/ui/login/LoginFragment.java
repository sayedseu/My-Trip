package com.example.mytrip.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mytrip.R;
import com.example.mytrip.app.ViewModelProviderFactory;
import com.example.mytrip.databinding.FragmentLoginBinding;
import com.example.mytrip.ui.activity.MainActivity;
import com.example.mytrip.utils.NetworkInfo;
import com.example.mytrip.utils.Validator;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class LoginFragment extends DaggerFragment {
    @Inject
    ViewModelProviderFactory providerFactory;
    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
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
        loginViewModel = new ViewModelProvider(this, providerFactory).get(LoginViewModel.class);
        subscribeObserver();

        binding.loginToSignUpButton.setOnClickListener(clicked -> {
            navController.navigate(R.id.action_loginFragment_to_signupFragment);
        });

        binding.loginButton.setOnClickListener(clicked -> {
            if (isValid()) {
                if (NetworkInfo.hasNetwork(requireContext())) {
                    loginViewModel.login(
                            binding.loginEmail.getText().toString(),
                            binding.loginPassword.getText().toString()
                    );
                    binding.loginButton.setEnabled(false);
                } else showSnackBar("No internet connection.");
            }
        });
    }

    private void subscribeObserver() {
        loginViewModel.observeLoginResult().removeObservers(getViewLifecycleOwner());
        loginViewModel.observeLoginResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        binding.loginProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case ERROR:
                        binding.loginButton.setEnabled(true);
                        binding.loginProgressBar.setVisibility(View.GONE);
                        loginViewModel.clearObserver();
                        showSnackBar("Invalid email or password.");
                        break;
                    case SUCCESS:
                        binding.loginButton.setEnabled(true);
                        binding.loginProgressBar.setVisibility(View.GONE);
                        if (resource.data) {
                            startActivity(new Intent(requireActivity(), MainActivity.class));
                            requireActivity().finish();
                            loginViewModel.clearObserver();
                        }
                        break;
                }
            }
        });
    }

    private boolean isValid() {
        boolean flag = true;
        if (!Validator.isUserEmailValid(binding.loginEmail.getText().toString())) {
            flag = false;
            binding.loginEmail.setError("Invalid");
        }
        if (!Validator.isPasswordValid(binding.loginPassword.getText().toString())) {
            flag = false;
            binding.loginPassword.setError("Invalid");
        }
        return flag;
    }

    private void showSnackBar(String message) {
        Snackbar.make(this.requireView(), message, Snackbar.LENGTH_SHORT).show();
    }

}
