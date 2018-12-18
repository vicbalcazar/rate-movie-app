package com.cpsc411.campususer.app4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.google.tabmanager.TabManager;

import java.util.ArrayList;


public class TabListsActivity extends FragmentActivity {

    TabHost tabHost;
    TabManager tabManager;
    MovieDatabase db;

    private TextView dateTextView;
    private RatingBar ratingBar;

    private String currentTabTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_lists);

        //get widget references
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        ratingBar = (RatingBar) findViewById(R.id.movieRatingBar);

        //get tab manager
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        tabManager = new TabManager(this, tabHost, R.id.realtabcontent);

        //get database
        db = new MovieDatabase(getApplicationContext());
        ArrayList<List> lists = db.getLists();

        //add a tab for each list
        if (lists != null && !lists.isEmpty()) {
            for (List list : lists) {
                TabSpec tabSpec = tabHost.newTabSpec(list.getName());
                tabSpec.setIndicator(list.getName());
                tabManager.addTab(tabSpec, MovieFragment.class, null);
            }
        }

        //set current tab to the last tab opened
        if (savedInstanceState != null) {
            tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }


    }



    @Override
    protected void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
        outstate.putString("tab", tabHost.getCurrentTabTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tab_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuAddTask:
                Intent intent = new Intent(this, AddEditActivity.class);
                intent.putExtra("tab", tabHost.getCurrentTabTag());
                startActivity(intent);
                break;
            case R.id.menuDelete:
                ArrayList<Movie> movies = db.getMovies(tabHost.getCurrentTabTag());
                db.deleteAll();

                // Refresh list
                MovieFragment currentFragment = (MovieFragment)
                        getSupportFragmentManager().
                                findFragmentByTag(tabHost.getCurrentTabTag());
                currentFragment.refreshMovieList();

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
