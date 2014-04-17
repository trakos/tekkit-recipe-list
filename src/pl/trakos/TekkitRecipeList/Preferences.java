package pl.trakos.TekkitRecipeList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

public class Preferences
{
    static Preferences instance;

    public static Preferences getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new Preferences(context);
        }
        return instance;
    }

    public static Preferences getInstance()
    {
        if (instance == null)
        {
            throw new RuntimeException("Preferences not yet initialized!");
        }
        return instance;
    }

    public Preferences(Context context)
    {
        this.context = context;
        loadOptions();
    }

    Context context;

    public boolean isLandscape()
    {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public int getCurrentColumnCount()
    {
        return isLandscape() ? columnsLandscape : columnsPortrait;
    }

    public void setCurrentColumnCount(int count)
    {
        if (isLandscape())
        {
            columnsLandscape = count;
        }
        else
        {
            columnsPortrait = count;
        }
        saveOptions();
    }

    public void saveOptions()
    {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES, 0);
        settings.edit()
                .putInt(PREF_LANDSCAPE_COLUMN_COUNT, columnsLandscape)
                .putInt(PREF_PORTRAIT_COLUMN_COUNT, columnsPortrait)
                .commit();
    }

    public void loadOptions()
    {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES, 0);
        columnsLandscape = settings.getInt(PREF_LANDSCAPE_COLUMN_COUNT, 1);
        columnsPortrait = settings.getInt(PREF_LANDSCAPE_COLUMN_COUNT, 1);
    }

    public final String PREFERENCES = "TekkitRecipeList";
    public final String PREF_LANDSCAPE_COLUMN_COUNT = "column_count_landscape";
    public final String PREF_PORTRAIT_COLUMN_COUNT = "column_count_portait";

    public int columnsPortrait;
    public int columnsLandscape;
}
