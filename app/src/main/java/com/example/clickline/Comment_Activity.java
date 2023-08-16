package com.example.clickline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.clickline.Adapters.Comment_Adapter;
import com.example.clickline.Model.Comment_List;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Comment_Activity extends AppCompatActivity {


    CircleImageView profile;
    EditText comments;
    ImageView send;
    String postId;
    String publisher;
    RecyclerView recyclerView;
    List<Comment_List> lists;
    Comment_Adapter comment_adapter;
    Toolbar toolbar;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("User");

        setContentView(R.layout.activity_comment);
        profile = findViewById(R.id.profile_image_comments);
        comments = findViewById(R.id.input_comment);
        send = findViewById(R.id.send);
        recyclerView = findViewById(R.id.commentRecycler);
        recyclerView.setHasFixedSize(true);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_back);



        postId = getIntent().getStringExtra("postId");
        publisher = getIntent().getStringExtra("publisher");

        getComments();


        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("profile").exists()) {
                    String p = snapshot.child("profile").getValue().toString();
                    Picasso.get().load(p).placeholder(R.drawable.profile_icon).into(profile);
                } else {
                   profile.setImageResource(R.drawable.profile_icon);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });









        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = comments.getText().toString();

                if(comment.isEmpty()){
                    Toast.makeText(Comment_Activity.this, "Empty comment, please input something", Toast.LENGTH_SHORT).show();

                }else{
                    uploadComment(comment);
                    
                }
            }
        });


    }

    private void getComments() {
        lists = new ArrayList<>();
        DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lists.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Comment_List comment_list = dataSnapshot.getValue(Comment_List.class);
                    lists.add(comment_list);

                }
                comment_adapter = new Comment_Adapter(getApplicationContext(),lists);
                recyclerView.setAdapter(comment_adapter);
                comment_adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void uploadComment(String comment) {
        String timeStamp = System.currentTimeMillis()+"";
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("comment", comment);
        map.put("commenter", user.getUid());
        map.put("date", timeStamp);
        databaseReference.push().setValue(map);
        comments.setText("");


    }
}