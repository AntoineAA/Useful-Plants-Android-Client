package org.tic.floris.floristic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.tic.floris.floristic.Managers.WebViewManager;
import org.tic.floris.floristic.Models.Species;

import java.util.Arrays;

public class SpeciesWikiFragment extends SpeciesFragment {

    private WebView web;

    private int pageHost;
    private int pageUrl;

    private static final String[] EXTS = {
            "jpeg", "JPEG",
            "jpg", "JPG",
            "png", "PNG",
            "gif", "GIF"
    };

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SpeciesWikiFragment newInstance(int sectionNumber, Species species, String name,
                                                  int pageHost, int pageUrl) {
        SpeciesWikiFragment fragment = new SpeciesWikiFragment();
        fragment.setSpecies(species);
        fragment.setFragmentName(name);
        fragment.setPageHost(pageHost);
        fragment.setPageUrl(pageUrl);
        Bundle args = new Bundle();
        args.putInt(SpeciesFragment.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SpeciesWikiFragment() {
    }

    public void setPageHost(int pageHost) {
        this.pageHost = pageHost;
    }

    public void setPageUrl(int pageUrl) {
        this.pageUrl = pageUrl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail_wiki, container, false);

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
                if (Uri.parse(url).getHost().contains(
                        SpeciesWikiFragment.this.getActivity().getString(SpeciesWikiFragment.this.pageHost))
                        || Arrays.asList(SpeciesWikiFragment.EXTS).contains(url.substring(url.lastIndexOf(".") + 1))) {
                    return false;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                SpeciesWikiFragment.this.getActivity().startActivity(intent);
                return true;
            }
        });

        this.web.clearHistory();
        this.web.clearCache(true);
        try {
            String search = this.species.getName();
            String url = this.getActivity().getResources().getString(SpeciesWikiFragment.this.pageUrl, search);

            this.web.loadUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.getActivity().getResources().getString(R.string.exception), e.toString());
        }
    }
}
