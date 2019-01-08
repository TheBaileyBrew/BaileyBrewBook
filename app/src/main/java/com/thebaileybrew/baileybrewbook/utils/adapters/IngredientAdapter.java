package com.thebaileybrew.baileybrewbook.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thebaileybrew.baileybrewbook.R;
import com.thebaileybrew.baileybrewbook.database.models.Ingredient;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private static final String TAG = IngredientAdapter.class.getSimpleName();

    private final LayoutInflater layoutInflater;
    private List<Ingredient> ingredients;

    public IngredientAdapter(Context context, List<Ingredient> ingredients) {
        this.layoutInflater = LayoutInflater.from(context);
        this.ingredients = ingredients;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.ingredient_list_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final Ingredient ingredient = ingredients.get(position);
        String ingName = ingredient.getIngredientName();
        String ingMeasure = ingredient.getIngredientMeasure();
        String ingQty = String.valueOf(ingredient.getIngredientQuantity());
        viewHolder.ingredientName.setText(ingName);
        viewHolder.ingredientMeasure.setText(measurementConversion(ingMeasure, ingQty));
        viewHolder.ingredientQuantity.setText(ingQty);
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (ingredients == null) {
            return 0;
        } else {
            return ingredients.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView ingredientName;
        final TextView ingredientMeasure;
        final TextView ingredientQuantity;

        private ViewHolder(View ingredientView) {
            super(ingredientView);
            ingredientName= ingredientView.findViewById(R.id.ingredient_name);
            ingredientMeasure = ingredientView.findViewById(R.id.ingredient_measurement);
            ingredientQuantity = ingredientView.findViewById(R.id.ingredient_quantity);
        }
    }

    private String measurementConversion(String measure, String qty) {
        String newMeasure;
        float quantity = Float.parseFloat(qty);
        switch(measure) {
            case "CUP":
                if (quantity > 1.0) {
                    newMeasure = "Cups";
                } else {
                    newMeasure = "Cup";
                }
                newMeasure = newMeasure + " (C)";
                return newMeasure;
            case "G":
                if (quantity > 1.0) {
                    newMeasure = "Grams";
                } else {
                    newMeasure = "Gram";
                }
                newMeasure = newMeasure + " (G)";
                return newMeasure;
            case "TSP":
                if (quantity > 1.0) {
                    newMeasure = "Teaspoons";
                } else {
                    newMeasure = "Teaspoon";
                }
                newMeasure = newMeasure + " (TSP)";
                return newMeasure;
            case "TBLSP":
                if (quantity > 1.0) {
                    newMeasure = "Tablespoons";
                } else {
                    newMeasure = "Tablespoon";
                }
                newMeasure = newMeasure + " (TBLSP)";
                return newMeasure;
            case "K":
                if (quantity > 1.0) {
                    newMeasure = "Kilograms";
                } else {
                    newMeasure = "Kilogram";
                }
                newMeasure = newMeasure + " (K)";
                return newMeasure;
            case "OZ":
                if (quantity > 1.0) {
                    newMeasure = "Ounces";
                } else {
                    newMeasure = "Ounce";
                }
                newMeasure = newMeasure + " (OZ)";
                return newMeasure;
            case "UNIT":
                if (quantity > 1.0) {
                    newMeasure = "Each";
                } else {
                    newMeasure = "Each";
                }
                newMeasure = newMeasure + " (UNIT / EA)";
                return newMeasure;
            default:
                return measure;
        }
    }
}



    /*
    private static final String TAG = IngredientAdapter.class.getSimpleName();

    private Context context;
    private List<Ingredient> ingredientList;

    public IngredientAdapter(Context context, List<Ingredient> list) {
        super(context, 0, list);
        this.context = context;
        ingredientList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if (listView == null) {
            listView = LayoutInflater.from(context).inflate(R.layout.ingredient_list_view, parent, false);
        }
        Ingredient current = ingredientList.get(position);
        Log.e(TAG, "getView: " + current.getIngredientName() );
        TextView quantityTextView = listView.findViewById(R.id.ingredient_quantity);
        TextView measurementTextView = listView.findViewById(R.id.ingredient_measurement);
        TextView ingredientNameTextView = listView.findViewById(R.id.ingredient_name);

        measurementTextView.setText(measurementConversion(ingMeasure, ingQty));
        quantityTextView.setText(ingQty);
        ingredientNameTextView.setText(ingName);


        return super.getView(position, convertView, parent);
    }



}
*/
