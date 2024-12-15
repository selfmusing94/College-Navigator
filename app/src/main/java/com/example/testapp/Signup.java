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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class Signup extends AppCompatActivity {
    TextView login;
    EditText rgusername, rgpass, rgrepass, rgmail;
    Button rgsignup;
    CircleImageView rgImg;
    FirebaseAuth auth;
    Uri ImgURI;
    String imguri;
    String pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    String status = "Sita Ram! I am using this Application";

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
                progressDialog.show(); // Show the progress dialog here
                auth.createUserWithEmailAndPassword(emaill, passs).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String id = task.getResult().getUser ().getUid();
                        DatabaseReference databaseReference = database.getReference("Users").child(id);
                        String status = "Sita Ram! I am using this Application";

                        // Use the selected image URI directly
                        if (ImgURI != null) {
                            imguri = ImgURI.toString(); // Convert URI to String
                        } else {
                            imguri = "https://drive.google.com/file/d/1-x3ARzGgxScNZnaitTOE0KVrMV6d8AAJ/view?usp=drive_link"; // Set a default image URL if no image is selected
                        }

                        Users user = new Users(id, namee, emaill, passs, imguri, status);
                        databaseReference.setValue(user).addOnSuccessListener(aVoid -> {
                            progressDialog.dismiss(); // Dismiss the dialog on success
                            Toast.makeText(Signup.this, "User  Created Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Signup.this, Login.class);
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss(); // Dismiss the dialog on failure
                            Toast.makeText(Signup.this, "Failed to create user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        progressDialog.dismiss(); // Dismiss the dialog on failure
                        runOnUiThread(() -> {
                            Toast.makeText(Signup.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        });
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK && data != null) {
            ImgURI = data.getData();
            rgImg.setImageURI(ImgURI);
        }
    }
}
