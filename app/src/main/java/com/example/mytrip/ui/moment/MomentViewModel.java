package com.example.mytrip.ui.moment;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mytrip.app.Resource;
import com.example.mytrip.data.model.Moment;
import com.example.mytrip.utils.CollectionName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MomentViewModel extends ViewModel {
    private final MutableLiveData<Resource<List<Moment>>> momentList = new MutableLiveData<>();
    private CollectionReference reference;

    @Inject
    MomentViewModel(FirebaseAuth firebaseAuth, CollectionReference collectionReference) {
        reference = collectionReference.document(firebaseAuth.getCurrentUser().getUid())
                .collection(CollectionName.Moment);
    }

    LiveData<Resource<List<Moment>>> observeMomentList() {
        momentList.setValue(Resource.loading(null));
        reference.orderBy("date")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            momentList.setValue(Resource.error("", null));
                        }
                        if (queryDocumentSnapshots != null) {
                            List<Moment> moments = new ArrayList<>();
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                Moment moment = snapshot.toObject(Moment.class);
                                moments.add(moment);
                            }
                            momentList.setValue(Resource.success(moments));
                        }
                    }
                });
        return momentList;
    }
}
