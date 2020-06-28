package com.udacity.android.makeupapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.udacity.android.makeupapp.adapter.SearchResultsAdapter;
import com.udacity.android.makeupapp.adapter.SearchResultsAdapterOnClickHandler;
import com.udacity.android.makeupapp.api.http.ApiClient;
import com.udacity.android.makeupapp.api.model.Product;
import com.udacity.android.makeupapp.database.ProductsDB;
import com.udacity.android.makeupapp.databinding.ActivitySearchResultsBinding;
import com.udacity.android.makeupapp.viewmodel.FavoritesViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import timber.log.Timber;

public class SearchResultsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Product>>, SearchResultsAdapterOnClickHandler {

    ActivitySearchResultsBinding b;

    private static final int RESULTS_LOADER_ID = 100;
    private static final String SEARCH_TERM = "SearchTerm";
    public static final String PRODUCT_DETAILS_JSON = "productDetailsJson";
    public static final String HAS_BACK_PRESSED = "hasBackPressed";

    private SearchResultsAdapter searchResultsAdapter;

    boolean hasBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySearchResultsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchTerm = intent.getStringExtra(SearchManager.QUERY);

            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
            b.searchResultsRecyclerView.setLayoutManager(layoutManager);
            b.searchResultsRecyclerView.setHasFixedSize(true);
            searchResultsAdapter = new SearchResultsAdapter(this);

            if (savedInstanceState == null) {
                searchForProducts(searchTerm);
            } else {
                searchForProducts(searchTerm);
//                restoreRecipesView(savedInstanceState);
            }
            b.searchResultsRecyclerView.setAdapter(searchResultsAdapter);
        }
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
            startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<List<Product>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Product>>(this) {

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }

                if (!hasBackPressed) {
                    b.loadingIndicator.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public List<Product> loadInBackground() {
                String searchTerm = args.getString(SEARCH_TERM);

                if (TextUtils.isEmpty(searchTerm)) {
                    return null;
                }

                try {
                    return new ApiClient().getProducts(searchTerm);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Product>> loader, List<Product> products) {
        b.loadingIndicator.setVisibility(View.GONE);
        showResults(products);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Product>> loader) {

    }

    private void showResults(List<Product> products) {
        if (products != null && !products.isEmpty()) {
            Timber.d("Show products");
            ProductsDB favoritesDB = ProductsDB.getInstance(this);
            searchResultsAdapter.setDB(favoritesDB);
            searchResultsAdapter.setSearchResults(products);
            b.searchResultsRecyclerView.setVisibility(View.VISIBLE);
            b.noConnectionErrorMsg.setVisibility(View.GONE);
        } else {
            Timber.d("No product found!");
            b.searchResultsRecyclerView.setVisibility(View.GONE);
            b.noConnectionErrorMsg.setVisibility(View.VISIBLE);
        }
    }

    private void searchForProducts(String searchTerm) {
        Bundle searchTermBundle = new Bundle();

        searchTermBundle.putString(SEARCH_TERM, searchTerm);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        Loader<List<Product>> searchResultsLoader = loaderManager.getLoader(RESULTS_LOADER_ID);
        if (searchResultsLoader == null) {
            loaderManager.initLoader(RESULTS_LOADER_ID, searchTermBundle, this).forceLoad();
        } else {
            loaderManager.restartLoader(RESULTS_LOADER_ID, searchTermBundle, this).forceLoad();
        }
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
                this.hasBackPressed = data.getBooleanExtra(HAS_BACK_PRESSED, true);
                reloadResults();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void reloadResults() {
        FavoritesViewModel viewModel = ViewModelProviders.of(this).get(FavoritesViewModel.class);
        viewModel.getFavorites().observe(this, favorites -> {
            Timber.d("Reload products");
            ProductsDB favoritesDB = ProductsDB.getInstance(this);
            searchResultsAdapter.setDB(favoritesDB);
            searchResultsAdapter.setSearchResults(searchResultsAdapter.getSearchResults());
        });
    }
}