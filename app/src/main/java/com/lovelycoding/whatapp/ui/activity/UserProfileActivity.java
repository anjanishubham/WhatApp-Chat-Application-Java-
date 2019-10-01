package com.lovelycoding.whatapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.model.Contact;
import com.lovelycoding.whatapp.model.Notification;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UserProfileActivity";
    Toolbar toolbar;
    CircleImageView civUserProfileImage;
    TextView tvUserName, tvUserStatus;
    AppCompatButton btSendMessageRequest,btDeclineMessageRequest;

    //val
    FirebaseAuth mAuth;
    DatabaseReference contactDatabaseRef,userDatabaseRef,chatRequestDatabaseRef,notificationDatabaseRef;
    DatabaseReference receiveRequestDatabaseRef;
    private String SEND_REQUEST="request_sent";
    private String CANCEL_REQUEST="request_cancel";
    private String EXCEPT_REQUEST="accept_request";
    private String FRIEND="friend";

    String sendRequestUserId, receiveRequestUserId;
    String currentState;

    Contact userDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getUserProfileDetail();
        mAuth = FirebaseAuth.getInstance();
        userDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestDatabaseRef= FirebaseDatabase.getInstance().getReference().child("chat request");
        contactDatabaseRef= FirebaseDatabase.getInstance().getReference().child("contacts");
        notificationDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Notification");
        sendRequestUserId = mAuth.getCurrentUser().getUid();
        receiveRequestUserId=userDetail.getUid();

        initView();
        initToolbar();
        if (userDetail != null)
            setUserDetail();

        btSendMessageRequest.setOnClickListener(this);
        btDeclineMessageRequest.setOnClickListener(this);

    }


    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setTitle(userDetail.getName() + "  Profile");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: finish activity !!");
                finish();
            }
        });
    }


    private void initView() {

        toolbar = findViewById(R.id.user_profile_toolbar);
        civUserProfileImage = findViewById(R.id.civ_user_profile_image);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserStatus = findViewById(R.id.tv_user_status);
        btSendMessageRequest = findViewById(R.id.bt_send_message_request);
        btDeclineMessageRequest=findViewById(R.id.bt_decline_message_request);
        currentState=SEND_REQUEST;
    }
    private void getUserProfileDetail() {

        {
            userDetail = getIntent().getParcelableExtra("userProfile");
            if (userDetail != null)
                Log.d(TAG, "getUserProfileDetail: " + userDetail.toString());
            Log.d(TAG, "getUserProfileDetail: " + getIntent().getParcelableExtra("userProfile"));
        }
    }



    private void setUserDetail() {

        checkMessageStatusType();

        if (sendRequestUserId.equals(userDetail.getUid())) {
            btSendMessageRequest.setVisibility(View.INVISIBLE);
        }
        tvUserStatus.setText(userDetail.getStatus());
        tvUserName.setText(userDetail.getName());
        Log.d(TAG, "setUserDetail: "+userDetail.getImage());
        Glide.with(this).load(userDetail.getImage()).apply(RequestOptions.placeholderOf(R.drawable.profile_image)).into(civUserProfileImage);


    }

    private void checkMessageStatusType() {
        chatRequestDatabaseRef.child(sendRequestUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receiveRequestUserId)) {
                    String requestType=dataSnapshot.child(receiveRequestUserId).child("request_type").getValue().toString();
                    if(requestType.equals("sent")){
                        btSendMessageRequest.setText("remove contact");
                        btDeclineMessageRequest.setVisibility(View.INVISIBLE);
                        btDeclineMessageRequest.setEnabled(false);
                        currentState=CANCEL_REQUEST;
                    }
                    if(requestType.equals("receive"))
                    {


                    }
                }
                else {
                    contactDatabaseRef.child(sendRequestUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                           if(dataSnapshot.hasChild(receiveRequestUserId))
                           {
                               btSendMessageRequest.setText("remove contact");
                               btDeclineMessageRequest.setVisibility(View.INVISIBLE);
                               btDeclineMessageRequest.setEnabled(false);
                               currentState=FRIEND;
                           }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void cancelChatRequest() {
        chatRequestDatabaseRef.child(sendRequestUserId).
                child(receiveRequestUserId).
                child("request_type").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    chatRequestDatabaseRef.child(receiveRequestUserId).
                            child(sendRequestUserId).
                            child("request_type").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                btSendMessageRequest.setText("Send message request");
                                currentState=SEND_REQUEST;
                                btDeclineMessageRequest.setEnabled(false);
                                btDeclineMessageRequest.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendChatRequest() {

        chatRequestDatabaseRef.child(sendRequestUserId).
                child(receiveRequestUserId).
                child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    chatRequestDatabaseRef.child(receiveRequestUserId).
                            child(sendRequestUserId).
                            child("request_type").setValue("receive").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {

                                Notification notification=new Notification();
                                String from,type;
                                from=sendRequestUserId;
                                type="request";
                                notification.setFrom(from);
                                notification.setType(type);
                                notificationDatabaseRef.child(receiveRequestUserId).setValue(notification);
                                currentState=CANCEL_REQUEST;
                                btSendMessageRequest.setText("remove request");
                            }
                        }
                    });
                }
            }
        });
    }

    private void acceptChatRequest() {
        contactDatabaseRef.child(sendRequestUserId).
                child(receiveRequestUserId).
                child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    contactDatabaseRef.child(receiveRequestUserId).
                            child(sendRequestUserId).
                            child("contacts").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                cancelChatRequest();
                                currentState=FRIEND;
                                btSendMessageRequest.setText("remove contact");
                                btDeclineMessageRequest.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }
    private void removeContact() {
        contactDatabaseRef.child(sendRequestUserId).
                child(receiveRequestUserId).
                child("contacts").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    contactDatabaseRef.child(receiveRequestUserId).
                            child(sendRequestUserId).
                            child("contacts").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                btSendMessageRequest.setText("Send message request");
                                currentState=SEND_REQUEST;
                                btDeclineMessageRequest.setEnabled(false);
                                btDeclineMessageRequest.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: "+currentState);


        switch (view.getId())
        {
            case R.id.bt_send_message_request:
                if(currentState.equals(SEND_REQUEST))
                    sendChatRequest();
                else if(currentState.equals(CANCEL_REQUEST))
                {
                    cancelChatRequest();
                }
                if(currentState.equals(EXCEPT_REQUEST))
                {
                    acceptChatRequest();
                }
                if(currentState.equals(FRIEND)){
                    Log.d(TAG, "onClick:removed contact called  ");
                    removeContact();
                }
                break;
            case R.id.bt_decline_message_request:
            {
                cancelChatRequest();
            }

        }

        Toast.makeText(this, "request send !!", Toast.LENGTH_SHORT).show();
    }



}
