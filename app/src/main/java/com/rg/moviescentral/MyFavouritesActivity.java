package com.rg.moviescentral;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.youtube.player.internal.m;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.rg.moviescentral.Adapter.AdapterNowPlaying;
import com.rg.moviescentral.customUI.Fab;
import com.rg.moviescentral.model.Movie;
import com.rg.moviescentral.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import static com.rg.moviescentral.extras.Keys.EndPointNowPlaying.KEY_GENEREIDS;

public class MyFavouritesActivity extends AppCompatActivity implements AdapterNowPlaying.ItemClickCallback, View.OnClickListener {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    Button sortName,sortYear;
    TextView textView;
    DatabaseReference mDatabaseReference;
    ArrayList<Movie> movies;
    AdapterNowPlaying adapterNowPlaying;
    FirebaseUser mFirebaseUser;
    FirebaseAuth mFirebaseAuth;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;
    MaterialSheetFab<Fab> materialSheetFab;
    ImageLoader imageLoader;
    String movieID;
    static DrawerLayout drawerLayout;
    private final static String MOVIE_URL = "https://api.themoviedb.org/3/movie/";
    InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favourites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.favourite_toolbar);
        setSupportActionBar(toolbar);

        sortName = (Button) findViewById(R.id.sort_by_name);
        sortYear = (Button) findViewById(R.id.sort_by_year);
        sortYear.setOnClickListener(this);
        sortName.setOnClickListener(this);

        AdView mAdView = (AdView) findViewById(R.id.adViewFavourites);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        toolbar.setTitle("My Favourites");
        drawerLayout  = (DrawerLayout) findViewById(R.id.favourite_drawer);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        textView = (TextView) findViewById(R.id.no_favourites_text);
        recyclerView = (RecyclerView) findViewById(R.id.now_playing_recyclerview);
        adapterNowPlaying = new AdapterNowPlaying(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) findViewById(R.id.now_playing_progressbar);
        adapterNowPlaying.setItemCallback(this);
        recyclerView.setAdapter(adapterNowPlaying);
        recyclerView.setNestedScrollingEnabled(false);

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        imageLoader = volleySingleton.getImageLoader();

        Fab fab = (Fab) findViewById(R.id.fab2);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.dim_overlay);
        int sheetColor = getResources().getColor(R.color.colorPrimary);
        int fabColor = getResources().getColor(R.color.colorAccent);

        setupInterstitialAd();

/*
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(-1, -1, 16, mAdView.getHeight() + 16);
        fab.setLayoutParams(params);

*/
        // Initialize material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser == null){
            Toast.makeText(getApplicationContext(),"Please Login first",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,MainActivity.class));
        }
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("userdata").child(mFirebaseUser.getUid()).child("movie_fav");
        movies = new ArrayList<>();

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    movies.clear();
                    adapterNowPlaying.setMoviesList(movies);
                    adapterNowPlaying.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }
                else {
                    for (DataSnapshot i : dataSnapshot.getChildren()) {
                        fetchMovieDetails(i.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public String getMovieUrl(String movieID) {
        return MOVIE_URL + movieID + "?api_key=" + MyApplication.API_KEY_TMDB;
    }

    public void setupInterstitialAd(){
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-5000638620408100/9081249270");
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        interstitialAd.loadAd(adRequest);

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d("fav act","ad loaded----------------");
                interstitialAd.show();
            }
        });
    }

    public void fetchMovieDetails(String id){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                JsonObjectRequest.Method.GET,
                getMovieUrl(id),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.has("id")) {
                            Movie m = parseResponse(response);
                            movies.add(m);
                            adapterNowPlaying.setMoviesList(movies);
                            adapterNowPlaying.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private Movie parseResponse(JSONObject response) {
        if (response == null || response.length() == 0) {
            return null;
        }
        String backdrop = "";
        String homepage = "";
        String imdbID = "", overview = "", posterPath = "", status = "Unknown", releaseDate = "", tagline = "", title = "",tmdbid="";
        int runTime = -1, voteCount = -1;
        Movie m = null;
        long revenue=0,budget=0;
        ArrayList<Integer> genereID = new ArrayList<>();
        double voteAverage = 0, popularity = 0;
        try {
            tmdbid = "" + response.getLong("id");
            if (response.has("backdrop_path")) {
                backdrop = "https://image.tmdb.org/t/p/w780" + response.getString("backdrop_path");
            }
            if (response.has("homepage") && response.getString("homepage") != "") {
                homepage = response.getString("homepage");
            }
            if (response.has("imdb_id")) {
                imdbID = response.getString("imdb_id");
            }
            if (response.has("overview")) {
                overview = response.getString("overview");
            }
            if (response.has("poster_path")) {
                posterPath = "https://image.tmdb.org/t/p/w154" + response.getString("poster_path");
            }
            if (response.has("runtime")) {
                runTime = response.getInt("runtime");
            }
            if (response.has("status")) {
                status = response.getString("status");
            }
            if (response.has("release_date")) {
                releaseDate = response.getString("release_date");
            }
            if (response.has("tagline")) {
                tagline = response.getString("tagline");
            }
            if (response.has("vote_count")) {
                voteCount = response.getInt("vote_count");
            }
            if (response.has("vote_average")) {
                voteAverage = response.getDouble("vote_average");
            }
            if (response.has("popularity")) {
                popularity = response.getDouble("popularity");
            }
            title = response.getString("title");
            if(response.has("revenue")){
                revenue = response.getLong("revenue");
            }
            if(response.has("budget")){
                budget = response.getLong("budget");
            }
            JSONArray generes = response.getJSONArray("genres");
            for (int j = 0; j < generes.length(); j++) {
                genereID.add((Integer) generes.getJSONObject(j).getInt("id"));
            }
            m = new Movie(tmdbid, posterPath, overview, title, genereID, releaseDate, voteCount, voteAverage, popularity, homepage, backdrop, imdbID, status, tagline, "", runTime,budget,revenue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return m;
    }

    @Override
    public void onItemClick(int p) {
        Intent intent = new Intent(this, MovieDetail.class).putExtra("movie_data", movies.get(p));
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.sort_by_name){
            Collections.sort(movies,new MovieNameComparator());
            adapterNowPlaying.notifyDataSetChanged();
        }
        else {
            Collections.sort(movies,new MovieDateComparator());
            adapterNowPlaying.notifyDataSetChanged();
        }
        materialSheetFab.hideSheet();
    }

    static class MovieNameComparator implements Comparator<Movie>
    {
        @Override
        public int compare(Movie movie, Movie t1) {
            return movie.getTitle().compareTo(t1.getTitle());
        }
    }

    public static void closeDrawer(){
        if(drawerLayout != null)
            drawerLayout.closeDrawer(GravityCompat.START);
    }

    static class MovieDateComparator implements Comparator<Movie>
    {
        @Override
        public int compare(Movie movie, Movie t1) {
            return movie.getReleaseDate().compareTo(t1.getReleaseDate());
        }
    }

    @Override
    public void onBackPressed() {
        if(materialSheetFab.isSheetVisible()){
            materialSheetFab.hideSheet();
        }
        else{
            finish();
        }
    }
}
