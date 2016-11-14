package com.rg.moviescentral.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/*

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeThumbnailView;
*/
import com.rg.moviescentral.MyApplication;
import com.rg.moviescentral.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieTrailerFragment /*extends YouTubePlayerSupportFragment implements YouTubePlayer.OnInitializedListener */{
/*
    YouTubeThumbnailView trailer;
    String videoKey;

    public MovieTrailerFragment() {
        // Required empty public constructor
    }

    public static MovieTrailerFragment newInstance(String videoKey){
        MovieTrailerFragment trailerFragment = new MovieTrailerFragment();
        trailerFragment.setVideoKey(videoKey);
        trailerFragment.initialize(MyApplication.YOUTUBE_API_KEY,trailerFragment);

        return trailerFragment;
    }

    public void setVideoKey(String s){
        videoKey = s;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movie_trailer, container, false);
        trailer = (YouTubeThumbnailView) v.findViewById(R.id.trailer);
        return v;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo("srH-2pQdKhg");
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.d("youtube",youTubeInitializationResult.toString());
    }*/
}
