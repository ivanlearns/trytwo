package com.example.trytwoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText mUserMail, mUserPassword, mRepeatUserPassword;
    private Button mRegisterButton;
    private TextView mBackToLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference dRef;



    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        mBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });





    }

    private void InitializeFields() {

        mUserMail = findViewById(R.id.register_email);
        mUserPassword = findViewById(R.id.register_password);
        mRepeatUserPassword = findViewById(R.id.repeat_password);
        mBackToLogin = findViewById(R.id.back_to_login_button);
        mRegisterButton = findViewById(R.id.register_page_button);

        loadingBar = new ProgressDialog(this);
    }

    private void CreateNewAccount(){
        final String newEmail = mUserMail.getText().toString().trim();
        String newPassword = mUserPassword.getText().toString().trim();
        String repeatPassword = mRepeatUserPassword.getText().toString().trim();


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

         if (!newPassword.equals(repeatPassword)){
            mRepeatUserPassword.setError("Las contraseñas deben ser iguales.");

        }else{
             loadingBar.setTitle("Creando una nueva cuenta");
             loadingBar.setMessage("Por favor, espere.");
             loadingBar.setCanceledOnTouchOutside(true);
             loadingBar.show();

            mAuth.createUserWithEmailAndPassword(newEmail, newPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                 if(task.isSuccessful()){
                     String currentUserID = mAuth.getCurrentUser().getUid();
                     dRef.child("Users").child(currentUserID).setValue("");
                     Toast.makeText(RegisterActivity.this, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
                     loadingBar.dismiss();
                     SendUserToMainActivity();
                 }
                 else {String message = task.getException().toString();
                     Toast.makeText(RegisterActivity.this,"Error: " + message, Toast.LENGTH_SHORT).show();
                     loadingBar.dismiss();

             }
         }

         });}

}

    private void SendUserToMainActivity() {

        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}