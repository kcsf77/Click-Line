package com.example.clickline;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddPost_Activity extends AppCompatActivity {
    TextView cancel_post, post;
    ImageView selected_image;
    EditText description;
    Button pick_gallery;
    ProgressDialog progressDialog;




    Uri imageUri;
    String url;
    public static final int PICK_IMAGE =100;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    StorageReference storageReference;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference().child("Posted_Images");

        initialize();


        pick_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,PICK_IMAGE);
            }
        });


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri == null){
                    Toast.makeText(AddPost_Activity.this, "No Image selected !", Toast.LENGTH_SHORT).show();
                }else{
                    uploadPost();
                }
            }
        });


        cancel_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddPost_Activity.this, Home_Activity.class);
                startActivity(intent);
                finish();
            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imageUri = data.getData();
            selected_image.setImageURI(imageUri);
        }else{
            Toast.makeText(this, "No Image selected !", Toast.LENGTH_SHORT).show();
            selected_image.setImageResource(R.drawable.profile_icon);
        }

    }

    private void initialize(){
        cancel_post = findViewById(R.id.cancel_post);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        pick_gallery = findViewById(R.id.pick_gallery);
        selected_image = findViewById(R.id.selected_image);
        progressDialog = new ProgressDialog(this);



    }

    private void uploadPost(){
        progressDialog.setTitle("Uploading post...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri != null){
            StorageReference sRef = storageReference.child(System.currentTimeMillis()+"."+fileExtension(imageUri));
            sRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            url = uri.toString();
                            reference = FirebaseDatabase.getInstance().getReference().child("Posts");
                            String timestamp =  String.valueOf(System.currentTimeMillis());

                            String postid = reference.push().getKey();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("postid", postid);
                            map.put("postImage", url);
                            map.put("description",description.getText().toString());
                            map.put("publisher", user.getUid());
                            map.put("timestamp", timestamp);

                            progressDialog.dismiss();
                            reference.child(postid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(AddPost_Activity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AddPost_Activity.this, Home_Activity.class));
                                        finish();
                                    }else{
                                        Toast.makeText(AddPost_Activity.this, "Post Failed", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddPost_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }


    }


    private String fileExtension(Uri photoUri) {
        ContentResolver contentResolver =getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(photoUri));

    }



}