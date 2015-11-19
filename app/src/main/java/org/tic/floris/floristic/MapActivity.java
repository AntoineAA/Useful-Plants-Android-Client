package org.tic.floris.floristic;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.tic.floris.floristic.Managers.ActivityManager;
import org.tic.floris.floristic.Managers.GpsManager;
import org.tic.floris.floristic.Managers.WebViewManager;
import org.tic.floris.floristic.Models.Species;


public class MapActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String INTERFACE_NAME = "FloristicInterface";
    private static final String HTML_CONTENT = "file:///android_asset/views/map.html";

    public static final String LOCATION_PARAM = "location";
    public static final String URL_PARAM = "url";
    public static final String URL_PARAM_SINGLE = "url_single";

    private SwipeRefreshLayout swipeRefreshLayout;

    private WebView web;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location location;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        this.swipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipt_to_refresh);
        this.swipeRefreshLayout.setColorSchemeResources(R.color.color_primary);

        this.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                MapActivity.this.swipeRefreshLayout.setRefreshing(true);
            }
        });

        this.web = (WebView) this.findViewById(R.id.web);
        WebViewManager.disableHardwareAcceleration(this.web);

        this.googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        this.locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1000);

        if (this.getIntent() != null) {
            if (this.getIntent().hasExtra(MapActivity.LOCATION_PARAM)) {
                this.location = this.getIntent().getExtras().getParcelable(MapActivity.LOCATION_PARAM);
                this.initMap(this.location);
            }
            if (this.getIntent().hasExtra(MapActivity.URL_PARAM)) {
                this.url = this.getIntent().getExtras().getString(MapActivity.URL_PARAM);
                this.url += "&" + this.getResources().getString(R.string.api_species_include_geojson);
            }
            if (this.getIntent().hasExtra(MapActivity.URL_PARAM_SINGLE)) {
                this.url = this.getIntent().getExtras().getString(MapActivity.URL_PARAM_SINGLE);
            }
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.closeActivity();
    }

    private void initMap(Location loc) {
        this.location = loc;

        if (this.googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(this.googleApiClient, this);
            this.googleApiClient.disconnect();
        }

        Log.i(this.getString(R.string.exception), "lat: " + this.location.getLatitude());
        Log.i(this.getString(R.string.exception), "long: " + this.location.getLongitude());

        this.load();
    }

    private void load() {
        this.web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getHost().equals(MapActivity.HTML_CONTENT)) {
                    return false;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                MapActivity.this.startActivity(intent);
                return true;
            }
        });

        this.web.clearHistory();
        this.web.clearCache(true);

        this.web.addJavascriptInterface(new WebAppInterface(this, this.location), MapActivity.INTERFACE_NAME);

        this.web.getSettings().setJavaScriptEnabled(true);
        this.web.loadUrl(MapActivity.HTML_CONTENT);
    }

    public String getUrl() {
        return this.url;
    }

    /**
     * custom JavaScript interface. allows communication between JavaScript and Java
     */
    private class WebAppInterface {
        MapActivity activity;
        Location location;

        /**
         * instantiate the interface and set the context
         */
        WebAppInterface(MapActivity activity, Location location) {
            this.activity = activity;
            this.location = location;
        }

        /**
         * loads the current location latitude
         *
         * @return returns the current location latitude
         */
        @JavascriptInterface
        public String getLatitude() {
            return this.location.getLatitude() + "";
        }

        /**
         * loads the current location longitude
         *
         * @return returns the current location longitude
         */
        @JavascriptInterface
        public String getLongitude() {
            return this.location.getLongitude() + "";
        }

        @JavascriptInterface
        public String getCurrentLocationString() {
            return this.activity.getString(R.string.current_location);
        }

        @JavascriptInterface
        public String getDetailString() {
            return this.activity.getString(R.string.detail);
        }

        @JavascriptInterface
        public String getGeoJsonUrl() {
            if (this.activity.getUrl() != null && this.activity.getUrl().length() > 0) {
                return this.activity.getUrl();
            }
            String apiRoot = this.activity.getResources().getString(R.string.api_url);
            String url = this.activity.getResources().getString(R.string.api_observation, apiRoot);
            url += "?" + this.activity.getResources().getString(R.string.api_observation_lat, this.getLatitude());
            url += "&" + this.activity.getResources().getString(R.string.api_observation_long, this.getLongitude());
            return url;
        }

        @JavascriptInterface
        public void onLoadEnd() {
            this.activity.onLoadEnd();
        }

        @JavascriptInterface
        public void displayDetail(String id) {
            Species sp = new Species();
            sp.setIdElastic(id);

            Gson gson = new Gson();

            Intent intent = new Intent(MapActivity.this, DetailActivity.class);
            intent.putExtra(BrowserActivity.SPECIES_OBJECT, gson.toJson(sp));

            this.activity.startActivity(intent);
            this.activity.overridePendingTransition(R.anim.left_side_in, R.anim.non_mobile);
        }
    }

    public void onLoadEnd() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MapActivity.this.swipeRefreshLayout.setRefreshing(false);
                MapActivity.this.swipeRefreshLayout.setVisibility(View.GONE);
            }
        });
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

    private void closeActivity() {
        this.finish();
        this.overridePendingTransition(R.anim.non_mobile, R.anim.left_side_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.unbindDrawables(this.findViewById(R.id.root_view));
    }
}
