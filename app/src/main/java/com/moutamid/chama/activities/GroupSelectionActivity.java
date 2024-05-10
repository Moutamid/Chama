package com.moutamid.chama.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.moutamid.chama.R;
import com.moutamid.chama.databinding.ActivityGroupSelectionBinding;

import java.util.ArrayList;

public class GroupSelectionActivity extends AppCompatActivity {
    ActivityGroupSelectionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<String> names = new ArrayList<String>();

        // Add names to the ArrayList
        names.add("Alexa");
        names.add("Nick Jones");
        names.add("Alba Alter");
        names.add("Jeena");
        names.add("Roger");
        names.add("Nick Jones");
        names.add("Alba Alter");
        names.add("Jeena");
        names.add("Roger");
        names.add("Alba Alter");
        names.add("Jeena");
        names.add("Roger");

        for (String name : names) {
            LayoutInflater inflater = getLayoutInflater();
            View customLayout = inflater.inflate(R.layout.group_item, null);
            MaterialCardView cardView = customLayout.findViewById(R.id.card);
            MaterialRadioButton radioButton = customLayout.findViewById(R.id.radio);
            TextView nameView = customLayout.findViewById(R.id.name);
            nameView.setText(name);
            cardView.setOnClickListener(v -> {
                radioButton.setChecked(!radioButton.isChecked());
            });

            binding.groups.addView(customLayout);
        }

    }
}