package com.example.clickline.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clickline.AccountSettings_Activity;
import com.example.clickline.Adapters.Favorites_Adapter;
import com.example.clickline.Login_activity;
import com.example.clickline.Model.Post;
import com.example.clickline.R;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class Profile_Fragment extends Fragment {

    ImageView backgroundProfile, pickImage, logOut;
    CircleImageView mainProfile;
    TextView followers, following , followersCount, followingCount, postsNumber;
    TextView fullName, displayName, bio, logOutText;
    Button editProfile, updateBackground;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
   StorageReference storageReference;

   ArrayList<Post> list;
   Favorites_Adapter favorites_adapter;





    Uri photoUri;
    String photoUrl;
    private static final int PICK_IMAGE = 100;






    public Profile_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("User");
        storageReference = FirebaseStorage.getInstance().getReference().child("Background");







        initialize(view);
        getUserData();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        list = new ArrayList<>();

        getImages();
        favorites_adapter = new Favorites_Adapter(getContext(), list);
        recyclerView.setAdapter(favorites_adapter);

        getFollowingCount();
        getFollowersCount();



        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,PICK_IMAGE);


            }
        });



        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent intent = new Intent(getContext(), AccountSettings_Activity.class);
             intent.putExtra("name", fullName.getText().toString());
             startActivity(intent);
            }
        });



        updateBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(photoUri == null){
                    return;
                }else{
                    uploadbackground();

                }
            }
        });


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmLogout();
            }
        });



        return view;


    }


    private void confirmLogout(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Confirm Log out");
        alert.setMessage("Do you want log out?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                auth.signOut();
                startActivity(new Intent(getContext(), Login_activity.class).
                        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(),"Cancelled", Toast.LENGTH_SHORT ).show();
            }
        });

        alert.create().show();


    }

    private void getImages() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);


                    if(post.getPublisher().equals( user.getUid())){
                        list.add(post);
                        postsNumber.setText("Posts"+"("+list.size()+")");
                    }
                    Collections.reverse(list);
                    favorites_adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() !=null){
            photoUri = data.getData();
            backgroundProfile.setImageURI(photoUri);
            updateBackground.setVisibility(View.VISIBLE);
        }


    }

    private void initialize(View v){

        backgroundProfile = v.findViewById(R.id.background);
        mainProfile = v.findViewById(R.id.profile);
        pickImage = v.findViewById(R.id.pick_image);
        followers = v.findViewById(R.id.followers);
        following = v.findViewById(R.id.following);
        followersCount = v.findViewById(R.id.followers_count);
        followingCount = v.findViewById(R.id.following_count);
        fullName = v.findViewById(R.id.fullName);
        displayName = v.findViewById(R.id.displayName);
        bio = v.findViewById(R.id.bio);
        editProfile = v.findViewById(R.id.edit_profile);
        updateBackground =v.findViewById(R.id.update_bg);
        recyclerView = v.findViewById(R.id.recyclerViewProfile);
        postsNumber = v.findViewById(R.id.posts_number);
        logOut = v.findViewById(R.id.logout);
        logOutText =v.findViewById(R.id.text_logout);
    }

    private void uploadbackground() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Updating background Image");
        progressDialog.setMessage("please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final StorageReference sRef = storageReference.child(System.currentTimeMillis()+"."+"jpg");
        sRef.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        photoUrl = uri.toString();
                        //data

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("background", photoUrl);


                        reference.child(user.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getActivity(), "background updated", Toast.LENGTH_SHORT).show();
                                    updateBackground.setVisibility(View.INVISIBLE);
                                    progressDialog.dismiss();
                                }else{
                                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                                    updateBackground.setVisibility(View.INVISIBLE);
                                    progressDialog.dismiss();

                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        updateBackground.setVisibility(View.INVISIBLE);
                        progressDialog.dismiss();
                    }
                });
            }
        });

    }


    private String fileExtension(Uri photoUri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(photoUri));

    }






    private void getUserData(){

    reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            String fName = snapshot.child("full_name").getValue().toString();
            String dName = snapshot.child("display_name").getValue().toString();


            if (snapshot.child("bio").exists()) {
                String b = snapshot.child("bio").getValue().toString();
                bio.setText(b);
            } else {
                bio.setText("add bio");
            }

            if (snapshot.child("profile").exists()) {
                String p = snapshot.child("profile").getValue().toString();
                Picasso.get().load(p).placeholder(R.drawable.profile_icon).into(mainProfile);

            } else {

                mainProfile.setImageResource(R.drawable.profile_icon);

            }

            if (snapshot.child("background").exists()) {
                String bg = snapshot.child("background").getValue().toString();
                Picasso.get().load(bg).placeholder(backgroundProfile.getDrawable()).into(backgroundProfile);
            }

                fullName.setText(fName);
                displayName.setText(dName);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    });

    }


    private void getFollowersCount(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Follow_data")
                .child(user.getUid()).child("followers");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followersCount.setText("" +snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getFollowingCount(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Follow_data")
                .child(user.getUid()).child("following");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingCount.setText("" +snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




}