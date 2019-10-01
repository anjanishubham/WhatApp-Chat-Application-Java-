package com.lovelycoding.whatapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        mViewModel= ViewModelProviders.of(this).get(ContactActivtyViewModel.class);
        mAdapter=new FindFriendRecycleAdapter(this);
        initView();
        initToolbar();
        initObserver();

        initReycleView();
    }

    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setTitle("Contact List");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: finish activity !!");
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
        mProgressBar.setVisibility(View.VISIBLE);
        mViewModel.getUserList().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                if(contacts!=null) {
                    for (Contact c : contacts)
                        Log.d(TAG, "onChanged: " + c.toString());

                    mContactList.clear();
                    mContactList.addAll(contacts);
                    mAdapter.setContactList(contacts);
                    mAdapter.notifyDataSetChanged();
                    mProgressBar.setVisibility(View.GONE);

                }

            }


        });
    }

    private void initView() {
        toolbar=findViewById(R.id.find_friend_toolbar);
        recyclerView=findViewById(R.id.friend_actionbar_recycle_view);
        mProgressBar=findViewById(R.id.progressbar_find_contact_activity);

    }

    @Override
    public void onClickToFindFriend(int position) {

        sentToUserProfileActivity(mContactList.get(position));
    }

    private void sentToUserProfileActivity(Contact contact) {
        Intent userProfileIntent=new Intent(this,UserProfileActivity.class);
        userProfileIntent.putExtra("userProfile",contact);
        startActivity(userProfileIntent);
    }
}
