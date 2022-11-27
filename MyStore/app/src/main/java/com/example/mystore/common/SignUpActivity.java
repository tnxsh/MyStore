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
import com.example.mystore.databinding.ActivitySignUpBinding;
import com.example.mystore.models.UserModel;
import com.example.mystore.user.RestaurantsActivity;
import com.example.mystore.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    String name,email,password,type;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        preferences = getSharedPreferences("mystore",MODE_PRIVATE);
        editor = preferences.edit();

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validation()) {
                    ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
                    dialog.setMessage("Please wait...");
                    dialog.setCancelable(false);
                    dialog.show();

                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Constants.user = new UserModel(mAuth.getUid(),name,email,type,false);
                                myRef.child("Accounts").child(type.toUpperCase()).child(mAuth.getUid()).setValue(Constants.user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialog.dismiss();
                                            editor.putString("type",type).commit();
                                            Toast.makeText(SignUpActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                                            if (type.equals("user")) {
                                                startActivity(new Intent(SignUpActivity.this, RestaurantsActivity.class));
                                            } else {
                                                startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                            }
                                            finish();
                                        } else {
                                            dialog.dismiss();
                                            Toast.makeText(SignUpActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                dialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean Validation() {
        boolean valid = true;
        name = binding.etName.getText().toString().trim();
        if (binding.rbUser.isChecked())
            type = "user";
        else if (binding.rbVendor.isChecked())
            type = "vendor";
        else if (binding.rbAdmin.isChecked())
            type = "admin";

        email = type+binding.etMail.getText().toString().trim();
        password = binding.etPassword.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etName.setError("empty");
            binding.etName.requestFocus();
            valid = false;
        }
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