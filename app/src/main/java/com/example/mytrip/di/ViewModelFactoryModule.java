package com.example.mytrip.di;

import androidx.lifecycle.ViewModelProvider;

import com.example.mytrip.app.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelFactoryModule {
    @Binds
    public abstract ViewModelProvider.Factory bindViewModelProvider(ViewModelProviderFactory viewModelProviderFactory);
}
