package com.example.mystore.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mystore.R;
import com.example.mystore.common.HomeActivity;
import com.example.mystore.databinding.ItemProductBinding;
import com.example.mystore.models.UserModel;
import com.example.mystore.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {

    Context context;
    List<UserModel> userModelsList;

    public AccountAdapter(List<UserModel> userModelsList) {
        this.userModelsList = userModelsList;
    }

    @NonNull
    @Override
    public AccountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new AccountAdapter.ViewHolder(ItemProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountAdapter.ViewHolder holder, int position) {
        UserModel model = userModelsList.get(position);
        holder.binding.tvProductName.setText(model.getName());
        holder.binding.tvProductPrice.setText(model.getType());
        if (model.getImage() != null) {
            Glide.with(context).load(model.getImage()).placeholder(R.drawable.profile).into(holder.binding.imgProduct);
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.user != null) {
                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.putExtra("isUser",true);
                    intent.putExtra("User",model);
                    context.startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Ban Account")
                            .setMessage("Are you sure to ban account?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Accounts").child(model.getType().toUpperCase());
                                    model.setBan(true);
                                    reference.child(model.getId()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, model.getType()+" banned successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("No",null)
                            .show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userModelsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductBinding binding;
        public ViewHolder(@NonNull ItemProductBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
