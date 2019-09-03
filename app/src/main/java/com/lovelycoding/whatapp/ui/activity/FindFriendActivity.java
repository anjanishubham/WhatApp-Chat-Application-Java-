package com.lovelycoding.whatapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.adapter.findfriend.FindFriendRecycleAdapter;
import com.lovelycoding.whatapp.adapter.findfriend.OnclickFindFriend;
import com.lovelycoding.whatapp.model.Contact;
import com.lovelycoding.whatapp.viewmodel.ContactActivtyViewModel;

import java.util.ArrayList;
import java.util.List;

public class FindFriendActivity extends AppCompatActivity implements OnclickFindFriend {
    private static final String TAG = "FindFriendActivity";
    Toolbar toolbar;
    RecyclerView recyclerView;
    ContactActivtyViewModel mViewModel;

    //val
    private  List<Contact> mContactList=new ArrayList<>();
    private FindFriendRecycleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        mViewModel= ViewModelProviders.of(this).get(ContactActivtyViewModel.class);
        mAdapter=new FindFriendRecycleAdapter();
        initObserver();
        initView();
        initToolbar();
        initReycleView();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setTitle("Contact");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initReycleView() {
        mAdapter.setContactList(mContactList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(mAdapter);

    }

    private void initObserver() {
        mViewModel.getUserList().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                if(contacts!=null) {
                    for (Contact c : contacts)
                        Log.d(TAG, "onChanged: " + c.toString());

                    mContactList.clear();
                    mContactList.addAll(contacts);
                }
                mAdapter.setContactList(contacts);
                mAdapter.notifyDataSetChanged();

            }


        });
    }

    private void initView() {
        toolbar=findViewById(R.id.find_friend_toolbar);
        recyclerView=findViewById(R.id.friend_actionbar_recycle_view);

    }

    @Override
    public void onClickToFindFriend(int position) {

        Toast.makeText(this, "Hot start ", Toast.LENGTH_SHORT).show();
    }
}
