package com.rg.moviescentral.fragments;


import android.graphics.LinearGradient;
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
import com.rg.moviescentral.Adapter.AdapterMovieCast;
import com.rg.moviescentral.MyApplication;
import com.rg.moviescentral.R;
import com.rg.moviescentral.model.Celeb;
import com.rg.moviescentral.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieCastFragment extends Fragment {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView textView;
    AdapterMovieCast adapterMovieCast;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    String movieID;
    final static String MOVIE_CAST_URL = "https://api.themoviedb.org/3/movie/" + "/credits?api_key=" + MyApplication.API_KEY_TMDB;
    ArrayList<Celeb> celebs;

    public MovieCastFragment() {
        // Required empty public constructor
    }

    public void setMovieId(String id){
        movieID = id;
    }

    public String getMovieCastUrl(){
        return "https://api.themoviedb.org/3/movie/" + movieID + "/credits?api_key=" + MyApplication.API_KEY_TMDB;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapterMovieCast = new AdapterMovieCast(getActivity());
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movie_cast, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.movie_detail_cast);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setNestedScrollingEnabled(false);
        progressBar = (ProgressBar) v.findViewById(R.id.cast_progress_bar);
        textView = (TextView) v.findViewById(R.id.no_cast_text);
        recyclerView.setAdapter(adapterMovieCast);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateCelebList();
    }

    public void populateCelebList(){
        JsonObjectRequest request = new JsonObjectRequest(JsonObjectRequest.Method.GET,
                getMovieCastUrl(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MovieCastFrag",response.toString());
                        celebs = parseCastResponse(response);
                        if(celebs.size() == 0){
                            textView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            adapterMovieCast.setCelebList(celebs);
                            adapterMovieCast.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("MovieCastFrag",error.toString());
            }
        });
        requestQueue.add(request);
    }

    ArrayList<Celeb> parseCastResponse(JSONObject response) {
        ArrayList<Celeb> c = new ArrayList<>();
        try {
            JSONArray cast = response.getJSONArray("cast");
            String id,name,character,imageurl;
            for(int i=0;i<cast.length();i++){
                JSONObject index = cast.getJSONObject(i);
                id = "" + index.getString("id");
                name = index.getString("name");
                character = index.getString("character");
                imageurl = index.getString("profile_path");
                c.add(new Celeb(id,imageurl,name,character));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return c;
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
