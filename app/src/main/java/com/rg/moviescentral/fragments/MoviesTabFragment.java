package com.rg.moviescentral.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.rg.moviescentral.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesTabFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;

    public MoviesTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies_tab,container,false);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager_movies);
        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

}

class PagerAdapter extends FragmentPagerAdapter {

    int numTabs = 4;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        NowPlayingFragment playingFragment=null;
        if(position == 0){
            playingFragment = new NowPlayingFragment();
            playingFragment.setCategory("now_playing");
        }
        else if(position == 1){
            playingFragment = new NowPlayingFragment();
            playingFragment.setCategory("top_rated");
        }
        else if(position == 2){
            playingFragment = new NowPlayingFragment();
            playingFragment.setCategory("upcoming");
        }
        else if(position == 3){
            playingFragment = new NowPlayingFragment();
            playingFragment.setCategory("popular");
        }
        return playingFragment;
    }

    @Override
    public int getCount() {
        return numTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Now Playing";
            case 1:
                return "Top Rated";
            case 2:
                return "Upcoming";
            case 3:
                return "Popular";
        }
        return "";
    }
}