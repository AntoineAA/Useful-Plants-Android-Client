package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FacetTrait {

    @SerializedName("key")
    private String value;

    @SerializedName("doc_count")
    private int count;

    @SerializedName("values")
    private ApiFacetTraits facets;

    public String getValue() {
        return this.value;
    }

    public int getCount() {
        return this.count;
    }

    public List<Facet> getFacets() {
        if (this.facets != null) {
            return this.facets.getFacets();
        }
        return null;
    }

    public class ApiFacetTraits {

        @SerializedName("buckets")
        private List<Facet> facets;

        public List<Facet> getFacets() {
            return this.facets;
        }
    }
}
