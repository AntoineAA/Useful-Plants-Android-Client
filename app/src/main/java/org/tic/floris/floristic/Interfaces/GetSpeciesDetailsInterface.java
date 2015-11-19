package org.tic.floris.floristic.Interfaces;

import org.tic.floris.floristic.Models.Species;

public interface GetSpeciesDetailsInterface {

    /**
     * callback that fires when result was found
     */
    public void onResultsFound(Species species);
}
