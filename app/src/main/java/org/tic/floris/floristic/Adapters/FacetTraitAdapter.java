package org.tic.floris.floristic.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.tic.floris.floristic.Models.FacetCategory;
import org.tic.floris.floristic.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FacetTraitAdapter extends ArrayAdapter<FacetCategory> {

    private Context context;
    private int layoutId;
    private List<FacetCategory> list;

    public FacetTraitAdapter(Context context, List<FacetCategory> list) {
        super(context, R.layout.item_facet_category_drawer, list);
        this.context = context;
        this.layoutId = R.layout.item_facet_category_drawer;
        this.list = list;
        this.sort();
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public FacetCategory getItem(int position) {
        return this.list.get(position);
    }

    private class ViewHolder {
        TextView txtFacet;
        ImageView imgMinus;
        TextView txtSelected;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        FacetCategory item = this.list.get(position);
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(this.layoutId, parent, false);
            holder = new ViewHolder();
            holder.txtFacet = (TextView) convertView.findViewById(R.id.txt_facet);
            holder.imgMinus = (ImageView) convertView.findViewById(R.id.img_minus);
            holder.txtSelected = (TextView) convertView.findViewById(R.id.txt_selected);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtFacet.setText("");
        holder.txtSelected.setText("");

        holder.txtFacet.setText(item.getName());

        if (item.getSelected() != null) {
            holder.imgMinus.setVisibility(View.VISIBLE);
            holder.txtSelected.setVisibility(View.VISIBLE);
            holder.txtSelected.setText(item.getSelected().getValue());
        } else {
            holder.imgMinus.setVisibility(View.GONE);
            holder.txtSelected.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void sort() {
        Collections.sort(this.list, new Comparator<FacetCategory>() {
            @Override
            public int compare(FacetCategory lhs, FacetCategory rhs) {
                if (lhs.getSelected() != null && rhs.getSelected() == null) {
                    return -1;
                }
                if (lhs.getSelected() == null && rhs.getSelected() != null) {
                    return 1;
                }
                return lhs.getName().compareTo(rhs.getName());
            }
        });
    }
}
