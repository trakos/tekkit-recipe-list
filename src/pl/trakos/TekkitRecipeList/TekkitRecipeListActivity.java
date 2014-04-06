package pl.trakos.TekkitRecipeList;

import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import pl.trakos.TekkitRecipeList.sql.DaoFactory;
import pl.trakos.TekkitRecipeList.sql.entities.Item;
import pl.trakos.TekkitRecipeList.view.Generator;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class TekkitRecipeListActivity extends ActionBarActivity
{
    class Tuple<A, B> implements Serializable
    {
        public final A a;
        public final B b;
        Tuple(A a, B b)
        {
            this.a = a;
            this.b = b;
        }
    }

    static enum NavigationLevels implements Serializable
    {
        LEVEL_MODS,
        LEVEL_CATEGORIES,
        LEVEL_ITEMS
    }

    public NavigationLevels navigationLevel = NavigationLevels.LEVEL_MODS;
    public ListDataRow[] selectedItems = new ListDataRow[NavigationLevels.values().length];
    public Tuple<Integer, Integer>[] currentScrolls = new Tuple[NavigationLevels.values().length];
    public ListDataRow[] pathToCurrentItem = null;
    public Tuple<Integer, Integer>[] pathToCurrentItemScrolls = null;
    public ArrayList<ListDataRow> modList;

    DrawerLayout drawerLayout;
    FrameLayout frameLayout;
    ListView listView;
    String title = "test";
    ActionBarDrawerToggle drawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaoFactory.getDaoFactory(this);
        try
        {
            modList = ListDataRow.fromMods(DaoFactory.getDaoFactory().items.getModList(), this);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        // avoid drawer catching back button listener
        drawerLayout = new DrawerLayout(this)
        {
            @Override
            public boolean onKeyUp(int keyCode, KeyEvent event)
            {
                return keyCode != KeyEvent.KEYCODE_BACK && super.onKeyUp(keyCode, event);
            }

            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event)
            {
                return keyCode != KeyEvent.KEYCODE_BACK && super.onKeyDown(keyCode, event);
            }
        };
        frameLayout = new FrameLayout(this);
        listView = new ListView(this);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close)
        {

            public void onDrawerClosed(View view)
            {
                if (pathToCurrentItem != null)
                {
                    selectedItems = pathToCurrentItem;
                    currentScrolls = pathToCurrentItemScrolls;
                    showLevel(NavigationLevels.LEVEL_ITEMS, null);
                }
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout.setLayoutParams(new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT));
        frameLayout.setLayoutParams(new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT));
        listView.setLayoutParams(new DrawerLayout.LayoutParams(320, DrawerLayout.LayoutParams.MATCH_PARENT, Gravity.LEFT));

        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setBackgroundDrawable(new ColorDrawable(0xFFEEEEEE));
        listView.setAdapter(new ListDataAdapter(this, (ArrayList<ListDataRow>) modList.clone()));
        listView.setOnItemClickListener(new DrawerItemClickListener());

        drawerLayout.addView(frameLayout);
        drawerLayout.addView(listView);
        setContentView(drawerLayout);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        switch (item.getItemId())
        {
            case android.R.id.home:
                goUp();
                /*if (drawerLayout.isDrawerOpen(listView))
                {
                    drawerLayout.closeDrawer(listView);
                    if (selectedItems[NavigationLevels.LEVEL_ITEMS.ordinal()] != null)
                    {
                        navigationLevel = NavigationLevels.LEVEL_ITEMS;
                        title = selectedItems[NavigationLevels.LEVEL_ITEMS.ordinal()].text;
                        setTitle(title);
                    }
                }
                else
                {
                    drawerLayout.openDrawer(listView);
                }*/
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        boolean drawerOpen = drawerLayout.isDrawerOpen(listView);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id)
        {
            ListDataAdapter adapter = (ListDataAdapter) listView.getAdapter();
            ListDataRow itemPosition = adapter.getItem(position);
            if (navigationLevel == NavigationLevels.LEVEL_ITEMS)
            {
                selectedItems[navigationLevel.ordinal()] = itemPosition;
                listView.setItemChecked(position, true);
                title = itemPosition.text;
                drawerLayout.closeDrawer(listView);
                pathToCurrentItem = selectedItems.clone();
                pathToCurrentItemScrolls = currentScrolls.clone();

                showItem(itemPosition.id1, itemPosition.id2);
            }
            else
            {
                showLevel(NavigationLevels.values()[navigationLevel.ordinal()+1], itemPosition);
            }
            setTitle(title);
        }
    }

    private void showItem(int itemId, int itemDamage)
    {
        Item item;
        try
        {
            item = DaoFactory.getDaoFactory().items.queryForId(itemId, itemDamage);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        frameLayout.removeAllViews();
        frameLayout.addView(Generator.getItemPage(this, item));
    }

    void showLevel(NavigationLevels changeToLevel, ListDataRow selectedRow)
    {
        ListDataAdapter adapter = (ListDataAdapter) listView.getAdapter();
        if (changeToLevel.ordinal() > 0)
        {
            getSupportActionBar().setDisplayUseLogoEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            drawerToggle.setDrawerIndicatorEnabled(false);
            if (selectedRow == null)
            {
                selectedRow = selectedItems[changeToLevel.ordinal() - 1];
            }
            else
            {
                selectedItems[changeToLevel.ordinal() - 1] = selectedRow;
            }
        }
        else
        {
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            drawerToggle.setDrawerIndicatorEnabled(true);
        }

        if (navigationLevel.ordinal() <= changeToLevel.ordinal())
        {
            currentScrolls[navigationLevel.ordinal()] = new Tuple<Integer, Integer>(listView.getFirstVisiblePosition(), listView.getChildAt(0) != null ? listView.getChildAt(0).getTop() : 0);
            currentScrolls[changeToLevel.ordinal()] = new Tuple<Integer, Integer>(0, 0);
        }

        navigationLevel = changeToLevel;

        try
        {
            switch (changeToLevel)
            {
                case LEVEL_MODS:
                    adapter.changeData(modList);
                    title = getResources().getString(R.string.app_name);
                    break;
                case LEVEL_CATEGORIES:
                    adapter.changeData(ListDataRow.fromCategories(DaoFactory.getDaoFactory().items.getCategoriesList(selectedRow.text), selectedRow.text, this));
                    title = selectedRow.text;
                    break;
                case LEVEL_ITEMS:
                    ListDataRow modItem = selectedItems[NavigationLevels.LEVEL_MODS.ordinal()];
                    adapter.changeData(ListDataRow.fromItems(DaoFactory.getDaoFactory().items.queryForEq("item_mod", modItem.text, "item_category_name", selectedRow.text), this));
                    break;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        if (currentScrolls[changeToLevel.ordinal()] != null)
        {
            listView.setSelectionFromTop(currentScrolls[changeToLevel.ordinal()].a, currentScrolls[changeToLevel.ordinal()].b);
        }

        setTitle(title);
    }

    @Override
    public void onBackPressed()
    {
        if (navigationLevel == NavigationLevels.LEVEL_MODS && drawerLayout.isDrawerOpen(listView))
        {
            drawerLayout.closeDrawer(listView);
        }
        else if (navigationLevel == NavigationLevels.LEVEL_MODS && !drawerLayout.isDrawerOpen(listView))
        {
            super.onBackPressed();
        }
        else
        {
            goUp();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_MENU:
                if (drawerLayout.isDrawerOpen(listView))
                {
                    drawerLayout.closeDrawer(listView);
                }
                else
                {
                    drawerLayout.openDrawer(listView);
                }
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    public void goUp()
    {
        if (!drawerLayout.isDrawerOpen(listView) && navigationLevel != NavigationLevels.LEVEL_MODS)
        {
            drawerLayout.openDrawer(listView);
        }
        else if (drawerLayout.isDrawerOpen(listView) && navigationLevel == NavigationLevels.LEVEL_MODS)
        {
            drawerLayout.closeDrawer(listView);
        }
        else if (navigationLevel != NavigationLevels.LEVEL_MODS)
        {
            showLevel(NavigationLevels.values()[navigationLevel.ordinal() - 1], null);
        }
    }
}
