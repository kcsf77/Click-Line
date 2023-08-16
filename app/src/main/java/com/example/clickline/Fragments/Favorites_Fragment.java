package com.example.clickline.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.clickline.Adapters.Favorites_Adapter;
import com.example.clickline.Model.Post;
import com.example.clickline.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Favorites_Fragment extends Fragment {


    List<String> mySaves;
    RecyclerView recyclerView;
    ArrayList<Post> list;
    Favorites_Adapter favorites_adapter;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;


    public Favorites_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_favorites_, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Favorites");
        recyclerView = view.findViewById(R.id.favoritesRecycler);
        recyclerView.setHasFixedSize(true);
        
        getFavorites();
        
        return view;
    }

    private void getFavorites() {

            mySaves = new ArrayList<>();
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    mySaves.add(dataSnapshot.getKey());
                }
                readFavorites();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readFavorites() {
        list = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    for(String id: mySaves){
                        if(post.getPostid().equals(id)){
                            list.add(post);


                        }

                    }
                }
                favorites_adapter = new Favorites_Adapter(getContext(), list);
                recyclerView.setAdapter(favorites_adapter);
                favorites_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}