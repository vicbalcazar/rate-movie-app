package com.cpsc411.campususer.app4;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.lang.Comparable.*;

/**
 * Created by campususer on 11/30/17.
 */

public class MovieFragment extends Fragment {

    private ListView movieListView;
    private String currentTabTag;
    private TabHost tabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savdInstanceState) {

        //inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_list,
                container, false);

        //get references to widgets
        movieListView = (ListView) view.findViewById(R.id.movieListView);

        //get the current tab
        tabHost = (TabHost) container.getParent().getParent();
        currentTabTag = tabHost.getCurrentTabTag();

        //tab listener
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {

                //get movie list for current tab from database
                Context context = getActivity().getApplicationContext();
                MovieDatabase db = new MovieDatabase(context);

                ArrayList<Movie> movies = db.getMovies(currentTabTag);
                int tab_index = tabHost.getCurrentTab();
                if (tab_index == 0) {
                    refreshMovieList();
                }
                else if (tab_index == 1) {
                    refreshMovieList();
                }
            }
        });

        // refresh the movie list view
        refreshMovieList();

        return view;
    }

    public void refreshMovieList() {
        //get movie list for current tab from database
        Context context = getActivity().getApplicationContext();
        MovieDatabase db = new MovieDatabase(context);

        int tab_index = tabHost.getCurrentTab();
        if (tab_index == 0) {
            ArrayList<Movie> movies = db.returnByRecentDate(currentTabTag);
            //create adapter and set it in the ListView widget
            MovieListAdapter adapter = new MovieListAdapter(context, movies);
            movieListView.setAdapter(adapter);
        }
        else if (tab_index == 1) {
            ArrayList<Movie> movies = db.returnByRating(currentTabTag);
            //create adapter and set it in the ListView widget
            MovieListAdapter adapter = new MovieListAdapter(context, movies);
            movieListView.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshMovieList();
    }

}
