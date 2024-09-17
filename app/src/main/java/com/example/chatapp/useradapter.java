package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // or Picasso, depending on your preference

import java.util.ArrayList;
import java.util.List;

public class useradapter extends RecyclerView.Adapter<useradapter.MyViewHolder> {
    private Context context;
    private List<userModel> userModelList;

    public useradapter(Context context) {
        this.context = context;
        this.userModelList = new ArrayList<>();
    }


    public void add(userModel userModel) {
        userModelList.add(userModel);
        notifyItemInserted(userModelList.size() - 1);
    }

    // Clears the list and notifies adapter
    public void clear() {
        userModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public useradapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.user_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull useradapter.MyViewHolder holder, int position) {
        userModel userModel = userModelList.get(position);

        // Set username and email
        holder.name.setText(userModel.getUsername());


        // Load user image using Glide (or another image loading library)
        String imageUrl = userModel.getProfilePhotoUrl();  // Assuming this method exists
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.person) // default placeholder image
                    .error(R.drawable.person)       // error image in case loading fails
                    .into(holder.profileImage);
        } else {
            // If no image URL is available, use the placeholder
            holder.profileImage.setImageResource(R.drawable.person);
        }

        // Set up onClick listener for opening the chat activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, chat.class);
                intent.putExtra("id", userModel.getId());
                intent.putExtra("name", userModel.getUsername());
                context.startActivity(intent);
            }
        });
    }

    public List<userModel> getUserModelList() {
        return userModelList;
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, email;
        private ImageView profileImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.username);
            profileImage = itemView.findViewById(R.id.userImageView);  // The ImageView in your layout
        }
    }
}
