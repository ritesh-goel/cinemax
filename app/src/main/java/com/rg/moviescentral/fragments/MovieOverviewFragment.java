package com.rg.moviescentral.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.rg.moviescentral.MyApplication;
import com.rg.moviescentral.R;
import com.rg.moviescentral.model.Movie;
import com.rg.moviescentral.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieOverviewFragment extends Fragment {

    ExpandableTextView expandableTextView;
    TextView moreDetail, tomatoScore, imdbScore, tmdbScore;
    String imdbId;
    int rottenScore = -1;
    LinearLayout linearLayout, trailerLinear;
    FrameLayout videoContainer;
    ImageView rottenScoreImage, imdbImage, tmdbImage;
    Bundle b;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    Movie m;


    public MovieOverviewFragment() {
        // Required empty public constructor
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getAliasURL() {
        return "http://api.rottentomatoes.com/api/public/v1.0/movie_alias.json?type=imdb&id=" + imdbId.substring(2) + "&apikey=" + MyApplication.ROTTEN_TOMATO_KEY;
    }

    public String getVideoURL() {
        return "https://api.themoviedb.org/3/movie/" + m.getId() + "/videos?api_key=" + MyApplication.API_KEY_TMDB;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imdbId", imdbId);
        outState.putInt("rottenScore",rottenScore);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movie_overview, container, false);
        expandableTextView = (ExpandableTextView) v.findViewById(R.id.expandable_description);
        moreDetail = (TextView) v.findViewById(R.id.movie_detail_more_detail);
        tomatoScore = (TextView) v.findViewById(R.id.tomato_score);
        imdbScore = (TextView) v.findViewById(R.id.imdb_score);
        linearLayout = (LinearLayout) v.findViewById(R.id.rating_container);
        trailerLinear = (LinearLayout) v.findViewById(R.id.trailer_linear_layout);
        rottenScoreImage = (ImageView) v.findViewById(R.id.rotten_score_image);
        videoContainer = (FrameLayout) v.findViewById(R.id.video_container);
        imdbImage = (ImageView) v.findViewById(R.id.imdb_image);
        tmdbImage = (ImageView) v.findViewById(R.id.tmdb_score_image);
        tmdbScore = (TextView) v.findViewById(R.id.tmdb_score);
        b = getArguments();
        if (savedInstanceState != null) {
            imdbId = savedInstanceState.getString("imdbId");
            if(savedInstanceState.getInt("rottenScore") != -1){
                setRottenScore(savedInstanceState.getInt("rottenScore"));
            }
        }
        m = (Movie) b.getSerializable("movie_data");
        expandableTextView.setText(m.getOverview());
        int hour = m.getRunTime() / 60, minute = m.getRunTime() % 60;
        moreDetail.setText(Html.fromHtml("<b>Release Date : </b>" + m.getReleaseDate().getDay() + "-" + (m.getReleaseDate().getMonth() + 1) + "-" + (m.getReleaseDate().getYear() + 2000 - 100)
                + "<br><b>Status : </b>" + m.getStatus()
                + "<br><b>Duration : </b>" + hour + " hour " + minute + " minute"
                + "<br><b>Budget : </b>$" + m.getBudget()
                + "<br><b>Revenue : </b>$" + m.getRevenue()));
        if(m.getVoteAverage() != -1){
            tmdbScore.setText(""+m.getVoteAverage());
            tmdbImage.setVisibility(View.VISIBLE);
        }
        getImdbData();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(rottenScore != -1){
            setRottenScore(rottenScore);
        }
    }

    public void getImdbData() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET,
                "http://www.omdbapi.com/?i=" + imdbId + "&plot=full&r=json",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            try {
                                if (response.has("imdbRating") && !response.getString("imdbRating").equals("N/A")) {
                                    imdbImage.setVisibility(View.VISIBLE);
                                    imdbScore.setText(response.getString("imdbRating").toString());
                                    linearLayout.setVisibility(View.VISIBLE);
                                } else {

                                }
                                if (response.has("Plot")) {
                                    //expandableTextView.setText(response.getString("Plot").toString());
                                }
                            } catch (JSONException e) {

                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        JsonObjectRequest aliasRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET,
                getAliasURL(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        try {
                            if (response.has("ratings") && response.getJSONObject("ratings").getInt("critics_score") != -1) {
                                rottenScore = response.getJSONObject("ratings").getInt("critics_score");
                                setRottenScore(rottenScore);
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        JsonObjectRequest videoRequest = new JsonObjectRequest(
                JsonObjectRequest.Method.GET,
                getVideoURL(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            if (response.has("results") && response.getJSONArray("results").length() > 0) {
                                final JSONArray videoArray = response.getJSONArray("results");
                                YouTubePlayerSupportFragment youTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();
                                final FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.video_container, youTubePlayerSupportFragment);
                                try{
                                    fragmentTransaction.commit();
                                }
                                catch (Exception e){

                                }
                                youTubePlayerSupportFragment.initialize(MyApplication.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                                    @Override
                                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                                        if (!b) {
                                            try {
                                                youTubePlayer.cueVideo(videoArray.getJSONObject(0).getString("key"));
                                                youTubePlayer.setShowFullscreenButton(false);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                                    }
                                });
                            } else {
                                trailerLinear.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
        requestQueue.add(aliasRequest);
        requestQueue.add(jsonObjectRequest);
        requestQueue.add(videoRequest);
    }

    public void setRottenScore(int s){
        rottenScoreImage.setVisibility(View.VISIBLE);
        tomatoScore.setText(s + "%");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
