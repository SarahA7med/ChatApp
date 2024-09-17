package com.example.chatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class chat extends AppCompatActivity {

    // Variables for user and message data
    private String receiverId, receiverName, senderRoom, receiverRoom,senderName;
    private DatabaseReference databaseReferenceSender, databaseReferenceReceiver;

    // UI components
    private ImageView sendButton;
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private MessageAdapter messageAdapter;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Retrieve receiver's ID and name from Intent
        receiverId = getIntent().getStringExtra("id");
        receiverName = getIntent().getStringExtra("name");

        // Set up the title with receiver's name
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(receiverName);
        }

        // Initialize sender and receiver rooms for the chat
        if (receiverId != null) {
            senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
            receiverRoom = receiverId + FirebaseAuth.getInstance().getUid();
        }

        // Initialize UI elements
        sendButton = findViewById(R.id.sendMessageicon);
        messageEditText = findViewById(R.id.messageEdit);
        recyclerView = findViewById(R.id.chatRecycler);

        // Set up the message adapter and RecyclerView
        messageAdapter = new MessageAdapter(this);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReferenceSender = FirebaseDatabase.getInstance().getReference("chats").child(senderRoom);
        databaseReferenceReceiver = FirebaseDatabase.getInstance().getReference("chats").child(receiverRoom);

        databaseReferenceSender.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MessageModel> messages = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    messages.add(messageModel);
                }
                Collections.reverse(messages);


                messageAdapter.clear();
                messageAdapter.addAll(messages);

                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(chat.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the send button click listener
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageEditText.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    sendMessage(messageText);
                } else {
                    Toast.makeText(chat.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to send a message
    private void sendMessage(String messageText) {
        String messageId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        MessageModel messageModel = new MessageModel(messageId, FirebaseAuth.getInstance().getUid(), messageText, timestamp);

        // Add message to adapter
        messageAdapter.add(messageModel);

        // Push message to the sender's and receiver's chat rooms
        databaseReferenceSender.child(messageId).setValue(messageModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Success callback
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(chat.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
                });
        databaseReferenceReceiver.child(messageId).setValue(messageModel);

        // Scroll to the latest message
        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);

        // Clear the input field
        messageEditText.setText("");
    }

    // Inflate the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    // Handle menu item selections
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() ==R.id.home) {
            Intent intent = new Intent(chat.this, HOme.class);
            startActivity(intent);
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
