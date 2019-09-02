package com.lovelycoding.whatapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.util.Util;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    //Ui component
    private AppCompatButton btCreateUserAccount;
    private AppCompatEditText etUserEmail, etUserPassword;
    private AppCompatTextView tvAlreadyRegister;
    private TextInputLayout tilUserEmail, tilUserPassword;
    private ProgressDialog mProgressBar;
    //Var
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        mAuth = FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();
        tvAlreadyRegister.setOnClickListener(this);
        btCreateUserAccount.setOnClickListener(this);

    }

    private void initView() {
        btCreateUserAccount = findViewById(R.id.bt_register_user);
        etUserEmail = findViewById(R.id.et_register_email);
        etUserPassword = findViewById(R.id.et_register_password);
        tvAlreadyRegister = findViewById(R.id.tv_already_register);
        tilUserEmail = findViewById(R.id.til_register_email);
        tilUserPassword = findViewById(R.id.til_register_password);
        mProgressBar = new ProgressDialog(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_already_register: {
                sendUserToLoginActivity();
                break;
            }
            case R.id.bt_register_user: {

                String email=etUserEmail.getText().toString().trim();
                String password=etUserPassword.getText().toString().trim();
                if (verifyUserInput(email,password)) {
                    mProgressBar.setTitle("Creating New Account ");
                    mProgressBar.setMessage("Please wait while we crating new Account ..");
                    mProgressBar.setCanceledOnTouchOutside(true);
                    mProgressBar.show();
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        String currentUserId=mAuth.getCurrentUser().getUid();
                                        rootRef.child("Users").child(currentUserId).setValue("");
                                        Toast.makeText(RegisterActivity.this, "Register sucessfully ", Toast.LENGTH_SHORT).show();
                                        mProgressBar.cancel();
                                        sendUserToMainActivity();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Error::" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        mProgressBar.cancel();
                                    }
                                }
                            });
                } else {
                    // Toast.makeText(this, "EmailId or Password is empty ", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean verifyUserInput(String email, String password) {


        if (!email.isEmpty()) {

            // tilUserEmail.setError(""+etUserEmail.getText().toString());
            if (!password.isEmpty() && password.length() >= 6) {
                if (Util.verifyEmailId(email)) {
                    return true;
                } else {
                    tilUserEmail.setError("Email Id is not Valid !!");
                }
            } else {
                tilUserPassword.setError("Password should gratern then 6 digit  !!");
            }

        } else
            tilUserEmail.setError("Email Id is empty ");
        return false;
    }

    private void sendUserToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }private void sendUserToMainActivity() {
        Intent intent=new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
