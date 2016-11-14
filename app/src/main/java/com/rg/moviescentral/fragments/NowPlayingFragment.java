package com.rg.moviescentral.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.rg.moviescentral.Adapter.AdapterNowPlaying;
import com.rg.moviescentral.MovieDetail;
import com.rg.moviescentral.MyApplication;
import com.rg.moviescentral.R;
import com.rg.moviescentral.extras.Keys;
import com.rg.moviescentral.model.Movie;
import com.rg.moviescentral.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.rg.moviescentral.extras.Keys.EndPointNowPlaying.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends Fragment implements AdapterNowPlaying.ItemClickCallback {

    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;
    public static final String URL_MOVIES = "https://api.themoviedb.org/3/movie/";
    private ArrayList<Movie> movieList = new ArrayList<>();
    RecyclerView recyclerView;
    private AdapterNowPlaying adapterNowPlaying;
    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar_recycler;
    View view;
    String category;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    int pageNo = 1, totalPages = 0;

    public NowPlayingFragment() {
        // Required empty public constructor
    }

    public void setCategory(String cat) {
        category = cat;
    }

    public String getUrlMoviesNowPlaying() {
        return URL_MOVIES + category + "?api_key=" + MyApplication.API_KEY_TMDB + "&page=" + pageNo;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        adapterNowPlaying = new AdapterNowPlaying(getActivity());
        adapterNowPlaying.setItemCallback(this);
//        sendJSONObjectRequest();
    }

    public void sendJSONObjectRequest() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                JsonObjectRequest.Method.GET,
                getUrlMoviesNowPlaying(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        movieList.addAll(parseJSONresponse(response));
                        adapterNowPlaying.setMoviesList(movieList);
                        adapterNowPlaying.notifyDataSetChanged();
                        progressBar_recycler.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    public ArrayList<Movie> parseJSONresponse(JSONObject response) {
        ArrayList<Movie> movies = new ArrayList<>();
        if (response == null || response.length() == 0) {
            return movies;
        }
        try {
            JSONArray results = response.getJSONArray(KEY_RESULT);
            int resultSize = results.length();
            totalPages = response.getInt("total_pages");

            for (int i = 0; i < resultSize; i++) {
                JSONObject currentMovie = results.getJSONObject(i);
                String id = currentMovie.getString(KEY_ID);
                String title = currentMovie.getString(KEY_TITLE);
                String overview = currentMovie.getString(KEY_OVERVIEW);
                String posterPath = "https://image.tmdb.org/t/p/w154" + currentMovie.getString(KEY_POSTERPATH);
                long voteCount = currentMovie.getLong(KEY_VOTECOUNT);
                double voteAverage = currentMovie.getDouble(KEY_VOTE_AVERAGE);
                double popularity = currentMovie.getDouble(KEY_POPULARITY);
                String releaseDate = currentMovie.getString(KEY_RELEASE_DATE);
                String backdrop = currentMovie.getString("backdrop_path");
                ArrayList<Integer> genereID = new ArrayList<>();
                JSONArray generes = currentMovie.getJSONArray(KEY_GENEREIDS);
                for (int j = 0; j < generes.length(); j++) {
                    genereID.add((Integer) generes.get(j));
                }
                Movie movie = new Movie(id, posterPath, overview, title, releaseDate, voteCount, voteAverage, popularity, genereID, backdrop);
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.now_playing_recyclerview);
        progressBar_recycler = (ProgressBar) view.findViewById(R.id.now_playing_progressbar);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    pageNo++;
                    if (pageNo <= totalPages) {
                        progressBar_recycler.setVisibility(View.VISIBLE);
                        sendJSONObjectRequest();
                        loading = true;
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setAdapter(adapterNowPlaying);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapterNowPlaying == null) {
            adapterNowPlaying = new AdapterNowPlaying(getActivity());
        }
        if (movieList == null || movieList.size() == 0) {
            if (volleySingleton == null) {
                volleySingleton = VolleySingleton.getInstance();
            }
            if (requestQueue == null) {
                requestQueue = volleySingleton.getRequestQueue();
            }
            if (imageLoader == null) {
                imageLoader = volleySingleton.getImageLoader();
            }
            sendJSONObjectRequest();
            recyclerView.setAdapter(adapterNowPlaying);
        }
    }


    @Override
    public void onItemClick(int p) {
        Intent intent = new Intent(getActivity(), MovieDetail.class).putExtra("movie_data", movieList.get(p));
        startActivity(intent);
    }
}
