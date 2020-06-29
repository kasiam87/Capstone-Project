package com.udacity.android.makeupapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

import com.google.gson.Gson;
import com.udacity.android.makeupapp.api.model.Product;
import com.udacity.android.makeupapp.database.AnotherThreadUsingRepository;
import com.udacity.android.makeupapp.database.ProductsDB;
import com.udacity.android.makeupapp.databinding.ActivityDetailsScreenBinding;
import com.udacity.android.makeupapp.utils.ImageLoader;
import com.udacity.android.makeupapp.viewmodel.FavoritesViewModel;

import timber.log.Timber;

import static com.udacity.android.makeupapp.constants.IntentExtras.PRODUCT_DETAILS_JSON;

public class DetailsScreen extends AppCompatActivity {

    ActivityDetailsScreenBinding b;

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityDetailsScreenBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(PRODUCT_DETAILS_JSON)) {
                    showProductDetails(intent.getStringExtra(PRODUCT_DETAILS_JSON));
                }
            }
        } else {
            // Restore state
        }
    }

    private void showProductDetails(String productJson) {
        product = new Gson().fromJson(productJson, Product.class);

        ImageLoader.loadImage(product.imageLink, b.productDetailsImage);
        b.productDetailBrand.setText(product.brand);
        b.productDetailProductName.setText(product.name);
        b.productDetailPrice.setText(String.format("%s%s", product.price, product.priceSign != null ? product.priceSign : ""));

        setFavoritesButton();
        b.productDetailFavoriteButton
                .setOnCheckedChangeListener((buttonView, isChecked) ->
                        addOrRemoveFromFavorites(product, b.productDetailFavoriteButton));
    }

    private void setFavoritesButton() {
        FavoritesViewModel viewModel = ViewModelProviders.of(this).get(FavoritesViewModel.class);
        viewModel.getFavorites().observe(this, favorites -> {
            if (favorites != null && !favorites.isEmpty()){
                boolean isFavorite = false;
                for (Product favorite : favorites) {
                    if (favorite.id.equals(product.id)) {
                        isFavorite = true;
                        break;
                    }
                }
                b.productDetailFavoriteButton.setChecked(isFavorite);
            }
        });
    }

    public void addOrRemoveFromFavorites(Product product, CheckBox favoriteButton) {
        ProductsDB favoritesDB = ProductsDB.getInstance(this);
        if (favoriteButton.isChecked()) {
            Timber.d("Add '%s' to favorites", product.name);
            new AnotherThreadUsingRepository(favoritesDB).insertFavorite(product);
            favoriteButton.setButtonDrawable(R.drawable.details_heart_checked);
        } else {
            Timber.d("Remove '%s' from favorites", product.name);
            new AnotherThreadUsingRepository(favoritesDB).deleteFavorite(product);
            favoriteButton.setButtonDrawable(R.drawable.detail_heart_unchecked);
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

    private Intent createShareIntent() {
        return ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(product.productLink)
                .setSubject(String.format("%s - %s", product.brand, product.name))
                .getIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareIntent());
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(SearchResultsActivity.HAS_BACK_PRESSED, true);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}