package com.rg.moviescentral;

import android.app.SearchManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rg.moviescentral.fragments.MoviesTabFragment;

public class Home extends AppCompatActivity {

    FrameLayout frameLayout;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    static DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Movies");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if(isNetworkStatusAvialable (getApplicationContext())) {
        } else {
            Toast.makeText(getApplicationContext(), "Network unavailable! Please check your internet connection.", Toast.LENGTH_LONG).show();

        }
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        frameLayout = (FrameLayout) findViewById(R.id.container);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container,new MoviesTabFragment()).commit();

        AdView mAdView = (AdView) findViewById(R.id.adViewMovies);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public static void closeDrawer(){
        if(drawerLayout!=null)
            drawerLayout.closeDrawer(GravityCompat.START);
    }

    public static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if(netInfos != null)
                if(netInfos.isConnected())
                    return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu,menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) menu.findItem(R.id.search_movie_menu).getActionView();

        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView)
                MenuItemCompat.getActionView(menu.findItem(R.id.search_movie_menu));
        // Assumes current activity is the searchable activity
        if(null!=searchManager ) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(true);

        return true;
    }
}
