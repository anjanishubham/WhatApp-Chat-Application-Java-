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

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.adapter.findfriend.FindFriendViewHolder;
import com.lovelycoding.whatapp.adapter.privatechat.MessageAdapter;
import com.lovelycoding.whatapp.adapter.privatechat.OnClickFlieSharing;
import com.lovelycoding.whatapp.model.Contact;
import com.lovelycoding.whatapp.model.PrivateChatMessage;
import com.lovelycoding.whatapp.permission.RunTimePermission;
import com.lovelycoding.whatapp.ui.fileshared.FileShared;
import com.lovelycoding.whatapp.ui.fragment.FileSharingDialogFragment;
import com.lovelycoding.whatapp.ui.fragment.MyDialogFragment;
import com.lovelycoding.whatapp.util.Util;
import com.lovelycoding.whatapp.viewmodel.PrivateChatViewModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, FileShared,OnClickFlieSharing {
    private static final String TAG = "ChatActivity";
    Toolbar toolbar;
    CircleImageView circleImageView;
    AppCompatImageView ivBackButton;
    TextView tvUsername;// toolbar user name
    RecyclerView rvChatActivity;
    AppCompatEditText etMessageInput;
    AppCompatImageButton btSendMessage;
    private ProgressBar mProgressBar;
    private static final int CAMERA_PIC_REQUEST=100;
    private static final int GALLERY_PIC_REQUEST=101;
    private static final int GALLERY_DOC_REQUEST=101;


    Contact contact;
    FirebaseAuth mAuth;
    String messageSenderUserId, messageReceiverUserId;
    DatabaseReference rootRef;
    private PrivateChatViewModel mViewModel;
    private List<PrivateChatMessage> mMessageList = new ArrayList<>();
    private MessageAdapter mAdapter;
    AppCompatImageButton btFileSharing;
    public static boolean mPermission=false;
    public static View view;
    FileSharingDialogFragment fileSharingDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAdapter=new MessageAdapter(this);
        mAuth = FirebaseAuth.getInstance();
        messageSenderUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        getUserDetail();
        initView();
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
        btFileSharing.setOnClickListener(this);


    }
    private void updateUserStatus(final String state) {
        String currentUserId=contact.getUid();
        rootRef.child("Users").child(currentUserId).child("last_online_time").setValue(Util.getCurrentTime());
        rootRef.child("Users").child(currentUserId).child("last_online_date").setValue(Util.getCurrentDate());
        rootRef.child("Users").child(currentUserId).child("login_state").setValue(state);
        getUpdatedUserDetail();

    }

    private void initRecycleView() {

        mAdapter.setMessageListValue(mMessageList);
        rvChatActivity.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvChatActivity.setAdapter(mAdapter);

    }

    private void initToolbar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if(contact.getLogin_state().equals("online"))
        tvUsername.setText(contact.getName()+"\n"+contact.getLogin_state());
        else {
            tvUsername.setText(contact.getName()+"\n"+contact.getLast_online_date() +" "+contact.getLast_online_time() );

        }

    }

    private void initView() {
        toolbar = findViewById(R.id.chat_activity_toolbar);
        tvUsername = findViewById(R.id.user_name);
        rvChatActivity = findViewById(R.id.rv_private_chat_activity);
        etMessageInput = findViewById(R.id.et_message_input_chat_activity);
        btSendMessage = findViewById(R.id.bt_Send_message_chat_activity);
        ivBackButton = findViewById(R.id.iva_toolbar_back_button);
        mProgressBar=findViewById(R.id.progressbar_chat_activity);
        btFileSharing=findViewById(R.id.chat_activity_shared_file_button);


    }

    private void getUserDetail() {

        if (getIntent().hasExtra("user")) {
            contact = getIntent().getParcelableExtra("user");
            updateUserStatus("online");
            messageReceiverUserId = contact.getUid();
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

    private void getUpdatedUserDetail() {

        DatabaseReference userDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Users");
        userDatabaseRef.child(contact.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contact=dataSnapshot.getValue(Contact.class);
                initToolbar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateUserStatus("online");
        mProgressBar.setVisibility(View.VISIBLE);
        mMessageList.clear();
        rootRef.child("Messages").child(messageSenderUserId).child(messageReceiverUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                mProgressBar.setVisibility(View.GONE);
                PrivateChatMessage message = dataSnapshot.getValue(PrivateChatMessage.class);
                mMessageList.add(message);
                mAdapter.setMessageListValue(mMessageList);
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, "onChildAdded: "+mMessageList.size());
                rvChatActivity.smoothScrollToPosition(rvChatActivity.getAdapter().getItemCount());
              //  mProgressBar.setVisibility(View.GONE);
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

            case R.id.chat_activity_shared_file_button:
            {
                fileSharingDialogFragment = new FileSharingDialogFragment(this);

                fileSharingDialogFragment.show(getSupportFragmentManager(), "test");

                break;
            }

        }
    }




    @Override
    public void getCamera() {

        getRunTimePermission();
        if(mPermission)
        {
            takePicFromCamera();
            Log.d(TAG, "getCamera: camera open");
        }

       // Log.d(TAG, "getCamera: camera is clicked");
    }

    @Override
    public void getGallery() {
        getRunTimePermission();
        if(mPermission) {
            openImageFromGallery();
        }
    }




    @Override
    public void getDocument() {

        getRunTimePermission();
        if(mPermission)
            Log.d(TAG, "getGallery: document open");

       // Log.d(TAG, "getDocument: document get clicked !!!!!!!");
    }

    @Override
    public void onClickShareFile() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CAMERA_PIC_REQUEST && data!=null)
        {
            Log.d(TAG, "onActivityResult: pic has taken ");
        }
        else if(requestCode==GALLERY_PIC_REQUEST && data!=null){
            Log.d(TAG, "onActivityResult: image select form galley");   
        }
    }
    private void takePicFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }
    private void openImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),GALLERY_PIC_REQUEST);
        Log.d(TAG, "getGallery: gallery open");
    }

    private void getRunTimePermission() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted())
                        {
                            mPermission=true;
                        }
                        if(report.isAnyPermissionPermanentlyDenied()) {
                            allowPermission();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void allowPermission() {
        fileSharingDialogFragment.dismiss();

        Snackbar.make(btFileSharing,"Allow permission to access you camera and gallery ",Snackbar.LENGTH_LONG).setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }).show();

    }


}
