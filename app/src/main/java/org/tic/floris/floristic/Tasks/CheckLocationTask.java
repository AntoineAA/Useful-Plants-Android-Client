package org.tic.floris.floristic.Tasks;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.tic.floris.floristic.Interfaces.CheckLocationInterface;
import org.tic.floris.floristic.Models.Species;
import org.tic.floris.floristic.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CheckLocationTask extends AsyncTask<Integer, Integer, Boolean> {

    private static final int TIMEOUT = 30; // seconds

    private Activity activity;
    private CheckLocationInterface listener;

    private Species species;
    private Location location;

    private View view;
    private ProgressBar progress;

    private String url;

    public CheckLocationTask(Activity activity, CheckLocationInterface listener, Species species, Location location,
                             View view, ProgressBar progress) {
        this.activity = activity;
        this.listener = listener;

        this.species = species;
        this.location = location;

        this.view = view;
        this.progress = progress;

        String apiRoot = this.activity.getResources().getString(R.string.api_url);
        this.url = this.activity.getResources().getString(R.string.api_observation, apiRoot);
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        if (this.species.getGbifKey() != null || this.species.getPnetId() != null) {
            try {
                this.url += this.makeUrl();
                this.url += "&" + this.activity.getResources().getString(R.string.api_observation_check);

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
                    CheckLoc result = gson.fromJson(br, CheckLoc.class);

                    return result.getExists();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(this.activity.getResources().getString(R.string.exception), e.toString());
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean exists) {
        this.species.setIsVisible(exists);

        if (this.listener != null) {
            this.listener.onLocationChecked(exists);
        }

        if (this.view != null) {
            if (exists) {
                this.view.setVisibility(View.VISIBLE);
            } else {
                this.view.setVisibility(View.GONE);
            }
        }

        if (this.progress != null) {
            this.progress.setVisibility(View.GONE);
        }
    }

    private String makeUrl() throws UnsupportedEncodingException {
        String url = "";

        url += "?" + this.activity.getResources().getString(R.string.api_observation_lat, this.location.getLatitude());
        url += "&" + this.activity.getResources().getString(R.string.api_observation_long, this.location.getLongitude());

        if (this.species.getGbifKey() != null) {
            url += "&" + this.activity.getResources().getString(R.string.api_observation_gbif_key) + this.species.getGbifKey();
        }

        if (this.species.getPnetId() != null) {
            url += "&" + this.activity.getResources().getString(R.string.api_observation_pnet_id) + URLEncoder.encode(this.species.getPnetId(), "utf-8");
        }

        return url;
    }

    public String getUrl() throws UnsupportedEncodingException {
        return this.url + this.makeUrl();
    }

    public class CheckLoc {

        @SerializedName("exists")
        private Boolean exists;

        public Boolean getExists() {
            return this.exists;
        }
    }
}
