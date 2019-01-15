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




    private JsonParseUtils() {}




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
                int recipeID = jsonObject.optInt(ConstantUtils.RECIPE_ID);
                String recipeName = jsonObject.optString(ConstantUtils.RECIPE_NAME);
                int recipeServing = jsonObject.optInt(ConstantUtils.RECIPE_SERVINGS);
                JSONArray jsonIngredients = jsonObject.getJSONArray(ConstantUtils.RECIPE_INGREDIENTS_LIST);
                for (int ing = 0; ing <jsonIngredients.length(); ing++) {
                    JSONObject jsonIngredientsObject = jsonIngredients.getJSONObject(ing);
                    Ingredient currentIngredient = new Ingredient();
                    currentIngredient.setIngredientName(jsonIngredientsObject.optString(ConstantUtils.ING_NAME));
                    currentIngredient.setIngredientMeasure(jsonIngredientsObject.optString(ConstantUtils.ING_MEASURE));
                    currentIngredient.setIngredientQuantity(jsonIngredientsObject.optDouble(ConstantUtils.ING_QUANTITY));
                    recipeIngredients.add(currentIngredient);
                }
                JSONArray jsonSteps = jsonObject.getJSONArray(ConstantUtils.RECIPE_STEPS_LIST);
                for(int s = 0; s <jsonSteps.length(); s++) {
                    JSONObject jsonStepObject = jsonSteps.getJSONObject(s);
                    Step currentStep = new Step();
                    currentStep.setStepId(jsonStepObject.optInt(ConstantUtils.STEP_ID));
                    currentStep.setStepShortDescription(jsonStepObject.optString(ConstantUtils.STEP_SHORT_DESC));
                    currentStep.setFullDescription(jsonStepObject.optString(ConstantUtils.STEP_LONG_DESC));
                    currentStep.setStepVideoUrl(jsonStepObject.optString(ConstantUtils.STEP_VIDEO_URL));
                    recipeSteps.add(currentStep);
                }
                currentRecipe.setRecipeId(recipeID);
                currentRecipe.setRecipeName(recipeName);
                currentRecipe.setRecipeServing(recipeServing);
                currentRecipe.setRecipeIngredients(recipeIngredients);
                currentRecipe.setSteps(recipeSteps);
                repository.insertRecipe(currentRecipe);

            }
        } catch (JSONException je) {
            Log.e(TAG, "extractJsonDataToRoom: problem getting recipe", je);
        }
    }
}
