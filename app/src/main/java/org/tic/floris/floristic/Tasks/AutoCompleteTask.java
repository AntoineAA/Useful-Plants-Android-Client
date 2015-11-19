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
import org.tic.floris.floristic.Adapters.SearchDropDownAdapter;
import org.tic.floris.floristic.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AutoCompleteTask extends AsyncTask<Integer, Integer, List<String>> {

    private static final int TIMEOUT = 10; // seconds

    private Activity activity;
    private SearchDropDownAdapter adapter;

    private String search;

    private String url;

    public AutoCompleteTask(Activity activity, SearchDropDownAdapter adapter, String search) {
        this.activity = activity;
        this.adapter = adapter;

        this.search = search;

        String apiRoot = this.activity.getResources().getString(R.string.api_url);
        this.url = this.activity.getResources().getString(R.string.api_species_autocomplete, apiRoot);
    }

    @Override
    protected List<String> doInBackground(Integer... params) {
        try {
            if (this.search != null && !this.search.equals("")) {
                this.url += URLEncoder.encode(this.search, "utf-8");
            } else {
                return null;
            }

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT * 1000);
            HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT * 1000);

            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = client.execute(httpGet);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();

                InputStream content = entity.getContent();
                Reader reader = new InputStreamReader(content);
                BufferedReader br = new BufferedReader(reader);

                Gson gson = new Gson();
                ArrayList<String> results = gson.fromJson(br, ArrayList.class);

                if (results != null) {
                    return results;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.activity.getResources().getString(R.string.exception), e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<String> res) {
        List<String> results = new ArrayList<>();
        if (res != null && res.size() > 0) {
            results.addAll(res);
        }
        this.adapter.addResults(results);
    }
}
