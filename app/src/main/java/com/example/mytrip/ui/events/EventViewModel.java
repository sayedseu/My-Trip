package com.example.mytrip.ui.events;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mytrip.app.Resource;
import com.example.mytrip.data.model.Event;
import com.example.mytrip.utils.CollectionName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class EventViewModel extends ViewModel {
    private final MutableLiveData<Resource<List<Event>>> eventList = new MutableLiveData<>();
    private final MutableLiveData<Resource<Boolean>> deletingResult = new MutableLiveData<>();
    private CollectionReference reference;

    @Inject
    EventViewModel(FirebaseAuth firebaseAuth, CollectionReference collectionReference) {
        reference = collectionReference.document(firebaseAuth.getCurrentUser().getUid())
                .collection(CollectionName.EVENTS);
    }

    public LiveData<Resource<List<Event>>> retrieveEventList() {
        eventList.setValue(Resource.loading(null));
        reference.orderBy("destination")
                .addSnapshotListener(((queryDocumentSnapshots, e) -> {
                    if (e == null) {
                        if (queryDocumentSnapshots != null) {
                            List<Event> events = new ArrayList<>();
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                Event event = snapshot.toObject(Event.class);
                                events.add(event);
                            }
                            eventList.setValue(Resource.success(events));
                        }
                    } else eventList.setValue(Resource.error("", null));
                }));
        return eventList;
    }

    public void deleteItem(String uuid) {
        deletingResult.setValue(Resource.loading(null));
        reference.document(uuid)
                .delete()
                .addOnCompleteListener(task -> {
                    deletingResult.setValue(Resource.success(true));
                })
                .addOnFailureListener(e -> {
                    deletingResult.setValue(Resource.error("", null));
                });
    }

    LiveData<Resource<Boolean>> observeDeletingResult() {
        return deletingResult;
    }

    void clearObserver() {
        deletingResult.setValue(null);
    }
}
