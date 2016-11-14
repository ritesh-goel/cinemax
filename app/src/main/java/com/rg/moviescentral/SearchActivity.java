package com.rg.moviescentral;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rg.moviescentral.Adapter.AdapterNowPlaying;
import com.rg.moviescentral.model.Movie;
import com.rg.moviescentral.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.rg.moviescentral.extras.Keys.EndPointNowPlaying.KEY_GENEREIDS;
import static com.rg.moviescentral.extras.Keys.EndPointNowPlaying.KEY_ID;
import static com.rg.moviescentral.extras.Keys.EndPointNowPlaying.KEY_OVERVIEW;
import static com.rg.moviescentral.extras.Keys.EndPointNowPlaying.KEY_POPULARITY;
import static com.rg.moviescentral.extras.Keys.EndPointNowPlaying.KEY_POSTERPATH;
import static com.rg.moviescentral.extras.Keys.EndPointNowPlaying.KEY_RELEASE_DATE;
import static com.rg.moviescentral.extras.Keys.EndPointNowPlaying.KEY_RESULT;
import static com.rg.moviescentral.extras.Keys.EndPointNowPlaying.KEY_TITLE;
import static com.rg.moviescentral.extras.Keys.EndPointNowPlaying.KEY_VOTECOUNT;
import static com.rg.moviescentral.extras.Keys.EndPointNowPlaying.KEY_VOTE_AVERAGE;

public class SearchActivity extends AppCompatActivity implements AdapterNowPlaying.ItemClickCallback {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView textView;
    AdapterNowPlaying adapterNowPlaying;

    ArrayList<Movie> m = new ArrayList<>();
    VolleySingleton volleySingleton;
    int totalPages;
    RequestQueue requestQueue;
    private static String SEARCH_URL = "https://api.themoviedb.org/3/search/movie?api_key=" + MyApplication.API_KEY_TMDB + "&query=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.movie_search_results);
        progressBar = (ProgressBar) findViewById(R.id.search_progress_bar);
        textView = (TextView) findViewById(R.id.no_movie_found);
        adapterNowPlaying = new AdapterNowPlaying(this);
        adapterNowPlaying.setItemCallback(this);
        recyclerView.setAdapter(adapterNowPlaying);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchMovies(intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        searchMovies(intent);
    }

    public void searchMovies(Intent intent) {
        textView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        String query = intent.getStringExtra(SearchManager.QUERY);
        query = query.replaceAll(" ","%20");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                JsonObjectRequest.Method.GET
                , SEARCH_URL + query
                , null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                m = parseJSONresponse(response);
                adapterNowPlaying.setMoviesList(m);
                adapterNowPlaying.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                if(m.size() == 0){
                    textView.setVisibility(View.VISIBLE);
                }
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) menu.findItem(R.id.search_movie_menu).getActionView();

        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView)
                MenuItemCompat.getActionView(menu.findItem(R.id.search_movie_menu));
        // Assumes current activity is the searchable activity
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onItemClick(int p) {
        startActivity(new Intent(this,MovieDetail.class).putExtra("movie_data",m.get(p)));
    }
}
