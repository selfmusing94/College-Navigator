package com.example.testapp;

import java.util.List;

public class College {
    private String name;
    private String location;
    private  double rating;
    private int establishedYear;
    private List<String> courses;
    private  int cutoff;

    // Constructor
    public College(String name, String location, double rating, int establishedYear, List<String> courses, int cutoff) {
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.establishedYear = establishedYear;
        this.courses = courses;
        this.cutoff = cutoff;
    }


    // Getters

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

    public int getCutoff() {
        return cutoff;
    }

    // Setters
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

    public void setCutoff(int cutoff) {
        this.cutoff = cutoff;
    }

}
