package pl.trakos.TekkitRecipeList;

import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import pl.trakos.TekkitRecipeList.sql.DaoFactory;

import java.sql.SQLException;

@SuppressWarnings("deprecation")
public class TekkitRecipeListActivity extends ActionBarActivity
{
    static enum NavigationLevels
    {
        LEVEL_MODS,
        LEVEL_CATEGORIES,
        LEVEL_ITEMS
    }

    public NavigationLevels navigationLevel = NavigationLevels.LEVEL_MODS;
    public ListDataRow[] selectedItems = new ListDataRow[NavigationLevels.values().length];

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
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.show();

        drawerLayout.setLayoutParams(new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT));
        frameLayout.setLayoutParams(new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT));
        listView.setLayoutParams(new DrawerLayout.LayoutParams(320, DrawerLayout.LayoutParams.MATCH_PARENT, Gravity.START));

        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setBackgroundDrawable(new ColorDrawable(0xFFEEEEEE));
        try
        {
            listView.setAdapter(new ListDataAdapter(this, DaoFactory.getDaoFactory().items.getModList()));
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
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
        //
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        boolean drawerOpen = drawerLayout.isDrawerOpen(listView);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
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
                listView.setItemChecked(position, true);
                title = itemPosition.text;
                drawerLayout.closeDrawer(listView);

                // show item?
            }
            else
            {
                showLevel(NavigationLevels.values()[navigationLevel.ordinal()+1], itemPosition);
            }
            setTitle(title);
        }
    }

    void showLevel(NavigationLevels changeToLevel, ListDataRow selectedRow)
    {
        ListDataAdapter adapter = (ListDataAdapter) listView.getAdapter();
        if (changeToLevel.ordinal() > 0)
        {
            if (selectedRow == null)
            {
                selectedRow = selectedItems[changeToLevel.ordinal() - 1];
            }
            else
            {
                selectedItems[changeToLevel.ordinal() - 1] = selectedRow;
            }
        }

        navigationLevel = changeToLevel;

        try
        {
            switch (changeToLevel)
            {
                case LEVEL_MODS:
                    adapter.changeData(ListDataRow.fromStrings(DaoFactory.getDaoFactory().items.getModList()));
                    title = getResources().getString(R.string.app_name);
                    break;
                case LEVEL_CATEGORIES:
                    adapter.changeData(ListDataRow.fromStrings(DaoFactory.getDaoFactory().items.getCategoriesList(selectedRow.text)));
                    title = selectedRow.text;
                    break;
                case LEVEL_ITEMS:
                    ListDataRow modItem = selectedItems[NavigationLevels.LEVEL_MODS.ordinal()];
                    adapter.changeData(ListDataRow.fromItems(DaoFactory.getDaoFactory().items.queryForEq("item_mod", modItem.text, "item_category_name", selectedRow.text)));
                    break;
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        setTitle(title);
    }

    @Override
    public void onBackPressed()
    {
        if (!drawerLayout.isDrawerOpen(listView) && navigationLevel != NavigationLevels.LEVEL_MODS)
        {
            drawerLayout.openDrawer(listView);
        }
        else if (drawerLayout.isDrawerOpen(listView) && navigationLevel == NavigationLevels.LEVEL_MODS)
        {
            drawerLayout.closeDrawer(listView);
        }
        else if (navigationLevel == NavigationLevels.LEVEL_MODS)
        {
            super.onBackPressed();
        }
        else
        {
            showLevel(NavigationLevels.values()[navigationLevel.ordinal() - 1], null);
        }
    }
}
