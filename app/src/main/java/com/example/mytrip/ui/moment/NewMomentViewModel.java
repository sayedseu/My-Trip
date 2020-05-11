package com.example.mytrip.ui.moment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mytrip.app.Resource;
import com.example.mytrip.data.model.Moment;
import com.example.mytrip.utils.CollectionName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;

import javax.inject.Inject;

public class NewMomentViewModel extends ViewModel {
    private final MutableLiveData<Resource<Boolean>> uploadingResult = new MutableLiveData<>();
    private StorageReference parentReference;
    private CollectionReference reference;

    @Inject
    NewMomentViewModel(CollectionReference collectionReference, FirebaseStorage storage, FirebaseAuth firebaseAuth) {
        StorageReference storageReference = storage.getReference();
        parentReference = storageReference.child(firebaseAuth.getCurrentUser().getUid());
        reference = collectionReference.document(firebaseAuth.getCurrentUser().getUid()).collection(CollectionName.Moment);
    }

    void uploadImage(byte[] data, String message) {
        uploadingResult.setValue(Resource.loading(null));
        StorageReference childReference = parentReference.child(UUID.randomUUID().toString() + ".jpg");
        childReference.putBytes(data)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return childReference.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    String id = UUID.randomUUID().toString();
                    String date = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
                    Moment moment = new Moment(id, message, task.getResult().toString(), date);
                    reference.document(id).set(moment)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    uploadingResult.setValue(Resource.success(true));
                                } else {
                                    uploadingResult.setValue(Resource.error("", null));
                                }
                            })
                            .addOnFailureListener(e -> {
                                uploadingResult.setValue(Resource.error("", null));
                            });
                })
                .addOnFailureListener(e -> {
                    uploadingResult.setValue(Resource.error("", null));
                });
    }

    LiveData<Resource<Boolean>> observeUploadingResult() {
        return uploadingResult;
    }

    void clearObserver() {
        uploadingResult.setValue(null);
    }
}
