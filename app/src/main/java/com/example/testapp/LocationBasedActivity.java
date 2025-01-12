package com.example.testapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog; // Import ProgressDialog
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationBasedActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private RecyclerView rvCollegeResults;
    private LocationCollegeAdapter locationCollegeAdapter;
    private List<LocationCollege> collegeList;
    private TextInputEditText etLocation;
    private LinearLayout emptyStateLayout;
    private TextView tvCurrentLocation; // TextView to display current location
    private ProgressDialog progressDialog; // ProgressDialog to show fetching location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_based);

        etLocation = findViewById(R.id.etLocation);
        Button btnCurrentLocation = findViewById(R.id.btnCurrentLocation);
        Button btnSearchColleges = findViewById(R.id.btnSearchColleges);
        rvCollegeResults = findViewById(R.id.rvCollegeResults);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        tvCurrentLocation = findViewById(R.id.tvCurrentLocation); // Initialize TextView

        // Initialize RecyclerView
        rvCollegeResults.setLayoutManager(new LinearLayoutManager(this));
        collegeList = new ArrayList<>();
        locationCollegeAdapter = new LocationCollegeAdapter(collegeList);
        rvCollegeResults.setAdapter(locationCollegeAdapter);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching location...");
        progressDialog.setCancelable(false); // Prevent dismissing the dialog on back press

        btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCurrentLocation();
            }
        });

        btnSearchColleges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationInput = etLocation.getText().toString();
                onSearch(locationInput);
            }
        });
    }

    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        // Show progress dialog
        progressDialog.show();

        // Create a location request
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3500);
        locationRequest.setFastestInterval(1000);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    progressDialog.dismiss(); // Dismiss dialog if no location result
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    double userLatitude = location.getLatitude();
                    double userLongitude = location.getLongitude();
                    // Display current location
                    String address = fetchAddressFromLocation(userLatitude, userLongitude);
                    tvCurrentLocation.setVisibility(View.VISIBLE);
                    tvCurrentLocation.setText("Current Location: " + address);
                    fetchNearbyColleges(userLatitude,userLongitude);
                }
                fusedLocationClient.removeLocationUpdates(this); // Stop location updates
                progressDialog.dismiss(); // Dismiss the progress dialog after fetching the location
            }
        }, null);
    }

    private String fetchAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0); // Return the full address as a string
            } else {
                return "Unable to fetch address.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error fetching address.";
        }
    }

    private void fetchNearbyColleges(double latitude, double longitude) {
        // Mock data for nearby colleges
        collegeList.clear();
        collegeList.add(new LocationCollege("College A", latitude + 0.01, longitude + 0.01));
        collegeList.add(new LocationCollege("College B", latitude + 0.02, longitude + 0.02));
        collegeList.add(new LocationCollege("College C", latitude + 0.03, longitude + 0.03));

        locationCollegeAdapter.notifyDataSetChanged();
        rvCollegeResults.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE); // Hide empty state layout when results are available
    }

    private void onSearch(String query) {
        if (!query.isEmpty()) {
            emptyStateLayout.setVisibility(View.GONE);
            rvCollegeResults.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Searching for colleges in: " + query, Toast.LENGTH_SHORT).show();

            // Implement actual search logic here
        } else {
            emptyStateLayout.setVisibility(View.VISIBLE);
            rvCollegeResults.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission is required to access your current location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}