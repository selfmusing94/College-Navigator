package com.example.testapp;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.util.Pair;
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

        rgbutton=findViewById(R.id.loginrgbutton);
        button = findViewById(R.id.loginbutton);
        email = findViewById(R.id.editTextLoginEmail);
        password = findViewById(R.id.editTextLoginPassword);
        String pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        ProgressDialog progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Logging In...");
        progressDialog.setCancelable(false);

        ImageView logo = findViewById(R.id.imageView2);
        TextView big=findViewById(R.id.textView4),small=findViewById(R.id.textView10);



        rgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this,Signup.class);

                Pair pairs[] = new Pair[7];
                pairs[0]=new Pair<View,String>(logo,"logo_image");
                pairs[1]=new Pair<View,String>(big,"logo_text1");
                pairs[2]=new Pair<View,String>(small,"logo_text2");
                pairs[3]=new Pair<View,String>(email,"emailtransi");
                pairs[4]=new Pair<View,String>(password,"passtransi");
                pairs[5]=new Pair<View,String>(button,"butttransi");
                pairs[6]=new Pair<View,String>(rgbutton,"butttransi2");


                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this,pairs);

                startActivity(i,options.toBundle());
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
                    Toast.makeText(Login.this,"Email Id cannot be Empty. Enter Email",Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(pass))
                    Toast.makeText(Login.this,"Password cannot be Empty. Enter Password",Toast.LENGTH_SHORT).show();
                else if (!mail.matches(pattern))
                    email.setError("Enter Valid Mail Address");
                else if (pass.length()<6)
                    password.setError("Password must contain atleast 6 characters");
                else { progressDialog.show();
                    auth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // Dismiss progress dialog
                            progressDialog.dismiss();
                            if (task.isSuccessful())
                                try {
                                    Intent intent = new Intent(Login.this, App_Dashboard.class);
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

    }
}