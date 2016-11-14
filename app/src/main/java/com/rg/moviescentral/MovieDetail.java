package com.rg.moviescentral;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.rg.moviescentral.extras.Constants;
import com.rg.moviescentral.fragments.MovieCastFragment;
import com.rg.moviescentral.fragments.MovieDetailFragmet;
import com.rg.moviescentral.fragments.MovieOverviewFragment;
import com.rg.moviescentral.fragments.MovieReviewFragment;
import com.rg.moviescentral.fragments.MovieTrailerFragment;
import com.rg.moviescentral.model.Genere;
import com.rg.moviescentral.model.Movie;
import com.rg.moviescentral.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MovieDetail extends AppCompatActivity {

    String movieID, imdbId;
    private String MOVIE_URL = "https://api.themoviedb.org/3/movie/";
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    ImageLoader imageLoader;
    Movie movie, m;
    ImageView backdropImageView, posterImageView;
    TextView title, movieGenres;
    ProgressBar backdropProgress, posterProgress;
    FloatingActionButton fab;
    DatabaseReference mDatabaseReference;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FrameLayout detailContainer;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    MovieDetailFragmet movieDetailFragmet;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent = getIntent();
        m = (Movie) intent.getSerializableExtra("movie_data");
        movieID = m.getId();
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        imageLoader = volleySingleton.getImageLoader();
        detailContainer = (FrameLayout) findViewById(R.id.fragment_detail_container);

        AdView mAdView = (AdView) findViewById(R.id.adViewMovieDetail);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        backdropImageView = (ImageView) findViewById(R.id.movie_detail_backdrop);
        posterImageView = (ImageView) findViewById(R.id.poster_movie_detail);
        title = (TextView) findViewById(R.id.title_movie_detail);

        movieGenres = (TextView) findViewById(R.id.movie_detail_genres);
        backdropProgress = (ProgressBar) findViewById(R.id.backdrop_progress);
        posterProgress = (ProgressBar) findViewById(R.id.poster_progress);

        fab = (FloatingActionButton) findViewById(R.id.movie_detail_fab);
        setupInterstitialAd();

        // add movie to favourites on fab click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFirebaseUser == null) {
                    Toast.makeText(getApplicationContext(), "Please Login first", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getCurrentContext(), MainActivity.class));
                    finish();
                    return;
                }
                fabFavorite(1);
            }
        });

        setViewValues();
        sendJSONRequest();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            fab.setImageDrawable(ContextCompat.getDrawable(getCurrentContext(), R.drawable.ic_favorite_border_black_24dp));
            animateFab();
        } else {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("userdata").child(mFirebaseUser.getUid()).child("movie_fav");
            fabFavorite(0);
        }
    }

    public MovieDetail getCurrentContext() {
        return this;
    }

    public void fabFavorite(final int flag) {
        mDatabaseReference.child("" + movieID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (flag == 1) {
                        Toast.makeText(MovieDetail.this, "Movie removed from Favourites", Toast.LENGTH_SHORT).show();
                        fab.setImageDrawable(ContextCompat.getDrawable(getCurrentContext(), R.drawable.ic_favorite_border_black_24dp));
                        mDatabaseReference.child("" + movieID).removeValue();
                    } else {
                        fab.setImageDrawable(ContextCompat.getDrawable(getCurrentContext(), R.drawable.ic_favorite_black_24dp));
                    }
                } else {
                    if (flag == 1) {
                        Toast.makeText(MovieDetail.this, "Movie added to Favourites", Toast.LENGTH_SHORT).show();
                        fab.setImageDrawable(ContextCompat.getDrawable(getCurrentContext(), R.drawable.ic_favorite_black_24dp));
                        mDatabaseReference.child("" + movieID).setValue("");
                    } else {
                        fab.setImageDrawable(ContextCompat.getDrawable(getCurrentContext(), R.drawable.ic_favorite_border_black_24dp));
                    }
                }
                animateFab();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MovieDetail.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void animateFab(){
        fab.setScaleX(0);
        fab.setScaleY(0);
        fab.setVisibility(View.VISIBLE);
        fab.animate().scaleX(1).scaleY(1).setDuration(700).start();
    }

    public void setupInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5000638620408100/4511448875");
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
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
                Log.d("fav act", "ad loaded----------------");
                mInterstitialAd.show();
            }
        });
    }

    public void sendJSONRequest() {
        Log.d("movie response", "asbkajbakba");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET,
                getMovieUrl(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        movie = parseJsonResponse(response);
                        if (movie != null) {
                            Log.d("movie null check", movie.getBackdropPath());
                            imdbId = movie.getImdbID();
                            if (!movie.getBackdropPath().equals("https://image.tmdb.org/t/p/w780null")) {
                                setImage(backdropImageView, backdropProgress, movie.getBackdropPath());
                            } else {
                                setImage(backdropImageView, backdropProgress, movie.getPosterPath());
                                backdropImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            }

                            movieDetailFragmet = new MovieDetailFragmet();
                            Bundle b = new Bundle();
                            b.putSerializable("movie_data", movie);
                            movieDetailFragmet.setArguments(b);
                            movieDetailFragmet.setMovieAndImdbId(movieID);
                            movieDetailFragmet.setImdbId(imdbId);
                            fragmentTransaction.replace(R.id.fragment_detail_container, movieDetailFragmet);

                            try {
                                fragmentTransaction.commitAllowingStateLoss();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }


                            setViewValues();
                            setImage(posterImageView, posterProgress, movie.getPosterPath());
                            Log.d("backdrop path", movie.getBackdropPath());

                        } else {
                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public Movie parseJsonResponse(JSONObject response) {
        if (response == null || response.length() == 0) {
            return null;
        }
        String backdrop = "";
        String homepage = "";
        String imdbID = "", overview = "", posterPath = "", status = "Unknown", releaseDate = "", tagline = "", title = "";
        int runTime = -1, voteCount = -1;
        Movie movie = null;
        long revenue = 0, budget = 0;
        double voteAverage = 0, popularity = 0;
        try {
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
            if (response.has("revenue")) {
                revenue = response.getLong("revenue");
            }
            if (response.has("budget")) {
                budget = response.getLong("budget");
            }
            movie = new Movie(movieID, posterPath, overview, title, null, releaseDate, voteCount, voteAverage, popularity, homepage, backdrop, imdbID, status, tagline, "", runTime, budget, revenue);
        } catch (JSONException e) {

        }
        return movie;
    }

    public void setImage(final ImageView imageView, final ProgressBar progressBar, String url) {
        imageLoader.get(url + "?api_key=" + MyApplication.API_KEY_TMDB, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(response.getBitmap());
//                rootview.setBackground(response.getBitmap());
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    public String getMovieUrl() {
        return MOVIE_URL + movieID + "?api_key=" + MyApplication.API_KEY_TMDB + "&language=en";
    }

    public void setViewValues() {
        title.setText(Html.fromHtml(m.getTitle()));

        String genres = "";
        for (int i = 0; i < m.getGenereId().size(); i++) {
            genres += Constants.getGenre(m.getGenereId().get(i));
            if (i < m.getGenereId().size() - 1) {
                genres += ",";
            }
        }
        movieGenres.setText(genres);
    }
}