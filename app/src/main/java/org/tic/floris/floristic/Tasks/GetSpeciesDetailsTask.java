package org.tic.floris.floristic.Tasks;

import android.app.Activity;
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
import org.tic.floris.floristic.Interfaces.GetSpeciesDetailsInterface;
import org.tic.floris.floristic.Models.ApiResult;
import org.tic.floris.floristic.Models.Species;
import org.tic.floris.floristic.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class GetSpeciesDetailsTask extends AsyncTask<Integer, Integer, Species> {

    private static final int TIMEOUT = 10; // seconds

    private Activity activity;
    private GetSpeciesDetailsInterface listener;

    private Species species;

    private String url;

    public GetSpeciesDetailsTask(Activity activity, GetSpeciesDetailsInterface listener, Species species) {
        this.activity = activity;
        this.listener = listener;

        this.species = species;

        String apiRoot = this.activity.getResources().getString(R.string.api_url);
        this.url = this.activity.getResources().getString(R.string.api_species_get, apiRoot);
        this.url += this.species.getIdElastic();
    }

    @Override
    protected Species doInBackground(Integer... params) {
        try {
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
                ApiResult result = gson.fromJson(br, ApiResult.class);

                if (result.getIsFound()) {
                    this.species = result.getSpecies();
                    this.species.setIdElastic(result.getId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.activity.getResources().getString(R.string.exception), e.toString());
        }
        return this.species;
    }

    @Override
    protected void onPostExecute(Species species) {
        this.listener.onResultsFound(species);
    }
}
