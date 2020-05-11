package com.example.mytrip.di.main;

import androidx.lifecycle.ViewModel;

import com.example.mytrip.di.ViewModelKey;
import com.example.mytrip.ui.events.EventViewModel;
import com.example.mytrip.ui.events.NewEventViewModel;
import com.example.mytrip.ui.expense.ExpenseViewModel;
import com.example.mytrip.ui.home.HomeViewModel;
import com.example.mytrip.ui.moment.MomentViewModel;
import com.example.mytrip.ui.moment.NewMomentViewModel;
import com.example.mytrip.ui.weather.WeatherViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    public abstract ViewModel bindsHomeViewModel(HomeViewModel homeViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ExpenseViewModel.class)
    public abstract ViewModel bindExpenseViewModel(ExpenseViewModel expenseViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(EventViewModel.class)
    public abstract ViewModel bindEventFragment(EventViewModel eventViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NewEventViewModel.class)
    public abstract ViewModel bindNewEventFragment(NewEventViewModel newEventViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MomentViewModel.class)
    public abstract ViewModel bindMomentFragment(MomentViewModel momentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NewMomentViewModel.class)
    public abstract ViewModel bindNewMomentFragment(NewMomentViewModel newMomentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(WeatherViewModel.class)
    public abstract ViewModel bindWeatherViewModel(WeatherViewModel weatherViewModel);
}
