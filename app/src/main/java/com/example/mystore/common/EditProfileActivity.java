package com.example.mystore.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mystore.R;
import com.example.mystore.databinding.ActivityEditProfileBinding;
import com.example.mystore.models.UserModel;
import com.example.mystore.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;

public class EditProfileActivity extends AppCompatActivity implements IPickResult {
    ActivityEditProfileBinding binding;
    private boolean isImagePick;
    private Bitmap bitmap;
    private int PermissionReqCode = 1102;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SetUI();

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveProfile();
            }
        });

        binding.uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionReqCode);
                } else {
                    PickProfile();
                }
            }
        });
    }

    private void PickProfile() {
        PickImageDialog.build(new PickSetup()).show(this);
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            bitmap = r.getBitmap();
            binding.profileImage.setImageBitmap(bitmap);
            isImagePick = true;
        } else {
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionReqCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PickProfile();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void SetUI() {
        binding.titleName.setText(Constants.user.getName());
        binding.edName.setText(Constants.user.getName());
        binding.edPhone.setText(Constants.user.getPhone());
        binding.edAddress.setText(Constants.user.getAddress());
        isImagePick = false;
        if (Constants.user.getImage() != null) {
            Glide.with(this).load(Constants.user.getImage()).placeholder(R.drawable.profile).into(binding.profileImage);
        }

    }

    private void SaveProfile() {
        dialog = new ProgressDialog(EditProfileActivity.this);
        dialog.setMessage("Saving...");
        dialog.setCancelable(false);
        dialog.show();
        String name = binding.edName.getText().toString().trim();
        String phone = binding.edPhone.getText().toString().trim();
        String address = binding.edAddress.getText().toString().trim();

        if (isImagePick) {
            UserModel model = new UserModel(Constants.user.getId(),name,Constants.user.getEmail(),phone,address,Constants.user.getType(),"",Constants.user.getBan());
            UploadImage(model);
        } else {
            UserModel model = new UserModel(Constants.user.getId(),name,Constants.user.getEmail(),phone,address,Constants.user.getType(),"",Constants.user.getBan());
            WriteDB(model);
        }

    }

    private void WriteDB(UserModel model) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Accounts");
        reference.child(Constants.user.getType().toUpperCase()).child(Constants.user.getId()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    Constants.user = model;
                } else {
                    dialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void UploadImage(UserModel model) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child("Accounts").child(Constants.user.getId() + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask =  reference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(EditProfileActivity.this, "Error "+exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        model.setImage(uri.toString());
                        WriteDB(model);
                    }
                });
            }
        });
    }
}