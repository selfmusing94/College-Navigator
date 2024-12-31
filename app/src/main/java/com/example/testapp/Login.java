package com.example.testapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser ;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    // Firebase Authentication
    private FirebaseAuth auth;

    // Google Sign-In Client
    private GoogleSignInClient googleSignInClient;

    // Progress Dialog
    private ProgressDialog progressDialog;

    // UI Components
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ImageButton googleSignInButton,lggithub,lgfacebook;
    private TextView signupButton;
    private ImageView logo;
    private TextView bigText, smallText;

    // Session Manager
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // Setup Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase and UI Components
        initializeComponents();

        // Setup Click Listeners
        setupClickListeners();
    }

    private void initializeComponents() {
        // Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In...");
        progressDialog.setCancelable(false);

        // UI Components
        emailEditText = findViewById(R.id.editTextLoginEmail);
        passwordEditText = findViewById(R.id.editTextLoginPassword);
        loginButton = findViewById(R.id.loginbutton);
        signupButton = findViewById(R.id.loginrgbutton);
        googleSignInButton = findViewById(R.id.lggooglesignin); // Google sign-in button
        logo = findViewById(R.id.imageView2);
        bigText = findViewById(R.id.textView4);
        smallText = findViewById(R.id.textView10);
        lggithub = findViewById(R.id.lggithubsignin);
        lgfacebook = findViewById(R.id.lgfacebooksignin);

        // Configure Google Sign-In for already signed-in users
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Replace with your Web Client ID
                .requestEmail() // Request the user's email
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupClickListeners() {
        // Email Login
        loginButton.setOnClickListener(v -> {
            rippleCardClick(v);
            performEmailLogin();});

        // Signup Navigation
        signupButton.setOnClickListener(v -> {
            advancedanimateCardClick(v, () -> {
            navigateToSignup(); }); });

        // Google Sign-In
        googleSignInButton.setOnClickListener(v -> {
            advancedanimateCardClick(v, () -> {
                signInWithGoogle(); }); });

        lggithub.setOnClickListener(v -> {
            advancedanimateCardClick(v,() ->{
                signInWithGithub();
            }); });

        lgfacebook.setOnClickListener(v -> {
            advancedanimateCardClick(v,()->{
                signInWithFacebook();
            }); });

    }

    private void performEmailLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validation
        if (!validateInput(email, password)) return;

        // Show Progress Dialog
        progressDialog.show();

        // Firebase Email Authentication
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        FirebaseUser  user = auth.getCurrentUser ();
                        if (user != null) {
                            sessionManager.createLoginSession(user.getEmail());
                            navigateToDashboard();
                        }
                    } else {
                        Toast.makeText(Login.this,
                                "Login Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInput(String email, String password) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email cannot be empty");
            return false;
        }

        if (!Pattern.matches(emailPattern, email)) {
            emailEditText.setError("Enter a valid email address");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password cannot be empty");
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return false;
        }

        return true;
    }

    private void signInWithGoogle() {
        // Start Google sign-in flow
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        progressDialog.show();

        // Get credential and authenticate with Firebase
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser  user = auth.getCurrentUser ();
                        if (user != null) {
                            sessionManager.createLoginSession(user.getEmail()); // Save session
                            navigateToDashboard();
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(Login.this,
                                "Authentication Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToSignup() {
        Intent i = new Intent(Login.this, Signup.class);
        Pair<View, String>[] pairs = new Pair[7];
        pairs[0] = new Pair<>(logo, "logo_image");
        pairs[1] = new Pair<>(bigText, "logo_text1");
        pairs[2] = new Pair<>(smallText, "logo_text2");
        pairs[3] = new Pair<>(emailEditText, "emailtransi");
        pairs[4] = new Pair<>(passwordEditText, "passtransi");
        pairs[5] = new Pair<>(loginButton, "butttransi");
        pairs[6] = new Pair<>(signupButton, "butttransi2");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
        startActivity(i, options.toBundle());
        finish();
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(Login.this, App_Dashboard.class);
        startActivity(intent);
        finish();
    }

    private void signInWithGithub() {
        //Add github Login Logic
        Toast.makeText(this, "Feature Coming Soon.", Toast.LENGTH_SHORT).show();
    }

    private void signInWithFacebook() {
        //Add github Login Logic
        Toast.makeText(this, "Feature Coming Soon.", Toast.LENGTH_SHORT).show();
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