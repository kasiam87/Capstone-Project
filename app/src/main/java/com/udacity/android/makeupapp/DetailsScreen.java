package com.udacity.android.makeupapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.udacity.android.makeupapp.api.model.Product;
import com.udacity.android.makeupapp.database.AnotherThreadUsingRepository;
import com.udacity.android.makeupapp.database.ProductsDB;
import com.udacity.android.makeupapp.databinding.ActivityDetailsScreenBinding;
import com.udacity.android.makeupapp.utils.ImageLoader;
import com.udacity.android.makeupapp.utils.StringFormatter;
import com.udacity.android.makeupapp.viewmodel.FavoritesViewModel;

import timber.log.Timber;

import static android.view.View.GONE;

import static com.udacity.android.makeupapp.constants.IntentExtras.PRODUCT_DETAILS_JSON;

public class DetailsScreen extends AppCompatActivity {

    ActivityDetailsScreenBinding b;

    private Product product;

    private ProductsDB favoritesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityDetailsScreenBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        setSupportActionBar(b.detailToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        favoritesDB = ProductsDB.getInstance(this);

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
        b.productDetailBrand.setText(StringFormatter.capitalize(product.brand));
        b.productDetailProductName.setText(product.name);
        b.productDetailDescription.setText(product.description);

        setPrice();
        setFavoritesButton();
        setRating();
        setProductLink();
        setColors();
    }

    private void setPrice() {
        if (product.price != null) {
            b.productDetailPrice.setText(String.format("%s%s", product.price, product.priceSign != null ? product.priceSign : ""));
        } else {
            b.productDetailPrice.setVisibility(GONE);
        }
    }

    private void setFavoritesButton() {
        FavoritesViewModel viewModel = ViewModelProviders.of(this).get(FavoritesViewModel.class);
        viewModel.getFavorites().observe(this, favorites -> {
            if (favorites != null && !favorites.isEmpty()) {
                for (Product favorite : favorites) {
                    if (favorite.id.equals(product.id)) {
                        b.productDetailFavoriteButton.setButtonDrawable(R.drawable.details_heart_checked);
                        break;
                    }
                }
            }
        });
        b.productDetailFavoriteButton
                .setOnCheckedChangeListener((buttonView, isChecked) ->
                        addOrRemoveFromFavorites(product, b.productDetailFavoriteButton));
    }

    private void setRating() {
        if (product.rating != null) {
            b.productDetailRating.setRating(product.rating);
        } else {
            b.productDetailRating.setVisibility(GONE);
        }
    }

    private void setProductLink() {
        if (product.productLink != null && URLUtil.isValidUrl(product.productLink)) {
            b.productDetailLink.setClickable(true);
            b.productDetailLink.setMovementMethod(LinkMovementMethod.getInstance());
            String link = String.format("<a href='%s'> %s </a>", product.productLink, getString(R.string.link_display));
            b.productDetailLink.setText(Html.fromHtml(link));
        } else {
            b.productDetailLink.setVisibility(GONE);
        }
    }

    private void setColors() {
        if (product.productColors.size() != 0) {

            final ConstraintLayout constraintLayout = findViewById(R.id.details_constraint_layout);
            ImageView previousColorView = null;

            for (int i = 0; i < product.productColors.size(); i++) {
                ImageView colorView = new ImageView(getApplicationContext());

                colorView.setId(View.generateViewId());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    colorView.setImageDrawable(getDrawable(R.drawable.color_shape));
                } else {
                    colorView.setImageDrawable(getResources().getDrawable(R.drawable.color_shape));
                }

                colorView.setContentDescription(getString(R.string.color));
                Constraints.LayoutParams layoutParams =
                        new Constraints.LayoutParams(Constraints.LayoutParams.WRAP_CONTENT, Constraints.LayoutParams.WRAP_CONTENT);

                int margin = (int) getResources().getDimension(R.dimen.color_margin);
                layoutParams.setMargins(margin, margin, 0, margin);
                layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                layoutParams.bottomToTop = b.productDetailBrand.getId();
                if (i == 0) {
                    layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                } else {
                    layoutParams.startToEnd = previousColorView.getId();
                }

                previousColorView = colorView;
                colorView.setLayoutParams(layoutParams);

                colorView.setColorFilter(Color.parseColor(product.productColors.get(i).hexValue));

                constraintLayout.addView(colorView);
            }
        }
    }

    public void addOrRemoveFromFavorites(Product product, CheckBox favoriteButton) {
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