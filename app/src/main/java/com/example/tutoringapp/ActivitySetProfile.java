package com.example.tutoringapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ActivitySetProfile extends AppCompatActivity {

    TextView textBioTitle, textSchools, textTitle;
    ScrollView scrollView;
    ImageView img_ProfilePic;
    Button btn_Confirm;
    EditText field_Name, field_GPA, field_Bio;
    RadioButton rad_Yes, rad_No;
    DatabaseReference mTutorInfo, mSchools;
    FirebaseAuth mAuth;
    Integer RESULT_LOAD_IMG = 1;
    Uri uriProfilePic;
    StorageReference mStorage;
    String Uid, strSchools, imgUrl;
    RecyclerView list_Subjects, list_Schools;
    ArrayList<ObjectChk> lstSubjects = new ArrayList<>();
    ArrayList<ObjectChk> lstSchools = new ArrayList<>();
    ArrayList<String> lstChkSubjects = new ArrayList<>();
    ArrayList<String> lstChkSchools = new ArrayList<>();
    ArrayList<String> lstStrSchools = new ArrayList<>();
    Integer characterCount = 0;
    Gson gson;
    Boolean isAvailable = false, isRegistering = false;
    TutorProfile tutorProfile;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Register");

        setContentView(R.layout.activity_set_profile);
        mTutorInfo = FirebaseDatabase.getInstance().getReference().child("TutorInfo");
        mSchools = FirebaseDatabase.getInstance().getReference().child("Schools");
        mAuth = FirebaseAuth.getInstance();
        isRegistering = getIntent().getBooleanExtra("isRegistering", false);
        mSchools.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                strSchools = dataSnapshot.getValue().toString();
                Type type = new TypeToken<ArrayList<String>>(){}.getType();
                lstStrSchools = gson.fromJson(strSchools, type);
                if (isRegistering)
                {
                    for (int i = 0; i < lstStrSchools.size(); i++)
                    {
                        lstSchools.add(new ObjectChk(lstStrSchools.get(i), false));
                    }
                    RecyclerViewAdapterChecks SchoolsAdapter = new RecyclerViewAdapterChecks(ActivitySetProfile.this, lstSchools);
                    list_Schools.setAdapter(SchoolsAdapter);
                    list_Schools.setLayoutManager(new LinearLayoutManager(ActivitySetProfile.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mStorage = FirebaseStorage.getInstance().getReference().child("TutorProfilePics");
        gson = new Gson();

        pref = getApplicationContext().getSharedPreferences("TutorProfile", 0);
        editor = pref.edit();

        Uid = mAuth.getUid();
        if (!isRegistering)
        {
            mTutorInfo.child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    tutorProfile = dataSnapshot.getValue(TutorProfile.class);
                    while (lstStrSchools == null)
                    {
                    }
                    createProfileUI();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        list_Subjects = findViewById(R.id.recyclerview_Subjects);
        list_Schools = findViewById(R.id.recyclerview_Schools);

        scrollView = findViewById(R.id.scrollView2);
        img_ProfilePic = findViewById(R.id.img_ProfilePic);

        btn_Confirm = findViewById(R.id.btn_Confirm);

        textBioTitle = findViewById(R.id.textBioTitle);
        textSchools = findViewById(R.id.textSchools);
        textTitle = findViewById(R.id.textTitle);
        field_Name = findViewById(R.id.field_Name);
        field_GPA = findViewById(R.id.field_GPA);
        field_Bio = findViewById(R.id.field_Bio);

        field_Bio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                characterCount = field_Bio.length();
                textBioTitle.setText("About Me \n (" + characterCount+ " / 150 Characters)");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rad_Yes = findViewById(R.id.rad_Yes);
        rad_Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSchools.setVisibility(View.VISIBLE);
                list_Schools.setVisibility(View.VISIBLE);
                isAvailable = true;
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
        rad_No = findViewById(R.id.rad_No);
        rad_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSchools.setVisibility(View.GONE);
                list_Schools.setVisibility(View.GONE);
                isAvailable = false;
            }
        });

        btn_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Double.parseDouble(field_GPA.getText().toString()) > 5 || Double.parseDouble(field_GPA.getText().toString()) < 0)
                {
                    Toast.makeText(ActivitySetProfile.this, "Please Enter A Valid GPA", Toast.LENGTH_SHORT).show();
                }
                else if (isRegistering) {
                    startRegistration();
                }
                else
                {
                    for (int i = 0; i < lstSubjects.size(); i++)
                    {
                        if (lstSubjects.get(i).isChecked())
                        {
                            lstChkSubjects.add(lstSubjects.get(i).getName());
                        }
                    }
                    for (int i = 0; i < lstSchools.size(); i++)
                    {
                        if (lstSchools.get(i).isChecked())
                        {
                            lstChkSchools.add(lstStrSchools.get(i));
                        }
                    }


                    if (uriProfilePic != null)
                    {
                        mStorage.child(Uid).delete();
                        mStorage.putFile(uriProfilePic);
                    }

                    mStorage.child(Uid).getDownloadUrl()
                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    imgUrl = task.getResult().toString();
                                    TutorProfile tutorProfile = new TutorProfile(lstChkSchools, lstChkSubjects, Uid, field_Name.getText().toString(),field_GPA.getText().toString(),field_Bio.getText().toString(), mAuth.getCurrentUser().getEmail(), isAvailable, imgUrl);
                                    mTutorInfo.child(Uid).child("lstSchools").setValue(lstChkSchools);
                                    mTutorInfo.child(Uid).child("lstSubjects").setValue(lstChkSubjects);
                                    mTutorInfo.child(Uid).child("name").setValue(field_Name.getText().toString());
                                    mTutorInfo.child(Uid).child("gpa").setValue(field_GPA.getText().toString());
                                    mTutorInfo.child(Uid).child("bio").setValue(field_Bio.getText().toString());
                                    mTutorInfo.child(Uid).child("email").setValue(mAuth.getCurrentUser().getEmail());
                                    mTutorInfo.child(Uid).child("available").setValue(isAvailable);
                                    mTutorInfo.child(Uid).child("urlPic").setValue(imgUrl);

                                    String json = gson.toJson(tutorProfile);
                                    editor.putString("TutorProfile", json);
                                    editor.commit();

                                    Toast.makeText(ActivitySetProfile.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ActivitySetProfile.this, ActivityHome.class);
                                    intent.putExtra("Name", tutorProfile.getName());
                                    startActivity(intent);
                                }
                            });
                }
            }
        });



        img_ProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

        if (isRegistering)
        {
            for (int i = 0; i < Singleton.getInstance().getArrayList().size(); i++)
            {
                lstSubjects.add(new ObjectChk(Singleton.getInstance().getArrayList().get(i), false));
            }

            RecyclerViewAdapterChecks SubjectAdapter = new RecyclerViewAdapterChecks(this, lstSubjects);
            list_Subjects.setAdapter(SubjectAdapter);
            list_Subjects.setLayoutManager(new LinearLayoutManager(this));
        }
        textTitle.setText("Edit Your Profile Here");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_CANCELED)
        {

        }
        else if (resultCode == RESULT_OK) {
            uriProfilePic = data.getData();
            img_ProfilePic = findViewById(R.id.img_ProfilePic);
            img_ProfilePic.setImageURI(uriProfilePic);
        }
    }

    public void startRegistration()
    {
        final Dialog dialog = loadingDialog.create(this, "Loading...");
        dialog.show();
        Uid = mAuth.getCurrentUser().getUid();
        for (int i = 0; i < lstSubjects.size(); i++)
        {
            if (lstSubjects.get(i).isChecked())
            {
                lstChkSubjects.add(lstSubjects.get(i).getName());
            }
        }
        for (int i = 0; i < lstSchools.size(); i++)
        {
            if (lstSchools.get(i).isChecked())
            {
                lstChkSchools.add(lstStrSchools.get(i));
            }
        }

        mStorage.child(Uid).putFile(uriProfilePic)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        mStorage.child(Uid).getDownloadUrl()
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        imgUrl = task.getResult().toString();
                                        TutorProfile tutorProfile = new TutorProfile(lstChkSchools, lstChkSubjects, Uid, field_Name.getText().toString(),field_GPA.getText().toString(),field_Bio.getText().toString(), mAuth.getCurrentUser().getEmail(), isAvailable, imgUrl);
                                        mTutorInfo.child(Uid).setValue(tutorProfile);
                                        mStorage.child(Uid).putFile(uriProfilePic);

                                        String json = gson.toJson(tutorProfile);
                                        editor.putString("TutorProfile", json);
                                        editor.commit();

                                        dialog.dismiss();

                                        Intent intent = new Intent(ActivitySetProfile.this, ActivityHome.class);
                                        startActivity(intent);
                                    }
                                });
                    }
                });



    }
    public void createProfileUI()
    {
        isAvailable = tutorProfile.isAvailable;
        for (int i = 0; i < Singleton.getInstance().getArrayList().size(); i++)
        {
            lstSubjects.add(new ObjectChk(Singleton.getInstance().getArrayList().get(i), false));
        }

        for (int i = 0; i < tutorProfile.getLstSubjects().size(); ++i)
        {
            for (int x = 0; x < lstSubjects.size(); x++)
            {
               if (tutorProfile.getLstSubjects().get(i).equals(lstSubjects.get(x).getName()))
               {
                   lstSubjects.get(x).setChecked(true);
               }
            }
        }

        for (int i = 0; i < lstStrSchools.size(); i++)
        {
            lstSchools.add(new ObjectChk(lstStrSchools.get(i), false));
        }

        for (int i = 0; i < tutorProfile.getLstSchools().size(); ++i)
        {
            for (int x = 0; x < lstSchools.size(); x++)
            {
                if (tutorProfile.getLstSchools().get(i).equals(lstSchools.get(x).getName()))
                {
                    lstSchools.get(x).setChecked(true);
                }
            }
        }

        field_Name.setText(tutorProfile.getName());
        field_GPA.setText(tutorProfile.getGPA());
        field_Bio.setText(tutorProfile.getBio());
        if (tutorProfile.isAvailable)
        {
            rad_Yes.setChecked(true);
            textSchools.setVisibility(View.VISIBLE);
            list_Schools.setVisibility(View.VISIBLE);
        }
        else
        {
            rad_No.setChecked(true);
        }

        Glide.with(ActivitySetProfile.this)
                .load(tutorProfile.getUrlPic())
                .fitCenter()
                .into(img_ProfilePic);

        RecyclerViewAdapterChecks SubjectAdapter = new RecyclerViewAdapterChecks(this, lstSubjects);
        list_Subjects.setAdapter(SubjectAdapter);
        list_Subjects.setLayoutManager(new LinearLayoutManager(this));


        RecyclerViewAdapterChecks SchoolsAdapter = new RecyclerViewAdapterChecks(ActivitySetProfile.this, lstSchools);
        list_Schools.setAdapter(SchoolsAdapter);
        list_Schools.setLayoutManager(new LinearLayoutManager(ActivitySetProfile.this));
    }

}
