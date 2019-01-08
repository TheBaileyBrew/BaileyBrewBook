package com.thebaileybrew.baileybrewbook.database.models;


import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class Recipe {

    @PrimaryKey
    @ColumnInfo(name = "recipe_id")
    private int recipeId;
    @ColumnInfo(name = "recipe_name")
    private String recipeName;
    @ColumnInfo(name = "recipe_image")
    private String recipeImage;
    @ColumnInfo(name = "recipe_ingredients")
    private List<Ingredient> recipeIngredients = null;
    @ColumnInfo(name = "recipe_steps")
    private List<Step> steps = null;
    @ColumnInfo(name = "recipe_serving")
    private int recipeServing;

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public void setRecipeImage(String recipeImage) {
        this.recipeImage = recipeImage;
    }

    public void setRecipeIngredients(List<Ingredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public void setRecipeServing(int recipeServing) {
        this.recipeServing = recipeServing;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public int getRecipeServing() {
        return recipeServing;
    }

    public List<Ingredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public String getRecipeImage() {
        return recipeImage;
    }

    public String getRecipeName() {
        return recipeName;
    }
}
