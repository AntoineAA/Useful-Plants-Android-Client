package org.tic.floris.floristic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.tic.floris.floristic.Models.SortBy;
import org.tic.floris.floristic.R;

import java.util.List;

public class SortByAdapter extends ArrayAdapter<SortBy> {

    private Context context;
    private int layoutId;
    private int selectedLayoutId;
    private List<SortBy> list;

    public SortByAdapter(Context context, List<SortBy> list) {
        super(context, R.layout.item_sort_drawer, list);
        this.context = context;
        this.layoutId = R.layout.item_sort_drawer;
        this.selectedLayoutId = R.layout.item_sort_drawer_selected;
        this.list = list;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public SortBy getItem(int position) {
        return this.list.get(position);
    }

    private class ViewHolder {
        TextView txtSortBy;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        SortBy item = this.list.get(position);
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(this.selectedLayoutId, parent, false);
            holder = new ViewHolder();
            holder.txtSortBy = (TextView) convertView.findViewById(R.id.txt_sort_by);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtSortBy.setText("");

        holder.txtSortBy.setText(item.getName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        SortBy item = this.list.get(position);
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(this.layoutId, parent, false);
            holder = new ViewHolder();
            holder.txtSortBy = (TextView) convertView.findViewById(R.id.txt_sort_by);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtSortBy.setText("");

        holder.txtSortBy.setText(item.getName());

        return convertView;
    }
}
