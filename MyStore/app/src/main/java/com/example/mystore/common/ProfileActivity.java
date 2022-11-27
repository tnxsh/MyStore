package com.example.mystore.common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.mystore.R;
import com.example.mystore.databinding.ActivityProfileBinding;
import com.example.mystore.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.titleName.setText(Constants.user.getName());
        binding.txtName.setText(Constants.user.getName());
        if (Constants.user.getType().equals("vendor")) {
            binding.txtEmail.setText(Constants.user.getEmail().substring(6));
        } else {
            binding.txtEmail.setText(Constants.user.getEmail().substring(4));
        }

        binding.tvPhone.setText(Constants.user.getPhone());
        binding.tvAddress.setText(Constants.user.getAddress());
        binding.tvAccount.setText(Constants.user.getType());
        if (Constants.user.getImage() != null) {
            Glide.with(this).load(Constants.user.getImage()).placeholder(R.drawable.profile).into(binding.profileImage);
        }

        binding.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this,EditProfileActivity.class));
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Constants.user = null;
                startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
                finishAffinity();
            }
        });
    }
}