package com.thebaileybrew.baileybrewbook.database.models;

public class Ingredient {

    private Double ingredientQuantity;
    private String ingredientMeasure;
    private String ingredientName;

    public void setIngredientMeasure(String ingredientMeasure) {
        this.ingredientMeasure = ingredientMeasure;
    }

    public String getIngredientMeasure() {
        return ingredientMeasure;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientQuantity(Double ingredientQuantity) {
        this.ingredientQuantity = ingredientQuantity;
    }

    public Double getIngredientQuantity() {
        return ingredientQuantity;
    }
}
