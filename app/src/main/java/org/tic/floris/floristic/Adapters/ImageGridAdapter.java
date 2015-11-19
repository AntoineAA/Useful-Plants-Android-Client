package org.tic.floris.floristic.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;

import org.tic.floris.floristic.Interfaces.ImageInterface;
import org.tic.floris.floristic.Models.ImageMedia;
import org.tic.floris.floristic.Models.Species;
import org.tic.floris.floristic.R;

import java.net.URLEncoder;

public class ImageGridAdapter extends BaseAdapter {

    private Context context;
    private Species species;
    private AQuery aq;
    private int layoutId;

    public ImageGridAdapter(Context context, Species species) {
        this.context = context;
        this.species = species;
        this.aq = new AQuery(this.context);
        this.layoutId = R.layout.item_grid_gallery;
    }

    @Override
    public int getCount() {
        if (this.species.getImages() != null) {
            return this.species.getImages().size();
        }
        return 0;
    }

    @Override
    public ImageInterface getItem(int position) {
        return this.species.getImages().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        ImageView imgThumb;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ImageInterface item = this.species.getImages().get(position);
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(this.layoutId, parent, false);
            holder = new ViewHolder();
            holder.imgThumb = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            String apiRoot = this.context.getResources().getString(R.string.api_url);
            String idElastic = URLEncoder.encode(this.species.getIdElastic(), "utf-8");
            String sourceUrl = URLEncoder.encode(item.getUrl(), "utf-8");

            String url = this.context.getResources().getString(R.string.api_image_mini,
                    apiRoot, idElastic, sourceUrl);

            ImageOptions options = new ImageOptions();
            options.animation = AQuery.FADE_IN;

            this.aq.id(holder.imgThumb).image(url, options);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.context.getResources().getString(R.string.exception), e.toString());
        }

        return convertView;
    }
}
