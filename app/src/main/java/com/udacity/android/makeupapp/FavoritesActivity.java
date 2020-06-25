package com.udacity.android.makeupapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.udacity.android.makeupapp.databinding.ActivityFavoritesBinding;
import com.udacity.android.makeupapp.viewmodel.FavoritesViewModel;

import timber.log.Timber;

public class FavoritesActivity extends AppCompatActivity {

    ActivityFavoritesBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        showFavorites();
    }

    private void showFavorites() {
        FavoritesViewModel viewModel = ViewModelProviders.of(this).get(FavoritesViewModel.class);
        viewModel.getFavorites().observe(this, favorites -> {
            Timber.d("Receiving database update from ViewModel");
            // show favorites or error
            if (favorites != null && !favorites.isEmpty()) {
                //todo
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
}