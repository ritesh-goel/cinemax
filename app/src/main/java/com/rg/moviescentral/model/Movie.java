package com.rg.moviescentral.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ritesh Goel on 11-10-2016.
 */

public class Movie implements Serializable {

    private String id;
    private String posterPath;
    private String overview;
    private String title;
    private ArrayList<Integer> genereId;
    private Date releaseDate;
    private long voteCount;
    private double voteAverage;
    private double popularity;
    private String homepage;
    private String backdropPath;
    private String imdbID;
    private String status;
    private String tagline;
    private String videoKey;
    private int runTime;
    private long revenue;
    private long budget;

    public Movie() {
    }

    public Movie(String id, String posterPath, String overview, String title, ArrayList<Integer> genereId, String releaseDate, long voteCount, double voteAverage, double popularity, String homepage, String backdropPath, String imdbID, String status, String tagline, String videoKey, int runtime,long budget,long revenue) {
        this.id = id;
        this.posterPath = posterPath;
        this.overview = overview;
        this.title = title;
        this.genereId = genereId;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(releaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.setReleaseDate(date);
        this.voteCount = voteCount;
        this.voteAverage = voteAverage;
        this.popularity = popularity;
        this.homepage = homepage;
        this.backdropPath = backdropPath;
        this.imdbID = imdbID;
        this.status = status;
        this.tagline = tagline;
        this.videoKey = videoKey;
        this.runTime = runtime;
        this.revenue = revenue;
        this.budget = budget;
    }

    public Movie(String id, String posterPath, String overview, String title, String releaseDate, long voteCount, double voteAverage, double popularity, ArrayList<Integer> genres, String backdropPath) {
        this.id = id;
        this.posterPath = posterPath;
        this.overview = overview;
        this.title = title;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(releaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.setReleaseDate(date);
        this.setVoteCount(voteCount);
        this.setVoteAverage(voteAverage);
        this.setPopularity(popularity);
        this.genereId = genres;
        this.backdropPath = backdropPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Integer> getGenereId() {
        return genereId;
    }

    public void setGenereId(ArrayList<Integer> genereId) {
        this.genereId = genereId;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getVideoKey() {
        return videoKey;
    }

    public void setVideoKey(String videoKey) {
        this.videoKey = videoKey;
    }

    public int getRunTime() {
        return runTime;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public long getRevenue() {
        return revenue;
    }

    public void setRevenue(long revenue) {
        this.revenue = revenue;
    }

    public long getBudget() {
        return budget;
    }

    public void setBudget(long budget) {
        this.budget = budget;
    }
}
