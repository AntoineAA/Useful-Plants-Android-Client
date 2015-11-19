package org.tic.floris.floristic;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;

import org.tic.floris.floristic.Adapters.ImageGridAdapter;
import org.tic.floris.floristic.Models.Species;

public class SpeciesGalleryFragment extends SpeciesFragment {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SpeciesGalleryFragment newInstance(int sectionNumber, Species species, String name) {
        SpeciesGalleryFragment fragment = new SpeciesGalleryFragment();
        fragment.setSpecies(species);
        fragment.setFragmentName(name);
        Bundle args = new Bundle();
        args.putInt(SpeciesFragment.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SpeciesGalleryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail_gallery, container, false);

        GridView grid = (GridView) view.findViewById(R.id.grid);
        ImageGridAdapter adapter = new ImageGridAdapter(this.getActivity(), this.species);
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Gson gson = new Gson();

                Intent intent = new Intent(SpeciesGalleryFragment.this.getActivity(), GalleryActivity.class);
                intent.putExtra(BrowserActivity.SPECIES_OBJECT, gson.toJson(SpeciesGalleryFragment.this.species));

                intent.putExtra(GalleryActivity.ARG_START_POS, position);

                SpeciesGalleryFragment.this.getActivity().startActivity(intent);
                SpeciesGalleryFragment.this.getActivity().overridePendingTransition(R.anim.top_in, R.anim.non_mobile);
            }
        });

        return view;
    }
}
