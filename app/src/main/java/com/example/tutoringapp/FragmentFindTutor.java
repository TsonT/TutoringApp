package com.example.tutoringapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FragmentFindTutor extends Fragment {

    DatabaseReference mTutorInfo;
    ArrayList<TutorProfile> tutorProfileArrayList = new ArrayList<>();
    ArrayList<TutorProfile> filteredArrayList = new ArrayList<>();
    RecyclerView recyclerview_Tutors;
    FloatingActionButton btn_Search;
    ImageButton btn_Filter;
    EditText field_SearchName;
    ArrayList<ObjectChk> lstSubjects = new ArrayList<>();
    ArrayList<ObjectChk> lstSchools = new ArrayList<>();
    ArrayList<String> lstChkSubjects = new ArrayList<>();
    ArrayList<String> lstChkSchools = new ArrayList<>();
    ArrayList<String> lstStrSchools = new ArrayList<>();
    DatabaseReference mSchools = FirebaseDatabase.getInstance().getReference().child("Schools");
    Gson gson = new Gson();
    FirebaseAuth mAtuh = FirebaseAuth.getInstance();
    String strSchools;
    Double minGPA = -1.0;
    View view;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_find_tutor, container, false);
        tutorProfileArrayList.clear();
        final Dialog loadDialog = loadingDialog.create(getContext(), "Loading...");
        loadDialog.show();

        recyclerview_Tutors = view.findViewById(R.id.recyclerview_Tutors);
        btn_Search = view.findViewById(R.id.btn_Search);
        btn_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });
        btn_Filter = view.findViewById(R.id.btn_Filter);
        btn_Filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lstSubjects.clear();
                lstSchools.clear();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final View view = getLayoutInflater().inflate(R.layout.activity_dialog_filter, null);
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                Button btn_Cancel = view.findViewById(R.id.btn_Cancel);
                final EditText field_minGPA = view.findViewById(R.id.field_minGPA);
                if (minGPA > 0)
                {
                    field_minGPA.setText(minGPA.toString());
                }
                btn_Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button btn_Confirm = view.findViewById(R.id.btn_Confirm);
                btn_Confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (field_minGPA.getText().toString().equals(""))
                        {
                            minGPA = -1.0;
                        }
                        else {
                            minGPA = Double.parseDouble(field_minGPA.getText().toString());
                        }

                        lstChkSubjects.clear();

                        for (int i = 0; i < lstSubjects.size(); i++)
                        {
                            if (lstSubjects.get(i).isChecked())
                            {
                                lstChkSubjects.add(lstSubjects.get(i).getName());
                            }
                        }

                        lstChkSchools.clear();

                        for (int i = 0; i < lstSchools.size(); i++)
                        {
                            if (lstSchools.get(i).isChecked())
                            {
                                lstChkSchools.add(lstStrSchools.get(i));
                            }
                        }
                        dialog.dismiss();
                        startSearch();
                    }
                });

                RecyclerView recyclerView_Subjects = view.findViewById(R.id.recyclerview_Subjects);
                for (int i = 0; i < Singleton.getInstance().getArrayList().size(); i++)
                {
                    lstSubjects.add(new ObjectChk(Singleton.getInstance().getArrayList().get(i), false));
                }

                if (lstChkSubjects.size() > 0)
                {
                    for (String i: lstChkSubjects)
                    {
                        for (ObjectChk x: lstSubjects)
                        {
                            if (x.getName().equals(i))
                            {
                                x.setChecked(true);
                            }
                        }
                    }
                }

                RecyclerViewAdapterChecks SubjectAdapter = new RecyclerViewAdapterChecks(view.getContext(), lstSubjects);
                recyclerView_Subjects.setAdapter(SubjectAdapter);
                recyclerView_Subjects.setLayoutManager(new LinearLayoutManager(view.getContext()));

                final RecyclerView recyclerView_Schools = view.findViewById(R.id.recyclerview_Schools);
                mSchools.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        strSchools = dataSnapshot.getValue().toString();
                        Type type = new TypeToken<ArrayList<String>>(){}.getType();
                        lstStrSchools = gson.fromJson(strSchools, type);

                        for (int i = 0; i < lstStrSchools.size(); i++)
                        {
                            lstSchools.add(new ObjectChk(lstStrSchools.get(i), false));
                        }

                        Log.e("test", "test");
                        if (lstChkSchools.size() > 0)
                        {
                            for (String i: lstChkSchools)
                            {
                                for (ObjectChk x: lstSchools)
                                {
                                    if (x.getName().equals(i))
                                    {
                                        x.setChecked(true);
                                    }
                                }
                            }
                        }

                        RecyclerViewAdapterChecks SchoolsAdapter = new RecyclerViewAdapterChecks(view.getContext(), lstSchools);
                        recyclerView_Schools.setAdapter(SchoolsAdapter);
                        recyclerView_Schools.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        field_SearchName = view.findViewById(R.id.field_SearchName);
        field_SearchName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    startSearch();
                    return true;
                }
                return false;
            }
        });
        field_SearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mTutorInfo = FirebaseDatabase.getInstance().getReference().child("TutorInfo");
        mTutorInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadDialog.dismiss();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    TutorProfile tutorProfile = snapshot.getValue(TutorProfile.class);
                    if (tutorProfile.isAvailable && !tutorProfile.getUid().equals(mAtuh.getCurrentUser().getUid())) {
                        tutorProfileArrayList.add(tutorProfile);
                    }
                }
                RecyclerViewAdapterTutorProfile adapter = new RecyclerViewAdapterTutorProfile(getContext(), tutorProfileArrayList);
                recyclerview_Tutors.setLayoutManager(new GridLayoutManager(getContext(), 3));
                recyclerview_Tutors.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    public void startSearch()
    {
        filteredArrayList.clear();
        String filterName = field_SearchName.getText().toString();
        if (filterName.equals("") && lstChkSubjects.size() == 0 && lstChkSchools.size() == 0 && minGPA == -1)
        {
            RecyclerViewAdapterTutorProfile adapter = new RecyclerViewAdapterTutorProfile(getContext(), tutorProfileArrayList);
            recyclerview_Tutors.setAdapter(adapter);
        }
        else
        {
            Log.e("test", "test");
            for (int i = 0; i < tutorProfileArrayList.size(); i++)
            {
                if (tutorProfileArrayList.get(i).getName().toLowerCase().contains(filterName.toLowerCase()) ||filterName.equals("") )
                {
                    if (Double.parseDouble(tutorProfileArrayList.get(i).getGPA()) >= minGPA || minGPA == -1.00)
                    {
                        if (tutorProfileArrayList.get(i).getLstSubjects().containsAll(lstChkSubjects) || lstChkSubjects.size()==0)
                        {
                            if (tutorProfileArrayList.get(i).getLstSchools().containsAll(lstChkSchools) || lstChkSchools.size() == 0)
                            {
                                filteredArrayList.add(tutorProfileArrayList.get(i));
                            }
                        }
                    }
                }
            }
            RecyclerViewAdapterTutorProfile adapter = new RecyclerViewAdapterTutorProfile(getContext(), filteredArrayList);
            recyclerview_Tutors.setAdapter(adapter);

            if (filteredArrayList.size() == 0)
            {
                Toast.makeText(getContext(), "Sorry Could Not Find A Tutor Meeting Those Requirements", Toast.LENGTH_SHORT).show();
            }
        }
    }
}