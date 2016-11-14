package com.rg.moviescentral.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rg.moviescentral.R;
import com.rg.moviescentral.model.Review;

import java.util.ArrayList;

/**
 * Created by Ritesh Goel on 22-10-2016.
 */

public class AdapterMovieReview extends RecyclerView.Adapter<AdapterMovieReview.MyViewHolder> {

    Context context;
    ArrayList<Review> reviews;
    LayoutInflater layoutInflater;

    public AdapterMovieReview(Context context){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        reviews = new ArrayList<>();
    }

    public void setReviews(ArrayList<Review> reviews){
        this.reviews = reviews;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.review_custom_row,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Review r = reviews.get(position);
        holder.critic.setText(r.getCriticName());
        holder.publication.setText(r.getPublicaion());
        holder.date.setText(r.getDate());
        holder.text.setText(r.getReviewText());
        if(r.getFreshness().equals("fresh")){
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.fresh));
        }
        else{
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rotten));
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView critic,publication,date,text;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.review_image);
            publication = (TextView) itemView.findViewById(R.id.publication_name);
            date = (TextView) itemView.findViewById(R.id.review_date);
            critic = (TextView) itemView.findViewById(R.id.critic_name);
            text = (TextView) itemView.findViewById(R.id.review_text);
        }
    }

}
