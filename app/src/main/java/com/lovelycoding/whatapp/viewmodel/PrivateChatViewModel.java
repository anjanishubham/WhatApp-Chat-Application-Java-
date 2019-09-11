package com.lovelycoding.whatapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lovelycoding.whatapp.model.PrivateChatMessage;
import com.lovelycoding.whatapp.repository.Repository;

import java.util.List;

public class PrivateChatViewModel extends ViewModel {

    private DatabaseReference messageDatabaseRef;

    FirebaseAuth mAuth;
    private MutableLiveData<List<PrivateChatMessage>> messageLiveDataList=new MutableLiveData<>();
    private Repository mRepo;

    private void getMessageListFromRepo(String senderUserId) {
        final String receiverUserId;
        mAuth=FirebaseAuth.getInstance();
        receiverUserId=mAuth.getCurrentUser().getUid();
        messageDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Messages");
        mRepo=Repository.getRepositoryInstance();
        messageLiveDataList= mRepo.getMessageListFromDatabase(messageDatabaseRef,senderUserId,receiverUserId);
    }
    public LiveData<List<PrivateChatMessage>> getMessageList(String senderUserId) {
        getMessageListFromRepo(senderUserId);
         return messageLiveDataList;
    }
}
