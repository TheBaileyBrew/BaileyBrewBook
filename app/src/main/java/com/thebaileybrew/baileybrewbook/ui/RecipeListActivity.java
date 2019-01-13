package com.thebaileybrew.baileybrewbook.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thebaileybrew.baileybrewbook.R;
import com.thebaileybrew.baileybrewbook.database.RecipeRepository;
import com.thebaileybrew.baileybrewbook.database.models.Recipe;
import com.thebaileybrew.baileybrewbook.utils.adapters.RecipeCardRecycler;

import java.util.List;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeListActivity extends AppCompatActivity implements RecipeCardRecycler.RecipeClickHandler {
    private static final String TAG = RecipeListActivity.class.getSimpleName();

    private List<Recipe> recipes;
    private final RecipeRepository recipeRepository = new RecipeRepository(getApplication());
    private RecipeCardRecycler recipeCardRecycler;
    private ImageView recipeImagePath;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean mTwoPane;
    private boolean mSelected;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        recipeImagePath = findViewById(R.id.recipe_image_holder);

        if (findViewById(R.id.recipe_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.recipe_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        recipeCardRecycler = new RecipeCardRecycler(this, recipes, this, mTwoPane);
        recipes = recipeRepository.getRecipes(recipeCardRecycler);
        recyclerView.setAdapter(recipeCardRecycler);
        recyclerView.setLayoutManager(linearLayoutManager);
        recipeCardRecycler.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view, Recipe recipe) {
        if(mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(RecipeDetailFragment.ARG_ITEM_ID, recipe.getRecipeId());
            arguments.putBoolean(RecipeDetailFragment.ARG_BOOLEAN, mTwoPane);
            RecipeDetailFragment fragment = new RecipeDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_detail_container, fragment)
                    .commit();

            Picasso.get().load(recipe.getRecipeImage()).into(recipeImagePath);

        } else {
            if(!mSelected) {
                TextView currentTitle = view.findViewById(R.id.recipe_title);
                currentTitle.setTextColor(getColor(R.color.colorAccent));
                currentTitle.setTextSize(36);
                try {
                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(RecipeListActivity.this, RecipeDetailActivity.class);
                intent.putExtra(RecipeDetailFragment.ARG_ITEM_ID, recipe.getRecipeId());
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
        finish();
    }

}
