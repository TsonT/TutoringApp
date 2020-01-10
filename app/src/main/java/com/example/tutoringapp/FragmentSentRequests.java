package com.example.tutoringapp;

import android.app.Dialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentSentRequests extends Fragment {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String Uid = mAuth.getCurrentUser().getUid();
    DatabaseReference mUser = FirebaseDatabase.getInstance().getReference().child("TutorInfo").child(Uid);
    DatabaseReference mSent = mUser.child("SentRequests");
    ArrayList<ObjectRequest> lstSentRequests = new ArrayList<>();
    RecyclerView recyclerView;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_sent_requests, container, false);

        final Dialog dialog = new loadingDialog().create(view.getContext(), "Loading...");
        recyclerView = view.findViewById(R.id.recyclerView);

        dialog.show();
        mSent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e("string", "string");

                lstSentRequests.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    lstSentRequests.add(snapshot.getValue(ObjectRequest.class));
                }
                RecyclerViewAdapterRequests adapter = new RecyclerViewAdapterRequests(view.getContext(), lstSentRequests);
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                recyclerView.setAdapter(adapter);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        return view;
    }
}
