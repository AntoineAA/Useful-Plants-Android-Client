package org.tic.floris.floristic.Models;

import java.util.List;

public class FacetCategory {

    private String name;
    private List<Facet> facets;
    private Facet selected;

    public FacetCategory(String name, List<Facet> facets, Facet selected) {
        this.name = name;
        this.facets = facets;
        this.selected = selected;
    }

    public String getName() {
        return this.name;
    }

    public List<Facet> getFacets() {
        return this.facets;
    }

    public Facet getSelected() {
        return this.selected;
    }
}
