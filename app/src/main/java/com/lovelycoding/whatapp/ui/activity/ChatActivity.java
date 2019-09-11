package com.lovelycoding.whatapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.adapter.privatechat.MessageAdapter;
import com.lovelycoding.whatapp.model.Contact;
import com.lovelycoding.whatapp.model.PrivateChatMessage;
import com.lovelycoding.whatapp.viewmodel.PrivateChatViewModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ChatActivity";
    Toolbar toolbar;
    CircleImageView circleImageView;
    AppCompatImageView ivBackButton;
    TextView tvUsername;// toolbar user name
    RecyclerView rvChatActivity;
    AppCompatEditText etMessageInput;
    AppCompatImageButton btSendMessage;
    private ProgressBar mProgressBar;


    Contact contact;
    FirebaseAuth mAuth;
    String messageSenderUserId, messageReceiverUserId;
    DatabaseReference rootRef;
    private PrivateChatViewModel mViewModel;
    private List<PrivateChatMessage> mMessageList = new ArrayList<>();
    private MessageAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAdapter=new MessageAdapter();
        getUserDetail();
        mAuth = FirebaseAuth.getInstance();
        messageSenderUserId = mAuth.getCurrentUser().getUid();
        messageReceiverUserId = contact.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        initView();
        initToolbar();
        initRecycleView();
        etMessageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0)
                    btSendMessage.setVisibility(View.VISIBLE);
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btSendMessage.setOnClickListener(this);
        ivBackButton.setOnClickListener(this);


    }

    private void initRecycleView() {

        mAdapter.setMessageListValue(mMessageList);
        rvChatActivity.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvChatActivity.setAdapter(mAdapter);

    }

    private void initToolbar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvUsername.setText(contact.getName());

    }

    private void initView() {
        toolbar = findViewById(R.id.chat_activity_toolbar);
        tvUsername = findViewById(R.id.user_name);
        rvChatActivity = findViewById(R.id.rv_private_chat_activity);
        etMessageInput = findViewById(R.id.et_message_input_chat_activity);
        btSendMessage = findViewById(R.id.bt_Send_message_chat_activity);
        ivBackButton = findViewById(R.id.iva_toolbar_back_button);
        mProgressBar=findViewById(R.id.progressbar_chat_activity);


    }

    private void getUserDetail() {

        if (getIntent().hasExtra("user")) {
            contact = getIntent().getParcelableExtra("user");

        }
    }

    private void sendMessage(final String message) {

        String messageSenderRef = "Messages/" + messageSenderUserId + "/" + messageReceiverUserId;
        String messageReceiverRef = "Messages/" + messageReceiverUserId + "/" + messageSenderUserId;
        DatabaseReference userMessageRef = rootRef.child("Messages").child(messageSenderUserId).child(messageReceiverUserId).push();
        String messagePushId = userMessageRef.getKey();

        Map messageBody = new HashMap();
        messageBody.put("message", message);
        messageBody.put("messageFrom", messageSenderUserId);
        messageBody.put("messageType", "text");
        messageBody.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        Map messageBodyDetails = new HashMap();
        messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageBody);
        messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messageBody);
        rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete:message sending  " + message);
                } else {
                    Log.d(TAG, "onComplete: message sending failed" + task.getException().getLocalizedMessage());
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgressBar.setVisibility(View.GONE);
        mMessageList.clear();
        rootRef.child("Messages").child(messageSenderUserId).child(messageReceiverUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                mProgressBar.setVisibility(View.VISIBLE);
                PrivateChatMessage message = dataSnapshot.getValue(PrivateChatMessage.class);
                mMessageList.add(message);
                mAdapter.setMessageListValue(mMessageList);
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, "onChildAdded: "+mMessageList.size());
                rvChatActivity.smoothScrollToPosition(rvChatActivity.getAdapter().getItemCount());
                mProgressBar.setVisibility(View.GONE);
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

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.bt_Send_message_chat_activity: {
                Toast.makeText(this, "button clicked" + etMessageInput.getText().toString(), Toast.LENGTH_SHORT).show();
                btSendMessage.setVisibility(View.INVISIBLE);
                sendMessage(etMessageInput.getText().toString());
                etMessageInput.setText("");
                break;
            }
            case R.id.iva_toolbar_back_button:
                finish();
                break;
        }
    }


}
