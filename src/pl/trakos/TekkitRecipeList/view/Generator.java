package pl.trakos.TekkitRecipeList.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import pl.trakos.TekkitRecipeList.R;
import pl.trakos.TekkitRecipeList.sql.entities.Item;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Generator
{
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static int generateViewId()
    {
        while (true)
        {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF)
            {
                newValue = 1; // Roll over to 1, not 0.
            }
            if (sNextGeneratedId.compareAndSet(result, newValue))
            {
                return result;
            }
        }
    }

    static public View getItemPage(Context context, Item item)
    {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        TextView titleText = new TextView(context);
        titleText.setText(item.item_name);
        titleText.setTextSize(30);
        titleText.setGravity(Gravity.CENTER);
        titleText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(titleText);

        ImageView imageView = new ImageView(context);
        try
        {
            imageView.setImageDrawable(Drawable.createFromStream(context.getAssets().open("icons/" + item.item_icon), null));
        }
        catch (IOException e)
        {
            Log.e("image_not_found", e.getMessage());
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(imageView);

        titleText = new TextView(context);
        titleText.setText(R.string.recipes_crafting_item);
        titleText.setTextSize(15);
        titleText.setGravity(Gravity.CENTER);
        titleText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(titleText);

        return linearLayout;
    }
}
