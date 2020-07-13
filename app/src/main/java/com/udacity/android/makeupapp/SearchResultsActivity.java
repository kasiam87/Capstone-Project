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
import com.udacity.android.makeupapp.api.products.Brand;
import com.udacity.android.makeupapp.api.products.Tag;
import com.udacity.android.makeupapp.api.products.Type;
import com.udacity.android.makeupapp.database.ProductsDB;
import com.udacity.android.makeupapp.databinding.ActivitySearchResultsBinding;
import com.udacity.android.makeupapp.viewmodel.FavoritesViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.udacity.android.makeupapp.constants.IntentExtras.PRODUCT_DETAILS_JSON;

public class SearchResultsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Product>>, SearchResultsAdapterOnClickHandler {

    ActivitySearchResultsBinding b;

    private static final int RESULTS_LOADER_ID = 100;
    private static final String SEARCH_TERM = "SearchTerm";
    public static final String HAS_BACK_PRESSED = "hasBackPressed";
    public static final String SEARCH_RESULTS_ADAPTER_BUNDLE_KEY = "searchResultsAdapterBundleKey";
    public static final String ERROR_MSG_BUNDLE_KEY = "errorMsgBundleKey";

    private SearchResultsAdapter searchResultsAdapter;

    boolean hasBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySearchResultsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchTerm = intent.getStringExtra(SearchManager.QUERY);

            boolean isValidSearch = Brand.fromString(searchTerm) != null
                    || Tag.fromString(searchTerm) != null
                    || Type.fromString(searchTerm) != null;

            if (getSupportActionBar() != null && isValidSearch) {
                getSupportActionBar().setTitle(searchTerm);
            }

            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
            b.searchResultsRecyclerView.setLayoutManager(layoutManager);
            b.searchResultsRecyclerView.setHasFixedSize(true);
            searchResultsAdapter = new SearchResultsAdapter(this);

            if (savedInstanceState == null) {
                if (isValidSearch) {
                    searchForProducts(searchTerm);
                } else {
                    b.errorMsg.setText(String.format(getText(R.string.no_results_error_msg).toString(), searchTerm));
                    b.errorMsg.setVisibility(View.VISIBLE);
                }
            } else {
                ArrayList<Product> results = savedInstanceState.getParcelableArrayList(SEARCH_RESULTS_ADAPTER_BUNDLE_KEY);
                showResults(results, savedInstanceState.getString(ERROR_MSG_BUNDLE_KEY));
            }
            b.searchResultsRecyclerView.setAdapter(searchResultsAdapter);
        }
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelableArrayList(SEARCH_RESULTS_ADAPTER_BUNDLE_KEY, searchResultsAdapter.getSearchResults());
        bundle.putString(ERROR_MSG_BUNDLE_KEY, b.errorMsg.getText().toString());
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
        showResults(products, getText(R.string.no_connection_error_msg).toString());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Product>> loader) {

    }

    private void showResults(List<Product> products, String errorMsg) {
        searchResultsAdapter.setSearchResults(products);
        if (products != null && !products.isEmpty()) {
            Timber.d("Show products");
            ProductsDB favoritesDB = ProductsDB.getInstance(this);
            searchResultsAdapter.setDB(favoritesDB);
            b.searchResultsRecyclerView.setVisibility(View.VISIBLE);
            b.errorMsg.setVisibility(View.INVISIBLE);
        } else {
            Timber.d("No product found!");
            b.searchResultsRecyclerView.setVisibility(View.INVISIBLE);
            b.errorMsg.setText(errorMsg);
            b.errorMsg.setVisibility(View.VISIBLE);
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