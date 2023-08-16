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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText fullName, displayName, email, password;
    TextView switchLogin;
    Button signUp;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    String profile_url = "https://firebasestorage.googleapis.com/v0/b/clickline-4e642.appspot.com/o/profile_icon.png?alt=media&token=23cc5580-48ae-41c7-92ff-341cf3681040";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        auth = FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("User");



        switchLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Login_activity.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fn = fullName.getText().toString();
                String dn = displayName.getText().toString();
                String em = email.getText().toString();
                String pass = password.getText().toString();

                if(fn.isEmpty()|| dn.isEmpty() || em.isEmpty() || pass.isEmpty()){
                    Toast.makeText(MainActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }else if(pass.length()<6){
                    Toast.makeText(MainActivity.this, "password length should be more than 6 characters", Toast.LENGTH_SHORT).show();
                }else{
                    makeAccount(fn,dn,em,pass);



                }
            }
        });

    }

    private void makeAccount(String fn, String dn, String em, String pass) {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Creating New Account");
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        auth.createUserWithEmailAndPassword(em,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Please verify email", Toast.LENGTH_SHORT).show();
                                saveData(fn, dn ,em , pass );
                            }else{
                                Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });

                    
                }else{
                    Toast.makeText(MainActivity.this, "failed " + task.getException(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                }

            }
            
        });

    }

    private void saveData(String fn, String dn, String em, String pass) {
        user = auth.getCurrentUser();
        HashMap<String, Object> map = new HashMap<>();
        map.put("full_name", fn);
        map.put("display_name", dn);
        map.put("email", em);
        map.put("password", pass);
        map.put("User_id", user.getUid());
        reference.child(user.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this, Login_activity.class));
                    Toast.makeText(MainActivity.this, "Account Created" , Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }else{
                    Toast.makeText(MainActivity.this, "Failed" , Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });

    }


    private void initialize(){
        fullName = findViewById(R.id.et_fullName);
        displayName = findViewById(R.id.et_displayName);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        switchLogin = findViewById(R.id.tv_switchLogin);
        signUp = findViewById(R.id.btn_signUp);

    }
}