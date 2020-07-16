package com.udacity.android.makeupapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;
import com.udacity.android.makeupapp.R;
import com.udacity.android.makeupapp.api.model.Product;
import com.udacity.android.makeupapp.database.ProductDao;
import com.udacity.android.makeupapp.database.ProductsDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FavoritesRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FavoritesRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class FavoritesRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private int mAppWidgetId;

    private ProductDao productDao;
    private List<Product> products = new ArrayList<>();

    public FavoritesRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        productDao = ProductsDB.getInstance(context.getApplicationContext()).productDao();
    }

    public void onDestroy() {
        products.clear();
    }

    public int getCount() {
        return products.size();
    }

    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);

        try {
            Bitmap bitmap = Picasso.get().load(products.get(position).imageLink).get();
            remoteViews.setImageViewBitmap(R.id.widget_product_image, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        remoteViews.setTextViewText(R.id.widget_product_brand, products.get(position).brand);
        remoteViews.setTextViewText(R.id.widget_product_name, products.get(position).name);

        Bundle extras = new Bundle();
        extras.putInt(FavoritesWidgetProvider.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        remoteViews.setOnClickFillInIntent(R.id.widget_layout, fillInIntent);

        return remoteViews;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        products = productDao.getFavoritesList();
    }
}