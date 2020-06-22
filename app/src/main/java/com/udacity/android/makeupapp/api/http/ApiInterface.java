package com.udacity.android.makeupapp.api.http;

import com.udacity.android.makeupapp.api.model.Product;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    @GET("api/v1/products.json")
    Call<List<Product>> getProducts(@QueryMap Map<String, String> queryParams);
}
