package com.example.mytrip.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mytrip.R;
import com.example.mytrip.databinding.ActivityAuthBinding;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class AuthActivity extends DaggerAppCompatActivity {
    @Inject
    FirebaseAuth firebaseAuth;
    private ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(AuthActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
