package com.example.mystore.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.mystore.adapters.AccountAdapter;
import com.example.mystore.databinding.ActivityManageBinding;
import com.example.mystore.models.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageActivity extends AppCompatActivity {
    ActivityManageBinding binding;
    boolean isUser = false;
    List<UserModel> userModelList;
    AccountAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        isUser = getIntent().getBooleanExtra("User",false);
        userModelList = new ArrayList<>();
        adapter = new AccountAdapter(userModelList);
        binding.rvManage.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isUser) {
            binding.tvTitle.setText("Manage Users");
            LoadUsers("USER");
        } else {
            binding.tvTitle.setText("Manage Restaurant");
            LoadUsers("VENDOR");
        }
    }

    private void LoadUsers(String type) {
        userModelList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Accounts").child(type);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren()) {
                    UserModel model = snapshot1.getValue(UserModel.class);
                    userModelList.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageActivity.this, "Error "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}