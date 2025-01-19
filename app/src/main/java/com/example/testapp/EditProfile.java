package com.example.testapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser ;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private CircleImageView profilePicture;
    private TextInputEditText usernameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputLayout passwordEditTextLayout;
    private MaterialButton saveButton;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseUser  currentUser ;
    private Uri selectedImageUri; // To track the selected image URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance();
        currentUser  = auth.getCurrentUser ();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize Views
        profilePicture = findViewById(R.id.profile_picture);
        usernameEditText = findViewById(R.id.rgusername);
        emailEditText = findViewById(R.id.editprofileemail);
        passwordEditText = findViewById(R.id.editprofilepassword);
        passwordEditTextLayout = findViewById(R.id.editprofilepasswordInputLayout);
        saveButton = findViewById(R.id.editprofilesavebutton);

        // Load current user data
        loadUserData();

        // Set onClickListener for profile picture
        profilePicture.setOnClickListener(v -> openImageChooser());

        // Set onClickListener for save button
        saveButton.setOnClickListener(v -> updateProfile());
    }

    private void loadUserData() {
        if (currentUser  != null) {
            // Load user data from Firebase Realtime Database
            databaseReference.child(currentUser .getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DataSnapshot dataSnapshot = task.getResult();
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("mail").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("profilepic").getValue(String.class);

                    usernameEditText.setText(username);
                    emailEditText.setText(email);
                    emailEditText.setEnabled(false); // Make email field non-editable

                    // Load profile image using Glide
                    Glide.with(this).load(profileImageUrl).into(profilePicture);

                    // Show password field only if using email authentication
                    if (currentUser .getProviderData().get(0).getProviderId().equals("password")) {
                        passwordEditTextLayout.setVisibility(View.VISIBLE);
                    } else {
                        passwordEditTextLayout.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData(); // Store the selected image URI
            profilePicture.setImageURI(selectedImageUri);
        }
    }

    private void updateProfile() {
        // Update username and password if necessary
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        boolean changesMade = false;

        if (!username.isEmpty()) {
            updateUserProfile(username);
            changesMade = true;
        }

        if (selectedImageUri != null) {
            // Handle image upload and update profile picture URL
            uploadProfileImage();
            changesMade = true;
        }

        if (!password.isEmpty()) {
            currentUser .updatePassword(password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfile.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfile.this, "Password update failed", Toast.LENGTH_SHORT).show();
                }
            });
            changesMade = true;
        }

        if (!changesMade) {
            Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfileImage() {
        // Here you would upload the image to your server or storage
        // For demonstration, we will just update the profile image URL in the database
        if (currentUser  != null && selectedImageUri != null) {
            // Simulate getting a new image URL after upload
            String newImageUrl = selectedImageUri.toString(); // Replace with actual upload logic

            // Update the profile image URL in the database
            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("profilepic", newImageUrl);

            databaseReference.child(currentUser .getUid()).updateChildren(userUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfile.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditProfile.this, "Profile picture update failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateUserProfile(String username) {
        if (currentUser  != null) {
            // Create a map to update user data
            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("username", username);

            databaseReference.child(currentUser .getUid()).updateChildren(userUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditProfile.this, "Profile update failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}