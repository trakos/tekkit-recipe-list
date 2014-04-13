package pl.trakos.TekkitRecipeList.view;

import android.view.View;
import android.widget.ScrollView;
import pl.trakos.TekkitRecipeList.TekkitRecipeListActivity;
import pl.trakos.TekkitRecipeList.sql.DaoFactory;
import pl.trakos.TekkitRecipeList.sql.entities.Item;

import java.sql.SQLException;

public class ItemLayout extends ScrollView
{
    TekkitRecipeListActivity activity;
    private View itemPage;
    private String itemName;

    public ItemLayout(TekkitRecipeListActivity context)
    {
        super(context);
        activity = context;
    }

    public void showPreparedItem()
    {
        removeAllViews();
        addView(itemPage);
        activity.setTitle(itemName);
    }


    public void prepareItem(int itemId, int itemDamage)
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
        itemPage = Generator.getItemPage(activity, item, new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (v instanceof Generator.ItemImageView)
                {
                    Item item = ((Generator.ItemImageView) v).item;
                    activity.pushToHistoryAndShow(item);
                }
            }
        });
        itemName = item.item_name;
    }
}
