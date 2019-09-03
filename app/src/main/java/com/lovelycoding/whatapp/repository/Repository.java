package com.lovelycoding.whatapp.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lovelycoding.whatapp.model.Contact;
import com.lovelycoding.whatapp.model.GroupChatModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Repository  {
    public static Repository mRepository;
    private static final String TAG = "Repository";
    private MutableLiveData<List<String>> mGroupList = new MutableLiveData<>();
    private MutableLiveData<List<GroupChatModel>> groupMassageList = new MutableLiveData<>();
    private MutableLiveData<List<Contact>> userListLiveData =new MutableLiveData<>();
    private DatabaseReference groupRef;
    private DatabaseReference groupChatRef;

    public static Repository getRepositoryInstance() {
        if (mRepository == null) {
            mRepository = new Repository();
        }
        return mRepository;
    }
    public MutableLiveData<List<String>> getGroupList() {
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        try {
            groupRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> list = new ArrayList<>();
                    Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        list.add(((DataSnapshot) iterator.next()).getKey());
                        //Log.d(TAG, "onDataChange: " + ((DataSnapshot) iterator.next()).getKey());
                    }
                    mGroupList.setValue(list);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Log.d(TAG, "onCancelled: getting " + databaseError.getMessage().toString());
                }
            });
        }catch (Exception e)
        {
            Log.d(TAG, "getGroupList: "+e.getMessage().toString());
        }

        return mGroupList;
    }

    public void setDatabaseReference(DatabaseReference groupChatRef){
        this.groupChatRef =groupChatRef;
    }


    public MutableLiveData<List<Contact>> getUserList(DatabaseReference mDatabaseRef) {


        final List<Contact> mList = new ArrayList<>();
        Query query=mDatabaseRef;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                   Iterator<DataSnapshot> iterator= d.getChildren().iterator();

                   Contact contact=new Contact();
                   while (iterator.hasNext())
                   {
                       DataSnapshot dataSnapshot1=iterator.next();

                       if("image".equals(dataSnapshot1.getKey().trim()))
                       {
                           contact.setUserProfileUrl(dataSnapshot1.getValue().toString());
                       }
                       else if("name".equals(dataSnapshot1.getKey().trim()))
                       {
                           contact.setUserName(dataSnapshot1.getValue().toString());
                       }
                       else if("status".equals(dataSnapshot1.getKey().trim()))
                       {
                           contact.setUserStatus(dataSnapshot1.getValue().toString());
                       }
                   }

                    Log.d(TAG, "onDataChange: "+contact.toString());
                   mList.add(contact);

                }
                userListLiveData.setValue(mList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return userListLiveData;
    }



    public MutableLiveData<List<GroupChatModel>> getGroupMessageList()
    {
        List<GroupChatModel> list = new ArrayList<>();
        groupChatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                getGroupList(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                getGroupList(dataSnapshot);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return groupMassageList;
    }


    private void getGroupList(DataSnapshot dataSnapshot) {
        List<GroupChatModel> list = new ArrayList();

        Iterator iterator=dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()){
            GroupChatModel ob=new GroupChatModel();
            String massageDate = (String)((DataSnapshot)iterator.next()).getValue();
            String message = (String)((DataSnapshot)iterator.next()).getValue();
            String name = (String)((DataSnapshot)iterator.next()).getValue();
            String time = (String)((DataSnapshot)iterator.next()).getValue();
            ob.setMessageDate(massageDate);
            ob.setMessageTime(time);
            ob.setUserName(name);
            ob.setUserMassage(message);
            Log.d(TAG, "getGroupList: "+ob.toString());
            list.add(ob);

        }
        groupMassageList.setValue(list);

    }

}
