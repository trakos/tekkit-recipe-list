package pl.trakos.TekkitRecipeList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import pl.trakos.TekkitRecipeList.view.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ListDataAdapter extends ArrayAdapter<ListDataRow>
{

    static public ArrayList<ListDataRow> fromStrings(Collection<String> strings)
    {
        ArrayList<ListDataRow> listDataRows = new ArrayList<ListDataRow>(strings.size());
        for (String string : strings)
        {
            listDataRows.add(new ListDataRow(string));
        }
        return listDataRows;
    }

    public ListDataAdapter(Context context, String[] strings)
    {
        this(context, fromStrings(Arrays.asList(strings)));
    }

    public ListDataAdapter(Context context, ArrayList<ListDataRow> dataList)
    {
        super(context, -1, -1, dataList);
    }

    public void changeData(Collection<ListDataRow> newData)
    {
        clear();
        addAll(newData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ListDataRow item = super.getItem(position);

        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        relativeLayout.setPadding(10, 10, 10, 10);

        LinearLayout linearLayout = null;

        if (item.bitmapDrawable != null)
        {
            linearLayout = new LinearLayout(getContext());
            linearLayout.setId(Generator.generateViewId());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.rightMargin = 5;
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setPadding(3, 3, 3, 3);
            relativeLayout.addView(linearLayout);

            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(item.bitmapDrawable);
            imageView.setScaleType(ImageView.ScaleType.FIT_START);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
            linearLayout.addView(imageView);
        }

        TextView listText = new TextView(getContext());
        RelativeLayout.LayoutParams textLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (linearLayout != null)
        {
            textLayoutParams.addRule(RelativeLayout.RIGHT_OF, linearLayout.getId());
            textLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, linearLayout.getId());
        }
        listText.setLayoutParams(textLayoutParams);
        listText.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Widget_PopupMenu_Large);
        listText.setText(item.text);

        relativeLayout.addView(listText);


        return relativeLayout;
    }
}
