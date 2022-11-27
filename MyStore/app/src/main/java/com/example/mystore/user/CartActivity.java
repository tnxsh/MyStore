package com.example.mystore.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mystore.adapters.CartAdapter;
import com.example.mystore.adapters.ProductAdapter;
import com.example.mystore.databinding.ActivityCartBinding;
import com.example.mystore.databinding.DialogOrderBinding;
import com.example.mystore.models.CartModel;
import com.example.mystore.models.ProductModel;
import com.example.mystore.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    List<CartModel> cartModelList;
    CartAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        cartModelList = new ArrayList<>();
        adapter = new CartAdapter(cartModelList);
        binding.rvCart.setAdapter(adapter);

        binding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDialog();
            }
        });
    }

    private void OrderDialog() {
        DialogOrderBinding orderBinding = DialogOrderBinding.inflate(LayoutInflater.from(CartActivity.this),null,false);
        Dialog dialog = new Dialog(CartActivity.this);
        dialog.setContentView(orderBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        String items = "";
        int price = 0;
        for (CartModel model: cartModelList) {
            items += model.getQuantity()+ " * "+ model.getName() +" = "+model.getPrice()+"\n";
            price += (Integer.parseInt(model.getPrice())*Integer.parseInt(model.getQuantity()));
        }

        orderBinding.tvItem.setText(items);
        orderBinding.tvPrice.setText("RM "+price);
        orderBinding.btnConform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Carts").child(Constants.user.getId());
                reference.removeValue();
                Toast.makeText(CartActivity.this, "Thanks for Shopping", Toast.LENGTH_SHORT).show();
                finish();

            }
        });
        dialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadData();
    }

    private void LoadData() {
        cartModelList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Carts").child(Constants.user.getId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren()) {
                    CartModel model = snapshot1.getValue(CartModel.class);
                    cartModelList.add(model);
                }
                if (!(cartModelList.size() >0)) {
                    binding.btnOrder.setEnabled(false);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Error "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}