package com.example.trytwoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser mCurrentUser;
    private Button mLoginButton;
    private TextView mForgotPassword, mNeeedAccount, mPhoneLogin;
    private EditText mUserMail, mUserPassword;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        
        InitializeFields();
        mProgressBar.setVisibility(View.GONE);
        mNeeedAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginToAccount();
            }
        });

        mPhoneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneI = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                startActivity(phoneI);
            }
        });
        
    }

    private void InitializeFields() {

        mLoginButton = findViewById(R.id.login_button);
        mUserMail = findViewById(R.id.login_email);
        mUserPassword = findViewById(R.id.login_password);
        mForgotPassword = findViewById(R.id.forgot_password);
        mNeeedAccount = findViewById(R.id.goToRegister_text);
        mPhoneLogin = findViewById(R.id.register_text_phone);
        mProgressBar = findViewById(R.id.progress_Bar_login);

    }

   /*@Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser != null){
            SendUserToMainActivity();
        }
    }*/

    private void SendUserToMainActivity() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();

    }

    private void LoginToAccount(){
        String newEmail = mUserMail.getText().toString().trim();
        String newPassword = mUserPassword.getText().toString().trim();
        mProgressBar.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();

        if (TextUtils.isEmpty(newEmail)) {
            mUserMail.setError("Debes introducir un correo.");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            mUserPassword.setError("Debes Introducir una contraseña");
        }

        if (newPassword.length() < 6) {
            mUserPassword.setError("La contraseña debe tener 6 caraceteres.");

        }
        else{
            mAuth.signInWithEmailAndPassword(newEmail, newPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        SendUserToMainActivity();
                        Toast.makeText(LoginActivity.this, "Login Correcto", Toast.LENGTH_SHORT).show();

                        mProgressBar.setVisibility(View.GONE);
                    }
                    else{
                        String message = task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            });
        }


    }
}