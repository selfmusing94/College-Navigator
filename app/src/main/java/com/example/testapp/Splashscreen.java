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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

@SuppressLint("CustomSplashScreen")
public class Splashscreen extends AppCompatActivity {

    TextView by,selfm,name;
    ImageView logo;
    Animation top,bottom; //Animations
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//To hide statusbar


        top= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottom= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        logo=findViewById(R.id.image_logo);
        selfm=findViewById(R.id.textView3);
        by=findViewById(R.id.textView2);
        name=findViewById(R.id.text_name);

        by.setAnimation(bottom);
        selfm.setAnimation(bottom);
        logo.setAnimation(top);
        name.setAnimation(top);


        // Delay to transition to MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splashscreen.this, Login.class);
                Pair[] pairs = new Pair[3];
                pairs[0]=new Pair<View,String>(logo,"logo_image");
                pairs[1]=new Pair<View,String>(name,"logo_text1");
                pairs[2]=new Pair<View,String>(name,"logo_text2");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Splashscreen.this,pairs);

                startActivity(intent,options.toBundle());
                finish();
            }
        }, 2000); // 2-second delay
    }
}
