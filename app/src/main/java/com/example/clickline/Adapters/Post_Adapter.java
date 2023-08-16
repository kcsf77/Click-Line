package com.example.clickline.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clickline.Comment_Activity;
import com.example.clickline.Model.Post;
import com.example.clickline.Model.Users_List;
import com.example.clickline.OthersProfile_Activity;
import com.example.clickline.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Post_Adapter extends RecyclerView.Adapter<Post_Adapter.ViewHolder> {

    Context context;
    List<Post> postList;

    FirebaseAuth auth;
    FirebaseUser user;


    public Post_Adapter(Context context, List<Post> postList){
            this.context = context;
            this.postList = postList;



    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);



        return new ViewHolder(view);




    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        user= FirebaseAuth.getInstance().getCurrentUser();

        Post post = postList.get(position);
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(post.getTimestamp()));
        String date = DateFormat.format("dd-MM-yyyy hh:mm", calendar).toString();

        publisherInfo(holder.profileImage, holder.fullName, holder.displayName, post.getPublisher());
        Picasso.get().load(post.getPostImage()).placeholder(R.drawable.ic_tempimage_24).into(holder.post_image);
        holder.time.setText(date);
        holder.description.setText(post.getDescription());


        isLiked(post.getPostid(), holder.like, holder.likes_Count);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.like.getTag().equals("Like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid())
                            .child(user.getUid())
                            .setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid())
                            .child(user.getUid())
                            .removeValue();

                }
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Comment_Activity.class);
                intent.putExtra("postId", post.getPostid());
                intent.putExtra("publisher", post.getPublisher());
                context.startActivity(intent);
            }
        });


        isSaved(post.getPostid(), holder.save);

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(holder.save.getTag().equals("Save")){

                    Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().child("Favorites")
                            .child(user.getUid())
                            .child(post.getPostid())
                            .setValue(true);
                }else{
                    Toast.makeText(context, "Removed to favorites", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().child("Favorites")
                            .child(user.getUid())
                            .child(post.getPostid())
                            .removeValue();

                }




            }
        });




        DatabaseReference dReference = FirebaseDatabase.getInstance().getReference().child("Comments")
                .child(post.getPostid());


        dReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.comments_Count.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });









        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OthersProfile_Activity.class);
                intent.putExtra("userId", post.getPublisher());
                context.startActivity(intent);
            }
        });


        holder.fullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OthersProfile_Activity.class);
                intent.putExtra("userId", post.getPublisher());
                context.startActivity(intent);
            }
        });








    }

    @Override
    public int getItemCount() {
        return postList.size();
    }






    public class ViewHolder extends RecyclerView.ViewHolder {


        ImageView post_image;
        CircleImageView profileImage;
        TextView fullName, displayName, description, time;

       ImageView like, comments, save;
       TextView likes_Count, comments_Count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            like = itemView.findViewById(R.id.like);
            likes_Count = itemView.findViewById(R.id.likes_count);
            comments = itemView.findViewById(R.id.comments);
            comments_Count = itemView.findViewById(R.id.comments_count);
            post_image = itemView.findViewById(R.id.post_image);
            fullName = itemView.findViewById(R.id.pfull_Name);
            displayName = itemView.findViewById(R.id.pdisplay_Name);
            description = itemView.findViewById(R.id.description);
            time = itemView.findViewById(R.id.date);
            profileImage = itemView.findViewById(R.id.profile_image);
            save = itemView.findViewById(R.id.save);

        }
    }

    private void publisherInfo(final ImageView profile, final TextView fullName, final TextView displayName, final String userid){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
            databaseReference.child(userid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users_List users_list = snapshot.getValue(Users_List.class);
                    Picasso.get().load(users_list.getProfile()).placeholder(R.drawable.profile_icon).into(profile);
                    fullName.setText(users_list.getFull_name());
                    displayName.setText(users_list.getDisplay_name());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    private void isLiked(String postId, final ImageView imageView, final TextView textView ){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                textView.setText(snapshot.getChildrenCount()+"");

                if(snapshot.child(user.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("Liked");
                }else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void isSaved(String postId, ImageView imageView){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Favorites");

        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postId).exists()){

                    imageView.setImageResource(R.drawable.ic_saved);
                    imageView.setTag("Saved");
                }else{
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("Save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }






    }




