package pl.trakos.TekkitRecipeList;

import pl.trakos.TekkitRecipeList.sql.entities.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListDataRow
{
    public int id1;
    public int id2;
    public String text;

    public ListDataRow(int id1, int id2, String text)
    {
        this.id1 = id1;
        this.id2 = id2;
        this.text = text;
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

    public static Collection<ListDataRow> fromItems(List<Item> items)
    {
        ArrayList<ListDataRow> dataRows = new ArrayList<ListDataRow>(items.size());
        for (Item item : items)
        {
            dataRows.add(new ListDataRow(item.item_id, item.item_damage, item.item_name));
        }
        return dataRows;
    }
}
