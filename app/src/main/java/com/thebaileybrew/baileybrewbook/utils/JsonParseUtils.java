package com.thebaileybrew.baileybrewbook.utils;

import android.util.Log;

import com.thebaileybrew.baileybrewbook.database.RecipeRepository;
import com.thebaileybrew.baileybrewbook.database.models.Ingredient;
import com.thebaileybrew.baileybrewbook.database.models.Recipe;
import com.thebaileybrew.baileybrewbook.database.models.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonParseUtils {
    private final static String TAG = JsonParseUtils.class.getSimpleName();

    private final static String RECIPE_ID = "id";
    private final static String RECIPE_NAME = "name";
    private final static String RECIPE_SERVINGS = "servings";
    private final static String RECIPE_IMAGE_URL = "image";
    private final static String RECIPE_INGREDIENTS_LIST = "ingredients";
    private final static String RECIPE_STEPS_LIST = "steps";

    private final static String ING_NAME = "ingredient";
    private final static String ING_MEASURE = "measure";
    private final static String ING_QUANTITY = "quantity";

    private final static String STEP_ID = "id";
    private final static String STEP_SHORT_DESC = "shortDescription";
    private final static String STEP_LONG_DESC = "description";
    private final static String STEP_VIDEO_URL = "videoURL";



    public static void extractJsonDataToRoom(String jsonInput, RecipeRepository repository) {
        List<Ingredient> recipeIngredients;
        List<Step> recipeSteps;

        try {
            JSONArray jsonArray = new JSONArray(jsonInput);

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Recipe currentRecipe = new Recipe();
                recipeSteps = new ArrayList<>();
                recipeIngredients = new ArrayList<>();
                int recipeID = jsonObject.optInt(RECIPE_ID);
                String recipeName = jsonObject.optString(RECIPE_NAME);
                int recipeServing = jsonObject.optInt(RECIPE_SERVINGS);
                String recipeImageURL = jsonObject.optString(RECIPE_IMAGE_URL);
                JSONArray jsonIngredients = jsonObject.getJSONArray(RECIPE_INGREDIENTS_LIST);
                for (int ing = 0; ing <jsonIngredients.length(); ing++) {
                    JSONObject jsonIngredientsObject = jsonIngredients.getJSONObject(ing);
                    Ingredient currentIngredient = new Ingredient();
                    currentIngredient.setIngredientName(jsonIngredientsObject.optString(ING_NAME));
                    currentIngredient.setIngredientMeasure(jsonIngredientsObject.optString(ING_MEASURE));
                    currentIngredient.setIngredientQuantity(jsonIngredientsObject.optDouble(ING_QUANTITY));
                    recipeIngredients.add(currentIngredient);
                }
                JSONArray jsonSteps = jsonObject.getJSONArray(RECIPE_STEPS_LIST);
                for(int s = 0; s <jsonSteps.length(); s++) {
                    JSONObject jsonStepObject = jsonSteps.getJSONObject(s);
                    Step currentStep = new Step();
                    currentStep.setStepId(jsonStepObject.optInt(STEP_ID));
                    currentStep.setStepShortDescription(jsonStepObject.optString(STEP_SHORT_DESC));
                    currentStep.setFullDescription(jsonStepObject.optString(STEP_LONG_DESC));
                    currentStep.setStepVideoUrl(jsonStepObject.optString(STEP_VIDEO_URL));
                    recipeSteps.add(currentStep);
                }
                currentRecipe.setRecipeId(recipeID);
                currentRecipe.setRecipeName(recipeName);
                currentRecipe.setRecipeServing(recipeServing);
                currentRecipe.setRecipeImage(recipeImageURL);
                currentRecipe.setRecipeIngredients(recipeIngredients);
                currentRecipe.setSteps(recipeSteps);
                repository.insertRecipe(currentRecipe);

            }
        } catch (JSONException je) {
            Log.e(TAG, "extractJsonDataToRoom: problem getting recipe", je);
        }
    }
}
