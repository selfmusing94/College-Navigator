package com.example.testapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AboutMe extends AppCompatActivity {

    ImageButton linkedin,github,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Ensure edge-to-edge display
        setContentView(R.layout.aboutme);

        initializeViews();

        setupclicks();
    }
    private void initializeViews(){
        linkedin=findViewById(R.id.linkedin_button);
        github=findViewById(R.id.github_button);
        email=findViewById(R.id.email_button);
    }
    private void setupclicks(){
        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advancedanimateCardClick(v,()->openLinkedIn());
            }
        });

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advancedanimateCardClick(v,()->openGithub());
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advancedanimateCardClick(v,()->sendEmail());
            }
        });

    }

    private void openLinkedIn() {
        try {
            // Try to open LinkedIn app if installed
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.Linkedin_URL)));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // If LinkedIn app is not installed, open in browser
            openUrl(getString(R.string.Linkedin_URL));
        }
    }

    private void openGithub() {
        openUrl(getString(R.string.Github_URL));
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No browser app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + getString(R.string.Email_URL)));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Ram Ram from College Navigator App");

        try {
            startActivity(Intent.createChooser(intent, "Send email using..."));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No email app installed", Toast.LENGTH_SHORT).show();
        }
    }

    //Add click effect for cards
    private void rippleCardClick(View view) {
        // Create ripple effect
        view.setPressed(true);

        // Scale and ripple animation
        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    // Return to original state
                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start();

                    // Release pressed state
                    view.setPressed(false);
                })
                .start();
    }
    private void advancedanimateCardClick(@NonNull View view, Runnable postAnimationAction) {

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

