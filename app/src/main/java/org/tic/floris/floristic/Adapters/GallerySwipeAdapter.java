package org.tic.floris.floristic.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import org.tic.floris.floristic.Interfaces.ImageInterface;
import org.tic.floris.floristic.Models.Species;
import org.tic.floris.floristic.R;

import java.net.URLEncoder;

public class GallerySwipeAdapter extends PagerAdapter {

    private Context context;
    private Species species;
    private AQuery aq;
    private int layoutId;

    public GallerySwipeAdapter(Context context, Species species) {
        this.context = context;
        this.species = species;
        this.aq = new AQuery(this.context);
        this.layoutId = R.layout.item_gallery_detail;
    }

    @Override
    public int getCount() {
        if (this.species.getImages() != null) {
            return this.species.getImages().size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageInterface item = this.species.getImages().get(position);
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(this.layoutId, container, false);

        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView txtTitle = (TextView) view.findViewById(R.id.txt_title);
        TextView txtAuthor = (TextView) view.findViewById(R.id.txt_author);
        TextView txtSource = (TextView) view.findViewById(R.id.txt_source);

        try {
            String apiRoot = this.context.getResources().getString(R.string.api_url);
            String idElastic = URLEncoder.encode(this.species.getIdElastic(), "utf-8");
            String sourceUrl = URLEncoder.encode(item.getUrl(), "utf-8");

            String url = this.context.getResources().getString(R.string.api_image_normal,
                    apiRoot, idElastic, sourceUrl);

            this.aq.id(image).image(url, true, true, 0, 0, new BitmapAjaxCallback() {
                @Override
                public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                    iv.setImageBitmap(bm);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.context.getResources().getString(R.string.exception), e.toString());
        }

        txtTitle.setText(item.getTitle());
        txtAuthor.setText(item.getAuthor());

        if (item.getSource() != null && item.getSource().length() > 0) {
            txtSource.setText(item.getSource());
            txtSource.setVisibility(View.VISIBLE);
        } else {
            txtSource.setVisibility(View.GONE);
        }

        ((ViewPager) container).addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
