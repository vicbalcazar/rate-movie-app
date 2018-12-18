package com.cpsc411.campususer.app4;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by uhohi_000 on 12/6/2017.
 */

public class AddEditActivity extends Activity implements View.OnKeyListener {

    private EditText movieEditText;
    private RatingBar ratingEdit;
    private DatePicker datePicker;
    private Spinner listSpinner;
    private CheckBox checkBox;

    private SimpleDateFormat dateFormatter;
    private Date d;

    private MovieDatabase db;
    private boolean editMode;
    private String currentTabName = "";
    private Movie movie;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        // get references to widgets
        listSpinner = (Spinner) findViewById(R.id.listSpinner);
        movieEditText = (EditText) findViewById(R.id.movieEditText);
        ratingEdit = (RatingBar) findViewById(R.id.ratingEdit);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        // set listeners
        movieEditText.setOnKeyListener(this);
        ratingEdit.setOnKeyListener(this);

        // get the database object
        db = new MovieDatabase(this);

        // set the adapter for the spinner
        ArrayList<List> lists = db.getLists();
        ArrayAdapter<List> adapter = new ArrayAdapter<List>(
                this, R.layout.spinner_list, lists);
        listSpinner.setAdapter(adapter);

        // get edit mode from intent
        Intent intent = getIntent();
        editMode = intent.getBooleanExtra("editMode", false);

        // if editing
        if (editMode) {
            // get movie
            long movieId = intent.getIntExtra("movieId", -1);
            movie = db.getMovie(movieId);

            // update UI with movie
            movieEditText.setText(movie.getName());
            ratingEdit.setRating(movie.getRating());
        }

        // set the correct list for the spinner
        long listID;
        if (editMode) {   // edit mode - use same list as selected task
            listID = (int) movie.getListId();
        }
        else {            // add mode - use the list for the current tab
            currentTabName = intent.getStringExtra("tab");
            listID = (int) db.getList(currentTabName).getId();
        }
        // subtract 1 from database ID to get correct list position
        int listPosition = (int) listID - 1;
        listSpinner.setSelection(listPosition);

        listSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                if(selectedItem.equals("Most Recent"))
                {
                    Log.d("TABCHECK", " MOST RECENT TAB");
                    ratingEdit.setVisibility(View.GONE);
                    datePicker.setVisibility(View.VISIBLE);
                }
                else if(selectedItem.equals("Ratings"))
                {
                    Log.d("TABCHECK", " RATINGS");
                    ratingEdit.setVisibility(View.VISIBLE);
                    datePicker.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSave:
                if (checkBox.isChecked()) {
                    Intent notificationIntent =
                            new Intent(this,
                                    TabListsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    int flag = PendingIntent.FLAG_UPDATE_CURRENT;
                    PendingIntent pendingIntent =
                            PendingIntent.getActivity(this, 0, notificationIntent, flag);

                    int icon = R.drawable.ic_launcher;
                    CharSequence tickerText = "REMEMBER TO RATE THE MOVIE";
                    CharSequence contentTitle = getText(R.string.app_name);
                    CharSequence contentText = "Select to edit movie";

                    Notification notification =
                            new Notification.Builder(this)
                            .setSmallIcon(icon)
                            .setTicker(tickerText)
                            .setContentTitle(contentTitle)
                            .setContentText(contentText)
                            .setContentIntent(pendingIntent)
                            .build();
                    NotificationManager manager = (NotificationManager)
                            getSystemService(NOTIFICATION_SERVICE);
                    final int NOTIF_ID = 1;
                    manager.notify(NOTIF_ID, notification);
                }
                saveToDB();
                this.finish();
                break;
            case R.id.menuCancel:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveToDB() {
        // get data from widgets
        int listID = 1;
        String name = movieEditText.getText().toString();
        float ratings = ratingEdit.getRating();

        //get date formatted string
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        cal.set(Calendar.MONTH, datePicker.getMonth());
        cal.set(Calendar.YEAR, datePicker.getYear());
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormatter.format(new Date(cal.getTimeInMillis()));

        // if no movie name, exit method
        if (name == null || name.equals("")) {
            return;
        }

        // if add mode, create new movie
        if (!editMode) {
            movie = new Movie();
        }

        // put data in movie
        movie.setListId(listID);
        movie.setName(name);
        movie.setRating(ratings);
        movie.setDate(date);

        // update or insert movie
        if (editMode) {
            db.updateMovie(movie);
        }
        else {
            db.insertMovie(movie);
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            // hide the soft Keyboard
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveToDB();
            return false;
        }
        return false;
    }
}
