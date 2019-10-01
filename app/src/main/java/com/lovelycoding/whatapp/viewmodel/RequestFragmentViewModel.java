package com.lovelycoding.whatapp.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lovelycoding.whatapp.model.Contact;
import com.lovelycoding.whatapp.repository.Repository;

import java.util.List;

public class RequestFragmentViewModel extends ViewModel {

    private static final String TAG = "ContactFragmentViewMode";
    FirebaseAuth mAuth;
    DatabaseReference contactDatabaseRef;
    String currentUserId;

    public MutableLiveData<List<Contact>> mutableLiveData=new MutableLiveData<>();

    public void userList() {
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        contactDatabaseRef= FirebaseDatabase.getInstance().getReference().child("chat request").child(currentUserId);

        mutableLiveData= Repository.getRepositoryInstance().getRequestContactList(contactDatabaseRef,currentUserId);
        Log.d(TAG, "userList: "+mutableLiveData.toString());
    }

    public MutableLiveData<List<Contact>> getUserList() {
        userList();
        return mutableLiveData;
    }


}
