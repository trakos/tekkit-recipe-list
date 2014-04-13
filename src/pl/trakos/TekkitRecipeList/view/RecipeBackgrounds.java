package pl.trakos.TekkitRecipeList.view;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.HashMap;

public class RecipeBackgrounds
{
    static public HashMap<String, Drawable> guiBackgrounds = new HashMap<String, Drawable>();

    static public void load(Context context)
    {
        try
        {
            for (String guiBackground : context.getAssets().list("gui_backgrounds"))
            {
                guiBackgrounds.put(guiBackground, Drawable.createFromStream(context.getAssets().open("gui_backgrounds/" + guiBackground), null));
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }
}
