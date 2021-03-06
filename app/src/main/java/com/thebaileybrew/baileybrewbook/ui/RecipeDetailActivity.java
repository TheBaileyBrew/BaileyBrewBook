package com.thebaileybrew.baileybrewbook.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.os.PersistableBundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.thebaileybrew.baileybrewbook.BaileyBrewBook;
import com.thebaileybrew.baileybrewbook.R;
import com.thebaileybrew.baileybrewbook.database.RecipeRepository;
import com.thebaileybrew.baileybrewbook.database.models.Recipe;
import com.thebaileybrew.baileybrewbook.utils.ConstantUtils;

import java.util.List;

/**
 * An activity representing a single Recipe detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeListActivity}.
 */
public class RecipeDetailActivity extends AppCompatActivity {
    private static final String TAG = RecipeDetailActivity.class.getSimpleName();

    // --Commented out by Inspection (1/11/2019 1:06 PM):private static final String GOOGLE_SEARCH = "https://www.google.com/search?biw=1366&bih=675&tbm=isch&sa=1&ei=qFSJWsuTNc-wzwKFrZHoCw&q=";

    private ImageView recipeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        recipeImage = findViewById(R.id.recipe_imageview);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(ConstantUtils.ARG_ITEM_ID,
                    getIntent().getIntExtra(ConstantUtils.ARG_ITEM_ID,0));
            RecipeDetailFragment fragment = new RecipeDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, fragment)
                    .commit();
            int currentID = arguments.getInt(ConstantUtils.ARG_ITEM_ID);
            loadToolbarImage(currentID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    private void loadToolbarImage(int currentID) {
        RecipeRepository recipeRepository = new RecipeRepository(BaileyBrewBook.getContext());
        Recipe currentRecipe = recipeRepository.getSingleRecipe(currentID);
        Log.e(TAG, "loadToolbarImage: current name: " + currentRecipe.getRecipeName() );
        final String currentRecipeName = currentRecipe.getRecipeName();
        String singleImage ="";
        switch (currentRecipeName) {
            case "Brownies":
                singleImage = "https://assets.epicurious.com/photos/57c5bf64d8f441e50948d29d/2:1/w_1260%2Ch_630/milk-chocolate-brownies.jpg";
                break;
            case "Yellow Cake":
                singleImage = "https://prods3.imgix.net/images/articles/2017_08/Facebook-yellow-cake-chocolate-frosting-recipe-dessert.jpg";
                break;
            case "Cheesecake":
                singleImage = "https://d2gk7xgygi98cy.cloudfront.net/1820-3-large.jpg";
                break;
            case "Nutella Pie":
                singleImage = "https://hips.hearstapps.com/del.h-cdn.co/assets/16/32/1470773544-delish-nutella-cool-whip-pie-1.jpg";
                break;
        }



        Picasso.get().load(singleImage).into(recipeImage);
    }

    @Override
    public void onBackPressed() {
        tellFragments();
        super.onBackPressed();
    }

    private void tellFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment f: fragments) {
            if (f !=null && f instanceof RecipeDetailFragment) {
                ((RecipeDetailFragment) f).onBackPressed();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, RecipeListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
