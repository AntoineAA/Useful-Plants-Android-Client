package org.tic.floris.floristic.Adapters;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;

import org.tic.floris.floristic.Models.Species;
import org.tic.floris.floristic.R;
import org.tic.floris.floristic.Tasks.CheckLocationTask;

import java.net.URLEncoder;
import java.util.List;

public abstract class SpeciesAdapter extends GenericAdapter<Species> {

    private Activity activity;
    private AQuery aq;

    private Location location;

    public SpeciesAdapter(Activity activity, List<Species> list, Location location) {
        super(activity, list);
        this.activity = activity;
        this.aq = new AQuery(this.activity);
        this.location = location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    private class ViewHolder {
        TextView txtName;
        TextView txtFamilyName;
        TextView txtCommonName;
        ImageView imgThumb;
        TextView txtTag;
        ImageView imgLocation;
        ProgressBar progress;
    }

    @Override
    public View getDataRow(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Species item = this.dataList.get(position);
        LayoutInflater inflater = (LayoutInflater) this.activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_species, parent, false);
            holder = new ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            holder.txtFamilyName = (TextView) convertView.findViewById(R.id.txt_family_name);
            holder.txtCommonName = (TextView) convertView.findViewById(R.id.txt_common_name);
            holder.imgThumb = (ImageView) convertView.findViewById(R.id.img_thumb);
            holder.txtTag = (TextView) convertView.findViewById(R.id.txt_tag);
            holder.imgLocation = (ImageView) convertView.findViewById(R.id.icon_location);
            holder.progress = (ProgressBar) convertView.findViewById(R.id.progress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtName.setText("");
        holder.txtFamilyName.setText("");
        holder.txtCommonName.setText("");

        holder.imgThumb.setImageResource(android.R.color.transparent);

        holder.txtTag.setText("");

        holder.txtName.setText(item.getName());
        holder.txtFamilyName.setText(item.getFamily().getName());

        if (item.getFirstCommon("en") != null) {
            holder.txtCommonName.setText(item.getFirstCommon("en"));
        }

        if (item.getFirstUrlImage() != null && item.getFirstUrlImage().length() > 0) {
            try {
                String apiRoot = this.activity.getResources().getString(R.string.api_url);
                String idElastic = URLEncoder.encode(item.getIdElastic(), "utf-8");
                String sourceUrl = URLEncoder.encode(item.getFirstUrlImage(), "utf-8");

                String url = this.activity.getResources().getString(R.string.api_image_mini,
                        apiRoot, idElastic, sourceUrl);

                ImageOptions options = new ImageOptions();
                options.animation = AQuery.FADE_IN;

                this.aq.id(holder.imgThumb).image(url, options);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(this.activity.getResources().getString(R.string.exception), e.toString());
            }
        }

        holder.txtTag.setText(item.getUsesAsString(", "));

        holder.imgLocation.setVisibility(View.GONE);
        if (this.location != null) {
            holder.progress.setVisibility(View.VISIBLE);
        } else {
            holder.progress.setVisibility(View.GONE);
        }

        if (item.getIsVisible() != null) {
            if (item.getIsVisible()) {
                holder.imgLocation.setVisibility(View.VISIBLE);
            }
            holder.progress.setVisibility(View.GONE);
        } else {
            if (this.location != null) {
                CheckLocationTask task = new CheckLocationTask(this.activity, null, item, this.location, holder.imgLocation, holder.progress);
                task.execute();
            }
        }

        return convertView;
    }

    /**
     * add some species to the current list
     *
     * @param species the species list to push in
     */
    public void addSpecies(List<Species> species) {
        this.dataList.addAll(species);
        this.notifyDataSetChanged();
        this.onAddData();
    }

    /**
     * callback which fires after adding data to the list
     */
    public abstract void onAddData();
}
