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

import java.util.List;

public class FacetCategoryAdapter extends ArrayAdapter<FacetCategory> {

    private Context context;
    private int layoutId;
    private List<FacetCategory> list;

    public FacetCategoryAdapter(Context context, List<FacetCategory> list) {
        super(context, R.layout.item_facet_category_drawer, list);
        this.context = context;
        this.layoutId = R.layout.item_facet_category_drawer;
        this.list = list;
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
        if (item.getFacets().size() > 1 || item.getSelected() != null) {
            holder.txtFacet.setTextColor(this.context.getResources().getColor(R.color.category_text));
        } else {
            holder.txtFacet.setTextColor(this.context.getResources().getColor(R.color.category_text_disabled));
        }

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
}
