package com.udacity.android.makeupapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import com.udacity.android.makeupapp.FavoritesActivity;
import com.udacity.android.makeupapp.R;

public class FavoritesWidgetProvider extends AppWidgetProvider {

    public static final String OPEN_FAVORITES_ACTION = "com.udacity.android.makeupapp.widget.OPEN_FAVORITES_ACTION";
    public static final String EXTRA_ITEM = "com.udacity.android.makeupapp.widget.EXTRA_ITEM";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // update each of the widgets with the remote adapter
        for (int appWidgetId: appWidgetIds) {

            // Here we setup the intent which points to the FavoritesRemoteViewsService which will
            // provide the views for this collection.
            Intent intent = new Intent(context, FavoritesRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remoteViews.setRemoteAdapter(appWidgetId, R.id.widget_list_view, intent);

            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            remoteViews.setEmptyView(R.id.widget_list_view, R.id.widget_error);

            Intent openFavoritesIntent = new Intent(context, FavoritesActivity.class);
            openFavoritesIntent.setAction(FavoritesWidgetProvider.OPEN_FAVORITES_ACTION);
            openFavoritesIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent openFavoritesPendingIntent = PendingIntent.getActivity(context, 0,
                    openFavoritesIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_list_view, openFavoritesPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(OPEN_FAVORITES_ACTION)) {
            Intent favoritesActivityIntent = new Intent(context, FavoritesActivity.class);
            ContextCompat.startActivity(context, favoritesActivityIntent, null);
        }
        super.onReceive(context, intent);
    }
}