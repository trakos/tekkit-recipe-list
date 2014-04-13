package pl.trakos.TekkitRecipeList.navigation;

import java.io.Serializable;

public enum NavigationLevels implements Serializable
{
    LEVEL_MODS,
    LEVEL_CATEGORIES,
    LEVEL_ITEMS,
    LEVEL_ITEM;

    public static NavigationLevels next(NavigationLevels current)
    {
        if (current == LEVEL_ITEM)
        {
            throw new RuntimeException("current = LEVEL_ITEM");
        }
        return NavigationLevels.values()[current.ordinal() + 1];
    }

    public static NavigationLevels prev(NavigationLevels current)
    {
        if (current == LEVEL_MODS)
        {
            throw new RuntimeException("current = LEVEL_MODS");
        }
        return NavigationLevels.values()[current.ordinal() - 1];
    }
}
