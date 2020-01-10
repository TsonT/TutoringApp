package com.example.tutoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityStudentProfile extends AppCompatActivity {

    ObjectRequest objectRequest;
    String Name, Email, Image;
    TextView text_Name, text_Message, text_Date, text_Contact, text_Time, text_Location;
    Button btn_Deny, btn_Accept, btn_Cancel;
    DatabaseReference mStudentEmail;
    DatabaseReference mUsers = FirebaseDatabase.getInstance().getReference().child("TutorInfo");
    ImageView imageView_ProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        objectRequest = (ObjectRequest) getIntent().getSerializableExtra("Request");
        Name = objectRequest.getRequesterName();
        Image = getIntent().getStringExtra("Image");
        mStudentEmail = FirebaseDatabase.getInstance().getReference().child("TutorInfo").child(objectRequest.getRequesterUid()).child("email");
        mStudentEmail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Email = dataSnapshot.getValue().toString();
                text_Contact = findViewById(R.id.textview_Contact);
                text_Contact.setText(Email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        text_Name = findViewById(R.id.textview_Name);
        text_Name.setText(Name);
        text_Message = findViewById(R.id.textview_Message);
        text_Message.setText(objectRequest.getMessage());
        text_Date = findViewById(R.id.textview_Date);
        text_Date.setText(objectRequest.getDate());
        text_Time = findViewById(R.id.textview_Time);
        text_Time.setText(objectRequest.getTime());
        text_Location = findViewById(R.id.textview_Location);
        text_Location.setText(objectRequest.getLocation());

        btn_Deny = findViewById(R.id.btn_Deny);
        btn_Deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityStudentProfile.this);
                View viewComment = getLayoutInflater().inflate(R.layout.activity_dialog_comment, null);
                builder.setView(viewComment);
                final AlertDialog dialog = builder.create();
                dialog.show();
                Button btn_Cancel = viewComment.findViewById(R.id.btn_Cancel);
                btn_Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                final EditText field_Comment = viewComment.findViewById(R.id.field_Comment);

                Button btn_Confirm = viewComment.findViewById(R.id.btn_Confirm);
                btn_Confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        objectRequest.setAccepted(false);
                        makeResponses();
                        Toast.makeText(ActivityStudentProfile.this, "Request Denied", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(dialog.getContext(), ActivityHome.class);
                        startActivity(intent);
                    }
                });
            }
        });
        btn_Accept = findViewById(R.id.btn_Accept);
        btn_Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objectRequest.setAccepted(true);
                makeResponses();
                Toast.makeText(ActivityStudentProfile.this, "Request Accepted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ActivityStudentProfile.this, ActivityHome.class);
                startActivity(intent);
            }
        });

        imageView_ProfilePic = findViewById(R.id.imageview_ProfilePic);
        Glide.with(this)
                .load(Image)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView_ProfilePic);

        if (objectRequest.isAccepted != null)
            if (objectRequest.isAccepted)
            {
                btn_Deny.setVisibility(View.GONE);
                btn_Accept.setVisibility(View.GONE);
                btn_Cancel = findViewById(R.id.btn_Cancel);
                btn_Cancel.setVisibility(View.VISIBLE);
                btn_Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        mUsers.child(objectRequest.getRequesterUid()).child("Responses").child(objectRequest.getKey()).child("cancelled").setValue(true);
                                        mUsers.child(objectRequest.getRecipientUid()).child("Responses").child(objectRequest.getKey()).child("cancelled").setValue(true);
                                        dialog.dismiss();
                                        Intent intent = new Intent(ActivityStudentProfile.this, ActivityHome.class);
                                        startActivity(intent);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityStudentProfile.this);
                        builder.setMessage("Are You Sure You Would Like To Cancel This Request?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                });
            }

    }

    public void makeResponses()
    {
        DatabaseReference mTutor = FirebaseDatabase.getInstance().getReference().child("TutorInfo").child(objectRequest.getRecipientUid()).child("PendingRequests").child(objectRequest.getKey());
        mTutor.removeValue();
        DatabaseReference mTutorResponses = FirebaseDatabase.getInstance().getReference().child("TutorInfo").child(objectRequest.getRecipientUid()).child("Responses").child(objectRequest.getKey());
        mTutorResponses.setValue(objectRequest);

        DatabaseReference mStudent = FirebaseDatabase.getInstance().getReference().child("TutorInfo").child(objectRequest.getRequesterUid()).child("SentRequests").child(objectRequest.getKey());
        mStudent.removeValue();
        DatabaseReference mStudentResponses = FirebaseDatabase.getInstance().getReference().child("TutorInfo").child(objectRequest.getRequesterUid()).child("Responses").child(objectRequest.getKey());
        mStudentResponses.setValue(objectRequest);
    }
}
