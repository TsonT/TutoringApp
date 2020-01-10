package com.example.tutoringapp;

import java.util.ArrayList;
import java.util.Arrays;

public class Singleton {
    private ArrayList<String> arrayList;

    private static Singleton instance;

    private Singleton(){
        arrayList = new ArrayList<>(Arrays.asList("Math I", "Math II", "Math III", "Math IV", "Pre-Calc", "Calc I", "English", "Physics", "Chemistry", "Natural Sciences", "Biology", "US History", "World History", "Spanish"));;
    }

    public static Singleton getInstance(){
        if (instance == null){
            instance = new Singleton();
        }
        return instance;
    }

    public ArrayList<String> getArrayList() {
        return arrayList;
    }
}
