package com.example.tutoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.media.Image;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityRequestChat extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    ObjectRequest objectRequest;
    String Name, Image, Email;
    FirebaseAuth Auth = FirebaseAuth.getInstance();
    DatabaseReference mStudentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_chat);

        getSupportActionBar().setTitle("Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        objectRequest = (ObjectRequest) getIntent().getSerializableExtra("Request");
        Name = objectRequest.getRequesterName();
        Image = getIntent().getStringExtra("Image");
        mStudentEmail = FirebaseDatabase.getInstance().getReference().child("TutorInfo").child(objectRequest.getRequesterUid()).child("email");
        mStudentEmail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Email = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewPager = findViewById(R.id.viewPager);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        Fragment fragmentStudentRequest = new FragmentStudentRequest();

        Bundle bundleRequest = new Bundle();
        bundleRequest.putString("Name", Name);
        bundleRequest.putString("Image", Image);
        bundleRequest.putString("Email", Email);
        bundleRequest.putSerializable("Request", objectRequest);
        fragmentStudentRequest.setArguments(bundleRequest);
        adapter.addFragment(fragmentStudentRequest, "");

        Fragment fragmentChat = new FragmentChat();
        Bundle bundleChat = new Bundle();
        if (Auth.getCurrentUser().getUid().equals(objectRequest.getRecipientUid()))
        {
            bundleChat.putString("username", objectRequest.getRecipientName());
            bundleChat.putString("userUid", objectRequest.getRecipientUid());
            bundleChat.putString("recipientUid", objectRequest.getRequesterUid());
        }
        else
        {
            bundleChat.putString("username", objectRequest.getRequesterName());
            bundleChat.putString("userUid", objectRequest.getRequesterUid());
            bundleChat.putString("recipientUid", objectRequest.getRecipientUid());
        }
        bundleChat.putSerializable("Request", objectRequest);
        fragmentChat.setArguments(bundleChat);
        adapter.addFragment(fragmentChat, "");

        viewPager.setAdapter(adapter);

        tabLayout.getTabAt(0).setIcon(R.drawable.gray_requests_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.gray_chat_icon);

    }
}
