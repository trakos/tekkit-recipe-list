package pl.trakos.TekkitRecipeList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import pl.trakos.TekkitRecipeList.sql.DaoFactory;
import pl.trakos.TekkitRecipeList.sql.entities.Item;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListDataRow implements Serializable
{
    public int id1;
    public int id2;
    public String text;
    String drawablePath;
    public transient Drawable bitmapDrawable;

    protected Object writeReplace() throws ObjectStreamException
    {
        this.bitmapDrawable = null;
        return this;
    }

    protected Object readResolve() throws ObjectStreamException
    {
        /*if (drawablePath != null && !drawablePath.equals(""))
        {
            try
            {
                this.bitmapDrawable = Drawable.createFromStream(context.getAssets().open(drawablePath), null);
            }
            catch (IOException e)
            {
                Log.e("icon_not_found", e.getMessage());
            }
        }*/
        return this;
    }

    public ListDataRow(int id1, int id2, String text, String drawablePath, Context context)
    {
        this.id1 = id1;
        this.id2 = id2;
        this.text = text;
        this.drawablePath = drawablePath;
        if (drawablePath != null)
        {
            try
            {
                bitmapDrawable = Drawable.createFromStream(context.getAssets().open(drawablePath), null);
            }
            catch (IOException e)
            {
                Log.e("icon_not_found", e.getMessage());
            }
        }
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
            Item item;
            try
            {
                item = DaoFactory.getDaoFactory().items.getModRepresentative(mod);
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
            dataRows.add(new ListDataRow(0, 0, mod, "icons/" + item.item_icon, context));
        }
        return dataRows;
    }

    public static Collection<ListDataRow> fromItems(List<Item> items, Context context)
    {
        ArrayList<ListDataRow> dataRows = new ArrayList<ListDataRow>(items.size());
        for (Item item : items)
        {
            dataRows.add(new ListDataRow(item.item_id, item.item_damage, item.item_name, "icons/" + item.item_icon, context));
        }
        return dataRows;
    }

    public static Collection<ListDataRow> fromCategories(String[] categoriesList, String modName, Context context)
    {
        ArrayList<ListDataRow> dataRows = new ArrayList<ListDataRow>(categoriesList.length);
        for (String categoryName : categoriesList)
        {
            Item item;
            try
            {
                item = DaoFactory.getDaoFactory().items.getCategoryModRepresentative(modName, categoryName);
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
            dataRows.add(new ListDataRow(0, 0, categoryName, "icons/" + item.item_icon, context));
        }
        return dataRows;
    }

    public boolean equals(ListDataRow to)
    {
        return this.id1 == to.id1 && this.id2 == to.id2 && (this.text == null || this.text.equals(to.text));
    }

    public ListDataRow cloneWithoutDrawable()
    {
        return new ListDataRow(id1, id2, text, null, null);
    }
}
