package com.example.clickline.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clickline.Model.Comment_List;
import com.example.clickline.Model.Users_List;
import com.example.clickline.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Comment_Adapter extends RecyclerView.Adapter<Comment_Adapter.ViewHolder>{
    Context context;
    List<Comment_List> lists;


    public Comment_Adapter(Context context, List<Comment_List> lists) {
        this.context = context;
        this.lists = lists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_comment,parent,false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Comment_List comment_list = lists.get(position);


        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(comment_list.getDate()));
        String date = DateFormat.format("dd-MM-yyyy hh:mm", calendar).toString();


        holder.comment.setText(comment_list.getComment());
        holder.date.setText(" "+date);
        getUserInfo(holder.comment_profile, holder.fullName, comment_list.getCommenter());


    }

    @Override
    public int getItemCount() {
        return lists.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView comment_profile;
        TextView comment, date, fullName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            comment_profile = itemView.findViewById(R.id.comment_profile);
            comment = itemView.findViewById(R.id.tv_comment);
            date = itemView.findViewById(R.id.comment_date);
            fullName = itemView.findViewById(R.id.comment_fN);

        }
    }




    private void getUserInfo(ImageView imageview, TextView textView, String commenter){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(commenter);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users_List users_list = snapshot.getValue(Users_List.class);
                textView.setText(users_list.getFull_name());
                Glide.with(context).load(users_list.getProfile()).placeholder(R.drawable.profile_icon).into(imageview);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}
