package com.example.testapp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import android.os.Build;


public class App_Dashboard extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 1001; // Request code for permissions
    private DrawerLayout drawerLayout;
    private ImageButton button;
    private NavigationView navView;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView userName, userEmail; // Shared resource placeholders
    private ImageView profimage;

    private static final int PERMISSION_REQUEST_CODE = 100;

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

        // Initialize and set click listeners for cards
        setupCardClickListeners();

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
            } else if (itemId == R.id.aboutme) {
                navigatetoAboutme();
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
    private void navigatetoAboutme(){
        Intent intent = new Intent(App_Dashboard.this,AboutMe.class);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VIDEO,
                                Manifest.permission.READ_MEDIA_AUDIO
                        },
                        PERMISSION_REQUEST_CODE
                );
            } else {
                Toast.makeText(this, "Media permissions already granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            // For Android 12 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE
                );
            } else {
                Toast.makeText(this, "Storage permission already granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void redirectToLogin() {
        Intent intent = new Intent(App_Dashboard.this, Login.class);
        startActivity(intent);
        finish(); // Prevent back navigation to this activity
    }

    private void setupCardClickListeners() {
        // Card 1: College Predictor
        RelativeLayout card1 = findViewById(R.id.card1);
        card1.setOnClickListener(v -> {
            rippleCardClick(v);
            // Perform action after a short delay
            new Handler().postDelayed(() -> {
            Intent intent = new Intent(App_Dashboard.this, CollegePredictorActivity.class);
            startActivity(intent);
            }, 250);
        });

        // Card 2: Review System
        RelativeLayout card2 = findViewById(R.id.card2);
        card2.setOnClickListener(v -> {
            rippleCardClick(v);
            new Handler().postDelayed(() -> {
            Intent intent = new Intent(App_Dashboard.this, ReviewCollegeActivity.class);
            startActivity(intent);
        }, 250);
        });

        // Card 3: Top 10 Colleges
        RelativeLayout card3 = findViewById(R.id.card3);
        card3.setOnClickListener(v -> {
            rippleCardClick(v);
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(App_Dashboard.this, TopCollegesActivity.class);
                startActivity(intent);
            }, 250);
        });

        // Card 4: Location Based College Finder
        RelativeLayout card4 = findViewById(R.id.card4);
        card4.setOnClickListener(v -> {
            rippleCardClick(v);
            new Handler().postDelayed(() -> {
            Intent intent = new Intent(App_Dashboard.this, LocationBasedActivity.class);
            startActivity(intent);
            }, 250);
        });

        // Card 5: Cutoff Analysis
        RelativeLayout card5 = findViewById(R.id.card5);
        card5.setOnClickListener(v -> {
            rippleCardClick(v);
            new Handler().postDelayed(() -> {
            Intent intent = new Intent(App_Dashboard.this, CutoffAnalysisActivity.class);
            startActivity(intent);
            }, 250);
        });

        // Card 6: PG Finder
        RelativeLayout card6 = findViewById(R.id.card6);
        card6.setOnClickListener(v -> {
            rippleCardClick(v);
            new Handler().postDelayed(() -> {
            Intent intent = new Intent(App_Dashboard.this, PGFinderActivity.class);
            startActivity(intent);
            }, 250);
        });

        // Card 7: Senior Connect
        RelativeLayout card7 = findViewById(R.id.card7);
        card7.setOnClickListener(v -> {
            rippleCardClick(v);
            new Handler().postDelayed(() -> {
            Intent intent = new Intent(App_Dashboard.this, SeniorConnectActivity.class);
            startActivity(intent);
            }, 250);
        });

        // Card 8: AI Chatbot
        RelativeLayout card8 = findViewById(R.id.card8);
        card8.setOnClickListener(v -> {
            rippleCardClick(v);
            new Handler().postDelayed(() -> {
            Intent intent = new Intent(App_Dashboard.this, AIChatbotActivity.class);
            startActivity(intent);
            }, 250);
        });
    }

    //Add click effect for cards
    private void rippleCardClick(View view) {
        // Create ripple effect
        view.setPressed(true);

        // Scale and ripple animation
        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(150)
                .withEndAction(() -> {
                    // Return to original state
                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .start();

                    // Release pressed state
                    view.setPressed(false);
                })
                .start();
    }
    private void advancedanimateCardClick(View view, Runnable postAnimationAction) {

        // Comprehensive animation
        view.animate()
                .scaleX(0.90f)
                .scaleY(0.90f)
                .setDuration(100)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    // Bounce back animation
                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .setInterpolator(new OvershootInterpolator())
                            .withEndAction(postAnimationAction)
                            .start();
                })
                .start();
    }
}

