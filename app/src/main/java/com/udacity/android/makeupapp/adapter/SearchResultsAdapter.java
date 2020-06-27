package com.udacity.android.makeupapp.adapter;

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
import com.udacity.android.makeupapp.database.AnotherThreadUsingRepository;
import com.udacity.android.makeupapp.database.ProductsDB;
import com.udacity.android.makeupapp.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder> {

    private List<Product> searchResults;
    private ProductsDB favoritesDB;
    private final SearchResultsAdapterOnClickHandler resultsClickHandler;

    public SearchResultsAdapter(SearchResultsAdapterOnClickHandler clickHandler) {
        resultsClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
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

        boolean isFavorite = new AnotherThreadUsingRepository(favoritesDB).isFavorite(product);
        if (isFavorite){
            viewHolder.addToFavoritesButton.setButtonDrawable(R.drawable.heart_checked);
        } else {
            viewHolder.addToFavoritesButton.setButtonDrawable(R.drawable.heart_unchecked);
        }

        viewHolder.addToFavoritesButton
                .setOnCheckedChangeListener((buttonView, isChecked) ->
                        addOrRemoveFromFavorites(product, viewHolder.addToFavoritesButton));
    }

    @Override
    public int getItemCount() {
        return (searchResults == null) ? 0 : searchResults.size();
    }

    public void addOrRemoveFromFavorites(Product product, CheckBox addToFavoritesButton) {
        boolean isFavorite = new AnotherThreadUsingRepository(favoritesDB).isFavorite(product);
        if (isFavorite) {
            new AnotherThreadUsingRepository(favoritesDB)
                    .deleteFavorite(product);
            addToFavoritesButton.setButtonDrawable(R.drawable.heart_unchecked);
        } else {
            new AnotherThreadUsingRepository(favoritesDB)
                    .insertFavorite(product);
            addToFavoritesButton.setButtonDrawable(R.drawable.heart_checked);
        }
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
