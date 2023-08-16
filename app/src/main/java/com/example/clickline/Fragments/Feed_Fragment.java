package com.example.clickline.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.clickline.Adapters.Post_Adapter;
import com.example.clickline.AddPost_Activity;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class Feed_Fragment extends Fragment {

    ImageView add_post, search_user;
    CircleImageView feed_profile;
    RecyclerView recyclerView;
    List<Post> postList;
    Post_Adapter postAdapter;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    public Feed_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed_, container, false);
        add_post = view.findViewById(R.id.add_post);
        search_user = view.findViewById(R.id.search_user);
        recyclerView = view.findViewById(R.id.recycler);
        feed_profile= view.findViewById(R.id.feed_profile);


        auth = FirebaseAuth.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        postList = new ArrayList<>();
        postAdapter =  new Post_Adapter(getContext(),postList);

        recyclerView.setAdapter(postAdapter);

        getPost();
        getProfileImage();






        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddPost_Activity.class);
                startActivity(intent);
            }
        });

        search_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchUsers_Activity.class);
                startActivity(intent);
            }
        });


        return view;
    }

    private void getPost() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getProfileImage(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("profile").exists()){
                    String p = snapshot.child("profile").getValue().toString();
                    Picasso.get().load(p).placeholder(R.drawable.profile_icon).into(feed_profile);

                } else {

                    feed_profile.setImageResource(R.drawable.profile_icon);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}