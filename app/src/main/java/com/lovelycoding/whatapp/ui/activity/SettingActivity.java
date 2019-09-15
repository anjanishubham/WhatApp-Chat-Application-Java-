package com.lovelycoding.whatapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.model.Contact;
import com.lovelycoding.whatapp.permission.RunTimePermission;
import com.lovelycoding.whatapp.ui.fragment.MyDialogFragment;
import com.lovelycoding.whatapp.util.Util;
import com.lovelycoding.whatapp.viewmodel.SettingActivityViewModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, MyDialogFragment.onSomeEventListener {
    private static final String TAG = "SettingActivity";

    private CircleImageView mCircleImageView;
    private AppCompatEditText etUserName, etUserStatus;
    private AppCompatButton btUpdateProfile;
    private TextInputLayout tilUserName, tilUserStatus;
    private Toolbar toolbar;

    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private StorageReference userProfileImageRef;
    private String userProfileImageUrl = "", userName, userStatus;

    private static final int CAMERA_REQUEST = 10;
    private static final int GALLERY_REQUEST = 11;


    private SettingActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initToolbar();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Image");
        mViewModel = ViewModelProviders.of(this).get(SettingActivityViewModel.class);
        retrieveUserInfo();
        btUpdateProfile.setOnClickListener(this);
        mCircleImageView.setOnClickListener(this);

    }

    private void initToolbar() {
        // setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: toolbar back button");
                finish();
            }
        });
    }


    private void initView() {
        mCircleImageView = findViewById(R.id.profile_image);
        etUserName = findViewById(R.id.et_user_name);
        etUserStatus = findViewById(R.id.et_user_status);
        btUpdateProfile = findViewById(R.id.bt_update_profile);
        tilUserName = findViewById(R.id.til_user_name);
        tilUserStatus = findViewById(R.id.til_user_status);
        toolbar = findViewById(R.id.profile_setting_toolbar);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)


    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == RunTimePermission.REQUEST_CODE) {
//
//            if (RunTimePermission.mPermission) {
//                Log.d(TAG, "onRequestPermissionsResult: called testing ");
//            } else {
//                Log.d(TAG, "onRequestPermissionsResult: " + "result" + grantResults.length);
//                //  RunTimePermission.cameraPermission(this);
//            }
//        }
//    }

    private void updateSettings() {
        userName = etUserName.getText().toString();
        userStatus = etUserStatus.getText().toString();
        if (TextUtils.isEmpty(userName)) {
        }
        if (TextUtils.isEmpty(userStatus)) {

        } else {

            Contact contact = new Contact();
            contact.setUid(currentUserId);
            contact.setName(userName);
            contact.setStatus(userStatus);
            contact.setImage(userProfileImageUrl);
            contact.setLast_online_time(Util.getCurrentTime());
            contact.setLast_online_date(Util.getCurrentDate());
            contact.setDevice_token(FirebaseInstanceId.getInstance().getToken());
            contact.setLogin_state("online");
            rootRef.child("Users").child(currentUserId).setValue(contact);
            sentUserToMainActivity();

        }
    }

    private void sentUserToMainActivity() {
        Log.d(TAG, "sentUserToMainActivity: activity finished ");
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }


    private void retrieveUserInfo() {
        try {

            // Contact contact=rootRef.child("Users").child(senderUserId).
            rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    Contact contact = dataSnapshot.getValue(Contact.class);
                    Log.d(TAG, "onDataChange: retrive value :: " + contact.toString());
                    if (contact != null) {
                        setDataIntoSettingActivityUI(contact);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onDataChange: " + databaseError.getMessage().toString());

                }
            });
        } catch (Exception e) {
            Glide.with(SettingActivity.this).load(userProfileImageUrl).into(mCircleImageView);

            Log.d(TAG, "retreiveUserInfo: " + e.getLocalizedMessage());
        }
    }

    private void setDataIntoSettingActivityUI(Contact contact) {

        etUserName.setText(contact.getName());

        etUserStatus.setText(contact.getStatus());
        Log.d(TAG, "setDataIntoSettingActivityUI: " + contact.getImage());
        //
        Glide.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/whatapp-35e70.appspot.com/o/Profile%20Image%2FI4AVD2hXzqaXCq71Gzvly7jKOcf1.PNG?alt=media&token=89bd261e-48d1-42f7-9181-ff14b0192fd3").into(mCircleImageView);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void someEvent(String s) {
        if (RunTimePermission.mPermission) {
            if (s.equals("camera")) {
                Log.d(TAG, "someEvent: camera is open");
                if (checkCameraHardware(this))
                    openCamera();
            } else if (s.equals("gallery")) {
                Log.d(TAG, "someEvent: gallery is open");
                selectPicFromGallery();

            }

        } else {
            Log.d(TAG, "someEvent: " + RunTimePermission.mPermission);

        }
    }

    private void selectPicFromGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == CAMERA_REQUEST && data != null) {

            Bitmap image = (Bitmap) data.getExtras().get("data");
            mCircleImageView.setImageBitmap(image);
            saveImage(getImageUri(this, image));

        } else if (requestCode == GALLERY_REQUEST && data != null) {

            Uri contentURI = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                // String path = saveImage(bitmap);
                Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
                mCircleImageView.setImageBitmap(bitmap);
                saveImage(contentURI);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path);
        } catch (Exception e) {
            Log.d(TAG, "getImageUri: " + e.getLocalizedMessage());
            return Uri.parse("");
        }
    }

    public void saveImage(final Uri resultUri) {
        Log.d(TAG, "saveImage: " + resultUri);
        if (resultUri != null) {
            final StorageReference filePath = userProfileImageRef.child(currentUserId + ".PNG");

            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "onSuccess: " + uri.toString());
                                userProfileImageUrl = uri.toString();

                                rootRef.child("Users").child(currentUserId).child("image")
                                        .setValue(userProfileImageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(SettingActivity.this, "Profile Image Updated Successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.d(TAG, "onComplete: image Url updated  " + task.getException().getMessage());
                                            Toast.makeText(SettingActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });


                        Log.d(TAG, "onComplete: image is stored in fireBase database !!");
                    } else {
                        Log.d(TAG, "onComplete: getting error while loading the image !! " + task.getException().getMessage().toString());
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_update_profile:
                updateSettings();

                break;
            case R.id.profile_image: {


                MyDialogFragment myDialogFragment = new MyDialogFragment();

                myDialogFragment.show(getSupportFragmentManager(), "test");
                myDialogFragment.setCancelable(false);

            }
        }

    }
}
