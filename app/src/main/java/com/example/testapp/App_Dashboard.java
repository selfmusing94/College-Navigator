package com.example.testapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser ;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.navigation.NavigationView;

public class App_Dashboard extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 1001; // Request code for permissions
    private DrawerLayout drawerLayout;
    private ImageButton button;
    private NavigationView navView;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView userName, userEmail; // Shared resource placeholders
    private ImageView profimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Ensure edge-to-edge display
        setContentView(R.layout.activity_app_dashboard);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set up Toolbar and Drawer Layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer);
        button = findViewById(R.id.drawerbutton);
        navView = findViewById(R.id.navview);

        // Set up Google Sign-In client
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Ensure correct Web Client ID
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set up Navigation Drawer
        setupNavigationDrawer();

        // Drawer button functionality
        button.setOnClickListener(v -> drawerLayout.open());

        // Check for permissions
        checkPermissions();
    }

    private void setupNavigationDrawer() {
        // Load user details dynamically
        View headerView = navView.getHeaderView(0);
        profimage = headerView.findViewById(R.id.profimage);
        userName = headerView.findViewById(R.id.sidebarname); // Add IDs in layout
        userEmail = headerView.findViewById(R.id.sidebarmail);
        loadUserDetails();

        // Navigation Item Selection Listener
        navView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.logoutinmenu) {
                confirmLogout();
            } else if (itemId == R.id.CollegePredictor) {
                navigateToCollegePredictor();
            } else if (itemId == R.id.Collegereview) {
                Toast.makeText(App_Dashboard.this, "College Review", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.Top10) {
                Toast.makeText(App_Dashboard.this, "Top 10 Colleges", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.cutoff) {
                Toast.makeText(App_Dashboard.this, "Check Cutoffs", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.LocationBased) {
                Toast.makeText(App_Dashboard.this, "Location Based Activity", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.Senior) {
                Toast.makeText(App_Dashboard.this, "Connect with Seniors", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.pg) {
                Toast.makeText(App_Dashboard.this, "PG Recommendations", Toast.LENGTH_SHORT).show();
            } else if (itemId == R.id.bot) {
                Toast.makeText(App_Dashboard.this, "Chat with Bot", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.close();
            return true;
        });
    }

    private void loadUserDetails() {
        FirebaseUser  currentUser  = mAuth.getCurrentUser ();
        if (currentUser  != null) {
            // Fetch user details from Firebase Realtime Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser .getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Fetch username, email, and profile image URL
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String email = currentUser .getEmail(); // Get email from FirebaseUser
                        String profileImageUrl = dataSnapshot.child("profilepic").getValue(String.class);

                        // Update UI elements
                        userName.setText(username != null ? username : "Guest User");
                        userEmail.setText(email != null ? email : "No Email");

                        // Load profile image
                        loadProfileImage(profileImageUrl);
                    } else {
                        // Handle case where user data does not exist
                        userName.setText("Guest User");
                        userEmail.setText("No Email");
                        loadProfileImage(null); // Load default image
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(App_Dashboard.this, "Error loading user details", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void navigateToCollegePredictor() {
        Intent intent = new Intent(App_Dashboard.this, CollegePredictorActivity.class);
        startActivity(intent);
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    logoutUser (); // Perform the logout action
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss(); // Explicitly dismiss the dialog
                })
                .show();
    }

    private void logoutUser () {
        SessionManager sessionManager = new SessionManager(this);
        FirebaseUser  currentUser  = mAuth.getCurrentUser ();
        if (currentUser  != null) {
            if (GoogleSignIn.getLastSignedInAccount(App_Dashboard.this) != null) {
                mGoogleSignInClient.signOut().addOnCompleteListener(App_Dashboard.this, task -> {
                    if (task.isSuccessful()) {
                        mAuth.signOut();
                        Toast.makeText(App_Dashboard.this, "Logged out", Toast.LENGTH_SHORT).show();
                        sessionManager.logoutUser ();
                        redirectToLogin();
                    } else {
                        Toast.makeText(App_Dashboard.this, "Logout failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mAuth.signOut();
                Toast.makeText(App_Dashboard.this, "Logged out", Toast.LENGTH_SHORT).show();
                sessionManager.logoutUser ();
                redirectToLogin();
            }
        } else {
            redirectToLogin();
        }
    }

    private void loadProfileImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl) // Load the image from the URL
                    .into(profimage); // Set the image to the ImageView
        } else {
            // Load the default image if the URL is null or empty
            Toast.makeText(this, "Error Loading Image", Toast.LENGTH_SHORT).show(); // Show error message
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
        } else {
            // Permission is already granted, proceed with loading images
            loadUserDetails();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with loading images
                loadUserDetails();
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied to read external storage", Toast.LENGTH_SHORT).show();
                // Optionally, you can show a dialog explaining why the permission is needed
                showPermissionExplanation();
            }
        }
    }

    private void showPermissionExplanation() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("This app requires access to your storage to load images. Please grant the permission.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Redirect to app settings
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(App_Dashboard.this, Login.class);
        startActivity(intent);
        finish(); // Prevent back navigation to this activity
    }
}