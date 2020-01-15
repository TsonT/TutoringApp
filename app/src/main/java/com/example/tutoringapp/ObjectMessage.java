package com.example.tutoringapp;

public class ObjectMessage {

    String Sender, Time, Message, Date;

    public ObjectMessage(String sender, String time, String message, String date) {
        Sender = sender;
        Time = time;
        Message = message;
        Date = date;
    }

    public ObjectMessage() {
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
