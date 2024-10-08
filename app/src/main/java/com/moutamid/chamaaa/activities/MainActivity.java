package com.moutamid.chamaaa.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;

import com.fxn.stash.Stash;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.moutamid.chamaaa.R;
import com.moutamid.chamaaa.databinding.ActivityMainBinding;
import com.moutamid.chamaaa.fragments.HomeFragment;
import com.moutamid.chamaaa.models.Admins;
import com.moutamid.chamaaa.models.UserModel;
import com.moutamid.chamaaa.utilis.Constants;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding binding;
    ArrayList<Admins> admins = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Constants.checkApp(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                binding.drawLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.navView.setNavigationItemSelectedListener(this);
        binding.drawLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        binding.navView.setCheckedItem(R.id.home);

        binding.search.setOnClickListener(v -> startActivity(new Intent(this, GroupSelectionActivity.class)));
        binding.addGroup.setOnClickListener(v -> startActivity(new Intent(this, GroupSelectionActivity.class)));
        binding.addPeople.setOnClickListener(v -> startActivity(new Intent(this, GroupSelectionActivity.class)));
        disableMessageLayout();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS);
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1011);
            }
        }

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
            Log.d("NotificationHelper", "getToken: " + s);
            Constants.databaseReference().child(Constants.USER).child(Constants.auth().getCurrentUser().getUid()).child("fcmToken").setValue(s);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        });
    }

    /*    private void createGroup() {
            ChatModel senderModel = new ChatModel();

            ArrayList<UserModel> currentItems = new ArrayList<>();
            UserModel stashUser = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
            currentItems.add(stashUser);

            String ID = UUID.randomUUID().toString();

            senderModel.id = ID;
            senderModel.userID = "";
            senderModel.money = "";

            senderModel.timestamp = new Date().getTime();

            senderModel.name = "SOCO Group";
            senderModel.image = "";

            senderModel.isGroup = true;
            senderModel.isSocoGroup = true;
            senderModel.isMoneyShared = false;

            senderModel.adminID = Constants.auth().getCurrentUser().getUid();

            senderModel.lastMessage = "Start Messaging";
            senderModel.groupMembers = new ArrayList<>(currentItems);

            Constants.databaseReference().child(Constants.SOCO)
                    .child(ID).setValue(senderModel).addOnSuccessListener(unused -> {

                    }).addOnFailureListener(e -> {
                        Constants.dismissDialog();
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }*/
    private static final String TAG = "MainActivity";

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        Constants.databaseReference().child(Constants.ADMINS).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                admins.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Admins admin = snapshot.getValue(Admins.class);
                    admins.add(admin);
                }
            }
        });
    }

    public void enableMessageLayout() {
        binding.toolbar.setTitle("Messages");
        binding.toolbar.setSubtitle("");
        binding.search.setVisibility(View.GONE);
        binding.messageLayout.setVisibility(View.VISIBLE);
    }

    public void disableMessageLayout() {
        UserModel userModel = (UserModel) Stash.getObject(Constants.STASH_USER, UserModel.class);
        String name = "Hi " + userModel.name.split(" ")[0];
        binding.toolbar.setTitle(name);
        binding.toolbar.setSubtitle("Welcome back, lets explore what’s going on..");
        binding.search.setVisibility(View.VISIBLE);
        binding.messageLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
        if (id == R.id.expenses) {
            startActivity(new Intent(this, ExpensesActivity.class));
        }
        if (id == R.id.savings) {
            startActivity(new Intent(this, SavingActivity.class));
        }
        if (id == R.id.product) {
            Stash.clear(Constants.PRODUCT_REFERENCE);
            startActivity(new Intent(this, ProductListActivity.class));
        }
        if (id == R.id.services) {
            startActivity(new Intent(this, ServicesActivity.class));
        }
        if (id == R.id.manageUser) {
            startActivity(new Intent(this, ManageUsersActivity.class));
        }
        if (id == R.id.sales) {
            startActivity(new Intent(this, SalesActivity.class));
        }
        if (id == R.id.setting) {
            startActivity(new Intent(this, SettingActivity.class));
        } else if (id == R.id.logout) {
            new MaterialAlertDialogBuilder(this)
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        Constants.auth().signOut();
                        startActivity(new Intent(MainActivity.this, SplashActivity.class));
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        }
        binding.drawLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (binding.drawLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}