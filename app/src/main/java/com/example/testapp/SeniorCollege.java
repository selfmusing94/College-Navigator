package com.example.testapp;

// SeniorCollege.java
public class SeniorCollege {
    private String seniorId; // Renamed variable
    private String seniorName; // Renamed variable
    private String seniorLocation; // Renamed variable
    private String seniorImageUrl; // Renamed variable

    public SeniorCollege(String seniorId, String seniorName, String seniorLocation, String seniorImageUrl) { // Renamed constructor
        this.seniorId = seniorId; // Renamed variable
        this.seniorName = seniorName; // Renamed variable
        this.seniorLocation = seniorLocation; // Renamed variable
        this.seniorImageUrl = seniorImageUrl; // Renamed variable
    }

    // Getters and setters
    public String getSeniorId() { return seniorId; } // Renamed method
    public String getSeniorName() { return seniorName; } // Renamed method
    public String getSeniorLocation() { return seniorLocation; } // Renamed method
    public String getSeniorImageUrl() { return seniorImageUrl; } // Renamed method
}