package com.thebaileybrew.baileybrewbook.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.thebaileybrew.baileybrewbook.BaileyBrewBook;
import com.thebaileybrew.baileybrewbook.R;
import com.thebaileybrew.baileybrewbook.database.RecipeRepository;
import com.thebaileybrew.baileybrewbook.utils.JsonParseUtils;
import com.thebaileybrew.baileybrewbook.utils.NetworkUtils;
import com.thebaileybrew.baileybrewbook.utils.adapters.JsonLoader;

import java.net.URL;
import java.util.concurrent.ExecutionException;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenLoading extends AppCompatActivity {
    private final static String TAG = SplashScreenLoading.class.getSimpleName();

    private final static String RECIPE_PREFERENCE_LOAD = "share_prefs_load";
    private final static String RECIPE_INITIAL_LOAD = "initial_load";
    private final static String JSON_DATA_LOCATION = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private final static int SPLASH_DELAY = 3000;
    private final static int FLASH_SPLASH_DELAY = 1000;

    private Runnable loadDB;
    private Runnable noLoadDB;
    private Runnable reqIntent;

    private ImageView menuIcon;
    private TextView splashTitle;

    private Animation animCycle;
    private Animation animFadeIn;

    private URL recipeLocation;
    private String jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final RecipeRepository recipeRepository = new RecipeRepository(getApplication());
        setRunnables(recipeRepository);
        menuIcon = findViewById(R.id.menu_icon);
        splashTitle = findViewById(R.id.splash_title);
        animCycle = AnimationUtils.loadAnimation(BaileyBrewBook.getContext(), R.anim.anim_spin);
        animFadeIn = AnimationUtils.loadAnimation(BaileyBrewBook.getContext(), R.anim.anim_fade_in);


        SharedPreferences sharedPrefs = getSharedPreferences(RECIPE_PREFERENCE_LOAD, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        prefsEditor.putBoolean(RECIPE_INITIAL_LOAD, true);
        prefsEditor.apply();

        if (NetworkUtils.checkNetwork(BaileyBrewBook.getContext())) {
            prefsEditor.putBoolean(RECIPE_INITIAL_LOAD, false);
            Log.e(TAG, "onCreate: Network OK");
            if (sharedPrefs.getBoolean(RECIPE_INITIAL_LOAD, true)) {
                Log.e(TAG, "onCreate: Get Json Data");
                new Handler().postDelayed(loadDB, 0);
            }
        } else {
            Log.e(TAG, "onCreate: No Network");
            new Handler().postDelayed(noLoadDB, 0);
        }
        Log.e(TAG, "onCreate: requesting intent");
        new Handler().postDelayed(reqIntent, SPLASH_DELAY);
    }

    private void setRunnables(final RecipeRepository recipeRepository) {
        loadDB = new Runnable() {
            @Override
            public void run() {
                JsonLoader jsonLoader = new JsonLoader();
                try {
                    jsonResponse = jsonLoader.execute(JSON_DATA_LOCATION).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                JsonParseUtils.extractJsonDataToRoom(jsonResponse, recipeRepository);

                splashTitle.setVisibility(View.VISIBLE);
                splashTitle.startAnimation(animFadeIn);
                splashTitle.setVisibility(View.VISIBLE);
                menuIcon.startAnimation(animCycle);
            }
        };
        noLoadDB = new Runnable() {
            @Override
            public void run() {
                splashTitle.setVisibility(View.VISIBLE);
                splashTitle.startAnimation(animFadeIn);
                splashTitle.setVisibility(View.VISIBLE);
                menuIcon.startAnimation(animCycle);
            }
        };
        reqIntent = new Runnable() {
            @Override
            public void run() {
                requestIntent();
            }
        };
    }

    private void requestIntent() {
        Intent openRecipes = new Intent(SplashScreenLoading.this, RecipeListActivity.class);
        startActivity(openRecipes);
    }


}
