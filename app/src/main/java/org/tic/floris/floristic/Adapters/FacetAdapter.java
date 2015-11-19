package org.tic.floris.floristic.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.tic.floris.floristic.Managers.IntegerDisplayManager;
import org.tic.floris.floristic.Models.Facet;
import org.tic.floris.floristic.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class FacetAdapter extends ArrayAdapter<Facet> {

    private static final int SORT_VALUE = 0;
    private static final int SORT_COUNT = 1;

    private Context context;
    private int layoutId;
    private List<Facet> list;
    private List<Facet> originalList;

    private int sort = FacetAdapter.SORT_VALUE;

    private ImageButton view;

    public FacetAdapter(Context context, List<Facet> list, ImageButton view) {
        super(context, R.layout.item_facet, list);
        this.context = context;
        this.layoutId = R.layout.item_facet;

        this.originalList = list;
        this.list = new ArrayList<>();
        this.list.addAll(this.originalList);

        this.view = view;
        this.updateIcon();
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Facet getItem(int position) {
        return this.list.get(position);
    }

    private class ViewHolder {
        TextView txtFacet;
        TextView txtCount;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Facet item = this.list.get(position);
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(this.layoutId, parent, false);
            holder = new ViewHolder();
            holder.txtFacet = (TextView) convertView.findViewById(R.id.txt_facet);
            holder.txtCount = (TextView) convertView.findViewById(R.id.txt_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtFacet.setText("");
        holder.txtCount.setText("");

        holder.txtFacet.setText(item.getValue());
        holder.txtCount.setText(IntegerDisplayManager.getDecimalFormat(item.getCount(), ' '));

        return convertView;
    }

    /**
     * filters the list items that contain the string
     *
     * @param string the string filter
     */
    public void filter(String string) {
        string = string.toLowerCase(Locale.getDefault());
        Log.i(this.context.getString(R.string.exception), "search: " + string);

        this.list.clear();

        if (string.length() > 1) {
            for (Facet f : this.originalList) {
                if (f.getValue().toLowerCase(Locale.getDefault()).contains(string)) {
                    Log.i(this.context.getString(R.string.exception), "match: " + f.getValue());
                    this.list.add(f);
                }
            }
        } else {
            this.list.addAll(this.originalList);
        }

        this.notifyDataSetChanged();
    }

    /**
     * sort the facets list by value or by count
     */
    public void sort() {
        if (this.sort == FacetAdapter.SORT_VALUE) {
            this.sort = FacetAdapter.SORT_COUNT;
        } else {
            this.sort = FacetAdapter.SORT_VALUE;
        }

        if (this.sort == FacetAdapter.SORT_VALUE) {
            Collections.sort(this.list, new Comparator<Facet>() {
                @Override
                public int compare(Facet lhs, Facet rhs) {
                    return lhs.getValue().compareTo(rhs.getValue());
                }
            });
        }

        if (this.sort == FacetAdapter.SORT_COUNT) {
            Collections.sort(this.list, new Comparator<Facet>() {
                @Override
                public int compare(Facet lhs, Facet rhs) {
                    if ((rhs.getCount() - lhs.getCount()) == 0) {
                        return lhs.getValue().compareTo(rhs.getValue());
                    }
                    return rhs.getCount() - lhs.getCount();
                }
            });
        }

        this.updateIcon();

        this.notifyDataSetChanged();
    }

    private void updateIcon() {
        if (this.sort == FacetAdapter.SORT_VALUE) {
            this.view.setImageResource(R.drawable.ic_menu_sort_by_size);
        } else {
            this.view.setImageResource(R.drawable.ic_menu_sort_alphabetically);
        }
    }
}
