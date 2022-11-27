package com.example.mystore.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mystore.R;
import com.example.mystore.adapters.ProductAdapter;
import com.example.mystore.databinding.ActivityHomeBinding;
import com.example.mystore.models.ProductModel;
import com.example.mystore.models.UserModel;
import com.example.mystore.user.CartActivity;
import com.example.mystore.utils.Constants;
import com.example.mystore.vendor.AddEditProductActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    List<ProductModel> productModelList;
    ProductAdapter adapter;
    boolean isUser;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isUser = getIntent().getBooleanExtra("isUser",false);
        productModelList = new ArrayList<>();
        adapter = new ProductAdapter(productModelList);
        binding.rvProduct.setAdapter(adapter);
        if (isUser) {
            UserModel model = (UserModel) getIntent().getSerializableExtra("User");
            id = model.getId();
            binding.imgProfile.setEnabled(false);
            binding.txtName.setText(model.getName());
            if (Constants.user.getImage() != null) {
                Glide.with(this).load(model.getImage()).placeholder(R.drawable.profile).into(binding.imgProfile);
            }
            binding.btnCart.setImageResource(R.drawable.cart);
        } else {
            id = Constants.user.getId();
            binding.txtName.setText(Constants.user.getName());
            if (Constants.user.getImage() != null) {
                Glide.with(this).load(Constants.user.getImage()).placeholder(R.drawable.profile).into(binding.imgProfile);
            }
        }
        if (Constants.user.getType().equals("vendor")) {
            binding.btnCart.setImageResource(R.drawable.add);
        }

        if (Constants.user.getBan()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Account")
                    .setMessage("Your Account is banner by Admin")
                    .setCancelable(false)
                    .show();
        }

        binding.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.user.getType().equals("vendor")) {
                    AddProduct();
                } else if (Constants.user.getType().equals("user")) {
                    startActivity(new Intent(HomeActivity.this, CartActivity.class));
                }
            }
        });

        binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadData();
    }

    private void LoadData() {
        productModelList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren()) {
                    ProductModel model = snapshot1.getValue(ProductModel.class);
                    productModelList.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void AddProduct() {
        startActivity(new Intent(HomeActivity.this, AddEditProductActivity.class));
    }

}