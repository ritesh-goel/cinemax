package com.rg.moviescentral.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.rg.moviescentral.R;
import com.rg.moviescentral.extras.Constants;
import com.rg.moviescentral.model.Movie;
import com.rg.moviescentral.network.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by Ritesh Goel on 11-10-2016.
 */

public class AdapterNowPlaying extends RecyclerView.Adapter<AdapterNowPlaying.MyViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<Movie> movies = new ArrayList<>();
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private ItemClickCallback itemClickCallback;

    public AdapterNowPlaying(Context context) {
        layoutInflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
    }

    public interface ItemClickCallback {
        void onItemClick(int p);
    }

    public void setItemCallback(final ItemClickCallback itemCallback) {
        this.itemClickCallback = itemCallback;
        if(itemClickCallback != null);
    }

    public void setMoviesList(ArrayList<Movie> moviesList) {
        this.movies = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_row, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Movie movie = movies.get(position);
        holder.title.setText(movie.getTitle());
        int year = 2000;
        if(movie.getReleaseDate() != null) {
            year += movie.getReleaseDate().getYear() - 100;
        }
        holder.date.setText(""+year);
        String genres = "";
        for(int i=0;i<movie.getGenereId().size();i++){
            genres = genres + Constants.getGenre(movie.getGenereId().get(i));
            if(i<movie.getGenereId().size()-1){
                genres += ",";
            }
        }
        holder.genre.setText(genres);
        String imageURL = movie.getPosterPath();
        if (imageURL != null && imageURL != "null") {
            imageLoader.get(
                    imageURL
                    , new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            holder.imageView.setImageBitmap(response.getBitmap());
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Log.e("image error",error.getMessage());
                        }
                    });
        }
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickCallback.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView title, date, genre;
        private RatingBar ratingBar;
        private LinearLayout linearLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.row_image);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.row_details);
            title = (TextView) linearLayout.findViewById(R.id.row_title);
            date = (TextView) linearLayout.findViewById(R.id.released);
            ratingBar = (RatingBar) linearLayout.findViewById(R.id.ratingBar);
            genre = (TextView) linearLayout.findViewById(R.id.row_genre);
        }
    }
}
