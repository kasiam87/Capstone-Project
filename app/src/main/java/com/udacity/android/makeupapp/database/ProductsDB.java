package com.udacity.android.makeupapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.udacity.android.makeupapp.api.model.Product;

import timber.log.Timber;

@Database(entities = {Product.class}, version = 1, exportSchema = false)
public abstract class ProductsDB extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "productsDB";
    private static ProductsDB sInstance;

    public static ProductsDB getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Timber.d("Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        ProductsDB.class, ProductsDB.DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }
        Timber.d("Getting the database instance");
        return sInstance;
    }

    public abstract ProductDao productDao();
}
