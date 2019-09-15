
package com.lovelycoding.whatapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lovelycoding.whatapp.R;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PhoneLoginActivity";
    private AppCompatEditText etPhoneNumber,etVerificationCode;
    private AppCompatButton btLogin,btVerifyPassword;
    private TextInputLayout tilPhoneNumber,tilVerificationcode;


    //val
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;
    private ProgressDialog loadingBar;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        initView();
        loadingBar = new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        userDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users");
        btLogin.setOnClickListener(this);
        btVerifyPassword.setOnClickListener(this);
    }

    private void initView() {
        etPhoneNumber=findViewById(R.id.et_phone_number);
        etVerificationCode=findViewById(R.id.et_verification_code);
        btLogin=findViewById(R.id.bt_login);
        btVerifyPassword=findViewById(R.id.bt_verify_otp);
        tilPhoneNumber=findViewById(R.id.til_phone_login_email);
        tilVerificationcode=findViewById(R.id.til_phone_login_verify);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.bt_login:
            {
                if(verifyPhoneNumber())
                {
                    etVerificationCode.setVisibility(View.VISIBLE);
                    btVerifyPassword.setVisibility(View.VISIBLE);
                    btLogin.setVisibility(View.GONE);
                    String phone="+91"+etPhoneNumber.getText().toString();
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please wait, while we are authenticating using your phone...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    btLogin.setVisibility(View.INVISIBLE);
                    etPhoneNumber.setVisibility(View.INVISIBLE);
                    loadingBar.show();
                    // PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, PhoneLoginActivity.this, callbacks);

                    callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
                        {
                            signInWithPhoneAuthCredential(phoneAuthCredential);
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e)
                        {
                            Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number, Please enter correct phone number with your country code...", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();

                            etPhoneNumber.setVisibility(View.VISIBLE);
                            btLogin.setVisibility(View.VISIBLE);

                            etVerificationCode.setVisibility(View.INVISIBLE);
                            btVerifyPassword.setVisibility(View.INVISIBLE);
                        }

                        public void onCodeSent(String verificationId,
                                               PhoneAuthProvider.ForceResendingToken token)
                        {
                            // Save verification ID and resending token so we can use them later
                            mVerificationId = verificationId;
                            mResendToken = token;


                            Toast.makeText(PhoneLoginActivity.this, "Code has been sent, please check and verify...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            etPhoneNumber.setVisibility(View.INVISIBLE);
                            btLogin.setVisibility(View.INVISIBLE);

                            etVerificationCode.setVisibility(View.VISIBLE);
                            btVerifyPassword.setVisibility(View.VISIBLE);
                        }
                    };
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phone,60,TimeUnit.SECONDS,PhoneLoginActivity.this,callbacks);


                }
                break;
            }
            case R.id.bt_verify_otp:
            {
                String verificationCode = etVerificationCode.getText().toString();
                if (TextUtils.isEmpty(verificationCode))
                {
                    tilVerificationcode.setError("Code is not Valid  !!");
                }
                else
                {
                    tilVerificationcode.setError("");
                    loadingBar.setTitle("Verification Code");
                    loadingBar.setMessage("Please wait, while we are verifying verification code...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);


                }


            }
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                           // Toast.makeText(PhoneLoginActivity.this, "Congratulations, you're logged in Successfully.", Toast.LENGTH_SHORT).show();
                            String device_token,currentUserId;
                            currentUserId=mAuth.getCurrentUser().getUid();
                            device_token= FirebaseInstanceId.getInstance().getToken();
                            Log.d(TAG, "onComplete:device_token "+device_token);
                            userDatabaseRef.child(currentUserId).child("device_token").setValue(device_token).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    SendUserToMainActivity();
                                }
                            });
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }
    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    private boolean verifyPhoneNumber() {
        if(etPhoneNumber.getText().toString().length()==10)
        {
            return true;
        }
        else
        {
            tilPhoneNumber.setError("Phone number is not correct !!");
            return false;
        }
    }

}
