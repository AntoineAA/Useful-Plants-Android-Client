package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

public class Facet {

    @SerializedName("key")
    private String value;

    @SerializedName("doc_count")
    private int count;

    public String getValue() {
        return this.value;
    }

    public int getCount() {
        return this.count;
    }
}
