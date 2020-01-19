package com.example.tutoringapp;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentPendingRequests extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String Uid = mAuth.getCurrentUser().getUid();
    DatabaseReference mUser = FirebaseDatabase.getInstance().getReference().child("TutorInfo").child(mAuth.getCurrentUser().getUid());
    DatabaseReference mPending = mUser.child("PendingRequests");
    ArrayList<ObjectRequest> lstPendingRequests = new ArrayList<>();
    View view;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_pending_requests, container, false);

        final Dialog dialog = loadingDialog.create(view.getContext(), "Loading...");
        dialog.show();

        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mPending.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                lstPendingRequests.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    lstPendingRequests.add(snapshot.getValue(ObjectRequest.class));
                }
                RecyclerViewAdapterRequests adapter = new RecyclerViewAdapterRequests(view.getContext(), lstPendingRequests);
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

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        mPending.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                lstPendingRequests.clear();

                for (DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    lstPendingRequests.add(snapshot.getValue(ObjectRequest.class));
                }
                RecyclerViewAdapterRequests adapter = new RecyclerViewAdapterRequests(view.getContext(), lstPendingRequests);
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
