package pl.trakos.TekkitRecipeList.view;

import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import pl.trakos.TekkitRecipeList.TekkitRecipeListActivity;

public class ItemsSearch extends SearchView
{
    final TekkitRecipeListActivity activity;

    public ItemsSearch(TekkitRecipeListActivity context)
    {
        super(context);
        activity = context;
        init();
    }

    public ItemsSearch(TekkitRecipeListActivity context, AttributeSet attrs)
    {
        super(context, attrs);
        activity = context;
        init();
    }

    private void init()
    {
        setOnQueryTextListener(new OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                activity.searchForItem(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                return false;
            }
        });
    }
}
