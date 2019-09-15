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
import com.lovelycoding.whatapp.model.PrivateChatMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Repository {
    public static Repository mRepository;
    private static final String TAG = "Repository";
    private MutableLiveData<List<String>> mGroupList = new MutableLiveData<>();
    private MutableLiveData<List<GroupChatModel>> groupMassageList = new MutableLiveData<>();
    private MutableLiveData<List<Contact>> userListLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Contact>> contactListLiveData = new MutableLiveData<>();
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
        } catch (Exception e) {
            Log.d(TAG, "getGroupList: " + e.getMessage().toString());
        }

        return mGroupList;
    }

    public void setDatabaseReference(DatabaseReference groupChatRef) {
        this.groupChatRef = groupChatRef;
    }


    public MutableLiveData<List<Contact>> getUserList(DatabaseReference mDatabaseRef) {


        final List<Contact> mList = new ArrayList<>();
        Query query = mDatabaseRef;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Iterator<DataSnapshot> iterator = d.getChildren().iterator();

                    Contact contact = new Contact();
                    while (iterator.hasNext()) {
                        DataSnapshot dataSnapshot1 = iterator.next();

                        if ("image".equals(dataSnapshot1.getKey().trim())) {
                            contact.setImage(dataSnapshot1.getValue().toString());
                        } else if ("name".equals(dataSnapshot1.getKey().trim())) {
                            contact.setName(dataSnapshot1.getValue().toString());
                        } else if ("status".equals(dataSnapshot1.getKey().trim())) {
                            contact.setStatus(dataSnapshot1.getValue().toString());
                        } else if ("uid".equals(dataSnapshot1.getKey().trim())) {
                            contact.setUid(dataSnapshot1.getValue().toString());
                        }

                    }

                    Log.d(TAG, "onDataChange: " + contact.toString());
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


    public MutableLiveData<List<Contact>> getContactList(DatabaseReference contactDatabaseRef, String userId) {
        final List<Contact> mContactList = new ArrayList<>();

        Log.d(TAG, "getContactList userID: " + userId);
        final DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        contactDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Log.d(TAG, "onDataChange: "+dataSnapshot.);
               if(mContactList.size()>0)
                mContactList.clear();

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()) {
                    String userId = iterator.next().getKey();
                    Log.d(TAG, "onDataChange: " + userId);
                    userDatabaseRef.child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Contact contact = dataSnapshot.getValue(Contact.class);
                            mContactList.add(contact);
                            Log.d(TAG, "onDataChange getUserDetail: " + mContactList.size());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                Log.d(TAG, "onDataChange move to live Data: " + mContactList);
                contactListLiveData.setValue(mContactList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //  Log.d(TAG, "getContactList: "+mContactList.size());
        return contactListLiveData;

    }


    public MutableLiveData<List<GroupChatModel>> getGroupMessageList() {
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

        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()) {
            GroupChatModel ob = new GroupChatModel();
            String massageDate = (String) ((DataSnapshot) iterator.next()).getValue();
            String message = (String) ((DataSnapshot) iterator.next()).getValue();
            String name = (String) ((DataSnapshot) iterator.next()).getValue();
            String time = (String) ((DataSnapshot) iterator.next()).getValue();
            ob.setMessageDate(massageDate);
            ob.setMessageTime(time);
            ob.setUserName(name);
            ob.setUserMassage(message);
            Log.d(TAG, "getGroupList: " + ob.toString());
            list.add(ob);

        }
        groupMassageList.setValue(list);

    }

    public MutableLiveData<List<Contact>> getRequestContactList(final DatabaseReference databaseReference, String currentUserId) {
        MutableLiveData<List<Contact>> mContactLiveData=new MutableLiveData<>();
        Log.d(TAG, "current User Id: "+currentUserId);
        DatabaseReference requestDataRef=databaseReference;
        requestDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                Log.d(TAG, "onDataChange: "+dataSnapshot.getChildrenCount()+"user id"+dataSnapshot.getValue());
                while (iterator.hasNext())
                {
                   // Iterator<DataSnapshot> iterator1= (Iterator<DataSnapshot>) iterator.next();
                    Log.d(TAG, "onDataChange: "+iterator.next().getValue()+"userID "+dataSnapshot.getKey());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return mContactLiveData;
    }

    public MutableLiveData<List<PrivateChatMessage>> getMessageListFromDatabase(DatabaseReference messageDatabaseReference,String senderUserId,String receiverUserId) {
        MutableLiveData<List<PrivateChatMessage>> messageListLiveData=new MutableLiveData<>();
        final List<PrivateChatMessage> mList = new ArrayList<>();
        Log.d(TAG, "getMessageListFromDatabase: "+senderUserId+"receiver "+receiverUserId);
        messageDatabaseReference.child(senderUserId).child(receiverUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                PrivateChatMessage message=dataSnapshot.getValue(PrivateChatMessage.class);
                mList.add(message);
                Log.d(TAG, "onChildAdded: "+message.toString());
                Log.d(TAG, "onChildAdded: "+mList.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
        messageListLiveData.setValue(mList);
        Log.d(TAG, "getMessageListFromDatabase: "+mList.size());
        return messageListLiveData;
    }

}
