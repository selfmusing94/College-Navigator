package com.example.testapp;

// SeniorCollege.java
public class SeniorCollege {
    private String seniorId;
    private String seniorCollegeName;
    private String seniorLocation;
    private String seniorImageUrl;


    public SeniorCollege(String seniorId, String seniorName, String seniorLocation, String seniorImageUrl) { // Renamed constructor
        this.seniorId = seniorId;
        this.seniorCollegeName = seniorName;
        this.seniorLocation = seniorLocation;
        this.seniorImageUrl = seniorImageUrl;
    }

    // Getters and setters
    public String getSeniorId() { return seniorId; }
    public String getSeniorName() { return seniorCollegeName; }
    public String getSeniorLocation() { return seniorLocation; }
    public String getSeniorImageUrl() { return seniorImageUrl; }
}