package com.example.chatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.SigningInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signin extends AppCompatActivity {
    EditText userEmail,userPassword;
    Button login, register;
    String email,password;
    DatabaseReference databaseReference;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        databaseReference= FirebaseDatabase.getInstance().getReference("users");
        userEmail=findViewById(R.id.email);
        userPassword=findViewById(R.id.pass);
        login=findViewById(R.id.login);
        register=findViewById(R.id.registerButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=userEmail.getText().toString().trim();
                password=userPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    userEmail.setError("Please enter your email");
                    userEmail.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    userPassword.setError("Please enter your password");
                    userPassword.requestFocus();
                    return;
                }
                Signin();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(signin.this,signup.class);
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            startActivity(new Intent(signin.this,HOme.class));
            finish();
        }
    }

    private void Signin() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(),password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String User_name=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        Intent intent = new Intent(signin.this, HOme.class);
                        intent.putExtra("name", User_name);
                        startActivity(intent);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof FirebaseAuthInvalidUserException)
                        {
                            Toast.makeText(signin.this, "User does not exist", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            Toast.makeText(signin.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}