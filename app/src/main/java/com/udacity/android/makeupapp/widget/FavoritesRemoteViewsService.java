package com.udacity.android.makeupapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.android.makeupapp.R;
import com.udacity.android.makeupapp.api.model.Product;

import java.util.ArrayList;
import java.util.List;

public class FavoritesRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FavoritesRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class FavoritesRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final int MAX_LIST_SIZE = 5;
    private List<Product> products = new ArrayList<>();
    private Context context;
    private int mAppWidgetId;

    public FavoritesRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
        // for example downloading or creating content etc, should be deferred to onDataSetChanged()
        // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.

        for (int i = 0; i < MAX_LIST_SIZE; i++) {
            products.add(new Product("maybelline ", "product" + i));
        }
    }

    public void onDestroy() {
        // In onDestroy() you should tear down anything that was setup for your data source,
        // eg. cursors, connections, etc.
        products.clear();
    }

    public int getCount() {
        return products.size();
    }

    public RemoteViews getViewAt(int position) {
        // position will always range from 0 to getCount() - 1.

        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        remoteViews.setImageViewResource(R.id.widget_product_image, R.drawable.heart_unchecked);
        remoteViews.setTextViewText(R.id.widget_product_brand, products.get(position).brand);
        remoteViews.setTextViewText(R.id.widget_product_name, products.get(position).name);

        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        extras.putInt(FavoritesWidgetProvider.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        remoteViews.setOnClickFillInIntent(R.id.widget_layout, fillInIntent);

        // You can do heaving lifting in here, synchronously. For example, if you need to
        // process an image, fetch something from the network, etc., it is ok to do it here,
        // synchronously. A loading view will show up in lieu of the actual contents in the
        // interim.

        // Return the remote views object.
        return remoteViews;
    }

    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
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
        // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
        // on the collection view corresponding to this factory. You can do heaving lifting in
        // here, synchronously. For example, if you need to process an image, fetch something
        // from the network, etc., it is ok to do it here, synchronously. The widget will remain
        // in its current state while work is being done here, so you don't need to worry about
        // locking up the widget.
    }
}