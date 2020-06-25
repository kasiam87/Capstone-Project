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

public class FavoriteProductsAdapter extends RecyclerView.Adapter<FavoriteProductsAdapter.FavoriteProductsViewHolder> {

    private List<Product> products;

    @NonNull
    @Override
    public FavoriteProductsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();

        View view = LayoutInflater.from(context)
                .inflate(R.layout.favorites_grid_item, viewGroup, false);
        return new FavoriteProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteProductsViewHolder viewHolder, int position) {
        Product product = products.get(position);
        Timber.d("Set text to " + product.brand);
        viewHolder.productCard.setText(product.brand);
    }

    @Override
    public int getItemCount() {
        return (products == null) ? 0 : products.size();
    }

    class FavoriteProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView productCard;

        FavoriteProductsViewHolder(View view){
            super(view);
            productCard = view.findViewById(R.id.favorite_product);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Product product = products.get(adapterPosition);
            productClickHandler.onClick(product);
        }
    }

    private final FavoriteProductsAdapterOnClickHandler productClickHandler;

    public FavoriteProductsAdapter(FavoriteProductsAdapterOnClickHandler clickHandler) {
        productClickHandler = clickHandler;
    }

    public void setFavoriteProducts(List<Product> products) {
        if (products == null) {
            this.products = new ArrayList<>();
        } else {
            this.products = products;
        }

        notifyDataSetChanged();
    }

    public ArrayList<Product> getFavoriteProducts() {
        if (products == null){
            return new ArrayList<>();
        }
        return new ArrayList<>(products);
    }
}

