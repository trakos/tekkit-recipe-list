package pl.trakos.TekkitRecipeList;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import pl.trakos.TekkitRecipeList.navigation.NavigationHistory;
import pl.trakos.TekkitRecipeList.navigation.NavigationHistoryEntry;
import pl.trakos.TekkitRecipeList.navigation.NavigationLevels;
import pl.trakos.TekkitRecipeList.sql.DaoFactory;
import pl.trakos.TekkitRecipeList.sql.entities.Item;
import pl.trakos.TekkitRecipeList.view.RecipeBackgrounds;
import pl.trakos.TekkitRecipeList.view.ItemLayout;
import pl.trakos.TekkitRecipeList.view.MainList;

@SuppressWarnings("deprecation")
public class TekkitRecipeListActivity extends ActionBarActivity
{
    public NavigationHistory historyStack = new NavigationHistory();

    FrameLayout rootLayout;
    ItemLayout itemLayout;
    MainList listView;
    public String title = "test";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaoFactory.getDaoFactory(this);
        RecipeBackgrounds.load(this);

        rootLayout = new FrameLayout(this);
        itemLayout = new ItemLayout(this);
        listView = new MainList(this);

        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT));
        itemLayout.setLayoutParams(new FrameLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT));
        listView.setLayoutParams(new FrameLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT));

        rootLayout.addView(listView);
        rootLayout.addView(itemLayout);
        setContentView(rootLayout);


        listView.setOnItemClickListener(new MainList.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                NavigationHistoryEntry current = historyStack.peek();
                NavigationHistoryEntry newEntry = new NavigationHistoryEntry();
                newEntry.navigationLevel = current.navigationLevel != NavigationLevels.LEVEL_ITEM ? NavigationLevels.next(current.navigationLevel) : NavigationLevels.LEVEL_ITEM;
                newEntry.selectedEntries = current.selectedEntries.clone();
                newEntry.selectedEntries[newEntry.navigationLevel.ordinal()] = ((ListDataAdapter) listView.getAdapter()).getItem(position).cloneWithoutDrawable();

                historyStack.push(newEntry);
                displayView(newEntry);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                historyStack.peek().scrollFirstVisiblePosition = view.getFirstVisiblePosition();
                historyStack.peek().scrollTopIndex = view.getChildAt(0) != null ? view.getChildAt(0).getTop() : 0;
            }
        });

        displayView(historyStack.peek());
    }

    public void pushToHistoryAndShow(Item item)
    {
        NavigationHistoryEntry newEntry = new NavigationHistoryEntry();
        newEntry.navigationLevel = NavigationLevels.LEVEL_ITEM;
        newEntry.selectedEntries = new ListDataRow[NavigationLevels.values().length];
        newEntry.selectedEntries[NavigationLevels.LEVEL_CATEGORIES.ordinal()] = new ListDataRow(item.item_mod);
        newEntry.selectedEntries[NavigationLevels.LEVEL_ITEMS.ordinal()] = new ListDataRow(item.item_category_name);
        newEntry.selectedEntries[NavigationLevels.LEVEL_ITEM.ordinal()] = new ListDataRow(item.item_id, item.item_damage, item.item_name, null, this);

        historyStack.push(newEntry);
        displayView(newEntry);
    }

    android.os.Handler mHandler = new android.os.Handler();
    public void displayView(final NavigationHistoryEntry current)
    {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "loading", "please wait...", true);
        new Thread()
        {
            @Override
            public void run()
            {
                if (current.navigationLevel == NavigationLevels.LEVEL_ITEM)
                {
                    itemLayout.prepareItem(current.getCurrentEntry().id1, current.getCurrentEntry().id2);
                }
                else
                {
                    listView.prepareLevel(current);
                }
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (current.navigationLevel == NavigationLevels.LEVEL_ITEM)
                        {
                            listView.setVisibility(View.GONE);
                            itemLayout.setVisibility(View.VISIBLE);
                            itemLayout.showPreparedItem();
                        }
                        else
                        {
                            listView.setVisibility(View.VISIBLE);
                            itemLayout.setVisibility(View.GONE);
                            listView.usePreparedLevel(current);
                        }
                        progressDialog.hide();
                    }
                });
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                goUp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putSerializable("history", historyStack);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        historyStack = (NavigationHistory) savedInstanceState.getSerializable("history");
        displayView(historyStack.peek());
    }

    @Override
    public void onBackPressed()
    {
        if (historyStack.empty())
        {
            super.onBackPressed();
        }
        else
        {
            goBack();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_MENU:
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    public void goBack()
    {
        historyStack.pop();
        displayView(historyStack.peek());
    }

    public void goUp()
    {
        NavigationHistoryEntry current = historyStack.peek();
        if (current.navigationLevel == NavigationLevels.LEVEL_MODS)
        {
            return;
        }
        NavigationHistoryEntry newEntry = new NavigationHistoryEntry(NavigationLevels.prev(current.navigationLevel), 0, 0, current.selectedEntries.clone());
        NavigationHistoryEntry lastSameLevel = historyStack.findMostRecentOnLevel(newEntry.navigationLevel);
        if (lastSameLevel != null && (lastSameLevel.getCurrentEntry() == null || lastSameLevel.getCurrentEntry().equals(newEntry.getCurrentEntry())))
        {
            newEntry.scrollFirstVisiblePosition = lastSameLevel.scrollFirstVisiblePosition;
            newEntry.scrollTopIndex = lastSameLevel.scrollTopIndex;
        }
        historyStack.push(newEntry);
        displayView(newEntry);
    }
}
