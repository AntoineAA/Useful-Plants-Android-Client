package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiBucketTrait {

    @SerializedName("traits")
    private ApiBuckedTraits bucket;

    public List<FacetTrait> getFacets() {
        if (this.bucket != null) {
            return this.bucket.getFacets();
        }
        return null;
    }

    public class ApiBuckedTraits {

        @SerializedName("buckets")
        private List<FacetTrait> facets;

        public List<FacetTrait> getFacets () {
            return this.facets;
        }
    }
}
