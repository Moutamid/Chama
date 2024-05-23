package com.moutamid.chama.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fxn.stash.Stash;
import com.moutamid.chama.R;
import com.moutamid.chama.databinding.ActivitySignUpBinding;
import com.moutamid.chama.models.UserModel;
import com.moutamid.chama.utilis.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    ArrayAdapter<String> gender;
    String[] genderList = {"Male", "Female", "Other"};
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gender = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genderList);
        binding.genderList.setAdapter(gender);

        binding.signup.setOnClickListener(v -> {
            if (valid()) {
                Constants.showDialog();
                if (imageUri == null) {
                    uploadData("");
                } else {
                    uploadImage();
                }
            }
        });
        binding.login.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

    }

    private void uploadImage() {
        Constants.storageReference().child("images").child(new SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault()).format(new Date())).putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        uploadData(uri.toString());
                    });
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadData(String link) {
        Constants.auth().createUserWithEmailAndPassword(
                binding.email.getEditText().getText().toString(),
                binding.password.getEditText().getText().toString()
        ).addOnSuccessListener(authResult -> {
            UserModel userDetails = new UserModel();
            userDetails.id = Constants.auth().getCurrentUser().getUid();
            userDetails.email = binding.email.getEditText().getText().toString();
            userDetails.name = binding.firstName.getEditText().getText().toString().trim() + " " + binding.lastName.getEditText().getText().toString().trim();
            userDetails.image = link;
            userDetails.phoneNum = binding.ccp.getSelectedCountryCodeWithPlus() + binding.phone.getText().toString().trim();
            userDetails.password = binding.password.getEditText().getText().toString();
            userDetails.gender = binding.gender.getEditText().getText().toString();
            Constants.databaseReference().child(Constants.USER).child(userDetails.id).setValue(userDetails)
                    .addOnSuccessListener(unused -> {
                        Stash.put(Constants.STASH_USER, userDetails);
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    }).addOnFailureListener(e -> {
                        Constants.dismissDialog();
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }).addOnFailureListener(e -> {
            Constants.dismissDialog();
            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
    }

    private boolean valid() {
        if (binding.email.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getEditText().getText().toString()).matches()) {
            Toast.makeText(this, "Email is not valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.password.getEditText().getText().toString().isEmpty()) {
            Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}