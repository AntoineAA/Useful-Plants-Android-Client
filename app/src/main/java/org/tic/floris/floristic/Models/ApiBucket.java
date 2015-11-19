package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiBucket {

    @SerializedName("buckets")
    private List<Facet> facets;

    public List<Facet> getFacets() {
        return this.facets;
    }
}
