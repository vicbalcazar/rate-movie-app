package com.cpsc411.campususer.app4;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by uhohi_000 on 12/6/2017.
 */

public class MovieListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Movie> movies;
    private TabListsActivity tabListsActivity = new TabListsActivity();

    public MovieListAdapter(Context context, ArrayList<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MovieLayout movieLayout = null;
        Movie movie = movies.get(position);

        if (convertView == null) {
            movieLayout = new MovieLayout(context, movie);
        } else {
            movieLayout = (MovieLayout) convertView;
            movieLayout.setMovie(movie);
        }
        return movieLayout;
    }
}
