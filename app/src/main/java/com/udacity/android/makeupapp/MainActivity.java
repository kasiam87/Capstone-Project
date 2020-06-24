package com.udacity.android.makeupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.android.makeupapp.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding b;
    public static final String QUERY = "QUERY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }


        // this if for replacing actionBar with toolbar. Also need to add theme AppTheme.NoActionBar in manifest
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(myToolbar);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        b.searchBox.setSearchableInfo(
                searchManager.getSearchableInfo(new ComponentName(this, SearchResultsActivity.class)));

        b.searchBox.setIconifiedByDefault(false);
        b.searchBox.setSubmitButtonEnabled(true);

        //TODO add suggestions https://developer.android.com/guide/topics/search/adding-recent-query-suggestions

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
        if (item.getItemId() == R.id.favorites){
            showFavoriteProducts();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFavoriteProducts() {
        Intent intent = new Intent(this, FavoritesActivity.class);
        startActivity(intent);
    }
}
