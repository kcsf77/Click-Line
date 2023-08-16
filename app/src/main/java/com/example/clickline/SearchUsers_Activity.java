package com.example.clickline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.example.clickline.Adapters.Search_Adapter;
import com.example.clickline.Model.Users_List;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchUsers_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText search;
    Toolbar toolbar;

    List<Users_List> lists;
    Search_Adapter adapter;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        recyclerView = findViewById(R.id.recyclerView);
        search = findViewById(R.id.et_search);
        toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("User");

        getUsersList();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void searchUsers(String a) {

        Query query = reference.orderByChild("full_name").startAt(search.getText().toString()).endAt(search.getText().toString()+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                lists.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Users_List users_list =dataSnapshot.getValue(Users_List.class);
                    lists.add(users_list);
                }
                adapter = new Search_Adapter(SearchUsers_Activity.this, lists);
                recyclerView.setAdapter(adapter);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUsersList() {

        lists = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lists.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Users_List users_list = dataSnapshot.getValue(Users_List.class);
                    lists.add(users_list);
                }
                adapter = new Search_Adapter(SearchUsers_Activity.this, lists);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(SearchUsers_Activity.this ,"Search Failed", Toast.LENGTH_SHORT).show();

            }
        });

    }
}