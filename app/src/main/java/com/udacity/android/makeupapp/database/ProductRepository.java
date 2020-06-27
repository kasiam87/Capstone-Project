package com.udacity.android.makeupapp.database;

import androidx.lifecycle.LiveData;

import com.udacity.android.makeupapp.api.model.Product;

import java.util.List;

public interface ProductRepository {

    LiveData<List<Product>> getFavorites();

    void insertFavorite(Product product);

    void deleteFavorite(Product product);

    boolean isFavorite(Product product);

}
