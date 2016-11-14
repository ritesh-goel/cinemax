package com.rg.moviescentral.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rg.moviescentral.R;

import java.lang.reflect.Field;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragmet extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    String movieId,imdbId;
    static String rottenScore = "NA";
    Bundle b;

    public MovieDetailFragmet() {
        // Required empty public constructor
    }

    public void setMovieAndImdbId(String movieId){
        this.movieId = movieId;
    }

    public void setImdbId(String imdbId){
        this.imdbId = imdbId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_movie_detail_fragmet, container, false);
        tabLayout = (TabLayout) v.findViewById(R.id.movie_detail_tabs);
        viewPager = (ViewPager) v.findViewById(R.id.movie_detail_pager);
        MovieDetailPager movieDetailPager = new MovieDetailPager(getChildFragmentManager());
        viewPager.setAdapter(movieDetailPager);
        tabLayout.setupWithViewPager(viewPager);
        b = getArguments();
        return v;
    }

    class MovieDetailPager extends FragmentPagerAdapter {

        int numTabs = 3;

        public MovieDetailPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    MovieOverviewFragment movieOverviewFragment = new MovieOverviewFragment();
                    movieOverviewFragment.setImdbId(imdbId);
                    movieOverviewFragment.setArguments(b);
                    return movieOverviewFragment;
                case 1:
                    MovieCastFragment fragment = new MovieCastFragment();
                    fragment.setMovieId(movieId);
                    return fragment;
                case 2:
                    MovieReviewFragment movieReviewFragment = new MovieReviewFragment();
                    movieReviewFragment.setImdnId(imdbId);
                    return movieReviewFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return numTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Overview";
                case 1:
                    return "Cast";
                case 2:
                    return "Reviews";
            }
            return "";
        }
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