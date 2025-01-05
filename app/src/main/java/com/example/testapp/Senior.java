package com.example.testapp;

// Senior.java
public class Senior {
    private String seniorName;
    private String seniorBranch;
    private String seniorBatch;
    private String seniorImageUrl;
    private String college;
    private String LinkedInUrl;

    public Senior(String seniorName, String college, String seniorBranch, String seniorBatch, String seniorImageUrl,String LinkedInUrl) {
        this.seniorName = seniorName;
        this.college = college;
        this.seniorBranch = seniorBranch;
        this.seniorBatch = seniorBatch;
        this.seniorImageUrl = seniorImageUrl;
        this.LinkedInUrl = LinkedInUrl;
    }

    // Getters and setters
    public String getSeniorName() { return seniorName; }
    public String getSeniorBranch() { return seniorBranch; }
    public String getSeniorBatch() { return seniorBatch; }
    public String getSeniorImageUrl() { return seniorImageUrl; }
    public String getCollege() { return college; }
    public String getLinkedInUrl() {return LinkedInUrl; }

    public void setLinkedInPhotoUrl(String linkedInUrl) {
        seniorImageUrl = linkedInUrl;
    }
}