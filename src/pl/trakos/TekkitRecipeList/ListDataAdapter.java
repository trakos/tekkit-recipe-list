package pl.trakos.TekkitRecipeList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        LinearLayout listLayout = new LinearLayout(getContext());
        listLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));

        TextView listText = new TextView(getContext());
        listText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        ((LinearLayout.LayoutParams) listText.getLayoutParams()).topMargin = 10;
        ((LinearLayout.LayoutParams) listText.getLayoutParams()).bottomMargin = 10;
        ((LinearLayout.LayoutParams) listText.getLayoutParams()).leftMargin = 10;
        listText.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Widget_PopupMenu_Large);
        listText.setText(super.getItem(position).text);

        listLayout.addView(listText);


        return listLayout;
    }
}
