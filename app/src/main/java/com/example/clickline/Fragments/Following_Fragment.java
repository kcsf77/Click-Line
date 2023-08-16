package com.example.clickline.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.example.clickline.Adapters.Post_Adapter;
import com.example.clickline.Login_activity;
import com.example.clickline.Model.Post;
import com.example.clickline.R;
import com.example.clickline.SearchUsers_Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Following_Fragment extends Fragment {

    ImageView search;


    RecyclerView recyclerView;
    List<Post> postList;
    Post_Adapter postAdapter;
    ProgressBar progressBar;

    FirebaseUser user;
    DatabaseReference reference;
    FirebaseAuth auth;

    List<String> followingList;


    public Following_Fragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_following_, container,false);

        search = view.findViewById(R.id.search);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = view.findViewById(R.id.followingRecycler);
        recyclerView.setHasFixedSize(true);

        postList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SearchUsers_Activity.class));
            }
        });


        
        
        checkFollow();


        return view;

    }

    private void checkFollow() {
        followingList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("Follow_data").child(user.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    followingList.add(dataSnapshot.getKey());

                }

                getPosts();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void getPosts() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                progressBar.setVisibility(View.GONE);


            for(DataSnapshot dataSnapshot: snapshot.getChildren()){

                Post post =  dataSnapshot.getValue(Post.class);

                for(String id: followingList){
                    if(post.getPublisher().equals(id)){
                        postList.add(post);
                    }
                }
            }

            postAdapter = new Post_Adapter(getContext(), postList);
            recyclerView.setAdapter(postAdapter);
            postAdapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}