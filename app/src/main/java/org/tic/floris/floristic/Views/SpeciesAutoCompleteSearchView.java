package org.tic.floris.floristic.Views;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class SpeciesAutoCompleteSearchView extends SearchView {

    private SearchAutoComplete searchAutoComplete;

    private void init() {
        this.searchAutoComplete = (SearchAutoComplete) this.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        this.searchAutoComplete.setAdapter(null);
        this.searchAutoComplete.setOnItemSelectedListener(null);
    }

    public SpeciesAutoCompleteSearchView(Context context) {
        super(context);
        init();
    }

    public SpeciesAutoCompleteSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpeciesAutoCompleteSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.searchAutoComplete.setOnItemClickListener(listener);
    }

    public void setAdapter(ArrayAdapter<?> adapter) {
        this.searchAutoComplete.setAdapter(adapter);
    }
}
