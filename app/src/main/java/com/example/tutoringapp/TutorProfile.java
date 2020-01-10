package com.example.tutoringapp;

import android.net.Uri;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;

public class TutorProfile implements Serializable {

    ArrayList<String> lstSchools, lstSubjects;
    String Uid, Name, GPA, Bio, Email, urlPic;
    Boolean isAvailable;

    public TutorProfile(ArrayList<String> lstSchools, ArrayList<String> lstSubjects, String uid, String name, String GPA, String bio, String email, Boolean isAvailable, String urlPic) {
        this.lstSchools = lstSchools;
        this.lstSubjects = lstSubjects;
        Uid = uid;
        Name = name;
        this.GPA = GPA;
        Bio = bio;
        Email = email;
        this.isAvailable = isAvailable;
        this.urlPic = urlPic;
    }

    public TutorProfile()
    {

    }

    public ArrayList<String> getLstSchools() {
        return lstSchools;
    }

    public void setLstSchools(ArrayList<String> lstSchools) {
        this.lstSchools = lstSchools;
    }

    public ArrayList<String> getLstSubjects() {
        return lstSubjects;
    }

    public void setLstSubjects(ArrayList<String> lstSubjects) {
        this.lstSubjects = lstSubjects;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getGPA() {
        return GPA;
    }

    public void setGPA(String GPA) {
        this.GPA = GPA;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public String getUrlPic() {
        return urlPic;
    }

    public void setUrlPic(String urlPic) {
        this.urlPic = urlPic;
    }

    public String getSubjectsStr()
    {
        String strSubjects = "";
        for (int i = 0; i < lstSubjects.size(); i++)
        {
            strSubjects = strSubjects + lstSubjects.get(i) + ", ";

        }
        strSubjects = strSubjects.substring(0, strSubjects.length()-2);
        return strSubjects;
    }

    public String getSchoolsStr()
    {
        String strSchools = "";

        if (lstSchools.size() == 2) {
            for (int i = 0; i < lstSchools.size(); i++) {
                strSchools = strSchools + lstSchools.get(i) + ", ";
            }

            strSchools = strSchools.substring(0, lstSchools.size() - 2);
        }
        else if (lstSchools.size() == 1)
        {
            strSchools = lstSchools.get(0);
        }
        return strSchools;
    }
}
