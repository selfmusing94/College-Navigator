package com.example.testapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import android.util.Log;

public class Signup extends AppCompatActivity {




    TextView login;
    EditText rgusername, rgpass, rgrepass, rgmail;
    Button rgsignup;
    CircleImageView rgImg;
    FirebaseAuth auth;
    Uri ImgURI;
    String imguri;
    ImageButton rggoogle,rggit,rgface;
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
        rggoogle=findViewById(R.id.rggooglesignin);

        login.setOnClickListener(v -> {
            Intent intent = new Intent(Signup.this, Login.class);
            startActivity(intent);
            finish();
        });

        rgsignup.setOnClickListener(v -> {
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
                progressDialog.show();
                auth.createUserWithEmailAndPassword(emaill, passs).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String id = task.getResult().getUser().getUid();
                        DatabaseReference databaseReference = database.getReference("Users").child(id);

                        // Use the selected image URI directly
                        if (ImgURI != null) {
                            imguri = ImgURI.toString(); // Convert URI to String
                        } else {
                            imguri = "https://drive.google.com/file/d/1-x3ARzGgxScNZnaitTOE0KVrMV6d8AAJ/view?usp=drive_link"; // Set a default image URL if no image is selected
                        }

                        Users user = new Users(id, namee, emaill, passs, imguri, status);
                        databaseReference.setValue(user).addOnSuccessListener(aVoid -> {
                            progressDialog.dismiss();
                            Toast.makeText(Signup.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Signup.this, Login.class);
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(Signup.this, "Failed to create user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Signup.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        rgImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Your Profile Picture"), 10);
        });

        // Google Sign-In Setup
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()  // Request user's email
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Google Sign-In Button (Add this to your layout and bind it here)
        rggoogle.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
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
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        String id = user.getUid();
                        DatabaseReference databaseReference = database.getReference("Users").child(id);

                        String userName = acct.getDisplayName();
                        String userEmail = acct.getEmail();
                        String userProfilePic = acct.getPhotoUrl() != null ? acct.getPhotoUrl().toString() : "default_profile_pic_url";
                        String userStatus = "Sita Ram! I am using this Application";

                        Users googleUser = new Users(id, userName, userEmail, "N/A", userProfilePic, userStatus);
                        databaseReference.setValue(googleUser).addOnSuccessListener(aVoid -> {
                            Toast.makeText(Signup.this, "Google User Created Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Signup.this, Login.class));
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(Signup.this, "Failed to save Google User: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                    } else {
                        Toast.makeText(Signup.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
