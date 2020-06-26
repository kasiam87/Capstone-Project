package com.udacity.android.makeupapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.udacity.android.makeupapp.api.model.Product;

import java.util.List;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM favorites ORDER BY name")
    LiveData<List<Product>> getFavorites();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavorite(Product product);

    @Delete
    void deleteFavorite(Product product);

    @Query("SELECT EXISTS (SELECT 1 FROM favorites WHERE id=:id)")
    int isFavorite(int id);

}
