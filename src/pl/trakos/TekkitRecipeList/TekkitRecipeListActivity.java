package pl.trakos.TekkitRecipeList;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.widget.*;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import pl.trakos.TekkitRecipeList.navigation.NavigationHistory;
import pl.trakos.TekkitRecipeList.navigation.NavigationHistoryEntry;
import pl.trakos.TekkitRecipeList.navigation.NavigationLevels;
import pl.trakos.TekkitRecipeList.sql.DaoFactory;
import pl.trakos.TekkitRecipeList.sql.entities.Item;
import pl.trakos.TekkitRecipeList.view.ItemLayout;
import pl.trakos.TekkitRecipeList.view.ItemsSearch;
import pl.trakos.TekkitRecipeList.view.MainList;
import pl.trakos.TekkitRecipeList.view.RecipeBackgrounds;

import java.sql.SQLException;

@SuppressWarnings("deprecation")
public class TekkitRecipeListActivity extends ActionBarActivity
{
    public NavigationHistory historyStack = new NavigationHistory();
    final int MENU_ITEM1 = 1;
    final int MENU_ITEM2 = 2;
    public String title = "test";

    FrameLayout rootLayout;
    ItemLayout itemLayout;
    MainList listView;
    SearchView searchView;
    ListPopupWindow listPopupWindow;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.Theme_AppCompat);
        super.onCreate(savedInstanceState);
        DaoFactory.getDaoFactory(this);
        RecipeBackgrounds.load(this);
        Preferences.getInstance(this);

        rootLayout = new FrameLayout(this);
        itemLayout = new ItemLayout(this);
        listView = new MainList(this);
        listPopupWindow = new ListPopupWindow(this);

        listPopupWindow.setAdapter(new ListDataAdapter(this, new String[] { "1 column", "2 columns", "3 columns", "4 columns", "5 columns"}));
        listPopupWindow.setAdapter(new ListDataAdapter(this, new String[] { "1 column", "2 columns", "3 columns", "4 columns", "5 columns"}));

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
                ListDataRow selectedPosition = ((ListDataAdapter) listView.getAdapter()).getItem(position);
                NavigationHistoryEntry current = historyStack.peek();
                if (current.navigationLevel == NavigationLevels.LEVEL_SEARCH)
                {
                    // when searching I cant do it normally (like in else), because it wont have the context to make "going up" work
                    try
                    {
                        pushToHistoryAndShow(DaoFactory.getDaoFactory().items.queryForId(selectedPosition.id1, selectedPosition.id2));
                    }
                    catch (SQLException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                else
                {
                    NavigationHistoryEntry newEntry = new NavigationHistoryEntry();
                    newEntry.navigationLevel = current.navigationLevel != NavigationLevels.LEVEL_ITEM ? NavigationLevels.next(current.navigationLevel) : NavigationLevels.LEVEL_ITEM;
                    newEntry.selectedEntries = current.selectedEntries.clone();
                    newEntry.selectedEntries[newEntry.navigationLevel.ordinal()] = selectedPosition.cloneWithoutDrawable();

                    historyStack.push(newEntry);
                    displayView(newEntry);
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                int columnCount = 1 + (int) id;
                Preferences.getInstance().setCurrentColumnCount(columnCount);
                listPopupWindow.dismiss();
                displayView(historyStack.peek());
            }
        });
        searchView = new ItemsSearch(this);
        menu
                .add(Menu.NONE, MENU_ITEM2, MENU_ITEM1, R.string.adjust_column_count)
                .setIcon(R.drawable.ic_action_settings)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        listPopupWindow.setAnchorView(rootLayout);
                        listPopupWindow.setHeight(Math.min(rootLayout.getHeight(), 300));
                        listPopupWindow.setModal(true);
                        listPopupWindow.show();
                        //listPopupWindow.getListView().setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                        return true;
                    }
                })
                .setShowAsAction(SupportMenuItem.SHOW_AS_ACTION_IF_ROOM | SupportMenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu
                .add(Menu.NONE, MENU_ITEM1, MENU_ITEM2, R.string.search)
                .setIcon(R.drawable.ic_action_search)
                .setActionView(searchView)
                .setShowAsAction(SupportMenuItem.SHOW_AS_ACTION_IF_ROOM | SupportMenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        return super.onCreateOptionsMenu(menu);
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

    public void searchForItem(String queryText)
    {
        NavigationHistoryEntry newEntry = new NavigationHistoryEntry();
        newEntry.navigationLevel = NavigationLevels.LEVEL_SEARCH;
        newEntry.selectedEntries = new ListDataRow[NavigationLevels.values().length];
        newEntry.selectedEntries[NavigationLevels.LEVEL_SEARCH.ordinal()] = new ListDataRow(0, 0,queryText, null, this);

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
