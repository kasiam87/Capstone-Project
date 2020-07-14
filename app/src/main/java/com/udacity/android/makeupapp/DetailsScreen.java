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
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.udacity.android.makeupapp.api.model.Product;
import com.udacity.android.makeupapp.database.AnotherThreadUsingRepository;
import com.udacity.android.makeupapp.database.ProductsDB;
import com.udacity.android.makeupapp.databinding.ActivityDetailsScreenBinding;
import com.udacity.android.makeupapp.utils.ImageLoader;
import com.udacity.android.makeupapp.utils.StringFormatter;
import com.udacity.android.makeupapp.viewmodel.FavoritesViewModel;

import static android.view.View.GONE;

import static com.udacity.android.makeupapp.constants.IntentExtras.PRODUCT_DETAILS_JSON;

public class DetailsScreen extends AppCompatActivity {

    ActivityDetailsScreenBinding b;

    private Product product;

    private ProductsDB favoritesDB;

    private FirebaseAnalytics firebaseAnalytics;

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

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(PRODUCT_DETAILS_JSON)) {
                showProductDetails(intent.getStringExtra(PRODUCT_DETAILS_JSON));
            }
        }

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
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
                        b.favoriteFab.setImageDrawable(getResources().getDrawable(R.drawable.details_heart_checked));
                        return;
                    }
                }
                b.favoriteFab.setImageDrawable(getResources().getDrawable(R.drawable.details_heart_unchecked));
            }
        });
        b.favoriteFab.setOnClickListener(buttonView ->
                addOrRemoveFromFavorites(b.favoriteFab));
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

    public void addOrRemoveFromFavorites(FloatingActionButton fab) {
        boolean isFavorite = new AnotherThreadUsingRepository(favoritesDB).isFavorite(product);
        if (isFavorite) {
            new AnotherThreadUsingRepository(favoritesDB).deleteFavorite(product);
            fab.setImageDrawable(getResources().getDrawable(R.drawable.details_heart_unchecked));
        } else {
            new AnotherThreadUsingRepository(favoritesDB).insertFavorite(product);
            fab.setImageDrawable(getResources().getDrawable(R.drawable.details_heart_checked));
        }
    }

    private void addOrRemoveFromFavorites(MenuItem item) {
        boolean isFavorite = new AnotherThreadUsingRepository(favoritesDB).isFavorite(product);
        if (isFavorite) {
            new AnotherThreadUsingRepository(favoritesDB).deleteFavorite(product);
            item.setIcon(getResources().getDrawable(R.drawable.heart_unchecked));
            b.favoriteFab.setImageDrawable(getResources().getDrawable(R.drawable.details_heart_unchecked));
        } else {
            new AnotherThreadUsingRepository(favoritesDB).insertFavorite(product);
            item.setIcon(getResources().getDrawable(R.drawable.heart_checked));
            b.favoriteFab.setImageDrawable(getResources().getDrawable(R.drawable.details_heart_checked));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add_to_favorites:
                addOrRemoveFromFavorites(item);
                return true;
            case R.id.action_share:
                startActivity(createShareIntent());

                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.ITEM_ID, product.id.toString());
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, product.brand + ": " + product.name);
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, params);
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
        getMenuInflater().inflate(R.menu.details_screen_menu, menu);

        MenuItem likeItem = menu.findItem(R.id.action_add_to_favorites);
        b.detailAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                findViewById(R.id.action_add_to_favorites).setVisibility(View.VISIBLE);
                setMenuFavoritesButton(likeItem);
            } else {
                findViewById(R.id.action_add_to_favorites).setVisibility(GONE);
                setFavoritesButton();
            }
        });

        return true;
    }

    private void setMenuFavoritesButton(MenuItem menuItem) {
        FavoritesViewModel viewModel = ViewModelProviders.of(this).get(FavoritesViewModel.class);
        viewModel.getFavorites().observe(this, favorites -> {
            if (favorites != null && !favorites.isEmpty()) {
                for (Product favorite : favorites) {
                    if (favorite.id.equals(product.id)) {
                        menuItem.setIcon(getResources().getDrawable(R.drawable.heart_checked));
                        return;
                    }
                }
                menuItem.setIcon(getResources().getDrawable(R.drawable.heart_unchecked));
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(SearchResultsActivity.HAS_BACK_PRESSED, true);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}