package com.example.mytrip.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mytrip.app.Resource;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

public class LoginViewModel extends ViewModel {
    private final FirebaseAuth firebaseAuth;
    private MutableLiveData<Resource<Boolean>> loginResult = new MutableLiveData<>();

    @Inject
    LoginViewModel(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }


    public void login(String email, String password) {
        loginResult.setValue(Resource.loading(null));
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (firebaseAuth.getCurrentUser() != null) {
                            loginResult.setValue(Resource.success(true));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    loginResult.setValue(Resource.error("", null));
                });
    }

    LiveData<Resource<Boolean>> observeLoginResult() {
        return loginResult;
    }

    void clearObserver() {
        loginResult.setValue(null);
    }
}
