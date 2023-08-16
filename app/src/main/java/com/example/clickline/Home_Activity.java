package com.example.clickline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.clickline.Fragments.Favorites_Fragment;
import com.example.clickline.Fragments.Feed_Fragment;
import com.example.clickline.Fragments.Following_Fragment;
import com.example.clickline.Fragments.Profile_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Home_Activity extends AppCompatActivity {

   // Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

      // toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        frameLayout = findViewById(R.id.frame_layout);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              int id = item.getItemId();


              if(id == R.id.following){

                  Following_Fragment following_fragment = new Following_Fragment();
                  FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                  fragmentTransaction.replace(R.id.frame_layout,following_fragment);
                  fragmentTransaction.commit();


              }
                if(id == R.id.feed){

                    Feed_Fragment feed_fragment = new Feed_Fragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout,feed_fragment);
                    fragmentTransaction.commit();


                }
                if(id == R.id.favorites){

                    Favorites_Fragment favorites_fragment = new Favorites_Fragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout,favorites_fragment);
                    fragmentTransaction.commit();


                }
                if(id == R.id.profile){

                    Profile_Fragment profile_fragment = new Profile_Fragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout,profile_fragment);
                    fragmentTransaction.commit();

                    SharedPreferences.Editor editor = getSharedPreferences("USER", Context.MODE_PRIVATE).edit();
                    editor.putString("Profile_Id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                }

            return true;
            }

        });
        bottomNavigationView.setSelectedItemId(R.id.feed);





    }
}