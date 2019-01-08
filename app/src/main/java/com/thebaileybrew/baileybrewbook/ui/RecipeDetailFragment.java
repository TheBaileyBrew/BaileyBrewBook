package com.thebaileybrew.baileybrewbook.ui;

import android.app.Activity;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.shuhart.stepview.StepView;
import com.thebaileybrew.baileybrewbook.BaileyBrewBook;
import com.thebaileybrew.baileybrewbook.R;
import com.thebaileybrew.baileybrewbook.database.RecipeRepository;
import com.thebaileybrew.baileybrewbook.database.models.Ingredient;
import com.thebaileybrew.baileybrewbook.database.models.Recipe;
import com.thebaileybrew.baileybrewbook.database.models.Step;
import com.thebaileybrew.baileybrewbook.utils.adapters.IngredientAdapter;
import com.thebaileybrew.baileybrewbook.utils.interfaces.OnBackPressed;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Recipe detail screen.
 * This fragment is either contained in a {@link RecipeListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment implements View.OnClickListener, StepView.OnStepClickListener, OnBackPressed {
    private static final String TAG = RecipeDetailFragment.class.getSimpleName();

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_BOOLEAN = "boolean_two_pane";

    private Recipe mRecipe = new Recipe();
    private RecipeRepository recipeRepository;
    RecyclerView ingredientList;
    private List<Ingredient> allIngredients = new ArrayList<>();
    private List<Step> allSteps = new ArrayList<>();
    private StepView stepView;
    private TextView recipeStepTitle;
    private TextView recipeStepDescription;

    private FloatingActionButton fab;
    private View sheetView;
    private View overlayView;
    private int sheetColor;
    private int fabColor;
    private MaterialSheetFab materialSheetFab;

    private SimpleExoPlayer exoPlayer;
    private PlayerView exoPlayerView;

    private Boolean mTwoPane = false;

    public RecipeDetailFragment() {
        recipeRepository = new RecipeRepository(BaileyBrewBook.getContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            int recipeId = getArguments().getInt(ARG_ITEM_ID);
            mTwoPane = getArguments().getBoolean(ARG_BOOLEAN);
            Log.e(TAG, "onCreate: id: " + recipeId );
            mRecipe = recipeRepository.getSingleRecipe(recipeId);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);

            allIngredients = mRecipe.getRecipeIngredients();
            Log.e(TAG, "onCreate: ingredient size: " + allIngredients.size());
            allSteps = mRecipe.getSteps();
            Log.e(TAG, "onCreate: step size: " + allSteps.size());

            if (appBarLayout != null) {
                appBarLayout.setTitle(mRecipe.getRecipeName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mRecipe != null) {
            if (mTwoPane) {
                fab = (FloatingActionButton) rootView.findViewById(R.id.floating_button);
                fab.setOnClickListener(this);
            } else {
                FloatingActionButton twoPaneFab = (FloatingActionButton) rootView.findViewById(R.id.floating_button);
                twoPaneFab.setVisibility(View.INVISIBLE);
                fab = getActivity().findViewById(R.id.floating_button_locked);
                fab.setOnClickListener(this);
            }
            sheetView = rootView.findViewById(R.id.fab_sheet);
            overlayView = rootView.findViewById(R.id.overlay);
            sheetColor = getActivity().getResources().getColor(R.color.colorAccent);
            fabColor = getActivity().getResources().getColor(R.color.colorPrimaryDark);
            ingredientList = rootView.findViewById(R.id.ingredients_list);
            ingredientList.setLayoutManager(new LinearLayoutManager(BaileyBrewBook.getContext(),
                    RecyclerView.VERTICAL, false));
            ingredientList.setAdapter(new IngredientAdapter(BaileyBrewBook.getContext(), allIngredients));

            materialSheetFab = new MaterialSheetFab(fab, sheetView, overlayView, sheetColor, fabColor);
            exoPlayerView = rootView.findViewById(R.id.exoplayer);


            stepView = (StepView) rootView.findViewById(R.id.recipe_steps);
            stepView.setStepsNumber(allSteps.size());
            stepView.setOnStepClickListener(this);
            recipeStepTitle = rootView.findViewById(R.id.recipe_step_name);
            recipeStepTitle.setText(allSteps.get(0).getStepShortDescription());
            recipeStepDescription = rootView.findViewById(R.id.recipe_full_description);
            recipeStepDescription.setText(allSteps.get(0).getFullDescription());
            initializeExoPlayer();
        }

        return rootView;
    }

    public void onBackPressed() {
        if(materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else {
            onBackPressed();
        }
    }

    public void initializeExoPlayer() {
        //Get the first step instructional video playing
        if (allSteps.get(0).getStepVideoUrl() != null) {
            Uri currentStep = Uri.parse(allSteps.get(0).getStepVideoUrl());
            releasePlayer();
            TrackSelector track = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(BaileyBrewBook.getContext(),track,loadControl);
            exoPlayerView.setPlayer(exoPlayer);

            String userAgent = Util.getUserAgent(BaileyBrewBook.getContext(), "Bailey Brew Book");
            MediaSource mediaSource = new ExtractorMediaSource(currentStep,
                    new DefaultDataSourceFactory(BaileyBrewBook.getContext(), userAgent),
                    new DefaultExtractorsFactory(), null, null);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);

        }
    }
    private void reloadExoPlayer(int step) {
        if (allSteps.get(step).getStepVideoUrl() != null) {
            exoPlayerView.setVisibility(View.VISIBLE);
            Uri currentStep = Uri.parse(allSteps.get(step).getStepVideoUrl());
            TrackSelector track = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(BaileyBrewBook.getContext(), track, loadControl);
            exoPlayerView.setPlayer(exoPlayer);

            String userAgent = Util.getUserAgent(BaileyBrewBook.getContext(), "Bailey Brew Book");
            MediaSource mediaSource = new ExtractorMediaSource(currentStep,
                    new DefaultDataSourceFactory(BaileyBrewBook.getContext(), userAgent),
                    new DefaultExtractorsFactory(), null, null);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        } else {
            exoPlayerView.setVisibility(View.INVISIBLE);
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    public void changeExoPlayer(int step) {
        releasePlayer();
        reloadExoPlayer(step);
    }




    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.floating_button:
            case R.id.floating_button_locked:
                materialSheetFab.showSheet();
                break;
            default:
                break;
        }
    }

    @Override
    public void onStepClick(int step) {
        int currentStep = stepView.getCurrentStep();
        Log.e(TAG, "onStepClick: current step: " + currentStep + "// selected: " + step );
        if (currentStep < step) {
            stepView.go(step, true);
        } else {
            stepView.done(false);
            stepView.go(step, true);
        }
        recipeStepTitle.setText(allSteps.get(step).getStepShortDescription());
        recipeStepDescription.setText(allSteps.get(step).getFullDescription());
        changeExoPlayer(step);

    }

    public Recipe getCurrentRecipe() {
        return mRecipe;
    }
}
