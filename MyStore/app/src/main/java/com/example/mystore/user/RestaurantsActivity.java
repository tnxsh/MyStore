package com.example.mystore.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mystore.R;
import com.example.mystore.adapters.AccountAdapter;
import com.example.mystore.adapters.ProductAdapter;
import com.example.mystore.common.HomeActivity;
import com.example.mystore.common.ProfileActivity;
import com.example.mystore.databinding.ActivityRestaurentsBinding;
import com.example.mystore.models.ProductModel;
import com.example.mystore.models.UserModel;
import com.example.mystore.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsActivity extends AppCompatActivity {

    ActivityRestaurentsBinding binding;
    List<UserModel> modelList;
    AccountAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        modelList = new ArrayList<>();
        adapter = new AccountAdapter(modelList);
        binding.rvRestaurant.setAdapter(adapter);
        binding.txtName.setText(Constants.user.getName());
        if (Constants.user.getImage() != null) {
            Glide.with(this).load(Constants.user.getImage()).placeholder(R.drawable.profile).into(binding.imgProfile);
        }
        if (Constants.user.getType().equals("user")) {
            binding.btnCart.setImageResource(R.drawable.cart);
        }

        if (Constants.user.getBan()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantsActivity.this);
            builder.setTitle("Account")
                    .setMessage("Your Account is banner by Admin")
                    .setCancelable(false)
                    .show();
        }

        binding.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RestaurantsActivity.this,CartActivity.class));
            }
        });

        binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RestaurantsActivity.this, ProfileActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadData();
    }

    private void LoadData() {
        modelList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Accounts").child("VENDOR");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren()) {
                    UserModel model = snapshot1.getValue(UserModel.class);
                    modelList.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RestaurantsActivity.this, "Error "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}