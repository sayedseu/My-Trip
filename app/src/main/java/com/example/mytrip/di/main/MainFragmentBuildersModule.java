package com.example.mytrip.di.main;

import com.example.mytrip.ui.events.EventFragment;
import com.example.mytrip.ui.events.NewEventFragment;
import com.example.mytrip.ui.expense.ExpenseFragment;
import com.example.mytrip.ui.home.HomeFragment;
import com.example.mytrip.ui.location.LocationFragment;
import com.example.mytrip.ui.moment.MomentFragment;
import com.example.mytrip.ui.moment.NewMomentFragment;
import com.example.mytrip.ui.weather.WeatherFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector
    public abstract HomeFragment contributeHomeFragment();

    @ContributesAndroidInjector
    public abstract ExpenseFragment contributeExpenseFragment();

    @ContributesAndroidInjector
    public abstract EventFragment eventFragment();

    @ContributesAndroidInjector
    public abstract NewEventFragment newEventFragment();

    @ContributesAndroidInjector
    public abstract MomentFragment momentFragment();

    @ContributesAndroidInjector
    public abstract NewMomentFragment newMomentFragment();

    @ContributesAndroidInjector
    public abstract WeatherFragment weatherFragment();

    @ContributesAndroidInjector
    public abstract LocationFragment locationFragment();
}
