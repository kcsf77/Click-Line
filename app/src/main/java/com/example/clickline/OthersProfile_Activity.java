package com.example.clickline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clickline.Adapters.Favorites_Adapter;
import com.example.clickline.Model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class OthersProfile_Activity extends AppCompatActivity {


    ImageView backgroundProfile, returnPage;
    CircleImageView mainProfile;
    TextView followers, following , followersCount, followingCount, postsNumber;
    TextView fullName, displayName, bio;
    Button followUser, isfollowingUser;
    RecyclerView recyclerView;
    String userId;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;


    ArrayList<Post> list;
    Favorites_Adapter favorites_adapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        initialize();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("User");
        userId = getIntent().getStringExtra("userId");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(OthersProfile_Activity.this, 3));
        list = new ArrayList<>();
        getImages();
        favorites_adapter = new Favorites_Adapter(OthersProfile_Activity.this, list);
        recyclerView.setAdapter(favorites_adapter);

        getUserData();


        followUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(followUser.getText().equals("follow")){

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow_data")
                            .child(user.getUid())
                            .child("following")
                            .child(userId)
                            .setValue(true);

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow_data")
                            .child(userId)
                            .child("followers")
                            .child(user.getUid())
                            .setValue(true);
                }
            }
        });


        isfollowingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isfollowingUser.getText().equals("following")){

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow_data")
                            .child(user.getUid())
                            .child("following")
                            .child(userId)
                            .removeValue();

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow_data")
                            .child(userId)
                            .child("followers")
                            .child(user.getUid())
                            .removeValue();
                }
            }
        });



        if(userId.equals((FirebaseAuth.getInstance().getUid()))){
            followUser.setVisibility(View.INVISIBLE);
           isfollowingUser.setVisibility(View.INVISIBLE);

        }else{
            isFollowing( userId, followUser, isfollowingUser);

        }


        returnPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getFollowersCount();
        getFollowingCount();


    }



    private void initialize(){

        backgroundProfile = findViewById(R.id.background);
        mainProfile = findViewById(R.id.profile);
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        followersCount = findViewById(R.id.followers_count);
        followingCount = findViewById(R.id.following_count);
        fullName = findViewById(R.id.fullName);
        displayName = findViewById(R.id.displayName);
        bio = findViewById(R.id.bio);
        followUser = findViewById(R.id.follow);
        isfollowingUser = findViewById(R.id.isfollowing);
        recyclerView = findViewById(R.id.recyclerViewOthersProfile);
        postsNumber = findViewById(R.id.posts_number);
        returnPage = findViewById(R.id.return_page);
    }

    private void getUserData(){

        reference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String fName = snapshot.child("full_name").getValue().toString();
                String dName = snapshot.child("display_name").getValue().toString();


                if(snapshot.child("bio").exists()){
                    String b = snapshot.child("bio").getValue().toString();
                    bio.setText(b);
                }else{
                    bio.setText("add bio");
                }

                if(snapshot.child("profile").exists()) {
                    String p = snapshot.child("profile").getValue().toString();
                    Picasso.get().load(p).placeholder(R.drawable.profile_icon).into(mainProfile);

                }

                else{

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


    private void getImages() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);


                    if(post.getPublisher().equals(userId)){
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



    private void isFollowing(String userId, Button follow, Button following){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow_data")
                .child(user.getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(userId).exists()){
                    follow.setVisibility(View.INVISIBLE);
                    following.setVisibility(View.VISIBLE);

                }else{
                    follow.setVisibility(View.VISIBLE);
                    following.setVisibility(View.INVISIBLE);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getFollowersCount(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Follow_data")
                .child(userId).child("followers");

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
                .child(userId).child("following");

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