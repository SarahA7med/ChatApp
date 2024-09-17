package com.example.chatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class signup extends AppCompatActivity {
    EditText userEmail, userName, userPassword;
    ImageView profileImageView;
    Uri imageUri;
    Button register;
    String email, password, username;
    DatabaseReference databaseReference;

    private static final int PICK_IMAGE_REQUEST = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        userEmail = findViewById(R.id.email);
        userPassword = findViewById(R.id.pass);
        register = findViewById(R.id.register);
        userName = findViewById(R.id.username);
        profileImageView = findViewById(R.id.profile_image);

        // Set click listener on profile ImageView
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = userName.getText().toString().trim();
                email = userEmail.getText().toString().trim();
                password = userPassword.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    userName.setError("Please enter your username");
                    userName.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    userEmail.setError("Please enter your email");
                    userEmail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    userPassword.setError("Please enter your password");
                    userPassword.requestFocus();
                    return;
                }

                Signup();
            }
        });
    }

    // Method to open the gallery to pick an image
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);  // Display the selected image in the ImageView
        }
    }

    private void Signup() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (imageUri != null) {
                            uploadProfilePhoto(authResult.getUser());
                        } else {
                            saveUserDataWithoutPhoto(authResult.getUser());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("SignupError", e.getMessage());
                        Toast.makeText(signup.this, "Signup failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadProfilePhoto(@NonNull FirebaseUser user) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_images")
                .child(user.getUid() + ".jpg");

        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        saveUserDataWithPhoto(user, uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("PhotoUploadError", e.getMessage());
                Toast.makeText(signup.this, "Failed to upload profile photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserDataWithPhoto(FirebaseUser user, String photoUrl) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(Uri.parse(photoUrl))
                .build();
        user.updateProfile(profileUpdates);

        userModel userModel = new userModel(user.getUid(), username, email, password, photoUrl);
        databaseReference.child(user.getUid()).setValue(userModel);

        Intent intent = new Intent(signup.this, HOme.class);
        intent.putExtra("name", username);
        startActivity(intent);
        finish();
    }

    private void saveUserDataWithoutPhoto(FirebaseUser user) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        user.updateProfile(profileUpdates);

        userModel userModel = new userModel(user.getUid(), username, email, password, null);
        databaseReference.child(user.getUid()).setValue(userModel);

        Intent intent = new Intent(signup.this, HOme.class);
        intent.putExtra("name", username);
        startActivity(intent);
        finish();
    }
}
