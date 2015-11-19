package org.tic.floris.floristic.Views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.tic.floris.floristic.Interfaces.GetLocalityInterface;
import org.tic.floris.floristic.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocalityDialogFragment extends DialogFragment implements
        DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {

    private GetLocalityInterface listener;

    private AutoCompleteTextView actxtLocality;

    private String locality;
    private Location location;

    public LocalityDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_locality, null);

        final AutoCompleteLocalityAdapter adapter = new AutoCompleteLocalityAdapter(getActivity());
        this.actxtLocality = (AutoCompleteTextView) view.findViewById(R.id.locality_text);
        this.actxtLocality.setAdapter(adapter);

        this.actxtLocality.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Address address = adapter.getItem(position);
                if (address != null && address.hasLatitude() && address.hasLongitude()) {
                    LocalityDialogFragment.this.location = new Location("");
                    LocalityDialogFragment.this.location.setLongitude(address.getLongitude());
                    LocalityDialogFragment.this.location.setLatitude(address.getLatitude());
                }
            }
        });

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle(getString(R.string.locality));

        alertDialogBuilder.setPositiveButton(
                getActivity().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        alertDialogBuilder.setNegativeButton(
                getActivity().getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (LocalityDialogFragment.this.listener != null) {
                            LocalityDialogFragment.this.listener.onLocalitySelected(null, null);
                        }
                    }
                });

        return alertDialogBuilder.create();
    }

    public void setLocalitySelectedListener(GetLocalityInterface listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateLocality();
                    dismiss();
                }
            });
        }
    }

    private void updateLocality() {
        this.locality = this.actxtLocality.getText().toString();
        if (this.listener != null) {
            if (this.locality != null && this.location != null) {
                this.listener.onLocalitySelected(this.location, this.locality);
            } else {
                this.listener.onLocalitySelected(null, null);
            }
        }
    }

    private class AutoCompleteLocalityAdapter extends ArrayAdapter<Address> implements Filterable {

        private LayoutInflater inflater;
        private Geocoder geocoder;
        private StringBuilder sb = new StringBuilder();

        public AutoCompleteLocalityAdapter(final Context context) {
            super(context, R.layout.item_locality_dropdown);
            this.inflater = LayoutInflater.from(context);
            this.geocoder = new Geocoder(context, Locale.getDefault());
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final TextView tv;

            if (convertView != null) {
                tv = (TextView) convertView;
            } else {
                tv = (TextView) this.inflater.inflate(R.layout.item_locality_dropdown, parent, false);
            }

            tv.setText(createFormattedAddressFromAddress(getItem(position)));
            return tv;
        }

        private String createFormattedAddressFromAddress(final Address address) {
            this.sb.setLength(0);
            this.sb.append(address.getLocality());
            return this.sb.toString();
        }

        @Override
        public Filter getFilter() {

            return new Filter() {
                @Override
                protected FilterResults performFiltering(final CharSequence constraint) {
                    List<Address> addressList = null;
                    if (constraint != null && constraint.length() > 2) {
                        try {
                            addressList = geocoder.getFromLocationName(constraint.toString(), 10);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(getActivity().getResources().getString(R.string.exception), e.toString());
                        }
                    }

                    if (addressList == null) {
                        addressList = new ArrayList<>();
                    }

                    List<String> checked = new ArrayList<>();
                    List<Address> addresses = new ArrayList<>();
                    for (Address a : addressList) {
                        if (a.getLocality() != null && !checked.contains(a.getLocality())) {
                            checked.add(a.getLocality());
                            addresses.add(a);
                        }
                    }

                    final FilterResults filterResults = new FilterResults();
                    filterResults.values = addresses;
                    filterResults.count = addresses.size();
                    return filterResults;
                }

                @Override
                protected void publishResults(final CharSequence constraint, final FilterResults results) {
                    clear();

                    if (results != null && results.count > 0) {
                        for (Address address : (List<Address>) results.values) {
                            add(address);
                        }

                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }

                @Override
                public CharSequence convertResultToString(final Object resultValue) {
                    return resultValue == null ? "" : ((Address) resultValue).getLocality();
                }
            };
        }
    }
}
