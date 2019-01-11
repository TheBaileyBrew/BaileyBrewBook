package com.thebaileybrew.baileybrewbook.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.thebaileybrew.baileybrewbook.R;
import com.thebaileybrew.baileybrewbook.database.models.Recipe;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class RecipeCardRecycler extends RecyclerView.Adapter<RecipeCardRecycler.ViewHolder> {
    // --Commented out by Inspection (1/11/2019 1:06 PM):private static final String TAG = RecipeCardRecycler.class.getSimpleName();

    private final LayoutInflater layoutInflater;
    private List<Recipe> recipeCollection;

    final private RecipeClickHandler recipeClickHandler;

    public interface RecipeClickHandler {
        void onClick(View view, Recipe recipe);
    }

    public RecipeCardRecycler(Context context, List<Recipe> recipeCollection, RecipeClickHandler recipeClickHandler, Boolean mTwoPane) {
        this.layoutInflater = LayoutInflater.from(context);
        this.recipeClickHandler = recipeClickHandler;
        this.recipeCollection = recipeCollection;
        Boolean mTwoPane1 = mTwoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recipe_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Recipe recipe = recipeCollection.get(position);
        viewHolder.recipeTitle.setText(recipe.getRecipeName());
        viewHolder.recipeServings.setText(String.valueOf(recipe.getRecipeServing()));

    }

    public void setRecipeCollection(List<Recipe> recipeCollection) {
        this.recipeCollection = recipeCollection;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (recipeCollection == null) {
            return 0;
        } else {
            return recipeCollection.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView recipeServings;
        final TextView recipeTitle;

        private ViewHolder(View recipeView) {
            super(recipeView);
            recipeServings = recipeView.findViewById(R.id.recipe_servings);
            recipeTitle = recipeView.findViewById(R.id.recipe_title);
            recipeView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Recipe currentRecipe = recipeCollection.get(getAdapterPosition());
            recipeClickHandler.onClick(v, currentRecipe);
        }
    }
}
