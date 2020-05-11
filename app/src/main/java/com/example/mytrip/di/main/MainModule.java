package com.example.mytrip.di.main;

import com.bumptech.glide.RequestManager;
import com.example.mytrip.adapter.DashboardAdapter;
import com.example.mytrip.adapter.EventsAdapter;
import com.example.mytrip.adapter.ExpenseAdapter;
import com.example.mytrip.adapter.MomentAdapter;
import com.example.mytrip.data.remote.ApiInterface;
import com.google.firebase.storage.FirebaseStorage;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public abstract class MainModule {

    @Provides
    public static DashboardAdapter dashboardAdapter(RequestManager requestManager) {
        return new DashboardAdapter(requestManager);
    }

    @Provides
    public static ExpenseAdapter expenseAdapter() {
        return new ExpenseAdapter();
    }

    @Provides
    public static EventsAdapter eventsAdapter() {
        return new EventsAdapter();
    }

    @Provides
    public static FirebaseStorage firebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    @Provides
    public static MomentAdapter momentAdapter(RequestManager requestManager) {
        return new MomentAdapter(requestManager);
    }

    @Provides
    public static ApiInterface apiInterface(Retrofit retrofit) {
        return retrofit.create(ApiInterface.class);
    }
}
