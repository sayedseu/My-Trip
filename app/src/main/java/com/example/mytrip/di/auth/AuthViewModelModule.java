package com.example.mytrip.di.auth;

import androidx.lifecycle.ViewModel;

import com.example.mytrip.di.ViewModelKey;
import com.example.mytrip.ui.login.LoginViewModel;
import com.example.mytrip.ui.signup.SignupViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    public abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SignupViewModel.class)
    public abstract ViewModel bindSignupViewModel(SignupViewModel loginViewModel);
}
