package com.example.mytrip.ui.home;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "sayed";

    @Inject
    public HomeViewModel() {
        Log.i(TAG, "HomeViewModel: ");
    }

}