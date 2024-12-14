package com.example.testapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicBoolean;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {   Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button;
        EditText email,password;
        TextView rgbutton;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        AtomicBoolean ispassvisible = new AtomicBoolean(false);
        rgbutton=findViewById(R.id.loginrgbutton);
        button = findViewById(R.id.loginbutton);
        email = findViewById(R.id.editTextLoginEmail);
        password = findViewById(R.id.editTextLoginPassword);
        String pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        ProgressDialog progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Logging In...");
        progressDialog.setCancelable(false);


        rgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this,Signup.class);
                startActivity(i);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.dismiss();
                String mail = email.getText().toString();
                String pass = password.getText().toString();

                if (TextUtils.isEmpty(mail))
                    Toast.makeText(Login.this,"Email Id cannot be Empty.Enter Email",Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(pass))
                    Toast.makeText(Login.this,"Password cannot be Empty.Enter Password",Toast.LENGTH_SHORT).show();
                else if (!mail.matches(pattern))
                    email.setError("Enter Valid Mail Address");
                else if (pass.length()<6)
                    password.setError("Password must be longer than 5 characters");
                else { progressDialog.show();
                    auth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // Dismiss progress dialog
                            progressDialog.dismiss();
                            if (task.isSuccessful())
                                try {
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } catch (Exception e) {
                                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            else {
                                Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }); // end of new Oncompletelistener
                } //end of else
            }
        });

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;  // Index for the right drawable (eye icon)
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= password.getRight() - password.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = password.getSelectionEnd();  // Save cursor position

                        // Toggle the password visibility
                        if (ispassvisible.get()) {
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.eye_off, 0);  // Set eye-off drawable
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());  // Hide password
                            ispassvisible.set(false);  // Update visibility state
                        } else {
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.eye, 0);  // Set eye-on drawable
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());  // Show password
                            ispassvisible.set(true);  // Update visibility state
                        }
                        password.setSelection(selection);  // Restore cursor position
                        return true;
                    }
                }
                return false;
            }
        });

    }
}