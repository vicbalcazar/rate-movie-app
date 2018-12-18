package com.cpsc411.campususer.app4;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.tabmanager.TabManager;

/**
 * Created by campususer on 11/30/17.
 */

public class MovieLayout extends RelativeLayout implements OnClickListener {

    private RatingBar ratingBar;
    private TextView movie_nameTextView;
    private TextView dateTextView;

    private Movie movie;
    private MovieDatabase db;
    private Context context;


    public MovieLayout(Context context) {
        super(context);
    }

    public  MovieLayout(Context context, Movie m) {
        super(context);

        // set context and get db object
        this.context = context;
        db = new MovieDatabase(context);

        // inflate the layout
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_movie, this, true);

        // get references to widgets
        ratingBar = (RatingBar) findViewById(R.id.movieRatingBar);
        movie_nameTextView = (TextView) findViewById(R.id.movienameTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);

        // set listeners
        ratingBar.setOnClickListener(this);
        this.setOnClickListener(this);

        setMovie(m);

    }

    public void setMovie(Movie m) {
        movie = m;
        movie_nameTextView.setText(movie.getName());
        ratingBar.setRating(movie.getRating());
        dateTextView.setText(movie.getDate());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                Intent intent = new Intent(context, AddEditActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("movieId", movie.getId());
                intent.putExtra("editMode", true);
                context.startActivity(intent);
                break;

        }
    }
}
