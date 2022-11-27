package com.example.mystore.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.mystore.R;
import com.example.mystore.admin.AdminActivity;
import com.example.mystore.models.UserModel;
import com.example.mystore.user.RestaurantsActivity;
import com.example.mystore.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    DatabaseReference myRef;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferences = getSharedPreferences("mystore",MODE_PRIVATE);
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (auth == null) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                } else {
                    String type = preferences.getString("type","");
                    if (type.length() > 0) {
                        if (type.equals("admin")) {
                            startActivity(new Intent(SplashActivity.this, AdminActivity.class));
                            finish();
                        } else {
                            myRef.child("Accounts").child(type.toUpperCase()).child(auth.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Constants.user = snapshot.getValue(UserModel.class);
                                    if (type.equals("user")) {
                                        startActivity(new Intent(SplashActivity.this, RestaurantsActivity.class));
                                    } else {
                                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                                    }
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(SplashActivity.this, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(SplashActivity.this, "Something went wrong ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }
        },2000);
    }
}