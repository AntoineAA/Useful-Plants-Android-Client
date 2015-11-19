package org.tic.floris.floristic;

import android.support.v4.app.Fragment;

import org.tic.floris.floristic.Models.Species;

public abstract class SpeciesFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    protected static final String ARG_SECTION_NUMBER = "section_number";

    protected Species species;
    protected String fragmentName;

    protected void setSpecies(Species species) {
        this.species = species;
    }

    protected void setFragmentName(String name) {
        this.fragmentName = name;
    }

    public String getFragmentName() {
        return this.fragmentName;
    }

    public boolean customOnBackPressed() {
        return false;
    }
}
