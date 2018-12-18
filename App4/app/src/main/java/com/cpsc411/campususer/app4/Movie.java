package com.cpsc411.campususer.app4;

/**
 * Created by campususer on 11/29/17.
 */

public class Movie {

    private int movieId;
    private int listId;
    private String name;
    private float rating;
    private String date;

    public Movie() {
        name = "";
        date = "";
    }

    public Movie(int listId, String name, float rating, String date) {
        this.listId = listId;
        this.name = name;
        this.rating = rating;
        this.date = date;
    }

    public Movie(int movieId, int listId, String name, float rating, String date) {
        this.movieId = movieId;
        this.listId = listId;
        this.name = name;
        this.rating = rating;
        this.date = date;
    }

    public void setId(int movieId) {
        this.movieId = movieId;
    }

    public int getId() {
        return movieId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public int getListId() {
        return listId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }



}
