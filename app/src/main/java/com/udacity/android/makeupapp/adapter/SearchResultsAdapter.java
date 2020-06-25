package com.udacity.android.makeupapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.udacity.android.makeupapp.R;
import com.udacity.android.makeupapp.api.model.Product;
import com.udacity.android.makeupapp.database.AppExecutors;
import com.udacity.android.makeupapp.database.ProductsDB;
import com.udacity.android.makeupapp.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder> {

    private List<Product> searchResults;
    private ProductsDB favoritesDB;

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();

        View view = LayoutInflater.from(context)
                .inflate(R.layout.search_results_grid_item, viewGroup, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder viewHolder, int position) {
        Product product = searchResults.get(position);
        viewHolder.brand.setText(product.brand);
        viewHolder.name.setText(product.name);
        viewHolder.price.setText(product.price);
        ImageLoader.loadImage(product.imageLink, viewHolder.image);

        viewHolder.addToFavoritesButton
                .setOnCheckedChangeListener((buttonView, isChecked) ->
                        updateFavoritesDB(product, viewHolder.addToFavoritesButton));
    }

    public void updateFavoritesDB(Product product, CheckBox addToFavoritesButton) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            if (addToFavoritesButton.isChecked()) {
                favoritesDB.productDao().insertFavorite(product);
            } else {
                favoritesDB.productDao().deleteFavorite(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (searchResults == null) ? 0 : searchResults.size();
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView brand;
        TextView name;
        TextView price;
        CheckBox addToFavoritesButton;

        SearchResultViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.product_image);
            brand = view.findViewById(R.id.product_brand);
            name = view.findViewById(R.id.product_name);
            price = view.findViewById(R.id.product_price);
            addToFavoritesButton = view.findViewById(R.id.results_add_to_favorites_button);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Product product = searchResults.get(adapterPosition);
            resultsClickHandler.onClick(product);
        }
    }

    private final SearchResultsAdapterOnClickHandler resultsClickHandler;

    public SearchResultsAdapter(SearchResultsAdapterOnClickHandler clickHandler) {
        resultsClickHandler = clickHandler;
    }

    public void setSearchResults(List<Product> searchResults) {
        if (searchResults == null) {
            this.searchResults = new ArrayList<>();
        } else {
            this.searchResults = searchResults;
        }

        notifyDataSetChanged();
    }

    public ArrayList<Product> getSearchResults() {
        if (searchResults == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(searchResults);
    }

    public void setDB(ProductsDB favoritesDB) {
        this.favoritesDB = favoritesDB;
    }
}
