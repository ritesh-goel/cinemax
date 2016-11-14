package com.rg.moviescentral.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rg.moviescentral.Home;
import com.rg.moviescentral.MainActivity;
import com.rg.moviescentral.MyFavouritesActivity;
import com.rg.moviescentral.R;
import com.rg.moviescentral.network.VolleySingleton;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    FirebaseUser mFirebaseUser;
    FirebaseAuth mFirebaseAuth;
    TextView name, email;
    CircleImageView profilePic;
    ImageLoader imageLoader;
    VolleySingleton volleySingleton;
    NavigationView navigationView;
    Button login;

    public NavigationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_navigation, container, false);
        navigationView = (NavigationView) v.findViewById(R.id.nav_menu);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        name = (TextView) headerLayout.findViewById(R.id.nav_name);
        email = (TextView) headerLayout.findViewById(R.id.nav_email);
        login = (Button) headerLayout.findViewById(R.id.nav_login_button);
        profilePic = (CircleImageView) headerLayout.findViewById(R.id.nav_profile_image);

        login.setOnClickListener(this);

        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();

        if (getActivity().getTitle().toString().equals("Movies")) {
            navigationView.setCheckedItem(R.id.movies_nav);
        } else if (getActivity().getTitle().toString().equals("My Favourites")) {
            navigationView.setCheckedItem(R.id.favorites_nav);
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("nav frag","on resume called");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            profilePic.setVisibility(View.GONE);
            name.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            Menu navMenu = navigationView.getMenu();
            MenuItem logout = navMenu.findItem(R.id.logout);
            logout.setVisible(false);
        } else {
            name.setText(mFirebaseUser.getDisplayName().substring(0, 1).toUpperCase() + mFirebaseUser.getDisplayName().substring(1));
            email.setText(mFirebaseUser.getEmail());
            if (mFirebaseUser.getPhotoUrl() != null) {
                imageLoader.get(mFirebaseUser.getPhotoUrl().toString(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response != null) {
                            profilePic.setImageBitmap(response.getBitmap());
                        }
                    }
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Home.closeDrawer();
        MyFavouritesActivity.closeDrawer();
        switch (item.getItemId()) {
            case R.id.movies_nav:
                if (!getActivity().getTitle().toString().equals("Movies")) {
                    startActivity(new Intent(getActivity(), Home.class));
                    getActivity().finish();
                }
                return true;

            case R.id.favorites_nav:
                if (mFirebaseUser == null) {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    return true;
                }
                if (!getActivity().getTitle().toString().equals("My Favourites")) {
                    startActivity(new Intent(getActivity(), MyFavouritesActivity.class));
                    getActivity().finish();
                }
                return true;

            case R.id.celeb_nav:
                Toast.makeText(getActivity(), "Feature To Be Added Soon", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.tv_nav:
                Toast.makeText(getActivity(), "Feature To Be Added Soon", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.logout:
                mFirebaseAuth.signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
                return true;

            case R.id.rate_us_nav:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.rg.moviescentral"));
                startActivity(i);
                break;

            case R.id.contact_us_nav:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"riteshgoel.dtu@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Cinemax");
                email.putExtra(Intent.EXTRA_TEXT, "");
                email.setType("text/html");
                Intent chooserEmail = Intent.createChooser(email, "Send via...");
                startActivity(chooserEmail);
                return true;

            case R.id.google_plus_nav:
                Intent tent = new Intent(Intent.ACTION_VIEW);
                tent.setData(Uri.parse("https://plus.google.com/communities/116846766125315205666"));
                Intent chooser = Intent.createChooser(tent, "Open using...");
                startActivity(chooser);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.nav_login_button){
            startActivity(new Intent(getActivity(),MainActivity.class));
            getActivity().finish();
        }
    }
}
