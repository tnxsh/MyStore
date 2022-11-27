package com.example.mystore.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mystore.R;
import com.example.mystore.databinding.ActivityProductDetailsBinding;
import com.example.mystore.models.CartModel;
import com.example.mystore.models.ProductModel;
import com.example.mystore.user.CartActivity;
import com.example.mystore.utils.Constants;
import com.example.mystore.vendor.AddEditProductActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {
    ActivityProductDetailsBinding binding;
    ProductModel product;
    boolean isCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Constants.user.getType().equals("vendor")) {
            binding.btnAddCart.setText("Edit");
            binding.imgDelete.setVisibility(View.VISIBLE);
        } else {
            binding.etQuantity.setVisibility(View.VISIBLE);
            binding.imgDelete.setVisibility(View.GONE);
        }

        product = (ProductModel) getIntent().getSerializableExtra("Product");
        binding.txtProductName.setText(product.getName());
        binding.txtProductCost.setText(product.getPrice());
        binding.txtProductDescription.setText(product.getDescription());
        if (product.getImage() != null) {
            Glide.with(this).load(product.getImage()).placeholder(R.drawable.profile).into(binding.imgProduct);
        }

        checkUserCart();

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteProduct();
            }
        });

        binding.btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.user.getType().equals("vendor")) {
                    Intent intent = new Intent(ProductDetailsActivity.this, AddEditProductActivity.class);
                    intent.putExtra("Product",product);
                    intent.putExtra("Edit",true);
                    startActivity(intent);
                } else {
                    AddToCart();
                }
            }
        });


    }

    private void checkUserCart() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Carts").child(Constants.user.getId());
        reference.child(product.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isCart = snapshot.exists();
                if (isCart) {
                    binding.btnAddCart.setText("Remove from Cart");
                } else {
                    binding.btnAddCart.setText("Add to Cart");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DeleteProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
        builder.setTitle("Delete")
                .setMessage("Are you sure to delete this product>")
                .setNegativeButton("No",null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
                        reference.child(product.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProductDetailsActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).show();
    }

    private void AddToCart() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Carts").child(Constants.user.getId());
        if (isCart) {
            reference.child(product.getId()).removeValue();
            binding.btnAddCart.setText("Add to Cart");
        } else {
            ProgressDialog dialog = new ProgressDialog(ProductDetailsActivity.this);
            dialog.setMessage("Adding to Cart");
            dialog.setCancelable(false);
            dialog.show();
            String q = binding.etQuantity.getText().toString().trim();
            if (q.isEmpty()) {
                q = "1";
            }
            CartModel cartModel = new CartModel(product,q);
            reference.child(product.getId()).setValue(cartModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        binding.btnAddCart.setText("Remove from Cart");
                        dialog.dismiss();
                        isCart = true;
                        startActivity(new Intent(ProductDetailsActivity.this, CartActivity.class));
                        finish();
                    } else {
                        isCart = false;
                        dialog.dismiss();
                        Toast.makeText(ProductDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}