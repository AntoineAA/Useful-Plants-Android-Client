package org.tic.floris.floristic.Tasks;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.tic.floris.floristic.Adapters.SpeciesAdapter;
import org.tic.floris.floristic.Interfaces.GetSpeciesInterface;
import org.tic.floris.floristic.Models.ApiAggregations;
import org.tic.floris.floristic.Models.ApiResponse;
import org.tic.floris.floristic.Models.Species;
import org.tic.floris.floristic.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class GetSpeciesTask extends AsyncTask<Integer, Integer, List<Species>> {

    private static final int TIMEOUT = 60; // seconds

    private Activity activity;
    private GetSpeciesInterface listener;
    private SpeciesAdapter adapter;

    private int size;
    private int from;

    private String family;
    private String genus;
    private String usageCat;
    private String usageType;
    private String project;

    private Map<String, String> traits;

    private String search;

    private String sortBy;

    private Location location;

    private Boolean flowers;
    private Boolean fruits;
    private Boolean leaves;

    private String url;

    private ApiAggregations aggregations = null;

    public GetSpeciesTask(Activity activity, GetSpeciesInterface listener, SpeciesAdapter adapter, int from, int size,
                          String family, String genus, String usageCat, String usageType, String project,
                          Map<String, String> traits,
                          String search, String sortBy, Location location,
                          Boolean flowers, Boolean fruits, Boolean leaves) {
        this.activity = activity;
        this.listener = listener;
        this.adapter = adapter;
        this.from = from;
        this.size = size;

        this.family = family;
        this.genus = genus;
        this.usageCat = usageCat;
        this.usageType = usageType;
        this.project = project;

        this.traits = traits;

        this.search = search;

        this.sortBy = sortBy;

        this.location = location;

        this.flowers = flowers;
        this.fruits = fruits;
        this.leaves = leaves;

        String apiRoot = this.activity.getResources().getString(R.string.api_url);
        this.url = this.activity.getResources().getString(R.string.api_species, apiRoot);
    }

    @Override
    protected List<Species> doInBackground(Integer... params) {
        try {
            this.url += this.makeUrl();

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT * 1000);
            HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT * 1000);

            HttpGet httpGet = new HttpGet(this.url);
            HttpResponse response = client.execute(httpGet);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();

                InputStream content = entity.getContent();
                Reader reader = new InputStreamReader(content);
                BufferedReader br = new BufferedReader(reader);

                Gson gson = new Gson();
                ApiResponse resp = gson.fromJson(br, ApiResponse.class);

                if (resp != null && resp.getHits() != null) {
                    this.adapter.setServerListSize(resp.getHits().getTotal());

                    if (resp.getAggregations() != null) {
                        this.aggregations = resp.getAggregations();
                    }

                    if (resp.getHits().getResults() != null && resp.getHits().getResults().size() > 0) {
                        return resp.getHits().getSpecies();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.activity.getResources().getString(R.string.exception), e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Species> species) {
        if (species != null) {
            this.listener.onResultsFound();
            this.adapter.addSpecies(species);
            if (this.aggregations != null) {
                this.listener.onLoadFacets(this.aggregations);
            }
        } else {
            if (from == 0) {
                this.listener.onNoResultsFound();
            }
        }
    }

    private String makeUrl() throws UnsupportedEncodingException {
        String url = "";

        url += "?" + this.activity.getResources().getString(R.string.api_species_size) + this.size;
        url += "&" + this.activity.getResources().getString(R.string.api_species_from) + this.from;

        if (this.family != null && !this.family.equals("")) {
            url += "&" + this.activity.getResources().getString(R.string.api_species_family) + URLEncoder.encode(this.family, "utf-8");
        }

        if (this.genus != null && !this.genus.equals("")) {
            url += "&" + this.activity.getResources().getString(R.string.api_species_genus) + URLEncoder.encode(this.genus, "utf-8");
        }

        if (this.usageCat != null && !this.usageCat.equals("")) {
            url += "&" + this.activity.getResources().getString(R.string.api_species_usage_cat) + URLEncoder.encode(this.usageCat, "utf-8");
        }

        if (this.usageType != null && !this.usageType.equals("")) {
            url += "&" + this.activity.getResources().getString(R.string.api_species_usage_type) + URLEncoder.encode(this.usageType, "utf-8");
        }

        if (this.project != null && !this.project.equals("")) {
            url += "&" + this.activity.getResources().getString(R.string.api_species_project) + URLEncoder.encode(this.project, "utf-8");
        }

        if (this.traits != null && this.traits.size() > 0) {
            for (Map.Entry<String, String> entry : this.traits.entrySet()) {
                url += "&" + this.activity.getResources().getString(R.string.api_species_traits);
                url += URLEncoder.encode(entry.getKey(), "utf-8");
                url += URLEncoder.encode(this.activity.getResources().getString(R.string.api_species_traits_divider), "utf-8");
                url += URLEncoder.encode(entry.getValue(), "utf-8");
            }
        }

        if (this.search != null && !this.search.equals("")) {
            url += "&" + this.activity.getResources().getString(R.string.api_species_search) + URLEncoder.encode(this.search, "utf-8");
        }

        if (this.from == 0) {
            url += "&" + this.activity.getResources().getString(R.string.api_species_include_facets);
        }

        if (this.sortBy != null && this.sortBy.length() > 0) {
            url += "&" + this.activity.getResources().getString(R.string.api_species_sort_by) + URLEncoder.encode(this.sortBy, "utf-8");
        }

        if (this.location != null) {
            url += "&" + this.activity.getResources().getString(R.string.api_species_latitude) + this.location.getLatitude();
            url += "&" + this.activity.getResources().getString(R.string.api_species_longitude) + this.location.getLongitude();
        }

        if (this.flowers != null && this.flowers) {
            url += "&" + this.activity.getResources().getString(R.string.api_species_current_flower);
        }

        if (this.fruits != null && this.fruits) {
            url += "&" + this.activity.getResources().getString(R.string.api_species_current_fruit);
        }

        if (this.leaves != null && this.leaves) {
            url += "&" + this.activity.getResources().getString(R.string.api_species_current_leaf);
        }

        return url;
    }

    public String getUrl() throws UnsupportedEncodingException {
        return this.url + this.makeUrl();
    }
}
