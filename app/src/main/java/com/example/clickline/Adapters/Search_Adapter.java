package com.example.clickline.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Search_Adapter extends RecyclerView.Adapter<Search_Adapter.ViewHolder> {


    Context context;
    List<Users_List> users_list;


    public Search_Adapter(Context context, List<Users_List> users_list) {
        this.context = context;
        this.users_list = users_list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);


        return new ViewHolder(view);




    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Users_List list = users_list.get(position);

        holder.username.setText(list.getFull_name());
        holder.info.setText(list.getDisplay_name());


        try{
            Picasso.get().load(list.getProfile()).placeholder(R.drawable.profile_icon).into(holder.profile);
        }catch (Exception e){
          holder.profile.setImageResource(R.drawable.profile_icon);
        }





        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(list.getUser_id().equals(FirebaseAuth.getInstance().getUid())){
                    Toast.makeText(context, "Own profile", Toast.LENGTH_SHORT).show();


                }else {

                    Intent intent = new Intent(context, OthersProfile_Activity.class);
                    intent.putExtra("userId", list.getUser_id());
                    context.startActivity(intent);
                }
            }
        });

        if(list.getUser_id().equals((FirebaseAuth.getInstance().getUid()))){
                holder.btn_follow.setVisibility(View.INVISIBLE);
                holder.btn_following.setVisibility(View.INVISIBLE);

        }else{
            isFollowing(list.getUser_id(), holder.btn_follow, holder.btn_following);

        }




        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.btn_follow.getText().equals("follow")){

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow_data")
                            .child(user.getUid())
                            .child("following")
                            .child(list.getUser_id())
                            .setValue(true);

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow_data")
                            .child(list.getUser_id())
                            .child("followers")
                            .child(user.getUid())
                            .setValue(true);
                }
            }
        });


        holder.btn_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.btn_following.getText().equals("following")){

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow_data")
                            .child(user.getUid())
                            .child("following")
                            .child(list.getUser_id())
                                    .removeValue();

                    FirebaseDatabase.getInstance().getReference()
                            .child("Follow_data")
                            .child(list.getUser_id())
                            .child("followers")
                            .child(user.getUid())
                            .removeValue();
                }
            }
        });





    }

    @Override
    public int getItemCount() {
        return users_list.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile;
        TextView username, info;
        Button btn_follow, btn_following;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);


        profile = itemView.findViewById(R.id.profile);
        username = itemView.findViewById(R.id.pfull_Name);
        info = itemView.findViewById(R.id.info);
        btn_follow = itemView.findViewById(R.id.btn_follow);
        btn_following = itemView.findViewById(R.id.btn_following);

    }
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
                follow.setVisibility(View.GONE);
                following.setVisibility(View.VISIBLE);

            }else{
                follow.setVisibility(View.VISIBLE);
                following.setVisibility(View.GONE);


            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });





}

}
