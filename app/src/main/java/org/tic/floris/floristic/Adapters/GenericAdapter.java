package org.tic.floris.floristic.Adapters;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.tic.floris.floristic.R;

import java.util.List;

public abstract class GenericAdapter<T> extends BaseAdapter {

    protected Activity activity;
    // the main data list to save loaded data
    protected List<T> dataList;

    // the serverListSize is the total number of items on the server side,
    // which should be returned from the web request results
    protected int serverListSize = -1;

    protected int lastViewMinHeight = -1;

    // two view types which will be used to determine whether a row should be displaying
    // data or a Progressbar
    public static final int VIEW_TYPE_LOADING = 0;
    public static final int VIEW_TYPE_ACTIVITY = 1;

    public GenericAdapter(Activity activity, List<T> list) {
        this.activity = activity;
        this.dataList = list;
    }

    public void setServerListSize(int serverListSize) {
        this.serverListSize = serverListSize;
    }

    public void setLastViewMinHeight(int height) {
        this.lastViewMinHeight = height;
    }

    /**
     * disable click events on indicating rows
     */
    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == VIEW_TYPE_ACTIVITY;
    }

    /**
     * one type is normal data row, the other type is Progressbar
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * the size of the List plus one, the one is the last row, which displays a Progressbar
     */
    @Override
    public int getCount() {
        return this.dataList.size() + 1;
    }

    /**
     * the size of the List
     */
    public int getDataCount() {
        return this.dataList.size();
    }

    /**
     * the size of the List
     */
    public int getTotalDataCount() {
        return this.serverListSize;
    }

    /**
     * return the type of the row,
     * the last row indicates the user that the ListView is loading more data
     */
    @Override
    public int getItemViewType(int position) {
        return (position >= this.dataList.size()) ? VIEW_TYPE_LOADING : VIEW_TYPE_ACTIVITY;
    }

    @Override
    public T getItem(int position) {
        return (getItemViewType(position) == VIEW_TYPE_ACTIVITY) ? this.dataList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return (getItemViewType(position) == VIEW_TYPE_ACTIVITY) ? position : -1;
    }

    /**
     * returns the correct view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == VIEW_TYPE_LOADING) {
            // display the last row
            return getFooterView(position, convertView, parent);
        }

        return getDataRow(position, convertView, parent);
    }

    /**
     * a subclass should override this method to supply the data row
     *
     * @param position    position in the original List
     * @param convertView the current view
     * @param parent      the parent view
     * @return returns the view to display
     */
    public abstract View getDataRow(int position, View convertView, ViewGroup parent);

    /**
     * returns a View to be displayed in the last row
     *
     * @param position    position in the current list (original + footer)
     * @param convertView the current view
     * @param parent      the parent view
     * @return returns the view to display
     */
    public View getFooterView(int position, View convertView, ViewGroup parent) {
        if (position >= this.serverListSize && this.serverListSize > 0) {
            // the ListView has reached the last row
            TextView tvLastRow = new TextView(this.activity);
            tvLastRow.setHint("-");
            tvLastRow.setGravity(Gravity.CENTER);

            if (this.lastViewMinHeight != -1) {
                tvLastRow.setHeight(this.lastViewMinHeight);
            }

            return tvLastRow;
        }

        View row = convertView;
        if (row == null) {
            row = this.activity.getLayoutInflater().inflate(
                    R.layout.item_progress, parent, false);
        }

        if (this.dataList.size() > 0) {
            row.setVisibility(View.VISIBLE);
        } else {
            row.setVisibility(View.GONE);
        }

        return row;
    }
}
