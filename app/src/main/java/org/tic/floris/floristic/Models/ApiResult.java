package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

public class ApiResult {

    @SerializedName("_id")
    private String id;

    @SerializedName("_source")
    private Species species;

    @SerializedName("found")
    private Boolean isFound;

    public String getId() {
        return this.id;
    }

    public Species getSpecies() {
        return this.species;
    }

    public Boolean getIsFound() {
        return this.isFound;
    }
}
