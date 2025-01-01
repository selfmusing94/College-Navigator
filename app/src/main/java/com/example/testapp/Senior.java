package com.example.testapp;

// Senior.java
public class Senior {
    private String seniorName; // Renamed variable
    private String seniorBranch; // Renamed variable
    private String seniorBatch; // Renamed variable
    private String seniorImageUrl; // Renamed variable

    public Senior(String seniorName, String seniorBranch, String seniorBatch, String seniorImageUrl) { // Renamed constructor
        this.seniorName = seniorName; // Renamed variable
        this.seniorBranch = seniorBranch; // Renamed variable
        this.seniorBatch = seniorBatch; // Renamed variable
        this.seniorImageUrl = seniorImageUrl; // Renamed variable
    }

    // Getters and setters
    public String getSeniorName() { return seniorName; } // Renamed method
    public String getSeniorBranch() { return seniorBranch; } // Renamed method
    public String getSeniorBatch() { return seniorBatch; } // Renamed method
    public String getSeniorImageUrl() { return seniorImageUrl; } // Renamed method
}