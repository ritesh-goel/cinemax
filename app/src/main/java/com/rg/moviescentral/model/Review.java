package com.rg.moviescentral.model;

/**
 * Created by Ritesh Goel on 22-10-2016.
 */

public class Review {

    String criticName;
    String publicaion;
    String date;
    String reviewText;
    String freshness;
    String link;

    public Review(String criticName, String publicaion, String date, String reviewText, String freshness, String link) {
        this.criticName = criticName;
        this.publicaion = publicaion;
        this.date = date;
        this.reviewText = reviewText;
        this.freshness = freshness;
        this.link = link;
    }

    public String getCriticName() {
        return criticName;
    }

    public void setCriticName(String criticName) {
        this.criticName = criticName;
    }

    public String getPublicaion() {
        return publicaion;
    }

    public void setPublicaion(String publicaion) {
        this.publicaion = publicaion;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getFreshness() {
        return freshness;
    }

    public void setFreshness(String freshness) {
        this.freshness = freshness;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
