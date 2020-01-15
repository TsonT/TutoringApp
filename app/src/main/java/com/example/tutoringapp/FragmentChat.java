package com.example.tutoringapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class FragmentChat extends Fragment {

    View view;
    String username, userUid, recipientUid, strDate, strTime;
    DatabaseReference mChat = FirebaseDatabase.getInstance().getReference().child("ChatLogs");
    DatabaseReference mUser = FirebaseDatabase.getInstance().getReference().child("TutorInfo");
    ArrayList<ObjectMessage> objectMessageArrayList;
    ObjectRequest objectRequest;
    RecyclerView recyclerView_Chat;
    EditText field_Message;
    ScrollView scrollView;
    Button btnSend;
    RecyclerViewAdapterChat recyclerViewAdapterChat;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        Bundle bundle = getArguments();

        username = bundle.getString("username");
        userUid = bundle.getString("userUid");
        recipientUid = bundle.getString("recipientUid");
        objectRequest = (ObjectRequest) bundle.getSerializable("Request");

        scrollView = view.findViewById(R.id.scrollView);

        mChat.child(objectRequest.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                {
                    GenericTypeIndicator<ArrayList<ObjectMessage>> typeIndicator = new GenericTypeIndicator<ArrayList<ObjectMessage>>() {};
                    objectMessageArrayList = dataSnapshot.getValue(typeIndicator);
                }
                else
                {
                    objectMessageArrayList = new ArrayList<>();
                }

                recyclerView_Chat = view.findViewById(R.id.recyclerView_Chat);
                recyclerViewAdapterChat = new RecyclerViewAdapterChat(getContext(), objectMessageArrayList);
                recyclerView_Chat.setAdapter(recyclerViewAdapterChat);
                recyclerView_Chat.setLayoutManager(new LinearLayoutManager(view.getContext()));
                recyclerView_Chat.scrollToPosition(objectMessageArrayList.size()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        btnSend = view.findViewById(R.id.btnSend);
        field_Message = view.findViewById(R.id.field_Message);
        field_Message.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    SendMessage();

                    return true;
                }
                return false;
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        mChat.child(objectRequest.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                {
                    GenericTypeIndicator<ArrayList<ObjectMessage>> typeIndicator = new GenericTypeIndicator<ArrayList<ObjectMessage>>() {};
                    objectMessageArrayList = dataSnapshot.getValue(typeIndicator);
                    recyclerViewAdapterChat = new RecyclerViewAdapterChat(getContext(), objectMessageArrayList);
                    recyclerView_Chat.setAdapter(recyclerViewAdapterChat);
                    recyclerView_Chat.scrollToPosition(objectMessageArrayList.size()-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    public void SendMessage()
    {
        String Message = field_Message.getText().toString();

        SimpleDateFormat sdfDay = new SimpleDateFormat("MM/dd/YYYY", Locale.getDefault());
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

        strDate = sdfDay.format(new Date());
        strTime = sdfTime.format(new Date());

        ObjectMessage objectMessage = new ObjectMessage(username, strTime, Message, strDate);
        objectMessageArrayList.add(objectMessage);
        mChat.child(objectRequest.getKey()).setValue(objectMessageArrayList);

        field_Message.setText("");
    }
}
