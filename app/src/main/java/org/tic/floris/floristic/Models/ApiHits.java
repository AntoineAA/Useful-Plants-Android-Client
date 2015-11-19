package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ApiHits {

    @SerializedName("total")
    private int total;

    @SerializedName("results")
    private List<ApiResult> results;

    public int getTotal() {
        return this.total;
    }

    public List<ApiResult> getResults() {
        return this.results;
    }

    public List<Species> getSpecies() {
        if (this.results == null || this.results.size() == 0) {
            return null;
        }
        List<Species> sps = new ArrayList<>();
        for (ApiResult r : this.results) {
            Species sp = r.getSpecies();
            sp.setIdElastic(r.getId());
            sps.add(sp);
        }
        return sps;
    }
}