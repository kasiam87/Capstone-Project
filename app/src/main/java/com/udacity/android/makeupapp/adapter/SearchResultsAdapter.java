package com.udacity.android.makeupapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.udacity.android.makeupapp.R;
import com.udacity.android.makeupapp.api.model.Product;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.SearchResultViewHolder> {

    private List<Product> searchResults;

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
        Timber.d("Set text to " + product.brand);
        viewHolder.productCard.setText(product.brand);
    }

    @Override
    public int getItemCount() {
        return (searchResults == null) ? 0 : searchResults.size();
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView productCard;

        SearchResultViewHolder(View view){
            super(view);
            productCard = view.findViewById(R.id.product);
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
        if (searchResults == null){
            return new ArrayList<>();
        }
        return new ArrayList<>(searchResults);
    }
}
