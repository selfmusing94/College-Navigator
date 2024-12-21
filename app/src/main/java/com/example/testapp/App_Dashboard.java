package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.navigation.NavigationView;

public class App_Dashboard extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageButton button;
    NavigationView navview;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

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
        navview = findViewById(R.id.navview);

        // Set up Google Sign-In client
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Ensure correct Web Client ID
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Navigation Item Selection Listener
        navview.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.logoutinmenu) {
                logoutUser();  // Call refactored logout method
            } else if (item.getItemId() == R.id.CollegePredictor) {
                Toast.makeText(App_Dashboard.this, "College Predictor", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.close();  // Close the drawer after selecting an item
            return true;
        });

        // Drawer button functionality
        button.setOnClickListener(v -> drawerLayout.open());
    }

    // Refactored logout method to avoid code duplication
    private void logoutUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (GoogleSignIn.getLastSignedInAccount(App_Dashboard.this) != null) {
                mGoogleSignInClient.signOut().addOnCompleteListener(App_Dashboard.this, task -> {
                    if (task.isSuccessful()) {
                        mAuth.signOut();
                        Toast.makeText(App_Dashboard.this, "Logged out from Google", Toast.LENGTH_SHORT).show();
                        redirectToLogin();
                    } else {
                        Toast.makeText(App_Dashboard.this, "Logout failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mAuth.signOut();
                Toast.makeText(App_Dashboard.this, "Logged out from Email", Toast.LENGTH_SHORT).show();
                redirectToLogin();
            }
        } else {
            redirectToLogin();
        }
    }

    // Method to redirect to login activity after logout
    private void redirectToLogin() {
        Intent intent = new Intent(App_Dashboard.this, Login.class);
        startActivity(intent);
        finish();  // Close the current activity to prevent going back to it after logout
    }
}
