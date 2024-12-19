package com.example.testapp;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
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
import com.google.firebase.auth.FirebaseUser;
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
    private ImageButton googleSignInButton;
    private TextView signupButton;
    private ImageView logo;
    private TextView bigText, smallText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

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
        googleSignInButton = findViewById(R.id.lggooglesignin); // Add this button to your layout
        logo = findViewById(R.id.imageView2);
        bigText = findViewById(R.id.textView4);
        smallText = findViewById(R.id.textView10);

        // Configure Google Sign-In for already signed-in users
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // Request the user's email
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupClickListeners() {
        // Email Login
        loginButton.setOnClickListener(v -> performEmailLogin());

        // Signup Navigation
        signupButton.setOnClickListener(v -> navigateToSignup());

        // Google Sign-In
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
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
                        navigateToDashboard();
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
        // Check if there's already a signed-in Google account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            // If an account exists, proceed with Firebase authentication
            firebaseAuthWithGoogle(account);
        } else {
            // If no account is found, show the Google Sign-In dialog to choose an account
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
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

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = auth.getCurrentUser();
                        navigateToDashboard();
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
}
