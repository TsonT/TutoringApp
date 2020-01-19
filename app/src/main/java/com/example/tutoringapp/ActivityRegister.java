package com.example.tutoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.storage.UploadTask;

public class ActivityRegister extends AppCompatActivity {

    EditText field_Email, field_Password, field_ConfirmPassword;

    Button btn_Register;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Register");

        mAuth = FirebaseAuth.getInstance();

        field_Email = findViewById(R.id.field_Email);
        field_Password = findViewById(R.id.field_Password);
        field_ConfirmPassword = findViewById(R.id.field_ConfirmPassword);
        btn_Register = findViewById(R.id.btn_Confirm);

        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.fetchSignInMethodsForEmail(field_Email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                                if (isNewUser)
                                {
                                    if (field_Password.getText().toString().trim().length()!=6)
                                    {
                                        Toast.makeText(ActivityRegister.this, "Your Password Has To Be 6 Characters", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        if (!field_Password.getText().toString().equals(field_ConfirmPassword.getText().toString()))
                                        {
                                            Toast.makeText(ActivityRegister.this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (field_Email.getText().length() < 2)
                                        {
                                            Toast.makeText(ActivityRegister.this, "Please Enter A Valid Email", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (field_Password.getText().length() < 2)
                                        {
                                            Toast.makeText(ActivityRegister.this, "Please Enter A Valid Password", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            startRegistration(field_Email.getText().toString(), field_Password.getText().toString());
                                            Intent intent = new Intent (ActivityRegister.this, ActivityLogIn.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                                else
                                {
                                    Toast.makeText(ActivityRegister.this, "That Email Is Already Registered! Please Enter A Different One", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });
    }

    public void startRegistration(final String email, String password)
    {
        final Dialog dialog = loadingDialog.create(this, "Loading...");
        dialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            dialog.dismiss();
                            Toast.makeText(ActivityRegister.this, "An Email Has Been Sent To " + email + " Please Verify That This Is Yours!", Toast.LENGTH_SHORT).show();
                            mAuth.getCurrentUser().sendEmailVerification();
                        }
                    }
                });
    }
}
