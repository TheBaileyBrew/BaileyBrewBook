package com.thebaileybrew.baileybrewbook.utils.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.thebaileybrew.baileybrewbook.R;
import com.thebaileybrew.baileybrewbook.ui.RecipeDetailActivity;

/**
 * Implementation of App Widget functionality.
 */
public class BrewBookRecipeWidget extends AppWidgetProvider {

    private final static String TAG = BrewBookRecipeWidget.class.getSimpleName();

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {
        //Get Recipe Name from Prefs
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String recipeName = sharedPreferences.getString("recipe_name", "Brew Book Recipe");

        // Construct the RemoteViews object
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.ingredient_grid_view);
        remoteViews.setTextViewText(R.id.recipe_title, recipeName);
        Log.e(TAG, "getIngredientsWidgetData: ");
        //Set the intent for GridView Adapter
        Intent intent = new Intent(context, BrewBookRecipeService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        remoteViews.setRemoteAdapter(appWidgetId, R.id.ingredient_grid_view, intent);
        //Set the intent to open activity when clicked
        Intent appIntent = new Intent(context, RecipeDetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context,0, appIntent, 0);
        remoteViews.setPendingIntentTemplate(R.id.ingredient_grid_view, appPendingIntent);
        //set empty view
        remoteViews.setEmptyView(R.id.ingredient_grid_view, R.id.empty_view);
        Log.e(TAG, "updateAppWidget: widget updated");

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

