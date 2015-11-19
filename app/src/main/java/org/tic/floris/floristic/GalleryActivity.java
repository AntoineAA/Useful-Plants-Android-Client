package org.tic.floris.floristic;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import org.tic.floris.floristic.Adapters.GallerySwipeAdapter;
import org.tic.floris.floristic.Managers.ActivityManager;
import org.tic.floris.floristic.Models.Species;

public class GalleryActivity extends ActionBarActivity {

    public final static String ARG_START_POS = "start_pos";

    private Species species;

    private ViewPager pager;
    private GallerySwipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Gson gson = new Gson();
        String strSpecies = getIntent().getStringExtra(BrowserActivity.SPECIES_OBJECT);
        this.species = gson.fromJson(strSpecies, Species.class);

        this.setTitle(this.species.getName());

        int start = 0;
        if (getIntent().hasExtra(GalleryActivity.ARG_START_POS)) {
            start = getIntent().getIntExtra(GalleryActivity.ARG_START_POS, 0);
        }

        this.pager = (ViewPager) this.findViewById(R.id.view_pager);
        this.adapter = new GallerySwipeAdapter(this, this.species);
        this.pager.setAdapter(this.adapter);
        this.pager.setCurrentItem(start);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml

        switch (item.getItemId()) {
            case android.R.id.home:
                this.closeActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.closeActivity();
    }

    private void closeActivity() {
        this.finish();
        this.overridePendingTransition(R.anim.non_mobile, R.anim.top_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.unbindDrawables(this.findViewById(R.id.root_view));
    }
}
