package com.thebaileybrew.baileybrewbook.ui;

import android.app.Activity;

import com.google.android.exoplayer2.DefaultLoadControl;
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

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final String SAVED_POSITION = "saved_player_position";
    private static  final String SAVED_STEP = "saved_stepper_position";

    private Recipe mRecipe = new Recipe();
    private final RecipeRepository recipeRepository;
    private RecyclerView ingredientList;
    private List<Ingredient> allIngredients = new ArrayList<>();
    private List<Step> allSteps = new ArrayList<>();
    private StepView stepView;
    private TextView recipeStepTitle;
    private TextView recipeStepDescription;
    private int currentSelectedStep;

    private MaterialSheetFab materialSheetFab;

    private SimpleExoPlayer exoPlayer;
    private PlayerView exoPlayerView;
    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady;

    private Boolean mTwoPane = false;

    private SharedPreferences sharedPreferences;

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
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);

            allIngredients = mRecipe.getRecipeIngredients();
            Log.e(TAG, "onCreate: ingredient size: " + allIngredients.size());
            allSteps = mRecipe.getSteps();
            Log.e(TAG, "onCreate: step size: " + allSteps.size());

            if (appBarLayout != null) {
                appBarLayout.setTitle(mRecipe.getRecipeName());
            }

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BaileyBrewBook.getContext());

            if(savedInstanceState != null) {
                playbackPosition = savedInstanceState.getLong(SAVED_POSITION, 0);
                currentSelectedStep = savedInstanceState.getInt(SAVED_STEP, 0);

            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(SAVED_POSITION, exoPlayer.getCurrentPosition());
        outState.putInt(SAVED_STEP, stepView.getCurrentStep());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mRecipe != null) {
            FloatingActionButton fab;
            if (mTwoPane) {
                fab = rootView.findViewById(R.id.floating_button);
                fab.setOnClickListener(this);
                FloatingActionButton ingredientFab = rootView.findViewById(R.id.floating_add_to_widget_two_pane);
                ingredientFab.setOnClickListener(this);
                TextView ingredientTextView = rootView.findViewById(R.id.textview_add_to_widget);
                ingredientTextView.setVisibility(View.VISIBLE);
            } else {
                FloatingActionButton twoPaneFab = rootView.findViewById(R.id.floating_button);
                twoPaneFab.setVisibility(View.INVISIBLE);
                FloatingActionButton ingredientFab = rootView.findViewById(R.id.floating_add_to_widget_two_pane);
                ingredientFab.setVisibility(View.GONE);
                TextView ingredientTextView = rootView.findViewById(R.id.textview_add_to_widget);
                ingredientTextView.setVisibility(View.GONE);
                fab = getActivity().findViewById(R.id.floating_button_locked);
                fab.setOnClickListener(this);
                FloatingActionButton widgetFab = getActivity().findViewById(R.id.floating_button_add_to_widget);
                widgetFab.setOnClickListener(this);
            }
            View sheetView = rootView.findViewById(R.id.fab_sheet);
            View overlayView = rootView.findViewById(R.id.overlay);
            int sheetColor = getActivity().getResources().getColor(R.color.colorAccent);
            int fabColor = getActivity().getResources().getColor(R.color.colorPrimaryDark);
            ingredientList = rootView.findViewById(R.id.ingredients_list);
            ingredientList.setLayoutManager(new LinearLayoutManager(BaileyBrewBook.getContext(),
                    RecyclerView.VERTICAL, false));
            ingredientList.setAdapter(new IngredientAdapter(BaileyBrewBook.getContext(), allIngredients));

            materialSheetFab = new MaterialSheetFab(fab, sheetView, overlayView, sheetColor, fabColor);
            exoPlayerView = rootView.findViewById(R.id.exoplayer);


            stepView = rootView.findViewById(R.id.recipe_steps);
            stepView.setStepsNumber(allSteps.size());
            stepView.setOnStepClickListener(this);
            stepView.go(currentSelectedStep, true);
            changeExoPlayer(currentSelectedStep);
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

    private void initializeExoPlayer() {
        //Get the first step instructional video playing
        if (allSteps.get(0).getStepVideoUrl() != null) {
            Uri currentStep = Uri.parse(allSteps.get(0).getStepVideoUrl());
            releasePlayer();
            TrackSelector track = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(BaileyBrewBook.getContext(),track,loadControl);
            exoPlayerView.setPlayer(exoPlayer);
            exoPlayer.setPlayWhenReady(playWhenReady);
            exoPlayer.seekTo(currentWindow, playbackPosition);

            String userAgent = Util.getUserAgent(BaileyBrewBook.getContext(), "Bailey Brew Book");
            MediaSource mediaSource = new ExtractorMediaSource(currentStep,
                    new DefaultDataSourceFactory(BaileyBrewBook.getContext(), userAgent),
                    new DefaultExtractorsFactory(), null, null);
            exoPlayer.prepare(mediaSource);

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
            playbackPosition = exoPlayer.getCurrentPosition(); //long
            currentWindow = exoPlayer.getCurrentWindowIndex(); //int
            playWhenReady = exoPlayer.getPlayWhenReady(); //boolean
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void changeExoPlayer(int step) {
        releasePlayer();
        reloadExoPlayer(step);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializeExoPlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || exoPlayer == null)) {
            initializeExoPlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.floating_button:
            case R.id.floating_button_locked:
                materialSheetFab.showSheet();
                break;
            case R.id.floating_button_add_to_widget:
            case R.id.floating_add_to_widget_two_pane:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("recipe_id", mRecipe.getRecipeId());
                editor.putString("recipe_name", mRecipe.getRecipeName());
                editor.apply();
                Log.e(TAG, "onClick: recipe is: " + mRecipe.getRecipeId() + " " + mRecipe.getRecipeName() );
                Toast.makeText(BaileyBrewBook.getContext(), "Recipe Saved To Widgets", Toast.LENGTH_SHORT).show();
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

}
