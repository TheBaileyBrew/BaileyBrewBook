package com.thebaileybrew.baileybrewbook.ui;

import android.app.Activity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
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
import com.thebaileybrew.baileybrewbook.utils.ConstantUtils;
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
    private DefaultBandwidthMeter exoBandwidthMeter;
    private TrackSelection.Factory exoTrackSelectionFactory;
    private TrackSelector exoTrackSelector;
    private DataSource.Factory exoDataSourceFactory;
    private MediaSource exoVideoSource;


    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady;
    private Uri exoVideoUri;

    private Boolean mTwoPane = false;

    private SharedPreferences sharedPreferences;

    public RecipeDetailFragment() {
        recipeRepository = new RecipeRepository(BaileyBrewBook.getContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ConstantUtils.ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            int recipeId = getArguments().getInt(ConstantUtils.ARG_ITEM_ID);
            mTwoPane = getArguments().getBoolean(ConstantUtils.ARG_BOOLEAN);
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
                playbackPosition = savedInstanceState.getLong(ConstantUtils.SAVED_POSITION, 0);
                currentSelectedStep = savedInstanceState.getInt(ConstantUtils.SAVED_STEP, 0);
                currentWindow = savedInstanceState.getInt(ConstantUtils.SAVED_WINDOW,0);
                exoVideoUri = Uri.parse(savedInstanceState.getString(ConstantUtils.SAVED_URI,""));
                playWhenReady = savedInstanceState.getBoolean(ConstantUtils.SAVED_PLAY_WHEN_READY,true);
            }

            if (!allSteps.get(0).getStepVideoUrl().isEmpty()) {
                exoVideoUri = Uri.parse(allSteps.get(0).getStepVideoUrl());
            }


        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ConstantUtils.SAVED_POSITION, exoPlayer.getCurrentPosition());
        outState.putInt(ConstantUtils.SAVED_STEP, stepView.getCurrentStep());
        outState.putInt(ConstantUtils.SAVED_WINDOW, exoPlayer.getCurrentWindowIndex());
        outState.putString(ConstantUtils.SAVED_URI, allSteps.get(stepView.getCurrentStep()).getStepVideoUrl());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail, container, false);
        exoPlayerView = rootView.findViewById(R.id.exoplayer);
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



            stepView = rootView.findViewById(R.id.recipe_steps);
            stepView.setStepsNumber(allSteps.size());
            stepView.setOnStepClickListener(this);
            stepView.go(currentSelectedStep, true);
            initializePlayer(Uri.parse(allSteps.get(stepView.getCurrentStep()).getStepVideoUrl()));
            recipeStepTitle = rootView.findViewById(R.id.recipe_step_name);
            recipeStepTitle.setText(allSteps.get(0).getStepShortDescription());
            recipeStepDescription = rootView.findViewById(R.id.recipe_full_description);
            recipeStepDescription.setText(allSteps.get(0).getFullDescription());
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

    public void initializePlayer(Uri videoUri) {
        if (videoUri == null) {
            exoPlayerView.setVisibility(View.GONE);
        } else {
            if (exoPlayer == null) {
                exoBandwidthMeter = new DefaultBandwidthMeter();
                exoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(exoBandwidthMeter);
                exoTrackSelector = new DefaultTrackSelector(exoTrackSelectionFactory);

                //Create the player
                exoPlayer = ExoPlayerFactory.newSimpleInstance(BaileyBrewBook.getContext(), exoTrackSelector);

                //Bind the player with the view
                exoPlayerView.setPlayer(exoPlayer);
                exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);

                //Get DataSource instance
                exoDataSourceFactory = new DefaultDataSourceFactory(BaileyBrewBook.getContext(),
                        Util.getUserAgent(BaileyBrewBook.getContext(),getString(R.string.app_name)),exoBandwidthMeter);

                //Get MediaSource
                exoVideoSource = new ExtractorMediaSource.Factory(exoDataSourceFactory).createMediaSource(videoUri);

                if (playbackPosition != C.TIME_UNSET) {
                    exoPlayer.seekTo(playbackPosition);
                }

                //Prepare the player
                exoPlayer.prepare(exoVideoSource);
            }
        }
    }



    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
            exoDataSourceFactory = null;
            exoVideoSource = null;
            exoTrackSelectionFactory = null;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer(exoVideoUri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || exoPlayer == null)) {
            initializePlayer(exoVideoUri);
        }
        if (exoPlayer != null) {
            if (playbackPosition > 0) {
                exoPlayer.setPlayWhenReady(playWhenReady);
                exoPlayerView.hideController();
            }

            exoPlayer.seekTo(playbackPosition);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            updatePositionData();
            if (Util.SDK_INT <= 23) {
                releasePlayer();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (exoPlayer != null) {
            updatePositionData();
            if (Util.SDK_INT <= 23) {
                releasePlayer();
            }
        }
    }

    private void updatePositionData() {
        if (exoPlayer != null) {
            playWhenReady = exoPlayer.getPlayWhenReady();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            playbackPosition = exoPlayer.getCurrentPosition();
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
        exoVideoUri = Uri.parse(allSteps.get(step).getStepVideoUrl());
        initializePlayer(exoVideoUri);

    }

}
