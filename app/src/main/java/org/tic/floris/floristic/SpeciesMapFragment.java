package org.tic.floris.floristic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.tic.floris.floristic.Managers.WebViewManager;
import org.tic.floris.floristic.Models.Species;

public class SpeciesMapFragment extends SpeciesFragment {

    private static final String INTERFACE_NAME = "FloristicInterface";
    private static final String HTML_CONTENT = "file:///android_asset/views/index.html";

    private WebView web;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SpeciesMapFragment newInstance(int sectionNumber, Species species, String name) {
        SpeciesMapFragment fragment = new SpeciesMapFragment();
        fragment.setSpecies(species);
        fragment.setFragmentName(name);
        Bundle args = new Bundle();
        args.putInt(SpeciesFragment.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SpeciesMapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail_map, container, false);

        this.web = (WebView) view.findViewById(R.id.web);
        WebViewManager.disableHardwareAcceleration(this.web);
        this.load();

        return view;
    }

    @Override
    public boolean customOnBackPressed() {
        if (this.web.canGoBack()) {
            this.web.goBack();
            return true;
        }
        return false;
    }

    private void load() {
        this.web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getHost().equals(SpeciesMapFragment.HTML_CONTENT)) {
                    return false;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                SpeciesMapFragment.this.getActivity().startActivity(intent);
                return true;
            }
        });

        this.web.clearHistory();
        this.web.clearCache(true);

        this.web.addJavascriptInterface(new WebAppInterface(this.species), SpeciesMapFragment.INTERFACE_NAME);

        this.web.getSettings().setJavaScriptEnabled(true);
        this.web.loadUrl(SpeciesMapFragment.HTML_CONTENT);
    }

    /**
     * custom JavaScript interface. allows communication between JavaScript and Java
     */
    private class WebAppInterface {
        Species species;

        /**
         * instantiate the interface and set the context
         */
        WebAppInterface(Species species) {
            this.species = species;
        }

        /**
         * loads the current Gbif key
         *
         * @return returns the current Gbif key for the species
         */
        @JavascriptInterface
        public String getSpeciesGbifKey() {
            if (this.species.getGbifKey() != null) {
                return this.species.getGbifKey().toString();
            }
            return "";
        }
    }
}
