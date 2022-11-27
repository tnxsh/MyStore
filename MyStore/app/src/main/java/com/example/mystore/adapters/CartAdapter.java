package com.example.mystore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mystore.R;
import com.example.mystore.common.ProductDetailsActivity;
import com.example.mystore.databinding.ItemProductBinding;
import com.example.mystore.models.CartModel;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    List<CartModel> productModelList;
    Context context;
    public CartAdapter(List<CartModel> productModelList) {
        this.productModelList = productModelList;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CartAdapter.ViewHolder(ItemProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartModel model = productModelList.get(position);
        holder.binding.tvProductName.setText(model.getName());
        holder.binding.tvProductPrice.setText("RM "+model.getPrice() +" * " + model.getQuantity());
        if (model.getImage() != null) {
            Glide.with(context).load(model.getImage()).placeholder(R.drawable.profile).into(holder.binding.imgProduct);
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("Product",model);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductBinding binding;
        public ViewHolder(@NonNull ItemProductBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
