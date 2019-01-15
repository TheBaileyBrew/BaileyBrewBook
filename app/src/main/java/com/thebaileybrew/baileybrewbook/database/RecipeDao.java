package com.thebaileybrew.baileybrewbook.database;



import com.thebaileybrew.baileybrewbook.database.models.Recipe;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipes ORDER BY recipe_name")
    List<Recipe> getRecipes();

    @Query("SELECT * FROM recipes WHERE recipe_id = :id")
    Recipe getSingleRecipe(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe recipe);

}
