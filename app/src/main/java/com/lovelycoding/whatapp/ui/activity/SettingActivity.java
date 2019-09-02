package com.lovelycoding.whatapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.permission.RunTimePermission;
import com.lovelycoding.whatapp.ui.fragment.MyDialogFragment;
import com.lovelycoding.whatapp.viewmodel.SettingActivityViewModel;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, MyDialogFragment.onSomeEventListener {
    private static final String TAG = "SettingActivity";

    private CircleImageView mCircleImageView;
    private AppCompatEditText etUserName,etUserStatus;
    private AppCompatButton btUpdateProfile;
    private TextInputLayout tilUserName,tilUserStatus;

    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private static final int CAMERA_REQUEST =10;




    private SettingActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference();
        mViewModel= ViewModelProviders.of(this).get(SettingActivityViewModel.class);
        retriveUserInfo();
        btUpdateProfile.setOnClickListener(this);
        mCircleImageView.setOnClickListener(this);

    }


    private void initView() {
        mCircleImageView=findViewById(R.id.profile_image);
        etUserName=findViewById(R.id.et_user_name);
        etUserStatus=findViewById(R.id.et_user_status);
        btUpdateProfile=findViewById(R.id.bt_update_profile);
        tilUserName=findViewById(R.id.til_user_name);
        tilUserStatus=findViewById(R.id.til_user_status);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_update_profile:
                updateSettings();

                break;
            case R.id.profile_image:
            {


                MyDialogFragment myDialogFragment=new MyDialogFragment();

                myDialogFragment.show(getSupportFragmentManager(),"test");
                myDialogFragment.setCancelable(false);

            }
        }

    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    
   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == RunTimePermission.REQUEST_CODE){

            if(RunTimePermission.mPermission){
                Log.d(TAG, "onRequestPermissionsResult: called testing ");
            }
            else{
                Log.d(TAG, "onRequestPermissionsResult: " +"result" +grantResults.length);
              //  RunTimePermission.cameraPermission(this);
            }
        }
    }

        private void updateSettings() {
        String setUsername=etUserName.getText().toString();
        String setUserStatus=etUserStatus.getText().toString();

        if(TextUtils.isEmpty(setUsername)){
        }
        if(TextUtils.isEmpty(setUserStatus)){

        }
        else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("name",setUsername);
            profileMap.put("status",setUserStatus);
            rootRef.child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(SettingActivity.this, "Profile Updated suceessfully", Toast.LENGTH_SHORT).show();
                                sentUserToMainActivity();
                            }
                            else {
                                Log.d(TAG, "onComplete: Error"+task.getException().toString());
                            }
                        }
                    });
        }
    }

    private void sentUserToMainActivity() {
        Intent intent=new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void retriveUserInfo() {
        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("name"))
                    {
                            etUserName.setText(dataSnapshot.child("name").getValue().toString());
                    }
                    if(dataSnapshot.hasChild("status")){
                        etUserStatus.setText(dataSnapshot.child("status").getValue().toString());
                    }
                    if(dataSnapshot.hasChild("image")){
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onDataChange: "+databaseError.getMessage().toString());

            }
        });
    }



    

    @Override
    public void someEvent(String s) {
        if(RunTimePermission.mPermission)
        {
            if(s.equals("camera"))
            {
                Log.d(TAG, "someEvent: camera is open");
               if(checkCameraHardware(this))
                openCamera();
            }
            else if(s.equals("gallery"))
            {
                Log.d(TAG, "someEvent: gallery is open");

            }

        }
        else {
            Log.d(TAG, "someEvent: "+RunTimePermission.mPermission);
            
        }
    }
    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == CAMERA_REQUEST) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            mCircleImageView.setImageBitmap(image);
        }
    }

    /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }*/
}
