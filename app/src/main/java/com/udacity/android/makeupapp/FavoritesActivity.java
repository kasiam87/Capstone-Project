package com.udacity.android.makeupapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
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
        }
        showFavorites();
    }

    private void showFavorites(){
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
}