package com.example.testapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Signup extends AppCompatActivity {

    TextView login;
    EditText rgusername, rgpass, rgrepass, rgmail;
    Button rgsignup;
    CircleImageView rgImg;
    FirebaseAuth auth;
    Uri ImgURI;
    String imguri;
    ImageButton rggoogle,rggithub,rgfacebook;
    String pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    String status = "Sita Ram! I am using this Application";

    // Google Sign-In
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "GoogleSignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating a new Account");
        progressDialog.setCancelable(false);

        login = findViewById(R.id.rgloginbutton);
        rgusername = findViewById(R.id.rgusername);
        rgmail = findViewById(R.id.rgemail);
        rgpass = findViewById(R.id.rgpassword);
        rgrepass = findViewById(R.id.rgrepassword);
        rgsignup = findViewById(R.id.rgbutton);
        rgImg = findViewById(R.id.profilerg);
        rggoogle = findViewById(R.id.rggooglesignin);
        rggithub = findViewById(R.id.rggithubsignin);
        rgfacebook=findViewById(R.id.rgfacebooksignin);

        login.setOnClickListener(v -> {
            advancedanimateCardClick(v,()->{
            Intent intent = new Intent(Signup.this, Login.class);
            startActivity(intent);
            finish();
            });
        });

        rgsignup.setOnClickListener(v -> {
            rippleCardClick(v);
            String namee = rgusername.getText().toString();
            String emaill = rgmail.getText().toString();
            String passs = rgpass.getText().toString();
            String repasss = rgrepass.getText().toString();

            if (TextUtils.isEmpty(namee)) {
                rgusername.setError("Please enter the Username");
            } else if (TextUtils.isEmpty(emaill)) {
                rgmail.setError("Please enter the Email ID");
            } else if (TextUtils.isEmpty(passs)) {
                rgpass.setError("Please enter the Password");
            } else if (TextUtils.isEmpty(repasss)) {
                rgrepass.setError("Please Confirm your Password");
            } else if (!emaill.matches(pattern)) {
                rgmail.setError("Enter a valid Email");
            } else if (passs.length() < 6) {
                rgpass.setError("Password must contain at least 6 characters");
            } else if (!repasss.equals(passs)) {
                rgrepass.setError("Both Passwords must match");
            } else {
                // Check if email is already registered
                progressDialog.show();
                auth.fetchSignInMethodsForEmail(emaill).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().getSignInMethods().isEmpty()) {
                            // Email is not already used, proceed with signup
                            createAccount(namee, emaill, passs);
                        } else {
                            // Email is already used, show a toast
                            progressDialog.dismiss();
                            Toast.makeText(Signup.this, "Email is already registered!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Signup.this, "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        rgImg.setOnClickListener(v -> {
            rippleCardClick(v);
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            startActivityForResult(Intent.createChooser(intent, "Select Your Profile Picture"), 10);
        });

        // Google Sign-In Setup
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Web Client ID here
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Google Sign-In Button (Add this to your layout and bind it here)
        rggoogle.setOnClickListener(v -> {
            advancedanimateCardClick(v,()->{
            signInWithGoogle();
            }); });

        rggithub.setOnClickListener(v -> {
            advancedanimateCardClick(v,() ->{
                signUpWithGithub();
            }); });

        rgfacebook.setOnClickListener(v -> {
            advancedanimateCardClick(v,()->{
                signUpWithFacebook();
            }); });
    }

    private void signInWithGoogle() {
        // Before starting the Google Sign-In process, check if a user is already signed in.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, show a message that they are already signed up.
            Toast.makeText(this, "This Google account is already linked to a user.", Toast.LENGTH_LONG).show();
            return;
        }

        // Proceed with Google sign-in if no user is already signed in
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
            }
        }

        // Handle Image Selection for Profile Picture
        if (requestCode == 10 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the selected image URI
            ImgURI = data.getData();

            // Use Glide to load the image into CircleImageView
            Glide.with(this)
                    .load(ImgURI)  // Load the image URI
                    .into(rgImg);   // Set it into the CircleImageView
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
                        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                        String idToken = account.getIdToken();
                        if (idToken == null) {
                            Toast.makeText(this, "Failed to get ID Token", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        progressDialog.show();
                        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                        auth.signInWithCredential(credential)
                                .addOnCompleteListener(this, task -> {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = auth.getCurrentUser();
                                        if (user != null) {
                                            // Check if user exists in the database
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                                            databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        // User exists, show toast and sign out
                                                        Toast.makeText(Signup.this, "This Gmail account is already in use. Please Login using the same account.", Toast.LENGTH_SHORT).show();
                                                        auth.signOut();
                                                        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(Signup.this, GoogleSignInOptions.DEFAULT_SIGN_IN);
                                                        googleSignInClient.revokeAccess();
                                                        Intent i = new Intent(Signup.this,Login.class);
                                                        startActivity(i);
                                                        finish();
                                                    } else {
                                                        // Create a new user
                                                        String id = user.getUid();
                                                        DatabaseReference databaseReference = database.getReference("Users").child(id);
                                                        String userName = account.getDisplayName();
                                                        String userEmail = account.getEmail();
                                                        String userProfilePic = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "default_profile_pic_url";
                                                        String userStatus = "Sita Ram! I am using this Application";
                                                        Users googleUser = new Users(id, userName, userEmail, "N/A", userProfilePic, userStatus);
                                                        databaseReference.setValue(googleUser)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    Toast.makeText(Signup.this, "Google User Created Successfully", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(Signup.this, App_Dashboard.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                });
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    // Handle database error
                                                    int errorCode = databaseError.getCode();
                                                    String errorMessage = databaseError.getMessage();

                                                    // Log the error
                                                    Log.e("DatabaseError", "Error code: " + errorCode + ", Error message: " + errorMessage);

                                                    // Handle specific error codes
                                                    switch (errorCode) {
                                                        case DatabaseError.NETWORK_ERROR:
                                                            Toast.makeText(Signup.this, "No internet connection", Toast.LENGTH_SHORT).show();
                                                            break;
                                                        case DatabaseError.PERMISSION_DENIED:
                                                            Toast.makeText(Signup.this, "Permission denied", Toast.LENGTH_SHORT).show();
                                                            break;
                                                        default:
                                                            Toast.makeText(Signup.this, "Database error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                                            break;
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Signup.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

    private void createAccount(String namee, String emaill, String passs) {
        progressDialog.show();
        auth.createUserWithEmailAndPassword(emaill, passs)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String id = task.getResult().getUser().getUid();
                        DatabaseReference databaseReference = database.getReference("Users").child(id);
                        if (ImgURI != null) {
                            imguri = ImgURI.toString();
                        } else {
                            imguri = "https://drive.google.com/file/d/1-x3ARzGgxScNZnaitTOE0KVrMV6d8AAJ/view?usp=sharing";
                        }
                        Users user = new Users(id, namee, emaill, passs, imguri, status);
                        databaseReference.setValue(user)
                                .addOnSuccessListener(aVoid -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(Signup.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Signup.this, Login.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(Signup.this, "Failed to create user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("createAccount", "Failed to create user", e);
                                });
                    } else {
                        progressDialog.dismiss();
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthException e) {
                            if (e.getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                                Toast.makeText(Signup.this, "Email already in use", Toast.LENGTH_SHORT).show();
                            } else if (e.getErrorCode().equals("ERROR_INVALID_EMAIL")) {
                                Toast.makeText(Signup.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                            } else if (e.getErrorCode().equals("ERROR_WEAK_PASSWORD")) {
                                Toast.makeText(Signup.this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Signup.this, "Authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            Log.e("createAccount", "Authentication failed", e);
                        } catch (Exception e) {
                            Toast.makeText(Signup.this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("createAccount", "An error occurred", e);
                        }
                    }
                });
    }

    private void signUpWithGithub() {
      //Add github Login Logic
        Toast.makeText(this, "Feature Coming Soon.", Toast.LENGTH_SHORT).show();
    }

    private void signUpWithFacebook() {
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