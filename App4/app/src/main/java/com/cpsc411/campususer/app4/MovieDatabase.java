package com.cpsc411.campususer.app4;

/**
 * Created by campususer on 11/29/17.
 */
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class MovieDatabase {

    //database constants
    public static final String  DB_NAME = "movielist.db";
    public static final int     DB_VERSION = 1;

    //list table constants
    public static final String  LIST_TABLE = "list";

    public static final String  LIST_ID = "_id";
    public static final int     LIST_ID_COL = 0;

    public static final String  LIST_NAME = "movie_list";
    public static final int     LIST_NAME_COL = 1;

    //movie table constants
    public static final String  MOVIE_TABLE = "movie";

    public static final String  MOVIE_ID = "_id";
    public static final int     MOVIE_ID_COL = 0;

    public static final String  MOVIE_LIST_ID = "list_id";
    public static final int     MOVIE_LIST_ID_COL = 1;

    public static final String  MOVIE_NAME = "movie_name";
    public static final int     MOVIE_NAME_COL = 2;

    public static final String  MOVIE_RATING = "rating";
    public static final int     MOVIE_RATING_COL = 3;

    public static final String  MOVIE_MOSTRECENT = "mostrecent";
    public static final int     MOVIE_MOSTRECENT_COL = 4;

    //CREATE and DROP TABLE statements
    public static final String CREATE_LIST_TABLE =
            "CREATE TABLE " + LIST_TABLE + " (" + LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LIST_NAME + " TEXT    NOT NULL UNIQUE);";

    public static final String CREATE_MOVIE_TABLE =
            "CREATE TABLE " + MOVIE_TABLE + " (" +
                    MOVIE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MOVIE_LIST_ID + " INTEGER NOT NULL, " +
                    MOVIE_NAME + " TEXT     NOT NULL UNIQUE, " +
                    MOVIE_RATING + " REAL NOT NULL, " +
                    MOVIE_MOSTRECENT + " TEXT);";

    public static final String DROP_LIST_TABLE =
            "DROP TABLE IF EXISTS " + LIST_TABLE;

    public static final String DROP_MOVIE_TABLE =
            "DROP TABLE IF EXISTS " + MOVIE_TABLE;

    //Create DBHelper inner class
    private static class DBHelper extends SQLiteOpenHelper {

        public  DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                         int version){
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_LIST_TABLE);
            db.execSQL(CREATE_MOVIE_TABLE);

            //insert default lists
            db.execSQL("INSERT INTO list VALUES (1, 'Most Recent')");
            db.execSQL("INSERT INTO list VALUES (2, 'Ratings')");

            //insert sample movies
            db.execSQL("INSERT INTO movie VALUES (1, 1, 'Thor: Ragnarok', 3, '10/27/1992')");
            db.execSQL("INSERT INTO movie VALUES (2, 1, 'HULK', 3.5, '10/27/1992')");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("Task list", "Upgrading db from version " + oldVersion +
                    " to " + newVersion);
            db.execSQL(MovieDatabase.DROP_LIST_TABLE);
            db.execSQL(MovieDatabase.DROP_MOVIE_TABLE);
            onCreate(db);
        }
    }

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    //constructor
    public MovieDatabase(Context context) {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    //private methods
    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if (db != null)
            db.close();
    }

    ////////////////////////////////////////
    ////////    PUBLIC METHODS  ////////////
    ////////////////////////////////////////

    public ArrayList<List> getLists() {
        ArrayList<List> lists = new ArrayList<List>();
        openReadableDB();
        Cursor cursor = db.query(LIST_TABLE,
                null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            List list = new List();
            list.setId(cursor.getInt(LIST_ID_COL));
            list.setName(cursor.getString(LIST_NAME_COL));

            lists.add(list);
        }
        cursor.close();
        closeDB();
        return lists;
    }

    public List getList(String name) {
        String where = LIST_NAME + "= ?";
        String[] whereArgs = {  name   };

        openReadableDB();
        Cursor cursor = db.query(LIST_TABLE, null,
                where, whereArgs, null, null, null);
        List list = null;
        cursor.moveToFirst();
        list = new List(cursor.getInt(LIST_ID_COL),
                        cursor.getString(LIST_NAME_COL));
        cursor.close();
        this.closeDB();

        return list;
    }

    public ArrayList<Movie> returnByRating(String listName){
        String where =
                MOVIE_LIST_ID + "= ?";
        int listID = getList(listName).getId();
        String[] whereArgs = { Integer.toString(listID)};

        this.openReadableDB();

        Cursor cursor = db.query(MOVIE_TABLE, null,
                where, whereArgs,
                null, null, MOVIE_RATING +" DESC");
        ArrayList<Movie> movies = new ArrayList<Movie>();

        while (cursor.moveToNext()) {
            movies.add(getMovieFromCursor(cursor));
        }

        if (cursor != null)
            cursor.close();
        this.closeDB();

        return movies;
    }

    public ArrayList<Movie> returnByRecentDate(String listName) {
        String where =
                MOVIE_LIST_ID + "= ?";
        int listID = getList(listName).getId();
        String[] whereArgs = { Integer.toString(listID)};

        this.openReadableDB();

        Cursor cursor = db.query(MOVIE_TABLE, null,
                where, whereArgs,
                null, null, MOVIE_MOSTRECENT +" DESC");
        ArrayList<Movie> movies = new ArrayList<Movie>();

        while (cursor.moveToNext()) {
            movies.add(getMovieFromCursor(cursor));
        }

        if (cursor != null)
            cursor.close();
        this.closeDB();

        return movies;
    }

    public ArrayList<Movie> getMovies(String listName) {
        String where =
                MOVIE_LIST_ID + "= ?";
        int listID = getList(listName).getId();
        String[] whereArgs = { Integer.toString(listID)};

        this.openReadableDB();

        Cursor cursor = db.query(MOVIE_TABLE, null,
                where, whereArgs,
                null, null, null);
        ArrayList<Movie> movies = new ArrayList<Movie>();
        while (cursor.moveToNext()) {
            movies.add(getMovieFromCursor(cursor));
        }
        if (cursor != null)
            cursor.close();
        this.closeDB();

        return movies;
    }

    public Movie getMovie(long id) {
        String where = MOVIE_ID + "= ?";
        String[] whereArgs = {  Long.toString(id) };

        this.openReadableDB();
        Cursor cursor = db.query(MOVIE_TABLE,
                null, where, whereArgs, null, null, null);
        cursor.moveToFirst();
        Movie movie = getMovieFromCursor(cursor);
        if (cursor != null)
            cursor.close();
        this.closeDB();

        return movie;
    }

    private static Movie getMovieFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        else {
            try {
                Movie movie = new Movie(
                        cursor.getInt(MOVIE_ID_COL),
                        cursor.getInt(MOVIE_LIST_ID_COL),
                        cursor.getString(MOVIE_NAME_COL),
                        cursor.getInt(MOVIE_RATING_COL),
                        cursor.getString(MOVIE_MOSTRECENT_COL));
                return movie;
            }catch (Exception e) {
                return null;
            }
        }
    }

    public long insertMovie(Movie movie) {
        ContentValues cv = new ContentValues();
        cv.put(MOVIE_LIST_ID, movie.getListId() );
        cv.put(MOVIE_NAME, movie.getName());
        cv.put(MOVIE_RATING, movie.getRating());
        cv.put(MOVIE_MOSTRECENT, movie.getDate());

        this.openWriteableDB();
        long rowID = db.insert(MOVIE_TABLE, null, cv);
        this.closeDB();

        return rowID;
    }

    public int updateMovie(Movie movie) {
        ContentValues cv = new ContentValues();
        cv.put(MOVIE_LIST_ID, movie.getListId() );
        cv.put(MOVIE_NAME, movie.getName());
        cv.put(MOVIE_RATING, movie.getRating());
        cv.put(MOVIE_MOSTRECENT, movie.getDate());

        String where = MOVIE_ID + "= ?";
        String[] whereArgs = { String.valueOf(movie.getId()) };

        this.openWriteableDB();
        int rowCount = db.update(MOVIE_TABLE, cv, where, whereArgs);
        this.closeDB();

        return rowCount;
    }

    public int deleteMovie(long id) {
        String where = MOVIE_ID + "= ?";
        String[] whereArgs = { String.valueOf(id) };

        this.openReadableDB();
        int rowCount = db.delete(MOVIE_TABLE, where, whereArgs);
        this.closeDB();

        return rowCount;
    }

    public void deleteAll() {
        this.openReadableDB();
        db.execSQL("delete from "+ MOVIE_TABLE);
        this.closeDB();

    }
}


