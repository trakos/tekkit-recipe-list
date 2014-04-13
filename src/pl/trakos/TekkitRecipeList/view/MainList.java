package pl.trakos.TekkitRecipeList.view;

import android.graphics.drawable.ColorDrawable;
import android.widget.AbsListView;
import android.widget.ListView;
import pl.trakos.TekkitRecipeList.ListDataAdapter;
import pl.trakos.TekkitRecipeList.ListDataRow;
import pl.trakos.TekkitRecipeList.R;
import pl.trakos.TekkitRecipeList.TekkitRecipeListActivity;
import pl.trakos.TekkitRecipeList.navigation.NavigationHistoryEntry;
import pl.trakos.TekkitRecipeList.navigation.NavigationLevels;
import pl.trakos.TekkitRecipeList.sql.DaoFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MainList extends ListView
{
    private final ListDataAdapter adapter;
    TekkitRecipeListActivity activity;
    public ArrayList<ListDataRow> modList;
    private Collection<ListDataRow> preparedData;

    public MainList(TekkitRecipeListActivity context)
    {
        super(context);
        this.activity = context;
        try
        {
            modList = ListDataRow.fromMods(DaoFactory.getDaoFactory(context).items.getModList(), context);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        this.adapter = new ListDataAdapter(context, (ArrayList<ListDataRow>) modList.clone());


        setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        setBackgroundDrawable(new ColorDrawable(0xFFEEEEEE));
        setAdapter(adapter);
    }

    public void usePreparedLevel(NavigationHistoryEntry current)
    {
        NavigationLevels changeToLevel = current.navigationLevel;

        if (changeToLevel.ordinal() > 0)
        {
            activity.getSupportActionBar().setDisplayUseLogoEnabled(false);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else
        {
            activity.getSupportActionBar().setDisplayUseLogoEnabled(false);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        adapter.changeData(preparedData);
        setSelectionFromTop(current.scrollFirstVisiblePosition, current.scrollTopIndex);
        activity.setTitle(activity.title);
    }

    public void prepareLevel(NavigationHistoryEntry current)
    {
        NavigationLevels changeToLevel = current.navigationLevel;
        ListDataRow selectedEntry = current.getCurrentEntry();

        try
        {
            switch (changeToLevel)
            {
                case LEVEL_MODS:
                    preparedData = modList;
                    activity.title = getResources().getString(R.string.app_name);
                    break;
                case LEVEL_CATEGORIES:
                    preparedData = ListDataRow.fromCategories(DaoFactory.getDaoFactory().items.getCategoriesList(selectedEntry.text), selectedEntry.text, getContext());
                    activity.title = selectedEntry.text;
                    break;
                case LEVEL_ITEMS:
                    ListDataRow modItem = current.selectedEntries[NavigationLevels.LEVEL_MODS.ordinal() + 1];
                    preparedData = ListDataRow.fromItems(DaoFactory.getDaoFactory().items.queryForEq("item_mod", modItem.text, "item_category_name", selectedEntry.text), getContext());
                    activity.title = modItem.text + "/" + selectedEntry.text;
                    break;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }


}
