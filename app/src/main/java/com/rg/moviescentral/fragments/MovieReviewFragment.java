package com.rg.moviescentral.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rg.moviescentral.Adapter.AdapterMovieReview;
import com.rg.moviescentral.MyApplication;
import com.rg.moviescentral.R;
import com.rg.moviescentral.model.Review;
import com.rg.moviescentral.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieReviewFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterMovieReview adapterMovieReview;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    String imdbId,rottenId;
    ArrayList<Review> reviews;
    ProgressBar progressBar;
    TextView textView;
    int rottenScore;

    public MovieReviewFragment() {
        // Required empty public constructor
    }

    public void setImdnId(String imdbId){
        this.imdbId = imdbId.substring(2);
    }

    public String getAliasURL(){
        return "http://api.rottentomatoes.com/api/public/v1.0/movie_alias.json?type=imdb&id=" + imdbId + "&apikey=" + MyApplication.ROTTEN_TOMATO_KEY;
    }

    public String getReviewURL(){
        return "http://api.rottentomatoes.com/api/public/v1.0/movies/" + rottenId + "/reviews.json?apikey=" + MyApplication.ROTTEN_TOMATO_KEY;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapterMovieReview = new AdapterMovieReview(getActivity());
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movie_review,container,false);
        progressBar = (ProgressBar) v.findViewById(R.id.review_progress_bar);
        recyclerView = (RecyclerView) v.findViewById(R.id.movie_detail_review);
        textView = (TextView) v.findViewById(R.id.no_review_text);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapterMovieReview);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAliasId();
    }

    public void fetchAliasId(){
        JsonObjectRequest aliasRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET,
                getAliasURL(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.has("id")){
                            try {
                                rottenId = response.getString("id");
                                populateReviews();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else if(response.has("error")) {
                            recyclerView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(aliasRequest);
    }

    public void populateReviews(){
        final JsonObjectRequest reviewRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET,
                getReviewURL(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        reviews = parseReviews(response);
                        if(reviews.size() == 0){
                            textView.setVisibility(View.VISIBLE);
                        }
                        else {
                            adapterMovieReview.setReviews(reviews);
                            adapterMovieReview.notifyDataSetChanged();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(reviewRequest);
    }

    ArrayList<Review> parseReviews(JSONObject response){
        ArrayList<Review> reviewList = new ArrayList<>();
        try {
            JSONArray rev = response.getJSONArray("reviews");
            String link;
            for(int i=0;i<rev.length();i++){
                link = "";
                JSONObject obj = rev.getJSONObject(i);
                if(obj.has("links") && obj.getJSONObject("links").has("review")) {
                    link = obj.getJSONObject("links").getString("review");
                }
                reviewList.add(new Review(obj.getString("critic"),obj.getString("publication"),obj.getString("date"),obj.getString("quote"),obj.getString("freshness"),link));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviewList;
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
