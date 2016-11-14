package com.rg.moviescentral.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.rg.moviescentral.R;
import com.rg.moviescentral.model.Celeb;
import com.rg.moviescentral.network.VolleySingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ritesh Goel on 20-10-2016.
 */

public class AdapterMovieCast extends RecyclerView.Adapter<AdapterMovieCast.MyViewHolder> {

    Context context;
    ArrayList<Celeb> celebs;
    LayoutInflater layoutInflater;
    VolleySingleton volleySingleton;
    ImageLoader imageLoader;
    public AdapterMovieCast(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        celebs = new ArrayList<>();
    }

    public void setCelebList(ArrayList<Celeb> celebList){
        this.celebs = celebList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = layoutInflater.inflate(R.layout.cast_custom_row,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Celeb c = celebs.get(position);
        holder.celebName.setText(c.getName());
        if(c.getCharacter().equals("")){
            holder.celebCharacter.setVisibility(View.GONE);
        }
        else {
            holder.celebCharacter.setText("as " + c.getCharacter());
        }
        String imageURL = c.getImageURL();
        imageLoader.get("https://image.tmdb.org/t/p/w185" + imageURL,
                new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if(response != null && response.getBitmap() != null){
                            holder.imageView.setImageBitmap(response.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
    }

    @Override
    public int getItemCount() {
        return celebs.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView celebName,celebCharacter;
        RelativeLayout castContainer;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.celeb_image);
            celebCharacter = (TextView) itemView.findViewById(R.id.movie_character_name);
            celebName = (TextView) itemView.findViewById(R.id.celeb_name);
            castContainer = (RelativeLayout) itemView.findViewById(R.id.cast_container);
        }
    }

}
