package org.tic.floris.floristic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tic.floris.floristic.Models.Species;
import org.tic.floris.floristic.Models.Trait;
import org.tic.floris.floristic.Models.Use;

public class SpeciesDataFragment extends SpeciesFragment {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SpeciesDataFragment newInstance(int sectionNumber, Species species, String name) {
        SpeciesDataFragment fragment = new SpeciesDataFragment();
        fragment.setSpecies(species);
        fragment.setFragmentName(name);
        Bundle args = new Bundle();
        args.putInt(SpeciesFragment.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SpeciesDataFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail_data, container, false);

        String divider = "\n";

        TextView txtName = (TextView) view.findViewById(R.id.txt_name);
        txtName.setText(this.species.getName());

        TextView txtAuthor = (TextView) view.findViewById(R.id.txt_author);
        txtAuthor.setText(this.species.getAuthor());

        if (this.species.getFamily() != null) {
            TextView txtFamilyName = (TextView) view.findViewById(R.id.txt_family_name);
            txtFamilyName.setText(this.species.getFamily().getName());

            TextView txtFamilyAuthor = (TextView) view.findViewById(R.id.txt_family_author);
            txtFamilyAuthor.setText(this.species.getFamily().getAuthor());
        }

        if (this.species.getGenus() != null) {
            TextView txtGenusName = (TextView) view.findViewById(R.id.txt_genus_name);
            txtGenusName.setText(this.species.getGenus().getName());

            TextView txtGenusAuthor = (TextView) view.findViewById(R.id.txt_genus_author);
            txtGenusAuthor.setText(this.species.getGenus().getAuthor());

            TextView txtGenusCommonNames = (TextView) view.findViewById(R.id.txt_genus_common_names);
            if (this.species.getGenus().getCommon("en") != null && this.species.getGenus().getCommon("en").getNamesAsString(divider).length() > 0) {
                txtGenusCommonNames.setText(this.species.getGenus().getCommon("en").getNamesAsString(divider));
            } else {
                txtGenusCommonNames.setVisibility(View.GONE);
            }
        }

        TextView txtCommonNames = (TextView) view.findViewById(R.id.txt_common_names);
        if (this.species.getCommon("en") != null && this.species.getCommon("en").getNamesAsString(divider).length() > 0) {
            txtCommonNames.setText(this.species.getCommon("en").getNamesAsString(divider));
        } else {
            txtCommonNames.setVisibility(View.GONE);
        }

        LinearLayout llUses = (LinearLayout) view.findViewById(R.id.layout_uses);
        if (this.species.getUses() != null && this.species.getUses().size() > 0) {
            for (Use u : this.species.getUses()) {
                TextView txtLabel = new TextView(this.getActivity());

                txtLabel.setText(u.getUse());
                txtLabel.setTextColor(this.getActivity().getResources().getColor(R.color.normal_grey));
                llUses.addView(txtLabel);

                if (u.getNamesAsString(divider).length() > 0) {
                    TextView txtUses = new TextView(this.getActivity());
                    txtUses.setText(u.getNamesAsString(divider));
                    txtUses.setTextColor(this.getActivity().getResources().getColor(R.color.normal_grey));

                    int px = this.getActivity().getResources().getDimensionPixelOffset(R.dimen.default_card_padding);

                    txtUses.setPadding(px, 0, 0, px);
                    llUses.addView(txtUses);
                }
            }
        } else {
            llUses.setVisibility(View.GONE);
        }

        TextView txtProjects = (TextView) view.findViewById(R.id.txt_projects);
        if (this.species.getProjectsAsString(divider).length() > 0) {
            txtProjects.setText(this.species.getProjectsAsString(divider));
        } else {
            txtProjects.setVisibility(View.GONE);
        }

        LinearLayout llTraits = (LinearLayout) view.findViewById(R.id.layout_traits);
        if (this.species.getTraits() != null && this.species.getTraits().size() > 0) {
            for (Trait t : this.species.getTraits()) {
                View itemView = inflater.inflate(R.layout.item_trait, null);

                TextView txtLabel = (TextView) itemView.findViewById(R.id.txt_trait);
                LinearLayout layoutTraits = (LinearLayout) itemView.findViewById(R.id.layout_traits);

                txtLabel.setText(t.getName());

                if (t.getValuesAsString(divider).length() > 0) {
                    TextView txtTraits = new TextView(this.getActivity());
                    txtTraits.setText(t.getValuesAsString(divider));
                    txtTraits.setTextColor(this.getActivity().getResources().getColor(R.color.normal_grey));

                    layoutTraits.addView(txtTraits);
                }

                llTraits.addView(itemView);
            }
        } else {
            llTraits.setVisibility(View.GONE);
        }

        return view;
    }
}
