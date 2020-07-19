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
import com.udacity.android.makeupapp.database.AnotherThreadUsingRepository;
import com.udacity.android.makeupapp.database.ProductsDB;
import com.udacity.android.makeupapp.utils.ImageLoader;
import com.udacity.android.makeupapp.widget.FavoritesWidgetProvider;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.udacity.android.makeupapp.utils.ViewSetter.setTextView;

public class FavoriteProductsAdapter extends RecyclerView.Adapter<FavoriteProductsAdapter.FavoriteProductsViewHolder> {

    private List<Product> products;
    private ProductsDB favoritesDB;
    private Context context;
    private final FavoriteProductsAdapterOnClickHandler productClickHandler;

    public FavoriteProductsAdapter(FavoriteProductsAdapterOnClickHandler clickHandler) {
        productClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public FavoriteProductsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.favorites_grid_item, viewGroup, false);
        return new FavoriteProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteProductsViewHolder viewHolder, int position) {
        Product product = products.get(position);
        setTextView(product.brand, viewHolder.brand);
        setTextView(product.name, viewHolder.name);

        if (product.price != null && !product.price.isEmpty()) {
            viewHolder.price.setText(String.format("%s%s", product.price, product.priceSign != null ? product.priceSign : ""));
        } else {
            Timber.d("Price is not available");
            viewHolder.price.setVisibility(View.GONE);
        }
        ImageLoader.loadImage(product.imageLink, viewHolder.image);

        viewHolder.removeButton.setOnCheckedChangeListener((buttonView, isChecked) ->
                removeFromFavorites(product, position));
    }

    public void removeFromFavorites(Product product, int position) {
        new AnotherThreadUsingRepository(favoritesDB).deleteFavorite(product);
        products.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, products.size());
        FavoritesWidgetProvider.updateAllWidgets(context);
    }

    @Override
    public int getItemCount() {
        return (products == null) ? 0 : products.size();
    }

    class FavoriteProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView brand;
        TextView name;
        TextView price;
        CheckBox removeButton;

        FavoriteProductsViewHolder(View view) {
            super(view);
            brand = view.findViewById(R.id.favorite_product_brand);
            name = view.findViewById(R.id.favorite_product_name);
            price = view.findViewById(R.id.favorite_product_price);
            image = view.findViewById(R.id.favorite_product_image);
            removeButton = view.findViewById(R.id.favorites_remove_button);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Product product = products.get(adapterPosition);
            productClickHandler.onClick(product);
        }
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
        if (products == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(products);
    }

    public void setDB(ProductsDB favoritesDB) {
        this.favoritesDB = favoritesDB;
    }
}

