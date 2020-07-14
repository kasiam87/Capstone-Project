package com.udacity.android.makeupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.udacity.android.makeupapp.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            b.searchBox.setSearchableInfo(
                    searchManager.getSearchableInfo(new ComponentName(this, SearchResultsActivity.class)));
        }

        b.searchBox.setIconifiedByDefault(false);
        b.searchBox.setSubmitButtonEnabled(true);

        MobileAds.initialize(this, initializationStatus -> {});
        AdRequest adRequest = new AdRequest.Builder().build();
        b.adView.loadAd(adRequest);
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.favorites) {
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}