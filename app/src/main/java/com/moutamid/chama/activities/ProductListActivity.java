package com.moutamid.chama.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fxn.stash.Stash;
import com.google.firebase.database.DataSnapshot;
import com.moutamid.chama.R;
import com.moutamid.chama.adapters.ProductBuyAdapter;
import com.moutamid.chama.adapters.ProductsAdapter;
import com.moutamid.chama.bottomsheets.BuyProduct;
import com.moutamid.chama.databinding.ActivityProductListBinding;
import com.moutamid.chama.models.ProductModel;
import com.moutamid.chama.utilis.Constants;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {
    ActivityProductListBinding binding;
    ArrayList<ProductModel> list;

    public interface Buy{
        void onBuy(ProductModel model);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.name.setText("Products");
        binding.toolbar.back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.products.setLayoutManager(new LinearLayoutManager(this));
        binding.products.setHasFixedSize(false);
        Constants.initDialog(this);
        Constants.showDialog();
        list = new ArrayList<>();
        Constants.databaseReference().child(Constants.PRODUCTS).get()
                .addOnSuccessListener(dataSnapshot -> {
                    Constants.dismissDialog();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ProductModel productModel = snapshot.getValue(ProductModel.class);
                            list.add(productModel);
                        }
                    }
                    if (list.isEmpty()) {
                        binding.notLayout.setVisibility(View.VISIBLE);
                        binding.products.setVisibility(View.GONE);
                    }else {
                        binding.notLayout.setVisibility(View.GONE);
                        binding.products.setVisibility(View.VISIBLE);
                    }
//                    Stash.put(Constants.PRODUCTS, list);
                    ProductBuyAdapter adapter = new ProductBuyAdapter(this, list, model -> {
                        BuyProduct fragment = new BuyProduct(model);
                        fragment.show(getSupportFragmentManager(), fragment.getTag());
                    });
                    binding.products.setAdapter(adapter);
                }).addOnFailureListener(e -> {
                    Constants.dismissDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

}