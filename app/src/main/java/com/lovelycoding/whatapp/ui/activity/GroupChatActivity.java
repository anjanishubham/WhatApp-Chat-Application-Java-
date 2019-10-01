package com.lovelycoding.whatapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.adapter.groupchat.MassageListAdapter;
import com.lovelycoding.whatapp.model.GroupChatModel;
import com.lovelycoding.whatapp.repository.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener, LifecycleOwner {

    private static final String TAG = "GroupChatActivity";
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AppCompatEditText etGroupChatMassage;
    private AppCompatImageButton btGroupChatSend;


    // var
   private String currentUser,currentUserId,currentUserName;
   private String currentGroupName="";
   private FirebaseAuth mAuth;
   private DatabaseReference userRef,groupNameRef,groupMessageKeyRef;

   private String userMessage,currentTime,currentDate;
   private Repository mRepository;
   private List<GroupChatModel> list = new ArrayList<>();
   private MassageListAdapter mAdapter;
   private NestedScrollView mScroll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        initToolbar();
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserName=userRef.child(currentUserId).child("name").toString();
        groupNameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
        mAdapter=new MassageListAdapter();
        //currentUserName=FirebaseDatabase.getInstance().getReference().child("Users").child("name").toString();
        initView();
        etGroupChatMassage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "afterTextChanged: ");
                    if(!editable.toString().isEmpty()){
                        btGroupChatSend.setVisibility(View.VISIBLE);
                        Log.d(TAG, "afterTextChanged: btclickable");
                    }
                    else {
                        btGroupChatSend.setVisibility(View.GONE);
                        Log.d(TAG, "afterTextChanged: btclickable false");
                    }
            }
        });
        getUserInfo();
        btGroupChatSend.setOnClickListener(this);
        mRepository=new Repository();
        mRepository.setDatabaseReference(groupNameRef);
        initObserver();
        intiRecycleView();


    }

    private void intiRecycleView() {

      //  mAdapter=new GroupChatAdapter(this);
        mRepository=new Repository();
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        // RecycleViewItemDecorator decorator = new RecycleViewItemDecorator(15);
        //mCartRecyclerView.addItemDecoration(decorator);
        mAdapter.setMessageList(list);
        recyclerView.setAdapter(mAdapter);
      //  recyclerView.scrollToPosition(list.size());
       // mScroll.addView(recyclerView);
        recyclerView.smoothScrollToPosition(0);

    }

    private void initObserver() {
        mRepository.getGroupMessageList().observe(this, new Observer<List<GroupChatModel>>() {
            @Override
            public void onChanged(List<GroupChatModel> groupChatModels) {
                if(groupChatModels!=null)
                {
                    Log.d(TAG, "onChanged: "+groupChatModels.size());
                    for(GroupChatModel e: groupChatModels)
                    {
                        Log.d(TAG, "onChanged: "+ e.getUserMassage());
                    }
                  //  list.clear();
                    list.addAll(groupChatModels);
                }
                mAdapter.setCurrentUserName(currentUserName);
                mAdapter.setMessageList(list);
                mAdapter.notifyDataSetChanged();

            }
        });
    }

    private void getUserInfo() {
        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    currentUserName=dataSnapshot.child("name").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initView() {
        recyclerView=findViewById(R.id.rv_private_chat_activity);
        etGroupChatMassage=findViewById(R.id.et_message_input_chat_activity);
        btGroupChatSend=findViewById(R.id.bt_Send_message_chat_activity);
        mScroll=findViewById(R.id.scrollview_chat_activity);

    }

    private void initToolbar()
    {
        toolbar=findViewById(R.id.chat_activity_toolbar);
        if(getIntent().hasExtra("currentGroupName"))
        {
            currentGroupName=getIntent().getStringExtra("currentGroupName");

        }
        toolbar.setTitle(currentGroupName);
        toolbar.setTitleTextColor(Color.WHITE);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.bt_Send_message_chat_activity:
            {
                saveMessageToDatabase();
                etGroupChatMassage.setText("");


                break;
            }
        }

    }

    private void saveMessageToDatabase() {
        userMessage=etGroupChatMassage.getText().toString();
        String messageKey=groupNameRef.push().getKey();
        Log.d(TAG, "onClick: bt clicked  !!!!!");
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDateFormat=new SimpleDateFormat("MM-dd-yyyy");
        currentDate=currentDateFormat.format(calendar.getTime());

        SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
        currentTime=currentTimeFormat.format(calendar.getTime());
        Map<String, Object> groupMessageKey = new HashMap<>();
        groupNameRef.updateChildren(groupMessageKey);
        groupMessageKeyRef=groupNameRef.child(messageKey);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name",currentUserName);
        userInfo.put("message",userMessage);
        userInfo.put("date",currentDate);
        userInfo.put("time",currentTime);
        Log.d(TAG, "saveMessageToDatabase: "+ currentUserName+ "   "+currentTime+ "  "+userMessage+"   "+currentDate);
        groupMessageKeyRef.updateChildren(userInfo);
    }


}
