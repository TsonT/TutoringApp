package com.example.tutoringapp;

import java.io.Serializable;

public class ObjectRequest implements Serializable {
     String RequesterUid, Key, RecipientUid, Message, Date, Time, Location, RequesterName, RecipientName, sentDateTime;
     Boolean isAccepted;
     Boolean  isCancelled = false;
     Boolean isFinished = false;

    public ObjectRequest(String requesterUid, String recipientUid, String key, String message, String date, Boolean isAccepted, String time, String location, String requesterName, String recipientName, String sentDateTime) {
        RequesterUid = requesterUid;
        Key = key;
        RecipientUid = recipientUid;
        Message = message;
        Date = date;
        this.isAccepted = isAccepted;
        Time = time;
        Location = location;
        RequesterName = requesterName;
        RecipientName = recipientName;
        this.sentDateTime = sentDateTime;
    }

    public ObjectRequest() {
    }

    public String getRequesterUid() {
        return RequesterUid;
    }

    public void setRequesterUid(String requesterUid) {
        RequesterUid = requesterUid;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getRecipientUid() {
        return RecipientUid;
    }

    public void setRecipientUid(String recipientUid) {
        RecipientUid = recipientUid;
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

    public Boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public void setFinished(Boolean finished) {
        isFinished = finished;
    }

    public Boolean getCancelled() {
        return isCancelled;
    }

    public void setCancelled(Boolean cancelled) {
        isCancelled = cancelled;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getRequesterName() {
        return RequesterName;
    }

    public void setRequesterName(String requesterName) {
        RequesterName = requesterName;
    }

    public String getRecipientName() {
        return RecipientName;
    }

    public void setRecipientName(String recipientName) {
        RecipientName = recipientName;
    }

    public String getSentDateTime() {
        return sentDateTime;
    }

    public void setSentDateTime(String sentDateTime) {
        this.sentDateTime = sentDateTime;
    }
}
