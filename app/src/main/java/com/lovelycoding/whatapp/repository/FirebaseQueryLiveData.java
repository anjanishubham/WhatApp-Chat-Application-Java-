package com.lovelycoding.whatapp.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirebaseQueryLiveData extends LiveData<DataSnapshot> {
    private static final String TAG = "FirebaseQueryLiveData";
    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public FirebaseQueryLiveData(Query query) {
        Log.d(TAG, "FirebaseQueryLiveData: Query counstructor ");
        this.query = query;
    }
    public FirebaseQueryLiveData(DatabaseReference ref) {
        Log.d(TAG, "FirebaseQueryLiveData: Database counstructor ");
        this.query = ref;
    }


    @Override
    protected void onActive() {
        Log.d(TAG, "onActive: ");
        query.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        Log.d(TAG, "onInactive: ");
        query.addValueEventListener(listener);
    }

    public class MyValueEventListener implements ValueEventListener
    {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            Log.d(TAG, "onDataChange: ValueEventLIstenr called ");
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }
}

