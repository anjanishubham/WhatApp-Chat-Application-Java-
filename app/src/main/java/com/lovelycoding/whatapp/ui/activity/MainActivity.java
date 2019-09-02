package com.lovelycoding.whatapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.adapter.TabAcessorAdapter;
import com.lovelycoding.whatapp.viewmodel.GroupViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //UI component
    private Toolbar mToolbar;
    private ViewPager myViewpager;
    private TabLayout myTablayout;




    private TabAcessorAdapter myTabAcessorAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
   public static GroupViewModel mGroupViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        rootRef=FirebaseDatabase.getInstance().getReference();
        if(currentUser!=null)
        Log.d(TAG, "onCreate: CurrentUser"+currentUser.getDisplayName()+currentUser.getEmail());
        initToolbar();
        initViewPage();

    }

    private void initViewPage() {

        myTablayout=findViewById(R.id.main_tabs_layout);
        myViewpager=findViewById(R.id.main_view_pager);

        myTabAcessorAdapter=new TabAcessorAdapter(getSupportFragmentManager());
        myViewpager.setAdapter(myTabAcessorAdapter);
        myTablayout.setupWithViewPager(myViewpager);
    }

    private void initToolbar() {
        mToolbar=findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WhatApp");

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser==null)
        {
            sendUserToLoginActivity();
        }
        else {
            verifyUserExistence();
        }
    }

    private void verifyUserExistence() {
        String  currentUserId=mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.child("name").exists()))
                {
                    Toast.makeText(MainActivity.this, "welcome", Toast.LENGTH_SHORT).show();
                }
                else {
                        sentUserToProfileSetting();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToLoginActivity() {

        Intent intent=new Intent(this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.main_logout_option: {
                mAuth.signOut();
                sendUserToLoginActivity();
                return true;
            }
            case R.id.main_setting_option:{
                sentUserToProfileSetting();
                     return true;}

            case R.id.main_find_firend_option:{
                return true;
            }
            case R.id.main_new_group_option:
            {
                sentUserToCreateNewGroup();
            }

            default:
                return true;
        }
    }

    private void sentUserToCreateNewGroup() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this,R.style.AlertDialog);
        mBuilder.setTitle("Enter New Group");
        final EditText editText=new EditText(this);
        editText.setHint(" enter group name ..");
        mBuilder.setView(editText);
        mBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String gName=editText.getText().toString();
                if(TextUtils.isEmpty(gName))
                {
                    Toast.makeText(MainActivity.this, "Enter Group Name", Toast.LENGTH_SHORT).show();
                }
                else {
                    createNewGroup(editText.getText().toString());
                    dialogInterface.cancel();
                }
            }
        });

        mBuilder.show();
    }

    private void createNewGroup(String gName) {
        rootRef.child("Groups").child(gName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Log.d(TAG, "group Successfully created ");
                }
                else {
                    Log.d(TAG, "having some issue while creating the Group ");
                }
            }
        });
    }

    private void sentUserToProfileSetting() {
        Intent intent=new Intent(this,SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

    }
}
