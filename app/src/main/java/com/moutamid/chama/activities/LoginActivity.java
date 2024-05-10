package com.moutamid.chama.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.moutamid.chama.R;
import com.moutamid.chama.databinding.ActivityLoginBinding;
import com.moutamid.chama.utilis.Constants;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    boolean isBioEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.faceId.setOnClickListener(v -> {
            binding.emailLayout.setVisibility(View.GONE);
            binding.bioLayout.setVisibility(View.VISIBLE);
            isBioEnable = true;
            binding.loginTitle.setText("Login using Face ID");
            binding.icon.setImageResource(R.drawable.faceid);
        });

        binding.fingerprint.setOnClickListener(v -> {
            binding.emailLayout.setVisibility(View.GONE);
            binding.bioLayout.setVisibility(View.VISIBLE);
            isBioEnable = true;
            binding.loginTitle.setText("Login using Finger Print");
            binding.icon.setImageResource(R.drawable.fingerprint);
        });

        binding.signup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });
        binding.login.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        if (!isBioEnable) {
            super.onBackPressed();
        } else {
            isBioEnable = false;
            binding.bioLayout.setVisibility(View.GONE);
            binding.emailLayout.setVisibility(View.VISIBLE);
        }
    }
}