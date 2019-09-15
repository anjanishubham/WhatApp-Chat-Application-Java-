package com.lovelycoding.whatapp.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.util.Util;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    //Ui component
    private AppCompatButton btUserSignInWithEmail, btSignInwithPhoneNumber;
    private AppCompatEditText etUserEmail, etUserPassword;
    private AppCompatTextView tvNeedNewAccountLink,tvForgetPassword;
    private AppCompatImageView ivLoginImage;
    private TextInputLayout tilUserEmail,tilUserPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressBar;
    private DatabaseReference userDatabaseRef;

////    SharedPreferences sp=getSharedPreferences("Login", 0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        userDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users");
        initView();

        checkUserLoginFirstTime();
        tvNeedNewAccountLink.setOnClickListener(this);
        btUserSignInWithEmail.setOnClickListener(this);
        btSignInwithPhoneNumber.setOnClickListener(this);
    }

    private void checkUserLoginFirstTime() {



    }


    private void initView() {
        btUserSignInWithEmail = findViewById(R.id.bt_login_emailId);
        btSignInwithPhoneNumber =findViewById(R.id.bt_login_with_phone_number);
        etUserEmail =findViewById(R.id.et_login_email);
        etUserPassword =findViewById(R.id.et_login_password);
        ivLoginImage=findViewById(R.id.iv_login_image);
        tvForgetPassword=findViewById(R.id.tv_forget_password);
        tvNeedNewAccountLink=findViewById(R.id.tv_need_new_account_link);
        tilUserEmail=findViewById(R.id.til_login_email);
        tilUserPassword=findViewById(R.id.til_login_password);
        mProgressBar=new ProgressDialog(this);

    }


    private void sendUserToMainActivity() {

        MainActivity.isTest=false;
        startActivity(new Intent(this,MainActivity.class));
        finishAffinity();
    }

    @Override
    public void onClick(View view) {

       switch (view.getId())
       {
           case R.id.tv_need_new_account_link:
           {
               sendUserToRegisterActivity();
                break;
           }
           case R.id.bt_login_emailId:
           {
               String email=etUserEmail.getText().toString().trim();
               String password=etUserPassword.getText().toString().trim();
               if(verifyUserInput(email,password))
               {
                   mProgressBar.setTitle("Sign In");
                   mProgressBar.setMessage("Please wait your account is login  ..");
                   mProgressBar.setCanceledOnTouchOutside(true);
                   mProgressBar.show();
                   mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {

                           if(task.isSuccessful())
                           {
                               mProgressBar.cancel();
                               Log.d(TAG, "onComplete:LoginSuccessful "+task.toString());

                               String device_token,currentUserId;
                               currentUserId=mAuth.getCurrentUser().getUid();
                               device_token= FirebaseInstanceId.getInstance().getToken();
                               Log.d(TAG, "onComplete:device_token "+device_token);
                               userDatabaseRef.child(currentUserId).child("device_token").setValue(device_token).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       sentUserToMainActivity();
                                   }
                               });



                           }
                           else {
                               Toast.makeText(LoginActivity.this, "Error::"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                               Log.d(TAG, "onComplete: LoginFail: "+task.getException().toString());
                               mProgressBar.cancel();
                           }
                       }
                   });
               }
               break;
           }
           case R.id.bt_login_with_phone_number:
               sentToUserToPhoneLoginActivity();
               break;
       }
    }

    private void sentToUserToPhoneLoginActivity() {
        Intent intent=new Intent(this, PhoneLoginActivity.class);
       // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sentUserToMainActivity() {
        Intent intent=new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToRegisterActivity() {
        Intent intent=new Intent(this,RegisterActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private boolean verifyUserInput(String email,String password) {


        if(!email.isEmpty())
        {

            if(!password.isEmpty())
            {
                if(Util.verifyEmailId(email)) {
                    return true;
                }
                else {
                    tilUserEmail.setError("Email Id is not Valid !!");
                }
            }
            else {
                tilUserPassword.setError("Password should gratern then 6 digit  !!");
            }

        }
        else
            tilUserEmail.setError("Email Id is empty ");
        return false;
    }
}
