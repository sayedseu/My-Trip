package com.example.mytrip.ui.events;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mytrip.app.Resource;
import com.example.mytrip.data.model.Event;
import com.example.mytrip.utils.CollectionName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;

import javax.inject.Inject;

public class NewEventViewModel extends ViewModel {
    private final MutableLiveData<Resource<Boolean>> savingResult = new MutableLiveData<>();
    private CollectionReference reference;

    @Inject
    NewEventViewModel(FirebaseAuth firebaseAuth, CollectionReference collectionReference) {
        ;
        reference = collectionReference.document(firebaseAuth.getCurrentUser().getUid())
                .collection(CollectionName.EVENTS);
    }

    void saveEvent(Event event) {
        savingResult.setValue(Resource.loading(null));
        reference.document(event.getId())
                .set(event)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        savingResult.setValue(Resource.success(true));
                    }
                })
                .addOnFailureListener(e -> {
                    savingResult.setValue(Resource.error("", null));
                });
    }

    LiveData<Resource<Boolean>> observeSavingResult() {
        return savingResult;
    }

    void clearObserver() {
        savingResult.setValue(null);
    }
}
