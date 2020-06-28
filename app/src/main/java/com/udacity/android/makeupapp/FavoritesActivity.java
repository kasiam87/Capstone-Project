package com.udacity.android.makeupapp;

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

import timber.log.Timber;

public class FavoritesActivity extends AppCompatActivity implements FavoriteProductsAdapterOnClickHandler {

    ActivityFavoritesBinding b;

    private FavoriteProductsAdapter favoriteProductsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        b.favoritesRecyclerView.setLayoutManager(layoutManager);
        b.favoritesRecyclerView.setHasFixedSize(true);
        favoriteProductsAdapter = new FavoriteProductsAdapter(this);

        if (savedInstanceState == null) {
            showFavorites();
        } else {
            showFavorites();
//                restoreFavorites(savedInstanceState);
        }
        b.favoritesRecyclerView.setAdapter(favoriteProductsAdapter);
        showFavorites();
    }

    private void showFavorites() {
        FavoritesViewModel viewModel = ViewModelProviders.of(this).get(FavoritesViewModel.class);
        viewModel.getFavorites().observe(this, favorites -> {
            Timber.d("Receiving database update from ViewModel");
            favoriteProductsAdapter.setFavoriteProducts(favorites);
            if (favorites != null && !favorites.isEmpty()) {
                ProductsDB favoritesDB = ProductsDB.getInstance(this);
                favoriteProductsAdapter.setDB(favoritesDB);
                b.favoritesRecyclerView.setVisibility(View.VISIBLE);
                b.noFavoritesErrorMsg.setVisibility(View.INVISIBLE);
            } else {
                b.noFavoritesErrorMsg.setVisibility(View.VISIBLE);
            }
        });
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
        intent.putExtra(SearchResultsActivity.PRODUCT_DETAILS_JSON, new Gson().toJson(product));
        startActivityForResult(intent, 1);
    }
}