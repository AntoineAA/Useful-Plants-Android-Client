package org.tic.floris.floristic;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.tic.floris.floristic.Interfaces.CheckLocationInterface;
import org.tic.floris.floristic.Interfaces.GetSpeciesDetailsInterface;
import org.tic.floris.floristic.Managers.ActivityManager;
import org.tic.floris.floristic.Managers.GpsManager;
import org.tic.floris.floristic.Models.Species;
import org.tic.floris.floristic.Tasks.CheckLocationTask;
import org.tic.floris.floristic.Tasks.GetSpeciesDetailsTask;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class DetailActivity extends ActionBarActivity implements ActionBar.TabListener, GetSpeciesDetailsInterface,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        CheckLocationInterface {

    private Species species;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private ActionBar actionBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location location;

    private MenuItem menuMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Gson gson = new Gson();
        String strSpecies = getIntent().getStringExtra(BrowserActivity.SPECIES_OBJECT);
        this.species = gson.fromJson(strSpecies, Species.class);

        this.setTitle(this.species.getName());

        // Set up the action bar.
        this.actionBar = getSupportActionBar();

        this.swipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipt_to_refresh);
        this.swipeRefreshLayout.setColorSchemeResources(R.color.color_primary);

        this.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                DetailActivity.this.swipeRefreshLayout.setRefreshing(true);
            }
        });

        GetSpeciesDetailsTask task = new GetSpeciesDetailsTask(this, this, this.species);
        task.execute();

        this.googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        this.locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1000);
    }

    private void initMap(Location loc) {
        this.location = loc;

        if (this.googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(this.googleApiClient, this);
            this.googleApiClient.disconnect();
        }

        Log.i(this.getString(R.string.exception), "lat: " + this.location.getLatitude());
        Log.i(this.getString(R.string.exception), "long: " + this.location.getLongitude());

        CheckLocationTask task = new CheckLocationTask(this, this, this.species, this.location, null, null);
        task.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.location == null) {
            this.googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(this.googleApiClient, this);
            this.googleApiClient.disconnect();
        }
    }

    @Override
    public void onResultsFound(Species species) {
        this.species = species;
        this.setTitle(this.species.getName());

        if (this.location != null) {
            this.initMap(this.location);
        }

        this.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this.species);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                DetailActivity.this.actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            this.actionBar.addTab(
                    this.actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        mViewPager.setVisibility(View.VISIBLE);
        this.swipeRefreshLayout.setRefreshing(false);
        this.swipeRefreshLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        this.menuMap = menu.findItem(R.id.action_map);
        this.menuMap.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case android.R.id.home:
                this.closeActivity();
                return true;
            case R.id.action_location_settings:
                if (GpsManager.hasGPSDevice(this)) {
                    GpsManager.startGPSIntent(this);
                }
                return true;
            case R.id.action_map:
                if (this.location != null) {
                    CheckLocationTask task = new CheckLocationTask(this, this, this.species, this.location, null, null);
                    try {
                        String url = task.getUrl();

                        Intent intent = new Intent(DetailActivity.this, MapActivity.class);
                        intent.putExtra(MapActivity.LOCATION_PARAM, this.location);
                        intent.putExtra(MapActivity.URL_PARAM_SINGLE, url);

                        this.startActivity(intent);
                        this.overridePendingTransition(R.anim.left_side_in, R.anim.non_mobile);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Log.e(this.getResources().getString(R.string.exception), e.toString());
                    }
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onLocationChecked(Boolean exists) {
        if (exists) {
            this.menuMap.setVisible(true);
        } else {
            this.menuMap.setVisible(false);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Species species;
        private List<SpeciesFragment> fragmentsList;

        public SectionsPagerAdapter(FragmentManager fm, Species species) {
            super(fm);

            this.species = species;
            this.fragmentsList = new ArrayList<>();

            Locale l = Locale.getDefault();

            this.fragmentsList.add(SpeciesDataFragment.newInstance(
                    1, DetailActivity.this.species, getString(R.string.tab_title_data).toUpperCase(l)));
            this.fragmentsList.add(SpeciesGalleryFragment.newInstance(
                    2, DetailActivity.this.species, getString(R.string.tab_title_gallery).toUpperCase(l)));
            this.fragmentsList.add(SpeciesWikiFragment.newInstance(
                    3, DetailActivity.this.species, getString(R.string.tab_title_wiki).toUpperCase(l),
                    R.string.wiki_host, R.string.wiki_url));
            this.fragmentsList.add(SpeciesWikiFragment.newInstance(
                    4, DetailActivity.this.species, getString(R.string.tab_title_plantuse).toUpperCase(l),
                    R.string.plantuse_host, R.string.plantuse_url));
            if (this.species.getGbifKey() != null) {
                this.fragmentsList.add(SpeciesMapFragment.newInstance(
                        5, DetailActivity.this.species, getString(R.string.tab_title_map).toUpperCase(l)));
            }
        }

        @Override
        public SpeciesFragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position < this.fragmentsList.size()) {
                return this.fragmentsList.get(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            return this.fragmentsList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position < this.fragmentsList.size()) {
                return this.fragmentsList.get(position).getFragmentName();
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        if (!this.mSectionsPagerAdapter.getItem(this.mViewPager.getCurrentItem()).customOnBackPressed()) {
            this.closeActivity();
        }
    }

    private void closeActivity() {
        this.finish();
        this.overridePendingTransition(R.anim.non_mobile, R.anim.left_side_out);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(this.getString(R.string.exception), "Connected");
        Location loc = LocationServices.FusedLocationApi.getLastLocation(this.googleApiClient);
        if (loc != null) {
            this.initMap(loc);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(this.googleApiClient, this.locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(this.getString(R.string.exception), "Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(this.getString(R.string.exception), "Failed");
    }

    @Override
    public void onLocationChanged(Location loc) {
        Log.i(this.getString(R.string.exception), "location changed");
        if (this.location == null && loc != null) {
            this.initMap(loc);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.unbindDrawables(this.findViewById(R.id.root_view));
    }
}
