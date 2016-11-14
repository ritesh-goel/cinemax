package com.rg.moviescentral.model;

/**
 * Created by Ritesh Goel on 16-10-2016.
 */

public class Celeb {

    String id;
    String biography;
    String birthday;
    int gender;
    String homepage;
    String imdbId;
    String name;
    String placeOfBirth;
    double popularity;
    String imageURL;
    String character;

    public Celeb(String id, String imageURL, String name, String character) {
        this.id = id;
        this.imageURL = imageURL;
        this.name = name;
        this.character = character;
    }

    public Celeb(String id, String biography, String birthday, int gender, String homepage, String imdbId, String name, String placeOfBirth, double popularity, String imageURL, String character) {
        this.id = id;
        this.biography = biography;
        this.birthday = birthday;
        this.gender = gender;
        this.homepage = homepage;
        this.imdbId = imdbId;
        this.name = name;
        this.placeOfBirth = placeOfBirth;
        this.popularity = popularity;
        this.imageURL = imageURL;
        this.character = character;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
