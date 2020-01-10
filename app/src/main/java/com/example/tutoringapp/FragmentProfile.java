package com.example.tutoringapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class FragmentProfile extends Fragment {

    TutorProfile tutorProfile;
    TutorProfile requester;
    DatabaseReference mRequester, mTutor;
    FirebaseAuth mAuth;
    String Uid, strRequestDate, token;
    ImageView imageView_ProfilePic;
    TextView textView_Name, textView_Contact, textView_GPA, text_Stars;
    RecyclerView recyclerview_Subjects, recyclerview_Schools, recyclerView_Reviews;
    Button btn_Edit;
    ArrayList<ObjectReview> objectReviewArrayList = new ArrayList<>();
    Float totalStars = (float) 0, avgStars = (float) 0;
    View view;
    SharedPreferences pref;
    Gson gson;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        gson = new Gson();

        pref = getActivity().getApplicationContext().getSharedPreferences("TutorProfile", 0);
        String json = pref.getString("TutorProfile", "");
        tutorProfile = gson.fromJson(json, TutorProfile.class);

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

        imageView_ProfilePic = view.findViewById(R.id.imageview_ProfilePic);
        Glide.with(this)
                .load(tutorProfile.getUrlPic())
                .apply(RequestOptions.circleCropTransform())
                .into(imageView_ProfilePic);
        textView_Name = view.findViewById(R.id.textview_Name);
        textView_Name.setText(tutorProfile.getName());

        textView_Contact = view.findViewById(R.id.textview_Contact);
        textView_Contact.setText(tutorProfile.getEmail());

        textView_GPA = view.findViewById(R.id.textview_GPA);
        textView_GPA.setText(tutorProfile.getGPA());

        recyclerview_Subjects = view.findViewById(R.id.recyclerview_Subjects);
        RecyclerViewAdapterStrings adapterSubjects = new RecyclerViewAdapterStrings(getContext(), tutorProfile.getLstSubjects());
        recyclerview_Subjects.setAdapter(adapterSubjects);
        recyclerview_Subjects.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerview_Schools = view.findViewById(R.id.recyclerview_Schools);
        RecyclerViewAdapterStrings adapterSchools = new RecyclerViewAdapterStrings(getContext(), tutorProfile.getLstSchools());
        recyclerview_Schools.setAdapter(adapterSchools);
        recyclerview_Schools.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView_Reviews = view.findViewById(R.id.recyclerview_Reviews);
        text_Stars = view.findViewById(R.id.text_Stars);

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

                RecyclerViewAdapterReviews adapterReviews = new RecyclerViewAdapterReviews(getContext(), objectReviewArrayList);
                recyclerView_Reviews.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView_Reviews.setAdapter(adapterReviews);

                text_Stars.setText(decimalFormat.format(avgStars));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_Edit = view.findViewById(R.id.btn_Edit);
        btn_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivitySetProfile.class);
                intent.putExtra("isRegistering", false);
                startActivity(intent);
            }
        });
        
        return view;
    }
}
