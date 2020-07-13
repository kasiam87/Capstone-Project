package com.udacity.android.makeupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.udacity.android.makeupapp.adapter.FavoriteProductsAdapter;
import com.udacity.android.makeupapp.adapter.FavoriteProductsAdapterOnClickHandler;
import com.udacity.android.makeupapp.api.model.Product;
import com.udacity.android.makeupapp.database.ProductsDB;
import com.udacity.android.makeupapp.databinding.ActivityFavoritesBinding;
import com.udacity.android.makeupapp.viewmodel.FavoritesViewModel;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.udacity.android.makeupapp.constants.IntentExtras.PRODUCT_DETAILS_JSON;

public class FavoritesActivity extends AppCompatActivity implements FavoriteProductsAdapterOnClickHandler {

    ActivityFavoritesBinding b;

    private FavoriteProductsAdapter favoriteProductsAdapter;

    public static final String FAVORITES_ADAPTER_BUNDLE_KEY = "favoritesAdapterBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.favorites_menu));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        b.favoritesRecyclerView.setLayoutManager(layoutManager);
        b.favoritesRecyclerView.setHasFixedSize(true);
        favoriteProductsAdapter = new FavoriteProductsAdapter(this);

        if (savedInstanceState == null) {
            getAndShowFavorites();
        } else {
            ArrayList<Product> results = savedInstanceState.getParcelableArrayList(FAVORITES_ADAPTER_BUNDLE_KEY);
            showFavorites(results);
        }
        b.favoritesRecyclerView.setAdapter(favoriteProductsAdapter);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(FAVORITES_ADAPTER_BUNDLE_KEY, favoriteProductsAdapter.getFavoriteProducts());
    }

    private void getAndShowFavorites() {
        FavoritesViewModel viewModel = ViewModelProviders.of(this).get(FavoritesViewModel.class);
        viewModel.getFavorites().observe(this, favorites -> {
            Timber.d("Receiving database update from ViewModel");
            showFavorites(favorites);
        });
    }

    private void showFavorites(List<Product> favorites) {
        favoriteProductsAdapter.setFavoriteProducts(favorites);
        if (favorites != null && !favorites.isEmpty()) {
            ProductsDB favoritesDB = ProductsDB.getInstance(this);
            favoriteProductsAdapter.setDB(favoritesDB);
            b.favoritesRecyclerView.setVisibility(View.VISIBLE);
            b.noFavoritesErrorMsg.setVisibility(View.INVISIBLE);
        } else {
            b.noFavoritesErrorMsg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(SearchResultsActivity.HAS_BACK_PRESSED, true);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onClick(Product product) {
        Intent intent = new Intent(this, DetailsScreen.class);
        intent.putExtra(PRODUCT_DETAILS_JSON, new Gson().toJson(product));
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                getAndShowFavorites();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}