package com.example.testapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

public class PGFinderActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private RecyclerView rvPGResults;
    private PGAdapter pgAdapter; // Adapter for displaying PGs
    private List<PG> pgList; // List of PGs
    private TextInputEditText etLocation;
    private LinearLayout emptyStateLayout;
    private TextView tvCurrentLocation; // TextView to display current location
    private ProgressDialog progressDialog; // ProgressDialog to show fetching location

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pgfinder); // Ensure this matches your layout file name

        etLocation = findViewById(R.id.etLocation);
        Button btnCurrentLocation = findViewById(R.id.btnCurrentLocation);
        Button btnSearchPGs = findViewById(R.id.btnSearchPGs);
        rvPGResults = findViewById(R.id.rvPGResults);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        tvCurrentLocation = findViewById(R.id.tvpgCurrentLocation); // Ensure this ID matches your layout

        // Initialize RecyclerView
        rvPGResults.setLayoutManager(new LinearLayoutManager(this));
        pgList = new ArrayList<>();
        pgAdapter = new PGAdapter(pgList); // Initialize the adapter
        rvPGResults.setAdapter(pgAdapter);

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

        btnSearchPGs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationInput = etLocation.getText().toString();
                onSearch(locationInput);
            }
        });
    }

    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        // Show progress dialog
        progressDialog.show();

        // Create a location request
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds

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
                    // Fetch nearby PGs based on user location
                    fetchNearbyPGs(userLatitude, userLongitude);
                }
                progressDialog.dismiss(); // Dismiss dialog after fetching location
            }
        }, null);
    }

    private String fetchAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0); // Return the first address line
            }
        } catch (IOException e) {
            Log.e("PGFinderActivity", "Geocoder IOException: " + e.getMessage());
        }
        return "Unknown Location"; // Fallback if address not found
    }

    private void fetchNearbyPGs(double latitude, double longitude) {
        // Implement your logic to fetch PGs based on latitude and longitude
        // For example, you can make a network request to your backend API
        // Update the pgList and notify the adapter
        // If no PGs found, show empty state layout
        if (pgList.isEmpty()) {
            rvPGResults.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            rvPGResults.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
        pgAdapter.notifyDataSetChanged(); // Notify adapter of data changes
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation(); // Retry fetching location if permission granted
            } else {
                Toast.makeText(this, "Location permission is required to use current location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onSearch(String query) {
        if (!query.isEmpty()) {
            // Clear the previous results
            pgList.clear();

            // Here you would implement the logic to search for PGs based on the query
            // For demonstration, let's add some mock data
            // In a real application, you would fetch this data from a database or API

            // Mock data based on the search query
            if (query.equalsIgnoreCase("PG A")) {
                pgList.add(new PG("PG A", 12.9716, 77.5946)); // Example coordinates
            } else if (query.equalsIgnoreCase("PG B")) {
                pgList.add(new PG("PG B", 12.9717, 77.5947)); // Example coordinates
            } else {
                // If no PGs found, you can show a message or update the empty state layout
                Toast.makeText(this, "No PGs found for: " + query, Toast.LENGTH_SHORT).show();
            }

            // Notify the adapter of the data change
            pgAdapter.notifyDataSetChanged();

            // Show or hide the empty state layout based on the results
            if (pgList.isEmpty()) {
                rvPGResults.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
            } else {
                rvPGResults.setVisibility(View.VISIBLE);
                emptyStateLayout.setVisibility(View.GONE);
            }
        } else {
            // If the query is empty, show the empty state layout
            emptyStateLayout.setVisibility(View.VISIBLE);
            rvPGResults.setVisibility(View.GONE);
        }
    }
}