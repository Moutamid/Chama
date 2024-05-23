package com.moutamid.chama.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.chama.R;
import com.moutamid.chama.databinding.ActivityProfileEditBinding;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileEditActivity extends AppCompatActivity {
    ActivityProfileEditBinding binding;
    private static final int PICK_FROM_GALLERY = 1001;
    Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.name.setText("Update Profile");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        UserModel userModel = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        String[] name = userModel.name.split(" ");
        binding.firstName.getEditText().setText(name[0]);
        if (name.length > 1) {
            binding.lastName.getEditText().setText(name[1]);
        }
        if (userModel.gender != null)
            binding.gender.getEditText().setText(userModel.gender);
        if (userModel.countryCode != null && userModel.phoneNum != null) {
            binding.phone.setText(userModel.phoneNum);
            binding.ccp.setCountryForPhoneCode(Integer.parseInt(userModel.countryCode.replace("+", "")));
        }

        Glide.with(this).load(userModel.image).placeholder(R.drawable.profile_icon).into(binding.profile);

        binding.profile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(intent, ""), PICK_FROM_GALLERY);
        });

        binding.update.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                if (imageURI == null) {
                    uploadData(userModel.image);
                } else {
                    uploadImage();
                }
            }
        });

    }

    private void uploadImage() {
        Constants.storageReference().child("images").child(new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault()).format(new Date())).putFile(imageURI)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        uploadData(uri.toString());
                    });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadData(String link) {
        UserModel userDetails = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        userDetails.name = binding.firstName.getEditText().getText().toString().trim() + " " + binding.lastName.getEditText().getText().toString().trim();
        userDetails.image = link;
        userDetails.countryCode = binding.ccp.getSelectedCountryCodeWithPlus();
        userDetails.phoneNum = binding.phone.getText().toString().trim();
        userDetails.gender = binding.gender.getEditText().getText().toString();
        Constants.databaseReference().child(Constants.USER).child(userDetails.id).setValue(userDetails)
                .addOnSuccessListener(unused -> {
                    Stash.put(Constants.STASH_USER, userDetails);
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean valid() {
        if (binding.firstName.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Enter First Name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.phone.getText().toString().isEmpty()) {
            Toast.makeText(this, "Phone Number is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.gender.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Gender is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageURI = data.getData();
            Glide.with(this).load(imageURI).placeholder(R.drawable.profile_icon).into(binding.profile);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
    }
}