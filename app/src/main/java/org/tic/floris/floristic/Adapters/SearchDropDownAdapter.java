package org.tic.floris.floristic.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.tic.floris.floristic.R;

import java.util.List;

public class SearchDropDownAdapter extends ArrayAdapter<String> {

    private Context context;
    private int layoutId;
    private List<String> list;

    public SearchDropDownAdapter(Context context, List<String> list) {
        super(context, R.layout.item_search_dropdown, list);
        this.context = context;
        this.layoutId = R.layout.item_search_dropdown;
        this.list = list;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public String getItem(int position) {
        return this.list.get(position);
    }

    private class ViewHolder {
        TextView txtResult;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        String item = this.list.get(position);
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(this.layoutId, parent, false);
            holder = new ViewHolder();
            holder.txtResult = (TextView) convertView.findViewById(R.id.txt_result);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtResult.setText("");

        holder.txtResult.setText(Html.fromHtml(item));

        return convertView;
    }

    /**
     * clears the list and add some new results
     *
     * @param list new results
     */
    public void addResults(List<String> list) {
        this.list.clear();
        this.list.addAll(list);
        this.notifyDataSetChanged();
    }
}
