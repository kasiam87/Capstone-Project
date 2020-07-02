package com.udacity.android.makeupapp.database;

import androidx.lifecycle.LiveData;

import com.udacity.android.makeupapp.api.model.Product;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

import timber.log.Timber;

public class AnotherThreadUsingRepository implements ProductRepository {

    private ProductsDB productsDB;

    public AnotherThreadUsingRepository(ProductsDB productsDB){
        this.productsDB = productsDB;
    }

    @Override
    public LiveData<List<Product>> getFavorites() {
        return execute(() -> productsDB.productDao().getFavorites());
    }

    @Override
    public void insertFavorite(Product product) {
        Timber.d("Add '%s' to favorites", product.name);
        execute(() -> {
            productsDB.productDao().insertFavorite(product);
            return true;
        });
    }

    @Override
    public void deleteFavorite(Product product) {
        Timber.d("Remove '%s' from favorites", product.name);
        execute(() -> {
            productsDB.productDao().deleteFavorite(product);
            return true;
        });
    }

    @Override
    public boolean isFavorite(Product product) {
        return execute(() ->
                productsDB.productDao().isFavorite(product.id)) == 1;
    }

    public <R> R execute(Callable<R> get) {
        LinkedBlockingQueue<R> queue = new LinkedBlockingQueue<>();
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                queue.put((R) get.call());
            } catch (Exception e) {
                Timber.e("Could not perform successfully DB operation");
            }
        });

        try {
            return queue.take();
        } catch (InterruptedException e) {
            Timber.e("Could not retrieve result from DB operation");
            throw new RuntimeException(e);
        }
    }
}
