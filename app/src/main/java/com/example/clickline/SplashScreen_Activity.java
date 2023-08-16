package com.example.clickline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen_Activity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(user !=null){
                    startActivity( new Intent(SplashScreen_Activity.this, Home_Activity.class));
                    finish();




                }else{
                    startActivity(new Intent(SplashScreen_Activity.this, Login_activity.class));
                    finish();

                }

            }
        },3000);


    }
}