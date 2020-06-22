package com.udacity.android.makeupapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.udacity.android.makeupapp.api.model.Product;
import com.udacity.android.makeupapp.database.ProductsDB;

import java.util.List;

import timber.log.Timber;

public class ViewModel extends AndroidViewModel {

    private LiveData<List<Product>> favorites;

    public ViewModel(@NonNull Application application) {
        super(application);
        Timber.d("Getting favorite products from DB");
        ProductsDB productsDB = ProductsDB.getInstance(this.getApplication());
        favorites = productsDB.productDao().getFavorites();
    }

    public LiveData<List<Product>> getFavorites() {
        return favorites;
    }
}
