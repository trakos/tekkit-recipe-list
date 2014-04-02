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
import pl.trakos.TekkitRecipeList.sql.entities.Item;

import java.io.IOException;

public class Generator
{
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

        return linearLayout;
    }
}
