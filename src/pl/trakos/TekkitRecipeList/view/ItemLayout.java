package pl.trakos.TekkitRecipeList.view;

import android.os.SystemClock;
import pl.trakos.TekkitRecipeList.R;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;
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
            int lastTouchItemId;
            int lastTouchItemDamage;
            long lastTouchTicks = -2000;

            @Override
            public void onClick(View v)
            {
                if (v instanceof Generator.ItemImageView)
                {
                    long currentTicks = SystemClock.uptimeMillis();
                    Item item = ((Generator.ItemImageView) v).item;
                    int touchedItemId = item.item_id;
                    int touchedItemDamage = item.item_damage;
                    if (lastTouchTicks + 2000 > currentTicks && touchedItemId == lastTouchItemId && touchedItemDamage == lastTouchItemDamage)
                    {
                        activity.pushToHistoryAndShow(item);
                    }
                    else
                    {
                        lastTouchTicks = currentTicks;
                        lastTouchItemId = touchedItemId;
                        lastTouchItemDamage = touchedItemDamage;
                        Toast.makeText(getContext(), item.item_name + "\n\n" + getContext().getResources().getString(R.string.tap_again_to_view), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        itemName = item.item_name;
    }
}
