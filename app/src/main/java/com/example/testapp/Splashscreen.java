package com.example.testapp;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Splashscreen extends AppCompatActivity {

    TextView by, selfm, name;
    ImageView logo;
    Animation top, bottom; // Animations

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // To hide status bar

        // Load animations
        top = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottom = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Initialize views
        logo = findViewById(R.id.image_logo);
        selfm = findViewById(R.id.textView3);
        by = findViewById(R.id.textView2);
        name = findViewById(R.id.text_name);

        // Set animations
        by.setAnimation(bottom);
        selfm.setAnimation(bottom);
        logo.setAnimation(top);
        name.setAnimation(top);

        // Delay to transition to the next activity
        new Handler().postDelayed(() -> {
            // Check if the user is logged in
            SessionManager sessionManager = new SessionManager(Splashscreen.this);
            Intent intent;

            if (sessionManager.isLoggedIn()) {
                // User is logged in, navigate to the dashboard
                intent = new Intent(Splashscreen.this, App_Dashboard.class);
            } else {
                // User is not logged in, navigate to the login screen
                intent = new Intent(Splashscreen.this, Login.class);
            }

            // Optional: Add shared element transition if needed
            Pair<View, String>[] pairs = new Pair[3];
            pairs[0] = new Pair<>(logo, "logo_image");
            pairs[1] = new Pair<>(name, "logo_text1");
            pairs[2] = new Pair<>(selfm, "logo_text2"); // Corrected to use selfm for the second text

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Splashscreen.this, pairs);
            startActivity(intent, options.toBundle());
            finish(); // Close the splash screen activity
        }, 2000); // 2-second delay
    }
}