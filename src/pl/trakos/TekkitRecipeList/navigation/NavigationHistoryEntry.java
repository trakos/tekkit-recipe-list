package pl.trakos.TekkitRecipeList.navigation;

import pl.trakos.TekkitRecipeList.ListDataRow;

import java.io.Serializable;


public class NavigationHistoryEntry implements Serializable
{
    public NavigationLevels navigationLevel;
    public int scrollTopIndex;
    public int scrollFirstVisiblePosition;
    public ListDataRow[] selectedEntries;

    public NavigationHistoryEntry(NavigationLevels navigationLevel, int scrollTopIndex, int scrollLevel, ListDataRow[] selectedEntries)
    {
        this.navigationLevel = navigationLevel;
        this.scrollTopIndex = scrollTopIndex;
        this.scrollFirstVisiblePosition = scrollLevel;
        this.selectedEntries = selectedEntries;
    }

    public NavigationHistoryEntry()
    {

    }

    public ListDataRow getCurrentEntry()
    {
        return selectedEntries[navigationLevel.ordinal()];
    }
}
