package org.tic.floris.floristic.Interfaces;

import org.tic.floris.floristic.Models.ApiAggregations;

public interface GetSpeciesInterface {

    /**
     * callback that fires when results were found
     */
    public void onResultsFound();

    /**
     * callback that fires when no results were found
     */
    public void onNoResultsFound();

    /**
     * callback that fires when new facets were loaded
     *
     * @param aggregations the object that contains facet's categories and facets
     */
    public void onLoadFacets(ApiAggregations aggregations);
}
