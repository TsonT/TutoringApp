package com.example.tutoringapp;

public class ObjectReview {

    Float Stars;
    String Comment;
    String Reviewer;
    String Key;

    public ObjectReview(Float stars, String comment, String reviewer, String key) {
        Stars = stars;
        Comment = comment;
        Reviewer = reviewer;
        Key = key;
    }

    public ObjectReview() {
    }

    public Float getStars() {
        return Stars;
    }

    public void setStars(Float stars) {
        Stars = stars;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getReviewer() {
        return Reviewer;
    }

    public void setReviewer(String reviewer) {
        Reviewer = reviewer;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
}
