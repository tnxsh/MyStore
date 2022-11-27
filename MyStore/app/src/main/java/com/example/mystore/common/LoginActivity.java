package com.example.mystore.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mystore.R;
import com.example.mystore.admin.AdminActivity;
import com.example.mystore.databinding.ActivityLoginBinding;
import com.example.mystore.models.UserModel;
import com.example.mystore.user.RestaurantsActivity;
import com.example.mystore.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    String email,password,type;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        preferences = getSharedPreferences("mystore",MODE_PRIVATE);
        editor = preferences.edit();

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

        binding.txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                finish();
            }
        });
    }

    private void Login() {
        if (Validation()) {
            ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (type.equals("admin")) {
                            if (email.equals("admin@happyfood.com") && password.equals("HappyFood")) {
                                dialog.dismiss();
                                editor.putString("type",type).commit();
                                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Wrong credentials", Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            }
                        } else {
                            myRef.child("Accounts").child(type.toUpperCase()).child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    editor.putString("type",type).commit();
                                    Constants.user = snapshot.getValue(UserModel.class);
                                    dialog.dismiss();
                                    if (type.equals("user")) {
                                        startActivity(new Intent(LoginActivity.this, RestaurantsActivity.class));
                                    } else {
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    }
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginActivity.this, "Error " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } else {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean Validation() {
        boolean valid = true;
        if (binding.rbUser.isChecked()) {
            type = "user";
            email = type+binding.etMail.getText().toString().trim();
        }else if (binding.rbVendor.isChecked()) {
            type = "vendor";
            email = type + binding.etMail.getText().toString().trim();
        } else if (binding.rbAdmin.isChecked()) {
            type = "admin";
            email = binding.etMail.getText().toString().trim();
        }



        password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            binding.etMail.setError("empty");
            binding.etMail.requestFocus();
            valid = false;
        }
        if (password.length() < 6) {
            binding.etPassword.setError("short");
            binding.etPassword.requestFocus();
            valid = false;
        }
        if (password.isEmpty()) {
            binding.etPassword.requestFocus();
            binding.etPassword.setError("missing");
            valid = false;
        }
        return valid;
    }
}