package com.example.tutoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ActivityLogIn extends AppCompatActivity {

    EditText field_Email, field_Password;
    Button btn_LogIn;
    ConstraintLayout layout;
    TextView text_Register;
    DatabaseReference mDatabase;
    String Name, Uid;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Gson gson;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Log In");

        mAuth = FirebaseAuth.getInstance();
        pref = getApplicationContext().getSharedPreferences("TutorProfile", 0);
        editor = pref.edit();
        gson = new Gson();

        field_Email = findViewById(R.id.field_Email);
        field_Password = findViewById(R.id.field_Password);

        btn_LogIn = findViewById(R.id.btn_LogIn);
        btn_LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogIn(field_Email.getText().toString(), field_Password.getText().toString());
            }
        });

        text_Register = findViewById(R.id.text_Register);
        text_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLogIn.this, ActivityRegister.class);
                startActivity(intent);
            }
        });
    }

    public void startLogIn(String email, String password)
    {
        final Dialog dialog = loadingDialog.create(this, "Logging In...");
        dialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Uid = mAuth.getCurrentUser().getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("TutorInfo").child(Uid);
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    TutorProfile tutorProfile = dataSnapshot.getValue(TutorProfile.class);
                                    String json = gson.toJson(tutorProfile);
                                    editor.putString("TutorProfile", json);
                                    editor.commit();

                                    dialog.dismiss();
                                    Toast.makeText(ActivityLogIn.this, "Log In Successful!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(ActivityLogIn.this, ActivityHome.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        else
                        {
                            dialog.dismiss();
                            Toast.makeText(ActivityLogIn.this, "Log In Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }



}
