package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class HOme extends AppCompatActivity {
    private RecyclerView recyclerView;
    private useradapter userAdapter;
    private Context context;
    private DatabaseReference databaseReference;
    private ImageView userImageView;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();
        setupToolbar();
        loadUsers();
    }

    private void initUI() {
        context = this;
        recyclerView = findViewById(R.id.resycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        userAdapter = new useradapter(context);
        recyclerView.setAdapter(userAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        userImageView = toolbar.findViewById(R.id.toolbar_image);
        String username = getIntent().getStringExtra("name");
        if (username != null) {
            toolbarTitle.setText(username);
        }
        loadUserProfileImage();
    }

    private void loadUserProfileImage() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profile_images").child(userId + ".jpg");

            // Load the image using Glide
            profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Use Glide to load the image into the ImageView
                Glide.with(context)
                        .load(uri)
                        .circleCrop()
                        .into(userImageView);
            }).addOnFailureListener(e -> {
                Log.e("HOme", "Failed to load profile image: " + e.getMessage());
            });
        }
    }

    private void loadUsers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAdapter.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userModel userModel = dataSnapshot.getValue(userModel.class);
                    if (userModel != null && userModel.getId() != null &&
                            !userModel.getId().equals(FirebaseAuth.getInstance().getUid())) {
                        userAdapter.add(userModel);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HOme", "Failed to load users: " + error.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            logoutUser();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(context, signin.class));
        finish();
    }
}
