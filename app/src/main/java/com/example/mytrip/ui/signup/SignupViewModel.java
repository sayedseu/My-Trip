package com.example.mytrip.ui.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mytrip.app.Resource;
import com.example.mytrip.utils.CollectionName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class SignupViewModel extends ViewModel {
    private final FirebaseAuth firebaseAuth;
    private final CollectionReference collectionReference;
    private MutableLiveData<Resource<Boolean>> signupResult = new MutableLiveData<>();

    @Inject
    SignupViewModel(FirebaseAuth firebaseAuth, CollectionReference collectionReference) {
        this.firebaseAuth = firebaseAuth;
        this.collectionReference = collectionReference;
    }

    void signup(String name, String email, String password) {
        signupResult.setValue(Resource.loading(null));
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (firebaseAuth.getCurrentUser() != null) {
                            Map<String, Object> userName = new HashMap<>();
                            userName.put("name", name);
                            collectionReference
                                    .document(firebaseAuth.getCurrentUser().getUid())
                                    .collection(CollectionName.PROFILE)
                                    .document()
                                    .set(userName)
                                    .addOnSuccessListener(aVoid -> {
                                        signupResult.setValue(Resource.success(true));
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    signupResult.setValue(Resource.error("", null));
                });
    }

    LiveData<Resource<Boolean>> observeSignupResult() {
        return signupResult;
    }

    void clearObserver() {
        signupResult.setValue(null);
    }
}
