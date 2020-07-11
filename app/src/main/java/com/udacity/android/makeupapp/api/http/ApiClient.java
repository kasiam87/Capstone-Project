package com.udacity.android.makeupapp.api.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.udacity.android.makeupapp.api.model.Product;
import com.udacity.android.makeupapp.api.products.Brand;
import com.udacity.android.makeupapp.api.products.Tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class ApiClient {

    private static final String PRODUCTS_BASE_URI = "http://makeup-api.herokuapp.com/";

    public List<Product> getProducts(String searchTerm) throws IOException {
        ApiInterface productRequest = getRetrofitClient(PRODUCTS_BASE_URI)
                .create(ApiInterface.class);

        Map<String, String> queryMap = new HashMap<>();
        if (Brand.fromString(searchTerm) != null) {
            queryMap.put("brand", searchTerm);
        } else if (Tag.fromString(searchTerm) != null) {
            queryMap.put("product_tags", searchTerm);
        } else {
            queryMap.put("product_type", searchTerm);
        }

        Call<List<Product>> call = productRequest.getProducts(queryMap);
        Timber.d("Performing api call %s", call.request().toString());

        return call.execute().body();
    }

    private Retrofit getRetrofitClient(String uri) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(uri)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
