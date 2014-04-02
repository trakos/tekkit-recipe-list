package pl.trakos.TekkitRecipeList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import pl.trakos.TekkitRecipeList.sql.DaoFactory;
import pl.trakos.TekkitRecipeList.sql.entities.Item;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListDataRow
{
    public int id1;
    public int id2;
    public String text;
    public Drawable bitmapDrawable;

    public ListDataRow(int id1, int id2, String text, Drawable bitmapDrawable)
    {
        this.id1 = id1;
        this.id2 = id2;
        this.text = text;
        this.bitmapDrawable = bitmapDrawable;
    }

    public ListDataRow(String text)
    {
        this.text = text;
    }

    public static Collection<ListDataRow> fromStrings(String[] modList)
    {
        ArrayList<ListDataRow> dataRows = new ArrayList<ListDataRow>(modList.length);
        for (String mod : modList)
        {
            dataRows.add(new ListDataRow(mod));
        }
        return dataRows;
    }

    public static ArrayList<ListDataRow> fromMods(String[] modList, Context context)
    {
        ArrayList<ListDataRow> dataRows = new ArrayList<ListDataRow>(modList.length);
        for (String mod : modList)
        {
            Drawable drawable = null;
            try
            {
                Item item = DaoFactory.getDaoFactory().items.getModRepresentative(mod);
                drawable = Drawable.createFromStream(context.getAssets().open("icons/" + item.item_icon), null);
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
            catch (IOException e)
            {
                Log.e("icon_not_found", e.getMessage());
            }
            dataRows.add(new ListDataRow(0, 0, mod, drawable));
        }
        return dataRows;
    }

    public static Collection<ListDataRow> fromItems(List<Item> items, Context context)
    {
        ArrayList<ListDataRow> dataRows = new ArrayList<ListDataRow>(items.size());
        for (Item item : items)
        {
            Drawable drawable = null;
            try
            {
                drawable = Drawable.createFromStream(context.getAssets().open("icons/" + item.item_icon), null);
            }
            catch (Exception e)
            {
                Log.e("icon_not_found", e.getMessage());
            }
            dataRows.add(new ListDataRow(item.item_id, item.item_damage, item.item_name, drawable));
        }
        return dataRows;
    }

    public static Collection<ListDataRow> fromCategories(String[] categoriesList, String modName, Context context)
    {
        ArrayList<ListDataRow> dataRows = new ArrayList<ListDataRow>(categoriesList.length);
        for (String categoryName : categoriesList)
        {
            Drawable drawable = null;
            try
            {
                Item item = DaoFactory.getDaoFactory().items.getCategoryModRepresentative(modName, categoryName);
                drawable = Drawable.createFromStream(context.getAssets().open("icons/" + item.item_icon), null);
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
            catch (IOException e)
            {
                Log.e("icon_not_found", e.getMessage());
            }
            dataRows.add(new ListDataRow(0, 0, categoryName, drawable));
        }
        return dataRows;
    }
}
