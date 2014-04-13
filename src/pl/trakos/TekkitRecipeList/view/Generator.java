package pl.trakos.TekkitRecipeList.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import pl.trakos.TekkitRecipeList.R;
import pl.trakos.TekkitRecipeList.sql.DaoFactory;
import pl.trakos.TekkitRecipeList.sql.entities.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Generator
{
    public static class ItemImageView extends ImageView
    {
        public Item item;

        public ItemImageView(Context context, Item item)
        {
            super(context);
            this.item = item;
        }
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static int generateViewId()
    {
        while (true)
        {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF)
            {
                newValue = 1; // Roll over to 1, not 0.
            }
            if (sNextGeneratedId.compareAndSet(result, newValue))
            {
                return result;
            }
        }
    }

    static public View getItemPage(Activity context, Item item, View.OnClickListener onItemTouchEvent)
    {
        loadedAssets = new HashMap<String, Drawable>();

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        TextView titleText = new TextView(context);
        titleText.setText(item.item_name);
        titleText.setTextSize(30);
        titleText.setGravity(Gravity.CENTER);
        titleText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(titleText);

        ImageView imageView = new ImageView(context);
        try
        {
            imageView.setImageDrawable(Drawable.createFromStream(context.getAssets().open("icons/" + item.item_icon), null));
        }
        catch (IOException e)
        {
            Log.e("image_not_found", e.getMessage());
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(imageView);

        linearLayout.addView(generateSubtitle(context, context.getResources().getString(R.string.recipes_crafting_item)));
        linearLayout.addView(generateRecipes(context, item, "result", onItemTouchEvent));

        linearLayout.addView(generateSubtitle(context, context.getResources().getString(R.string.recipes_obtaining)));
        linearLayout.addView(generateRecipes(context, item, "ingredient", onItemTouchEvent));

        linearLayout.addView(generateSubtitle(context, context.getResources().getString(R.string.recipes_other)));
        linearLayout.addView(generateRecipes(context, item, "other", onItemTouchEvent));

        return linearLayout;
    }

    static final float minColWidth = 350;
    private static View generateRecipes(Activity context, Item item, String type, View.OnClickListener onItemTouchEvent)
    {
        Display display = context.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        int columnCount = (int) Math.max(1, Math.floor(width / minColWidth));
        int columnWidth = width / columnCount;
        float scale = columnWidth / (float)context.getResources().getDrawable(R.drawable.recipebg).getIntrinsicWidth();

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        Collection<Recipe> recipes;
        try
        {
            recipes = DaoFactory.getDaoFactory().itemRecipes.whereItemIs(item, type);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        if (recipes.size() == 0) {
            return linearLayout;
        }
        if (recipes.size() > 50)
        {
            return generateSubtitle(context, String.format(context.getResources().getString(R.string.too_much_recipes), recipes.size()));
        }
        int k = 0;
        int itemsPerGrid = 5 * columnCount;
        GridLayout gridLayout = null;
        for (Recipe recipe : recipes)
        {
            if (k % itemsPerGrid == 0) {
                if (gridLayout != null) {
                    linearLayout.addView(gridLayout);
                }
                gridLayout = new GridLayout(context);
                //gridLayout.setOrientation(LinearLayout.VERTICAL);
                gridLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                gridLayout.setColumnCount(columnCount);
            }

            gridLayout.addView(generateRecipe(context, recipe, scale, onItemTouchEvent));

            k++;
        }
        linearLayout.addView(gridLayout);
        return linearLayout;
    }

    private static TextView generateSubtitle(Activity context, String text)
    {
        TextView titleText = new TextView(context);
        titleText.setText(text);
        titleText.setTextSize(15);
        titleText.setGravity(Gravity.CENTER);
        titleText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return titleText;
    }

    private static View generateRecipe(Activity context, Recipe recipe, float scale, View.OnClickListener onItemTouchEvent)
    {
        Handler recipeHandler;
        Map<RecipeIngredient, List<Item>> ingredientListDictionary = new HashMap<RecipeIngredient, List<Item>>();
        try
        {
            recipeHandler = DaoFactory.getDaoFactory().handlers.queryForId(recipe.recipe_handler_id);
            List<RecipeIngredient> recipes = DaoFactory.getDaoFactory().itemRecipeIngredients.queryForEq("ingredient_recipe_id", recipe.recipe_id);
            for (RecipeIngredient recipeIngredient : recipes)
            {
                List<RecipeIngredientOption> ingredientOptions = DaoFactory.getDaoFactory().itemRecipeIngredientOptions.queryForEq("option_ingredient_id", recipeIngredient.ingredient_id);
                ArrayList<Item> items = new ArrayList<Item>(ingredientOptions.size());
                for (RecipeIngredientOption ingredientOption : ingredientOptions)
                {
                    items.add(DaoFactory.getDaoFactory().items.queryForId(ingredientOption.option_item_id, ingredientOption.option_item_damage));
                }
                ingredientListDictionary.put(recipeIngredient, items);
            }

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        // galacticraft
        boolean isSquareSizedRecipe = recipeHandler.handler_id.startsWith("micdoodle8");

        Drawable backgroundDrawable = context.getResources().getDrawable(isSquareSizedRecipe ? R.drawable.recipebg_square : R.drawable.recipebg);
        Drawable recipeBackgroundDrawable = getAssetDrawable(context, "gui_backgrounds/" + recipeHandler.handler_image);

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (512 * scale), (int)(scale * (isSquareSizedRecipe ? 512 : 289))));
        frameLayout.addView(getImageView(context, backgroundDrawable, 512, scale, 0, 0));
        frameLayout.addView(getImageView(context, recipeBackgroundDrawable, 512, scale, 6, 44));

        TextView textView = generateSubtitle(context, recipeHandler.handler_name);
        textView.setPadding(0, (int) (scale *  15), 0, 0);
        frameLayout.addView(textView);

        for (RecipeIngredient recipeIngredient : ingredientListDictionary.keySet())
        {
            List<Item> items = ingredientListDictionary.get(recipeIngredient);
            // @todo: mryganie pomiedzy opcjami
            // @todo: amount
            Item item = items.get(0);
            Drawable iconDrawable;
            if (item != null)
            {
                iconDrawable = getAssetDrawable(context, "icons/" + item.item_icon);
            }
            else
            {
                iconDrawable = context.getResources().getDrawable(R.drawable.noicon);
            }
            View itemImageView = getImageView(context, iconDrawable, 40, scale, (int) (recipeIngredient.ingredient_x * 2.9f + 22), (int) (recipeIngredient.ingredient_y * 2.9f + 46), item, onItemTouchEvent);
            frameLayout.addView(itemImageView);
        }


        return frameLayout;
    }

    static View getImageView(Context context, Drawable drawable, int drawableWidth, float scale, int marginLeft, int marginTop)
    {
        return getImageView(context, drawable, drawableWidth, scale, marginLeft, marginTop, null, null);
    }

    static View getImageView(Context context, Drawable drawable, int drawableWidth, float scale, int marginLeft, int marginTop, Item item, View.OnClickListener onItemTouchEvent)
    {
        float imageScale = drawableWidth / (float)drawable.getIntrinsicWidth();


        ImageView imageView = new ItemImageView(context, item);
        imageView.setLayoutParams(new FrameLayout.LayoutParams((int) (imageScale * scale * drawable.getIntrinsicWidth()), (int) (imageScale * scale * drawable.getIntrinsicHeight())));
        imageView.setImageDrawable(drawable);
        if (onItemTouchEvent != null)
        {
            imageView.setOnClickListener(onItemTouchEvent);
        }
        
        FrameLayout frameLayoutPaddingContainer = new FrameLayout(context);
        frameLayoutPaddingContainer.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        frameLayoutPaddingContainer.setPadding((int) (marginLeft * scale), (int) (marginTop * scale), 0, 0);
        frameLayoutPaddingContainer.addView(imageView);
        return frameLayoutPaddingContainer;
    }

    static Map<String, Drawable> loadedAssets = new HashMap<String, Drawable>();

    static Drawable getAssetDrawable(Context context, String path)
    {
        if (!loadedAssets.containsKey(path))
        {
            try
            {
                loadedAssets.put(path, Drawable.createFromStream(context.getAssets().open(path), null));
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        return loadedAssets.get(path);
    }
}
