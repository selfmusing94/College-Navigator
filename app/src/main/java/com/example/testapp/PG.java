package com.example.testapp;

public class PG {
    private String name;
    private String location;
    private String roomType;
    private String rent;
    private String gender;
    private double latitude;
    private double longitude;

    public PG(String name, String location, String roomType, String rent, String gender, double latitude, double longitude) {
        this.name = name;
        this.location = location;
        this.roomType = roomType;
        this.rent = rent;
        this.gender = gender;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRent() {
        return rent;
    }

    public String getGender() {
        return gender;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}