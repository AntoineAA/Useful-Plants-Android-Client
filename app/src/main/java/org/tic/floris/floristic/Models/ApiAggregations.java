package org.tic.floris.floristic.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiAggregations {

    @SerializedName("genera")
    private ApiBucket genera;

    @SerializedName("families")
    private ApiBucket families;

    @SerializedName("usages")
    private ApiBucket usages;

    @SerializedName("sub_usages")
    private ApiBucket subUsages;

    @SerializedName("pnet")
    private ApiBucket projects;

    @SerializedName("traits")
    private ApiBucketTrait traits;

    public List<Facet> getGeneraFacets() {
        if (this.genera != null) {
            return this.genera.getFacets();
        }
        return null;
    }

    public List<Facet> getFamiliesFacets() {
        if (this.families != null) {
            return this.families.getFacets();
        }
        return null;
    }

    public List<Facet> getUsagesFacets() {
        if (this.usages != null) {
            return this.usages.getFacets();
        }
        return null;
    }

    public List<Facet> getSubUsagesFacets() {
        if (this.subUsages != null) {
            return this.subUsages.getFacets();
        }
        return null;
    }

    public List<Facet> getProjectsFacets() {
        if (this.projects != null) {
            return this.projects.getFacets();
        }
        return null;
    }

    public List<FacetTrait> getTraitsFacets() {
        if (this.traits != null && this.traits.getFacets() != null) {
            return this.traits.getFacets();
        }
        return null;
    }
}
