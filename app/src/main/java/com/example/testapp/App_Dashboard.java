package com.example.testapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class App_Dashboard extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageButton button;
    NavigationView navview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app_dashboard);

         drawerLayout=findViewById(R.id.drawer);
         button=findViewById(R.id.drawerbutton);
         navview=findViewById(R.id.navview);

         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 drawerLayout.open();
             }
         });

         View headerview = navview.getHeaderView(0 );
        ImageView userimg = findViewById(R.id.profimage);
        TextView name = findViewById(R.id.sidebarname),mail = findViewById(R.id.sidebarmail);

         navview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 int itemid = item.getItemId();

                 if(itemid==R.id.CollegePredictor)
                 {
                     Toast.makeText(App_Dashboard.this,"College Predictor",Toast.LENGTH_SHORT).show();
                 }
                 drawerLayout.close();//Closes the side drawer if something ia clicked
                 return false;
             }
         });



    }
}