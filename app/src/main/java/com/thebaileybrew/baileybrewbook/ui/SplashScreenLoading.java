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
import com.thebaileybrew.baileybrewbook.utils.ConstantUtils;
import com.thebaileybrew.baileybrewbook.utils.JsonParseUtils;
import com.thebaileybrew.baileybrewbook.utils.NetworkUtils;
import com.thebaileybrew.baileybrewbook.utils.adapters.JsonLoader;

import java.net.URL;
import java.util.concurrent.ExecutionException;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenLoading extends AppCompatActivity {
    private final static String TAG = SplashScreenLoading.class.getSimpleName();



    private Runnable loadDB;
    private Runnable noLoadDB;
    private Runnable reqIntent;
    private ImageView menuIcon;
    private TextView splashTitle;
    private Animation animCycle;
    private Animation animFadeIn;
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


        SharedPreferences sharedPrefs = getSharedPreferences(ConstantUtils.RECIPE_PREFERENCE_LOAD, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
        prefsEditor.putBoolean(ConstantUtils.RECIPE_INITIAL_LOAD, true);
        prefsEditor.apply();

        if (NetworkUtils.checkNetwork(BaileyBrewBook.getContext())) {
            prefsEditor.putBoolean(ConstantUtils.RECIPE_INITIAL_LOAD, false);
            Log.e(TAG, "onCreate: Network OK");
            if (sharedPrefs.getBoolean(ConstantUtils.RECIPE_INITIAL_LOAD, true)) {
                Log.e(TAG, "onCreate: Get Json Data");
                new Handler().postDelayed(loadDB, 0);
            }
        } else {
            Log.e(TAG, "onCreate: No Network");
            new Handler().postDelayed(noLoadDB, 0);
        }
        Log.e(TAG, "onCreate: requesting intent");
        new Handler().postDelayed(reqIntent, ConstantUtils.SPLASH_DELAY);
    }

    private void setRunnables(final RecipeRepository recipeRepository) {
        loadDB = new Runnable() {
            @Override
            public void run() {
                JsonLoader jsonLoader = new JsonLoader();
                try {
                    jsonResponse = jsonLoader.execute(ConstantUtils.JSON_DATA_LOCATION).get();
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
