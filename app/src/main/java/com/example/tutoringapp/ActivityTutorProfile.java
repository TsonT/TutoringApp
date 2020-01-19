package com.example.tutoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityTutorProfile extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    TutorProfile tutorProfile;
    TutorProfile requester;
    DatabaseReference mRequester, mTutor;
    FirebaseAuth mAuth;
    String Uid, strRequestDate, token;
    ImageView imageView_ProfilePic;
    TextView textView_Name, textView_Contact, textView_GPA, text_Stars;
    RecyclerView recyclerview_Subjects, recyclerview_Schools, recyclerView_Reviews;
    Button btn_Request;
    ArrayList<ObjectReview> objectReviewArrayList = new ArrayList<>();
    Float totalStars = (float) 0, avgStars = (float) 0;
    String time, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        tutorProfile = (TutorProfile) getIntent().getSerializableExtra("tutorProfile");

        mAuth = FirebaseAuth.getInstance();

        Uid = mAuth.getCurrentUser().getUid();
        mRequester = FirebaseDatabase.getInstance().getReference().child("TutorInfo").child(Uid);
        mRequester.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                requester = dataSnapshot.getValue(TutorProfile.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageView_ProfilePic = findViewById(R.id.imageview_ProfilePic);
        Glide.with(this)
                .load(tutorProfile.getUrlPic())
                .apply(RequestOptions.circleCropTransform())
                .into(imageView_ProfilePic);
        textView_Name = findViewById(R.id.textview_Name);
        textView_Name.setText(tutorProfile.getName());

        textView_Contact = findViewById(R.id.textview_Contact);
        textView_Contact.setText(tutorProfile.getEmail());

        textView_GPA = findViewById(R.id.textview_GPA);
        textView_GPA.setText(tutorProfile.getGPA());

        recyclerview_Subjects = findViewById(R.id.recyclerview_Subjects);
        RecyclerViewAdapterStrings adapterSubjects = new RecyclerViewAdapterStrings(this, tutorProfile.getLstSubjects());
        recyclerview_Subjects.setAdapter(adapterSubjects);
        recyclerview_Subjects.setLayoutManager(new LinearLayoutManager(this));

        recyclerview_Schools = findViewById(R.id.recyclerview_Schools);
        RecyclerViewAdapterStrings adapterSchools = new RecyclerViewAdapterStrings(this, tutorProfile.getLstSchools());
        recyclerview_Schools.setAdapter(adapterSchools);
        recyclerview_Schools.setLayoutManager(new LinearLayoutManager(this));

        recyclerView_Reviews = findViewById(R.id.recyclerview_Reviews);
        text_Stars = findViewById(R.id.text_Stars);

        mTutor = FirebaseDatabase.getInstance().getReference().child("TutorInfo").child(tutorProfile.getUid()).child("Reviews");
        mTutor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    ObjectReview objectReview = snapshot.getValue(ObjectReview.class);
                    objectReviewArrayList.add(objectReview);
                    totalStars = totalStars + objectReview.getStars();
                }

                avgStars = totalStars / (float) objectReviewArrayList.size();
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                decimalFormat.setRoundingMode(RoundingMode.CEILING);

                RecyclerViewAdapterReviews adapterReviews = new RecyclerViewAdapterReviews(ActivityTutorProfile.this, objectReviewArrayList);
                recyclerView_Reviews.setLayoutManager(new LinearLayoutManager(ActivityTutorProfile.this));
                recyclerView_Reviews.setAdapter(adapterReviews);

                text_Stars.setText(decimalFormat.format(avgStars));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btn_Request = findViewById(R.id.btn_Request);
        btn_Request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                int month = Calendar.getInstance().get(Calendar.MONTH);
                int year = Calendar.getInstance().get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ActivityTutorProfile.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDayOfMonth) {
                        mMonth = mMonth + 1;
                        strRequestDate = mMonth + "/" + mDayOfMonth + "/" + mYear;

                        DialogFragment timePicker = new TimePickerFragment();
                        timePicker.show(getSupportFragmentManager(), "time picker");


                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String middayStatus;
        if (hourOfDay >= 12)
        {
            middayStatus = "pm";
            hourOfDay = hourOfDay - 12;
        }
        else
        {
            middayStatus = "am";
        }
        if (minute < 10)
        {
            time = hourOfDay + ":" + "0" + minute + " " + middayStatus;
        }
        else
        {
            time = hourOfDay + ":" + minute + " " + middayStatus;
        }


        AlertDialog.Builder builderLocation = new AlertDialog.Builder(ActivityTutorProfile.this);
        View viewLocation = getLayoutInflater().inflate(R.layout.activity_dialog_comment, null);
        builderLocation.setView(viewLocation);
        final AlertDialog dialogLocation = builderLocation.create();
        dialogLocation.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogLocation.show();

        final EditText field_Location = viewLocation.findViewById(R.id.field_Comment);
        TextView text_Title = viewLocation.findViewById(R.id.text_title);
        text_Title.setText("Please Enter The Location You Would Like To Meet At");

        Button btn_LocationConfirm = dialogLocation.findViewById(R.id.btn_Confirm);
        btn_LocationConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = field_Location.getText().toString();
                dialogLocation.dismiss();


                AlertDialog.Builder builderComment = new AlertDialog.Builder(ActivityTutorProfile.this);
                View viewComment = getLayoutInflater().inflate(R.layout.activity_dialog_comment, null);
                builderComment.setView(viewComment);
                final AlertDialog dialogComment = builderComment.create();
                dialogComment.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogComment.show();

                Button btn_Cancel = viewComment.findViewById(R.id.btn_Cancel);
                btn_Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogComment.dismiss();
                    }
                });

                final EditText field_Comment = viewComment.findViewById(R.id.field_Comment);

                Button btn_Confirm = viewComment.findViewById(R.id.btn_Confirm);
                btn_Confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final DatabaseReference mRequests = FirebaseDatabase.getInstance().getReference().child("TutorInfo").child(tutorProfile.getUid()).child("PendingRequests");

                        final Dialog dialog1 = new loadingDialog().create(ActivityTutorProfile.this, "Sending...");
                        dialog1.show();

                        final String strDate, strTime;
                        final String strDateTime;

                        final SimpleDateFormat sdfDay = new SimpleDateFormat("MM/dd/YYYY", Locale.getDefault());
                        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

                        strDate = sdfDay.format(new Date());
                        strTime = sdfTime.format(new Date());

                        strDateTime = strDate + " - " + strTime;

                        mRequests.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                                {
                                    String pastDate = snapshot.child("date").getValue().toString();
                                    if (pastDate.equals(strDate))
                                    {
                                        Toast.makeText(ActivityTutorProfile.this, "Sorry Your Tutor Is Already Booked That Day! Please Select A Different Day Or Tutor", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        token = mRequests.push().getKey();
                                        final ObjectRequest objectRequest = new ObjectRequest(Uid, tutorProfile.getUid(), token, field_Comment.getText().toString(), strRequestDate, null, time, location, requester.getName(), tutorProfile.getName(), strDateTime);

                                        mRequests.child(token).setValue(objectRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                DatabaseReference mSent = FirebaseDatabase.getInstance().getReference().child("TutorInfo").child(Uid).child("SentRequests");
                                                mSent.child(token).setValue(objectRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        dialog1.dismiss();
                                                        Toast.makeText(ActivityTutorProfile.this, "Request Successfully Sent!", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(ActivityTutorProfile.this, ActivityHome.class);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        });

        Button btn_LocationCancel = dialogLocation.findViewById(R.id.btn_Cancel);
        btn_LocationCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLocation.dismiss();
            }
        });
    }
}


