package com.example.clickline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_activity extends AppCompatActivity {
     EditText email, password;
     Button logBtn;
     TextView switchSignUp;
     ProgressDialog progressDialog;

     FirebaseAuth auth;
     FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        
        
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString();
                String pass = password.getText().toString();
                
                if(em.isEmpty() || pass.isEmpty()){
                    Toast.makeText(Login_activity.this, "Fields Can't be empty", Toast.LENGTH_SHORT).show();
                    
                }else{
                    openAccount(em,pass);
                }
            }
        });

        switchSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login_activity.this, MainActivity.class));
            }
        });





    }

    private void openAccount(String em, String pass) {
        progressDialog = new ProgressDialog(Login_activity.this);
        progressDialog.setTitle("Opening Account");
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        auth.signInWithEmailAndPassword(em,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Login_activity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login_activity.this, Home_Activity.class));
                    progressDialog.dismiss();
                }else{
                    Toast.makeText(Login_activity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }
        });



    }


    private void initialize(){

        email = findViewById(R.id.et_logEmail);
        password = findViewById(R.id.et_logPassword);
        logBtn = findViewById(R.id.btn_logIN);
        switchSignUp = findViewById(R.id.tv_switchSignUp);
    }





}