package com.example.testapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class College {
    private int rank;
    private String name;
    private String location;
    private  double rating;
    private int establishedYear;
    private List<String> courses;

    // Constructor
    public College(int rank, String name, String location, double rating, int establishedYear, List<String> courses) {
        this.rank = rank;
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.establishedYear = establishedYear;
        this.courses = courses;
    }

    // Getters
    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public double getRating() {
        return rating;
    }

    public int getEstablishedYear() {
        return establishedYear;
    }

    public List<String> getCourses() {
        return courses;
    }

    // Setters
    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setEstablishedYear(int establishedYear) {
        this.establishedYear = establishedYear;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }

}