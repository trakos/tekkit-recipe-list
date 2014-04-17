package pl.trakos.TekkitRecipeList.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayout;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import pl.trakos.TekkitRecipeList.Preferences;
import pl.trakos.TekkitRecipeList.R;
import pl.trakos.TekkitRecipeList.sql.DaoFactory;
import pl.trakos.TekkitRecipeList.sql.entities.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

        String query = "";
        try
        {
            if (item.item_mod.indexOf('|') != -1)
            {
                item.item_mod = item.item_mod.substring(0, item.item_mod.indexOf('|'));
            }
            query = URLEncoder.encode(item.item_name + " " + item.item_mod, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
        TextView linkText = generateSubtitle(context, 1, Html.fromHtml("<a href=\"https://www.google.pl/search?q=" + query + "\">tap here to google for description</a>"));
        linkText.setMovementMethod(LinkMovementMethod.getInstance());
        linearLayout.addView(linkText);

        ImageView imageView = new ImageView(context);
        try
        {
            imageView.setImageDrawable(Drawable.createFromStream(context.getAssets().open("icons_full/" + item.item_icon), null));
        }
        catch (IOException e)
        {
            Log.e("image_not_found", e.getMessage());
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(imageView);

        linearLayout.addView(generateSubtitle(context, 1, context.getResources().getString(R.string.recipes_crafting_item)));
        linearLayout.addView(generateRecipes(context, item, "result", onItemTouchEvent));

        linearLayout.addView(generateSubtitle(context, 1, context.getResources().getString(R.string.recipes_obtaining)));
        linearLayout.addView(generateRecipes(context, item, "ingredient", onItemTouchEvent));

        linearLayout.addView(generateSubtitle(context, 1, context.getResources().getString(R.string.recipes_other)));
        linearLayout.addView(generateRecipes(context, item, "other", onItemTouchEvent));

        return linearLayout;
    }

    static float minColWidth = 400;
    private static View generateRecipes(Activity context, Item item, String type, View.OnClickListener onItemTouchEvent)
    {
        Display display = context.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        int columnCount = Preferences.getInstance(context).getCurrentColumnCount();//(int) Math.max(1, Math.floor(width / minColWidth));
        int columnWidth = width / columnCount;
        float scale = columnWidth / 512f;

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
            return generateSubtitle(context, scale, String.format(context.getResources().getString(R.string.too_much_recipes), recipes.size()));
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

    private static TextView generateSubtitle(Activity context, float scale, String text)
    {
        return generateSubtitle(context, scale, text, null);
    }

    private static TextView generateSubtitle(Activity context, int scale, Spanned spanned)
    {
        return generateSubtitle(context, scale, spanned, null);
    }

    private static TextView generateSubtitle(Activity context, float scale, String text, Integer color)
    {
        TextView titleText = new TextView(context);
        titleText.setText(text);
        titleText.setTextSize(scale * 15);
        titleText.setGravity(Gravity.CENTER);
        titleText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if (color != null)
        {
            titleText.setTextColor(color);
        }
        return titleText;
    }

    private static TextView generateSubtitle(Activity context, float scale, Spanned spanned, Integer color)
    {
        TextView titleText = generateSubtitle(context, scale, "", color);
        titleText.setText(spanned);
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
        Drawable recipeBackgroundDrawable = RecipeBackgrounds.guiBackgrounds.get(recipeHandler.handler_image);

        int height = (int)(scale * (isSquareSizedRecipe ? 512 : 289));

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (512 * scale), height));
        frameLayout.addView(getImageView(context, backgroundDrawable, 512, scale, 0, 0));
        frameLayout.addView(getImageView(context, recipeBackgroundDrawable, 512, scale, 6, 44));

        TextView textView = generateSubtitle(context, scale, recipeHandler.handler_name, Color.BLACK);
        textView.setPadding(0, (int) (scale *  15), 0, 0);
        frameLayout.addView(textView);

        for (RecipeIngredient recipeIngredient : ingredientListDictionary.keySet())
        {
            List<Item> items = ingredientListDictionary.get(recipeIngredient);
            // @todo: mryganie pomiedzy opcjami
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
            if (recipeIngredient.ingredient_amount > 1)
            {
                TextView amountText = generateSubtitle(context, scale, Integer.toString((int) recipeIngredient.ingredient_amount), Color.WHITE);
                amountText.setPadding((int) (scale * (recipeIngredient.ingredient_x * 2.9f + 22 + 28)), (int) (scale * (recipeIngredient.ingredient_y * 2.9f + 46 + 24)), 0, 0);
                amountText.setGravity(Gravity.LEFT);
                frameLayout.addView(amountText);
            }
        }

        TextView textView2 = generateSubtitle(context, scale, context.getResources().getString(R.string.tap_ingredient_to_view), Color.GRAY);
        textView2.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
        textView2.setPadding(0, (int) (scale * 15), 0, (int) (scale * 15));
        frameLayout.addView(textView2);

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
