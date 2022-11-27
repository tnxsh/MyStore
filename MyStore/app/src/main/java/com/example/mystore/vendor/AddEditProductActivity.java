package com.example.mystore.vendor;

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
import com.example.mystore.common.EditProfileActivity;
import com.example.mystore.databinding.ActivityAddEditProductBinding;
import com.example.mystore.models.ProductModel;
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

public class AddEditProductActivity extends AppCompatActivity implements IPickResult {

    ActivityAddEditProductBinding binding;
    boolean isEdit;
    String id,link;
    boolean isImagePick = false;
    Bitmap bitmap;
    FirebaseDatabase database;
    private int PermissionReqCode = 1102;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        isEdit = getIntent().getBooleanExtra("Edit",false);
        if (isEdit) {
            setUI();
        }

        binding.imgProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AddEditProductActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(AddEditProductActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(AddEditProductActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionReqCode);
                } else {
                    PickImage();
                }
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(AddEditProductActivity.this);
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);
                dialog.show();
                String name = binding.etProductName.getText().toString().trim();
                String price = binding.etProductPrice.getText().toString().trim();
                String des = binding.etProductDescription.getText().toString().trim();
                String key;
                if (isEdit)
                    key = id;
                else
                    key = database.getReference().push().getKey();
                ProductModel model = new ProductModel(key,name,price,des,null);
                if (isImagePick) {
                    UploadImage(model);
                } else {
                    if (isEdit) {
                        if (isImagePick) {
                            UploadImage(model);
                        } else {
                            model.setImage(link);
                            UpdateProduct(model);
                        }
                    }
                }

            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void UpdateProduct(ProductModel model) {
        DatabaseReference reference = database.getReference("Products").child(Constants.user.getId());
        reference.child(model.getId()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(AddEditProductActivity.this, "Update", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    Toast.makeText(AddEditProductActivity.this, "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setUI() {
        ProductModel model = (ProductModel) getIntent().getSerializableExtra("Product");
        id = model.getId();
        link = model.getImage();
        binding.btnSave.setText("Update");
        binding.etProductName.setText(model.getName());
        binding.etProductPrice.setText(model.getPrice());
        binding.etProductDescription.setText(model.getDescription());

        if (Constants.user.getImage() != null) {
            Glide.with(this).load(Constants.user.getImage()).placeholder(R.drawable.profile).into(binding.imgProductImage);
        }
    }

    private void UploadImage(ProductModel model) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child("Products").child(Constants.user.getId()).child(model.getId() + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask =  reference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(AddEditProductActivity.this, "Error "+exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        model.setImage(uri.toString());
                        UpdateProduct(model);
                    }
                });
            }
        });
    }

    private void PickImage() {
        PickImageDialog.build(new PickSetup()).show(this);
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            bitmap = r.getBitmap();
            binding.imgProductImage.setImageBitmap(bitmap);
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
                PickImage();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }
}