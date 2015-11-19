package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    @SerializedName("took")
    private int took;

    @SerializedName("hits")
    private ApiHits hits;

    @SerializedName("aggregations")
    private ApiAggregations aggregations;

    public ApiHits getHits() {
        return this.hits;
    }

    public ApiAggregations getAggregations() {
        return this.aggregations;
    }
}