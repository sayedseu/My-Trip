package com.example.mytrip.di;

import com.example.mytrip.di.auth.AuthFragmentBuildersModule;
import com.example.mytrip.di.auth.AuthViewModelModule;
import com.example.mytrip.di.main.MainFragmentBuildersModule;
import com.example.mytrip.di.main.MainModule;
import com.example.mytrip.di.main.MainViewModelModule;
import com.example.mytrip.ui.activity.AuthActivity;
import com.example.mytrip.ui.activity.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModules {

    @ContributesAndroidInjector(
            modules = {
                    AuthViewModelModule.class,
                    AuthFragmentBuildersModule.class
            }
    )
    abstract AuthActivity authActivity();

    @ContributesAndroidInjector(
            modules = {
                    MainFragmentBuildersModule.class,
                    MainViewModelModule.class,
                    MainModule.class
            }
    )
    abstract MainActivity mainActivity();

}
