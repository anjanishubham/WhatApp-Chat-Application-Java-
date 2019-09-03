package com.lovelycoding.whatapp.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lovelycoding.whatapp.model.Contact;
import com.lovelycoding.whatapp.repository.FirebaseQueryLiveData;
import com.lovelycoding.whatapp.repository.Repository;

import java.util.List;

public class ContactActivtyViewModel extends ViewModel {

    DatabaseReference mDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Users");
    MutableLiveData<List<Contact>> mutableLiveData=new MutableLiveData<>();

    public void userList() {

        mutableLiveData=Repository.getRepositoryInstance().getUserList(mDatabaseRef);
    }

    public MutableLiveData<List<Contact>> getUserList() {
        userList();
        return mutableLiveData;
    }


}
