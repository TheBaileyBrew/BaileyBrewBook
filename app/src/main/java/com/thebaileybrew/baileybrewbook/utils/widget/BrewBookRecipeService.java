package com.thebaileybrew.baileybrewbook.utils.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.thebaileybrew.baileybrewbook.BaileyBrewBook;
import com.thebaileybrew.baileybrewbook.R;
import com.thebaileybrew.baileybrewbook.database.RecipeRepository;
import com.thebaileybrew.baileybrewbook.database.models.Ingredient;
import com.thebaileybrew.baileybrewbook.database.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class BrewBookRecipeService extends RemoteViewsService {
    private final static String TAG = BrewBookRecipeService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.e(TAG, "onGetViewFactory: service running" );
        return new BrewBookRemoteViewsFactory(BaileyBrewBook.getContext());
    }
}

class BrewBookRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final static String TAG = BrewBookRemoteViewsFactory.class.getSimpleName();
    private final Context mContext;
    private Recipe mRecipe;
    private int mRecipeId;
    private RecipeRepository mRepository;
    private SharedPreferences sharedPreferences;
    private List<Ingredient> mIngredients = new ArrayList<>();

    public BrewBookRemoteViewsFactory(Context appContext) {
        mContext = appContext;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: shared prefs");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

    }

    @Override
    public void onDataSetChanged() {
        Log.e(TAG, "onDataSetChanged: changed");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mRecipeId = sharedPreferences.getInt("recipe_id", 0);
        mRepository = new RecipeRepository(BaileyBrewBook.getContext());
        mRecipe = mRepository.getSingleRecipe(mRecipeId);
        mIngredients = mRecipe.getRecipeIngredients();
        Log.e(TAG, "onDataSetChanged: " + mIngredients.size());


    }

    @Override
    public void onDestroy() {
        mIngredients.clear();

    }

    @Override
    public int getCount() {
        if (mIngredients == null) {
            return 0;
        } else {
            return mIngredients.size();
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.brew_book_widget_item);
        remoteViews.setTextViewText(R.id.widget_description,
                mIngredients.get(position).getIngredientName());
        Log.e(TAG, "getViewAt: NAME: " + mIngredients.get(position).getIngredientName() );
        remoteViews.setTextViewText(R.id.widget_measure,
                String.valueOf(mIngredients.get(position).getIngredientMeasure()));
        remoteViews.setTextViewText(R.id.widget_quantity,
                String.valueOf(mIngredients.get(position).getIngredientQuantity()));

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
