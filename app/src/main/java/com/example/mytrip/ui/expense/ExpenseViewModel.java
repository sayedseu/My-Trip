package com.example.mytrip.ui.expense;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mytrip.app.Resource;
import com.example.mytrip.data.model.Expense;
import com.example.mytrip.utils.CollectionName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ExpenseViewModel extends ViewModel {
    private final CollectionReference reference;
    private final FirebaseAuth firebaseAuth;
    private CollectionReference expenseReference;
    private MutableLiveData<Resource<Boolean>> addingResult = new MutableLiveData<>();
    private MutableLiveData<Resource<Boolean>> deletingResult = new MutableLiveData<>();
    private MutableLiveData<Resource<List<Expense>>> expenseList = new MutableLiveData<>();

    @Inject
    ExpenseViewModel(CollectionReference reference, FirebaseAuth firebaseAuth) {
        this.reference = reference;
        this.firebaseAuth = firebaseAuth;
        expenseReference = reference.document(firebaseAuth.getCurrentUser().getUid())
                .collection(CollectionName.EXPENSE);
    }

    LiveData<Resource<List<Expense>>> observeExpenseList() {
        expenseList.setValue(Resource.loading(null));
        expenseReference.orderBy("date")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e == null) {
                        if (queryDocumentSnapshots != null) {
                            List<Expense> expenses = new ArrayList<>();
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                                Expense expense = snapshot.toObject(Expense.class);
                                expenses.add(expense);
                            }
                            expenseList.setValue(Resource.success(expenses));
                        }
                    } else expenseList.setValue(Resource.error("", null));
                });
        return expenseList;
    }

    void addExpense(Expense expense) {
        addingResult.setValue(Resource.loading(null));
        expenseReference.document(expense.getId())
                .set(expense)
                .addOnSuccessListener(aVoid -> {
                    addingResult.setValue(Resource.success(true));
                })
                .addOnFailureListener(e -> {
                    addingResult.setValue(Resource.error("", null));
                });
    }

    public void deleteItem(String uuid) {
        deletingResult.setValue(Resource.loading(null));
        expenseReference.document(uuid)
                .delete()
                .addOnCompleteListener(task -> {
                    deletingResult.setValue(Resource.success(true));
                })
                .addOnFailureListener(e -> {
                    deletingResult.setValue(Resource.error("", null));
                });
    }

    LiveData<Resource<Boolean>> observeAddingResult() {
        return addingResult;
    }

    LiveData<Resource<Boolean>> observeDeletingResult() {
        return deletingResult;
    }

    void clearObserver() {
        addingResult.setValue(null);
        deletingResult.setValue(null);
    }
}
