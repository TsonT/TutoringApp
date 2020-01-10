package com.example.tutoringapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityLeaveReview extends AppCompatActivity {

    String reviewerUid, reviewedUid, reviewerName;
    Float numStars;
    Button btn_Confirm;
    EditText field_Comment;
    RatingBar ratingBar;
    Boolean isReviewForTutor;
    DatabaseReference mUsers = FirebaseDatabase.getInstance().getReference().child("TutorInfo");
    DatabaseReference mReviews;
    String Key;
    ObjectRequest objectRequest;
    TextView text_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Leave A Review");

        reviewerUid  = getIntent().getStringExtra("ReviewerUid");
        reviewedUid = getIntent().getStringExtra("ReviewedUid");
        reviewerName = getIntent().getStringExtra("ReviewerName");
        isReviewForTutor = getIntent().getBooleanExtra("isReviewForTutor", false);
        objectRequest = (ObjectRequest) getIntent().getSerializableExtra("ObjectRequest");

        text_Name = findViewById(R.id.text_Name);
        text_Name.setText(objectRequest.getRecipientName());

        mReviews = mUsers.child(reviewedUid).child("Reviews");

        ratingBar = findViewById(R.id.ratingBar);


        field_Comment = findViewById(R.id.field_Comment);
        btn_Confirm = findViewById(R.id.btn_Confirm);
        btn_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numStars = ratingBar.getRating();
                Key = mReviews.push().getKey();
                ObjectReview objectReview = new ObjectReview(numStars, field_Comment.getText().toString(), reviewerName, Key);
                mReviews.child(Key).setValue(objectReview);
                mUsers.child(reviewerUid).child("Responses").child(objectRequest.getKey()).removeValue();
                Toast.makeText(ActivityLeaveReview.this, "Thank You For Your Input!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ActivityLeaveReview.this, ActivityHome.class);
                startActivity(intent);
            }
        });
    }
}
