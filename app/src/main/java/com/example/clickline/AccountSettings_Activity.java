package com.example.clickline;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettings_Activity extends AppCompatActivity {

    EditText fullName, displayName, email, bio;
    Toolbar toolbar;
    CircleImageView profilePic;
    ImageView pick_image;
    Button saveChanges;
    ProgressDialog progressDialog;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    StorageReference storageReference;


    Uri photoUri;
    String photoUrl;
    private static final int PICK_IMAGE = 100;
    String name;






    String profile_url = "https://firebasestorage.googleapis.com/v0/b/clickline-4e642.appspot.com/o/profile_icon.png?alt=media&token=23cc5580-48ae-41c7-92ff-341cf3681040";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("User");
        storageReference = FirebaseStorage.getInstance().getReference().child("Profiles");

        name = getIntent().getStringExtra("name");










        initialize();
        getUserData();
        email.setEnabled(false);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_back);



        bio.setLinksClickable(true);
        bio.setAutoLinkMask(Linkify.WEB_URLS);
        Linkify.addLinks(bio, Linkify.WEB_URLS);

        if(ActivityCompat.checkSelfPermission(AccountSettings_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(AccountSettings_Activity.this,
                   new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


        }

        pick_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,PICK_IMAGE);


            }
        });





    saveChanges.setEnabled(false);



        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBtnFunction();
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() !=null){
            photoUri = data.getData();
            profilePic.setImageURI(photoUri);
            saveChanges.setEnabled(true);

        }


    }

    private void saveBtnFunction(){

        String fn = fullName.getText().toString();
        String dn = displayName.getText().toString();
        String em = email.getText().toString();
        String b = bio.getText().toString();

        if(fn.isEmpty()|| dn.isEmpty()|| em.isEmpty()){
            Toast.makeText(this, "Fill blanks", Toast.LENGTH_SHORT).show();
        }else if (photoUri == null){
            uploadData(fn,dn,b, profile_url);
        }else{
            uploadData(fn,dn,b, photoUrl);


        }




    }

    private void uploadData(String fn, String dn, String b, String profile) {
        progressDialog.setTitle("Updating profile");
        progressDialog.setMessage("please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final StorageReference sRef = storageReference.child(System.currentTimeMillis()+"."+fileExtension(photoUri));
        sRef.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        photoUrl = uri.toString();
                        //data

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("full_name", fn);
                        map.put("display_name", dn);
                        map.put("profile", photoUrl);
                        map.put("bio", b);


                        reference.child(user.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(AccountSettings_Activity.this, "profile updated", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AccountSettings_Activity.this, Home_Activity.class));
                                    progressDialog.dismiss();
                                }else{
                                    Toast.makeText(AccountSettings_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();

                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountSettings_Activity.this, "failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });

    }

    private String fileExtension(Uri photoUri) {
        ContentResolver contentResolver =getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(photoUri));

    }


    private void getUserData(){

        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String fName = snapshot.child("full_name").getValue().toString();
                String dName = snapshot.child("display_name").getValue().toString();
                String eMail = snapshot.child("email").getValue().toString();


                if(snapshot.child("bio").exists()){
                    String b = snapshot.child("bio").getValue().toString();
                    bio.setText(b);
                }else{
                    bio.setHint("add bio");
                }

                if(snapshot.child("profile").exists()) {
                    String p = snapshot.child("profile").getValue().toString();
                    Picasso.get().load(p).placeholder(R.drawable.profile_icon).into(profilePic);

                }

                else{

                    profilePic.setImageResource(R.drawable.profile_icon);

                }
                fullName.setText(fName);
                displayName.setText(dName);
                email.setText(eMail);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


















    private void initialize(){

        fullName = findViewById(R.id.change_fullName);
        displayName = findViewById(R.id.change_displayName);
        email = findViewById(R.id.change_email);
        bio = findViewById(R.id.change_bio);
        toolbar = findViewById(R.id.toolbarr);
        profilePic = findViewById(R.id.profilePic);
        pick_image = findViewById(R.id.pick_image);
        saveChanges = findViewById(R.id.save_btn);
        progressDialog = new ProgressDialog(AccountSettings_Activity.this);



    }






}