package com.example.tutoringapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FragmentHome extends Fragment {

    View view;
    TutorProfile tutorProfile;
    SharedPreferences pref;
    Gson gson;
    ImageView img_ProfilePic;
    DatabaseReference mTutor;
    TextView text_Welcome, text_Month;
    CompactCalendarView compactCalendarView;
    ArrayList<Event> arrayListEvents = new ArrayList<>();
    String urlPic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        gson = new Gson();

        pref = getActivity().getApplicationContext().getSharedPreferences("TutorProfile", 0);
        String json = pref.getString("TutorProfile", "");
        tutorProfile = gson.fromJson(json, TutorProfile.class);

        compactCalendarView = view.findViewById(R.id.compactcalendar_view);

        mTutor = FirebaseDatabase.getInstance().getReference().child("TutorInfo").child(tutorProfile.getUid()).child("Responses");
        mTutor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrayListEvents.clear();
                compactCalendarView.removeAllEvents();

                for (DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    ObjectRequest objectRequest = snapshot.getValue(ObjectRequest.class);

                    if (objectRequest.isAccepted && !objectRequest.isCancelled)
                    {
                        Date date = convertToDate(objectRequest.Date);
                        Event event = new Event(Color.GREEN, date.getTime(), objectRequest);
                        arrayListEvents.add(event);
                    }
                }

                compactCalendarView.addEvents(arrayListEvents);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        img_ProfilePic = view.findViewById(R.id.img_ProfilePic);
        text_Welcome = view.findViewById(R.id.text_Welcome);

        Glide.with(this)
                .load(tutorProfile.getUrlPic())
                .apply(RequestOptions.circleCropTransform())
                .into(img_ProfilePic);

        text_Welcome.setText("Welcome Back \n" + tutorProfile.getName() + "!");

        final DateFormat dateFormat = new DateFormat();

        text_Month = view.findViewById(R.id.text_Month);
        text_Month.setText(dateFormat.format("MMMM, yyyy", compactCalendarView.getFirstDayOfCurrentMonth()));

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                for (int i = 0; i < arrayListEvents.size(); i++)
                {
                    ObjectRequest objectRequest = (ObjectRequest) arrayListEvents.get(i).getData();

                    if (dateClicked.compareTo(convertToDate(objectRequest.getDate())) == 0)
                    {
                        final Intent intent = new Intent(getContext(), ActivityRequestChat.class);
                        intent.putExtra("Request", objectRequest);
                        DatabaseReference mImage = FirebaseDatabase.getInstance().getReference().child("TutorInfo").child(objectRequest.getRequesterUid()).child("urlPic");
                        mImage.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                urlPic = dataSnapshot.getValue().toString();
                                intent.putExtra("Image", urlPic);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                text_Month.setText(dateFormat.format("MMMM, yyyy", compactCalendarView.getFirstDayOfCurrentMonth()));
            }
        });


        compactCalendarView.showCalendar();

        return view;
    }

    public Date convertToDate(String strDate)
    {
        Date date = null;

        try {
            date = new SimpleDateFormat("MM/dd/yy").parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
