package com.example.mytrip.di.auth;

import com.example.mytrip.ui.login.LoginFragment;
import com.example.mytrip.ui.signup.SignupFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class AuthFragmentBuildersModule {
    @ContributesAndroidInjector
    public abstract LoginFragment contributeLoginFragment();

    @ContributesAndroidInjector
    public abstract SignupFragment contributeSignupFragment();
}
