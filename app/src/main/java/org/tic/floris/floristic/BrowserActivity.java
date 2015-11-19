package org.tic.floris.floristic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.tic.floris.floristic.Adapters.FacetAdapter;
import org.tic.floris.floristic.Adapters.FacetCategoryAdapter;
import org.tic.floris.floristic.Adapters.FacetTraitAdapter;
import org.tic.floris.floristic.Adapters.SearchDropDownAdapter;
import org.tic.floris.floristic.Adapters.SortByAdapter;
import org.tic.floris.floristic.Adapters.SpeciesAdapter;
import org.tic.floris.floristic.Interfaces.GetLocalityInterface;
import org.tic.floris.floristic.Interfaces.GetSpeciesInterface;
import org.tic.floris.floristic.Listeners.EndlessScrollListener;
import org.tic.floris.floristic.Managers.ActivityManager;
import org.tic.floris.floristic.Managers.GpsManager;
import org.tic.floris.floristic.Managers.IntegerDisplayManager;
import org.tic.floris.floristic.Models.ApiAggregations;
import org.tic.floris.floristic.Models.Facet;
import org.tic.floris.floristic.Models.FacetCategory;
import org.tic.floris.floristic.Models.FacetTrait;
import org.tic.floris.floristic.Models.SortBy;
import org.tic.floris.floristic.Models.Species;
import org.tic.floris.floristic.Tasks.AutoCompleteTask;
import org.tic.floris.floristic.Tasks.GetSpeciesTask;
import org.tic.floris.floristic.Views.LocalityDialogFragment;
import org.tic.floris.floristic.Views.SpeciesAutoCompleteSearchView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowserActivity extends ActionBarActivity implements GetSpeciesInterface,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GetLocalityInterface {

    private final static int START = 0;
    private final static int OFFSET = 50;
    private final static int VISIBLE_THRESHOLD = 5;

    // ------- ------- ------- ------- ------- ------- -------

    public static final String SPECIES_OBJECT = "species_object";

    // ------- ------- ------- ------- ------- ------- -------

    private SwipeRefreshLayout swipeRefreshLayout;

    // ------- ------- ------- ------- ------- ------- -------

    private DrawerLayout layoutDrawer; // content

    private View layoutRefresh; // layout to display when connexion is not available
    private Button btnRefresh; // refresh button to reload data

    private View layoutContent; // layout to display normal content

    private Button btnDrawerRight; // button to open the right panel
    private Button btnDrawerLeft; // button to open the left panel

    private ListView listSpecies; // species list
    private SpeciesAdapter adapterSpecies;

    private TextView txtCount; // species count

    // ------- ------- ------- ------- ------- ------- -------

    private Facet apiFamily = null; // current facet for family field
    private Facet apiGenus = null; // current facet for genus field
    private Facet apiUsageCat = null; // current facet for usage category field
    private Facet apiUsageType = null; // current facet for usage type field
    private Facet apiProject = null; // current facet for project field

    private Boolean apiFlowers = false;
    private Boolean apiFruits = false;
    private Boolean apiLeaves = false;

    private String apiSearch = null; // current search from the search view in the action bar

    private Location apiLocation = null;

    private Map<String, Facet> apiTraits = new HashMap<>();

    //private List<FacetCategoryTrait> apiTraits = new ArrayList<>();

    // ------- ------- ------- ------- ------- ------- -------

    private List<FacetCategory> facets = null; // list of available facet's categories
    private ListView listFacetCategories;
    private FacetCategoryAdapter adapterFacetCategory;

    private List<SortBy> sorts; // list of available sorts
    private Spinner spinSortsBy;
    private SortByAdapter adapterSortBy;

    private CheckBox cbGeography;
    private CheckBox cbCustomGeography;

    private CheckBox cbFlowers;
    private CheckBox cbFruits;
    private CheckBox cbLeaves;

    // ------- ------- ------- ------- ------- ------- -------

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location location;

    private Button btnGps;

    // ------- ------- ------- ------- ------- ------- -------

    private Boolean isFirstRun = true;

    // ------- ------- ------- ------- ------- ------- -------

    private List<FacetCategory> facetsTraits = null;  // list of available facet's traits
    private ListView listFacetTraits;
    private FacetTraitAdapter adapterFacetTrait;

    // ------- ------- ------- ------- ------- ------- -------

    private MenuItem menuMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        this.swipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipt_to_refresh);

        this.layoutDrawer = (DrawerLayout) this.findViewById(R.id.layout_drawer);

        this.layoutRefresh = this.findViewById(R.id.layout_refresh);
        this.btnRefresh = (Button) this.findViewById(R.id.btn_refresh);

        this.layoutContent = this.findViewById(R.id.layout_content);

        this.btnDrawerRight = (Button) this.findViewById(R.id.btn_drawer_right);
        this.btnDrawerLeft = (Button) this.findViewById(R.id.btn_drawer_left);

        this.listSpecies = (ListView) this.findViewById(R.id.list_species);
        this.listSpecies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Species sp = BrowserActivity.this.adapterSpecies.getItem(position);

                Gson gson = new Gson();

                Intent intent = new Intent(BrowserActivity.this, DetailActivity.class);
                intent.putExtra(BrowserActivity.SPECIES_OBJECT, gson.toJson(sp));

                BrowserActivity.this.startActivity(intent);
                BrowserActivity.this.overridePendingTransition(R.anim.left_side_in, R.anim.non_mobile);
            }
        });

        this.txtCount = (TextView) this.findViewById(R.id.txt_count);

        // ------- ------- ------- ------- ------- ------- -------

        this.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowserActivity.this.initSpeciesList();
            }
        });

        this.btnDrawerRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowserActivity.this.layoutDrawer.openDrawer(Gravity.END);
            }
        });

        this.btnDrawerLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowserActivity.this.layoutDrawer.openDrawer(Gravity.START);
            }
        });

        // ------- ------- ------- ------- ------- ------- -------

        this.listFacetCategories = (ListView) this.findViewById(R.id.list_facet_categories);
        this.listFacetCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FacetCategory c = BrowserActivity.this.adapterFacetCategory.getItem(position);

                if (c.getSelected() != null) {
                    if (c.getName().equals(BrowserActivity.this.getString(R.string.facet_family))) {
                        BrowserActivity.this.apiFamily = null;
                    } else if (c.getName().equals(BrowserActivity.this.getString(R.string.facet_genus))) {
                        BrowserActivity.this.apiGenus = null;
                    } else if (c.getName().equals(BrowserActivity.this.getString(R.string.facet_usage_cat))) {
                        BrowserActivity.this.apiUsageCat = null;
                    } else if (c.getName().equals(BrowserActivity.this.getString(R.string.facet_usage_type))) {
                        BrowserActivity.this.apiUsageType = null;
                    } else if (c.getName().equals(BrowserActivity.this.getString(R.string.facet_project))) {
                        BrowserActivity.this.apiProject = null;
                    }

                    BrowserActivity.this.initSpeciesList();
                } else {
                    if (c.getFacets().size() > 1) {
                        BrowserActivity.this.initFacetChooser(c);
                    }
                }

                Log.i(BrowserActivity.this.getString(R.string.exception), "facet: " + c.getName());
            }
        });

        this.spinSortsBy = (Spinner) this.findViewById(R.id.spin_sorts);
        this.spinSortsBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SortBy sb = BrowserActivity.this.adapterSortBy.getItem(position);
                if (!sb.getIsSelected()) {
                    for (SortBy s : BrowserActivity.this.sorts) {
                        s.setIsSelected(false);
                    }
                    sb.setIsSelected(true);
                }
                if (!BrowserActivity.this.isFirstRun) {
                    BrowserActivity.this.adapterSortBy.notifyDataSetChanged();
                    BrowserActivity.this.initSpeciesList();
                }
                BrowserActivity.this.isFirstRun = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // ------- ------- ------- ------- ------- ------- -------

        this.cbGeography = (CheckBox) this.findViewById(R.id.cb_geography);
        this.cbGeography.setClickable(true);
        this.cbGeography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BrowserActivity.this.location != null) {
                    if (BrowserActivity.this.cbGeography.isChecked()) {
                        BrowserActivity.this.apiLocation = BrowserActivity.this.location;
                        BrowserActivity.this.cbCustomGeography.setChecked(false);
                        BrowserActivity.this.cbCustomGeography.setText(R.string.search_locality);
                    } else {
                        BrowserActivity.this.apiLocation = null;
                    }
                    BrowserActivity.this.initSpeciesList();
                } else {
                    BrowserActivity.this.cbGeography.setEnabled(false);
                }
            }
        });

        this.cbCustomGeography = (CheckBox) this.findViewById(R.id.cb_custom_geography);
        this.cbCustomGeography.setClickable(true);
        this.cbCustomGeography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BrowserActivity.this.cbCustomGeography.isChecked()) {
                    LocalityDialogFragment dialog = new LocalityDialogFragment();
                    dialog.setCancelable(false);
                    dialog.setLocalitySelectedListener(BrowserActivity.this);
                    dialog.show(BrowserActivity.this.getSupportFragmentManager(), "fragment_locality");
                } else {
                    BrowserActivity.this.apiLocation = null;
                    BrowserActivity.this.cbCustomGeography.setText(R.string.search_locality);
                    BrowserActivity.this.initSpeciesList();
                }
            }
        });

        // ------- ------- ------- ------- ------- ------- -------

        this.cbFlowers = (CheckBox) this.findViewById(R.id.cb_flowers);
        this.cbFlowers.setClickable(true);
        this.cbFlowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BrowserActivity.this.cbFlowers.isChecked()) {
                    BrowserActivity.this.apiFlowers = true;
                } else {
                    BrowserActivity.this.apiFlowers = false;
                }
                BrowserActivity.this.initSpeciesList();
            }
        });

        this.cbFruits = (CheckBox) this.findViewById(R.id.cb_fruits);
        this.cbFruits.setClickable(true);
        this.cbFruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BrowserActivity.this.cbFruits.isChecked()) {
                    BrowserActivity.this.apiFruits = true;
                } else {
                    BrowserActivity.this.apiFruits = false;
                }
                BrowserActivity.this.initSpeciesList();
            }
        });

        this.cbLeaves = (CheckBox) this.findViewById(R.id.cb_leaves);
        this.cbLeaves.setClickable(true);
        this.cbLeaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BrowserActivity.this.cbLeaves.isChecked()) {
                    BrowserActivity.this.apiLeaves = true;
                } else {
                    BrowserActivity.this.apiLeaves = false;
                }
                BrowserActivity.this.initSpeciesList();
            }
        });

        // ------- ------- ------- ------- ------- ------- -------

        this.swipeRefreshLayout.setColorSchemeResources(R.color.color_primary);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BrowserActivity.this.initSpeciesList();
            }
        });

        // ------- ------- ------- ------- ------- ------- -------

        this.listFacetTraits = (ListView) this.findViewById(R.id.list_traits);
        this.listFacetTraits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FacetCategory c = BrowserActivity.this.adapterFacetTrait.getItem(position);

                if (c.getSelected() != null && BrowserActivity.this.apiTraits.containsKey(c.getName())) {
                    BrowserActivity.this.apiTraits.remove(c.getName());
                    BrowserActivity.this.initSpeciesList();
                } else {
                    if (c.getFacets().size() > 0) {
                        BrowserActivity.this.initFacetTraitChooser(c);
                    }
                }

                Log.i(BrowserActivity.this.getString(R.string.exception), "facet: " + c.getName());
            }
        });

        // ------- ------- ------- ------- ------- ------- -------

        this.initSorts();
        this.initSpeciesList();

        // ------- ------- ------- ------- ------- ------- -------

        this.btnGps = (Button) this.findViewById(R.id.btn_gps);
        if (GpsManager.hasGPSDevice(this)) {
            this.btnGps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GpsManager.startGPSIntent(BrowserActivity.this);
                }
            });
        } else {
            this.btnGps.setVisibility(View.GONE);
        }

        this.googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        this.locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.googleApiClient.connect();
        if (!GpsManager.isEnableGPS(this)) {
            if (this.apiLocation != null && this.cbGeography.isChecked()) {
                this.cbGeography.setChecked(false);
                this.apiLocation = null;
                this.initSpeciesList();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(this.googleApiClient, this);
            this.googleApiClient.disconnect();
        }
    }

    /**
     * initializes the species list with enabled filters (facets or searched string)
     * resets some views (refresh, footer, panel, species count)
     */
    private void initSpeciesList() {
        this.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                BrowserActivity.this.swipeRefreshLayout.setRefreshing(true);
            }
        });

        this.initLayouts();

        List<Species> species = new ArrayList<>();
        this.adapterSpecies = new SpeciesAdapter(this, species, this.location) {
            @Override
            public void onAddData() {
                BrowserActivity.this.txtCount.setText(
                        BrowserActivity.this.getResources().getString(
                                R.string.species_count,
                                IntegerDisplayManager.getDecimalFormat(this.getTotalDataCount(), ' '))
                );
            }
        };

        this.listSpecies.setAdapter(this.adapterSpecies);
        this.listSpecies.setOnScrollListener(new EndlessScrollListener(BrowserActivity.VISIBLE_THRESHOLD, BrowserActivity.START) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                BrowserActivity.this.loadDataFromApi(page);
            }
        });

        this.loadDataFromApi(0);
    }

    /**
     * initializes some views (refresh, species list, panel)
     */
    private void initLayouts() {
        if (this.layoutDrawer.isDrawerOpen(Gravity.END)) {
            this.layoutDrawer.closeDrawer(Gravity.END);
        }

        if (this.layoutDrawer.isDrawerOpen(Gravity.START)) {
            this.layoutDrawer.closeDrawer(Gravity.START);
        }

        this.layoutRefresh.setVisibility(View.GONE);
        this.layoutContent.setVisibility(View.VISIBLE);

        this.txtCount.setText(this.getString(R.string.species));

        if (this.menuMap != null) {
            if (this.apiLocation != null && this.listSpecies.getAdapter().getCount() > 0) {
                this.menuMap.setVisible(true);
            } else {
                this.menuMap.setVisible(false);
            }
        }
    }

    /**
     * displays the refresh view
     */
    private void displayRefreshLayout() {
        if (this.layoutDrawer.isDrawerOpen(Gravity.END)) {
            this.layoutDrawer.closeDrawer(Gravity.END);
        }

        if (this.layoutDrawer.isDrawerOpen(Gravity.START)) {
            this.layoutDrawer.closeDrawer(Gravity.START);
        }

        this.layoutRefresh.setVisibility(View.VISIBLE);
        this.layoutContent.setVisibility(View.GONE);
    }

    /**
     * initializes the "sort by" list
     */
    private void initSorts() {
        this.sorts = new ArrayList<>();
        this.sorts.add(new SortBy(
                this.getResources().getString(R.string.api_species_sort_by_species_label),
                this.getResources().getString(R.string.api_species_sort_by_species_value), true));
        this.sorts.add(new SortBy(
                this.getResources().getString(R.string.api_species_sort_by_family_label),
                this.getResources().getString(R.string.api_species_sort_by_family_value), false));
        this.sorts.add(new SortBy(
                this.getResources().getString(R.string.api_species_sort_by_score_label),
                this.getResources().getString(R.string.api_species_sort_by_score_value), false));

        this.adapterSortBy = new SortByAdapter(this, this.sorts);
        this.adapterSortBy.setDropDownViewResource(R.layout.item_sort_drawer);
        this.spinSortsBy.setAdapter(this.adapterSortBy);
    }

    private void resetApiFields() {
        this.apiFamily = null;
        this.apiGenus = null;
        this.apiUsageCat = null;
        this.apiUsageType = null;
        this.apiProject = null;

        this.apiFlowers = false;
        this.apiFruits = false;
        this.apiLeaves = false;

        this.cbFlowers.setChecked(false);
        this.cbFruits.setChecked(false);
        this.cbLeaves.setChecked(false);

        this.apiLocation = null;

        this.cbGeography.setChecked(false);
        this.cbCustomGeography.setChecked(false);
        this.cbCustomGeography.setText(R.string.search_locality);

        this.apiTraits.clear();
    }

    private GetSpeciesTask createQueryTask(int page) {
        Map<String, String> traits = null;
        if (this.apiTraits != null && this.apiTraits.size() > 0) {
            traits = new HashMap<>();
            for (Map.Entry<String, Facet> entry : this.apiTraits.entrySet()) {
                traits.put(entry.getKey(), entry.getValue().getValue());
            }
        }
        return new GetSpeciesTask(
                this, this, this.adapterSpecies,
                (page * BrowserActivity.OFFSET), BrowserActivity.OFFSET,
                (this.apiFamily != null) ? this.apiFamily.getValue() : null,
                (this.apiGenus != null) ? this.apiGenus.getValue() : null,
                (this.apiUsageCat != null) ? this.apiUsageCat.getValue() : null,
                (this.apiUsageType != null) ? this.apiUsageType.getValue() : null,
                (this.apiProject != null) ? this.apiProject.getValue() : null,
                traits, this.apiSearch, this.getSortBy(), this.apiLocation,
                this.apiFlowers, this.apiFruits, this.apiLeaves);
    }

    /**
     * appends more data into the adapter
     * if the connexion is not available, displays a refresh view
     *
     * @param page the page number to load
     */
    private void loadDataFromApi(int page) {
        Log.i(this.getString(R.string.exception), "page: " + page);

        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            GetSpeciesTask task = this.createQueryTask(page);
            task.execute();
        } else {
            this.listSpecies.setAdapter(null);
            this.displayRefreshLayout();
            Toast.makeText(this, this.getString(R.string.message_network_not_available), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browser, menu);

        this.menuMap = menu.findItem(R.id.action_map);
        this.menuMap.setVisible(false);

        // get the search item and the search view
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SpeciesAutoCompleteSearchView searchView = (SpeciesAutoCompleteSearchView) MenuItemCompat.getActionView(searchItem);

        List<String> results = new ArrayList<>();
        final SearchDropDownAdapter adapter = new SearchDropDownAdapter(this, results);
        searchView.setAdapter(adapter);
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String searched = adapter.getItem(position);
                searched = searched.replaceAll("<b>", "").replaceAll("</b>", "");
                searchView.setQuery(searched, false);
            }
        });

        // display a hint
        searchView.setQueryHint(getResources().getString(R.string.action_search));

        // add a submit button
        searchView.setSubmitButtonEnabled(true);

        // auto submit target
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // manage the close button
        ImageView btnClose = (ImageView) searchView.findViewById(R.id.search_close_btn);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset the text view
                EditText editText = (EditText) searchView.findViewById(R.id.search_src_text);
                editText.setText("");

                // close the search item
                MenuItemCompat.collapseActionView(searchItem);
                // clear the query
                searchView.setQuery("", false);

                // reset the species list
                BrowserActivity.this.resetApiFields();

                BrowserActivity.this.apiSearch = null;

                BrowserActivity.this.initSorts();
                BrowserActivity.this.initSpeciesList();
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (searchView.getQuery() != null && searchView.getQuery().length() > 0) {
                    BrowserActivity.this.finish();
                    return false;
                }
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                AutoCompleteTask task = new AutoCompleteTask(BrowserActivity.this, adapter, s);
                task.execute();
                return true;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ((searchView.getQuery() == null || searchView.getQuery().length() == 0)
                        && !hasFocus) {
                    MenuItemCompat.collapseActionView(searchItem);
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml

        switch (item.getItemId()) {
            case android.R.id.home:
                this.closeActivity();
                return true;
            case R.id.action_map:
                if (this.apiLocation != null && this.listSpecies.getAdapter().getCount() > 0) {
                    GetSpeciesTask task = this.createQueryTask(0);

                    try {
                        String url = task.getUrl();

                        Intent intent = new Intent(BrowserActivity.this, MapActivity.class);
                        intent.putExtra(MapActivity.LOCATION_PARAM, this.apiLocation);
                        intent.putExtra(MapActivity.URL_PARAM, url);

                        this.startActivity(intent);
                        this.overridePendingTransition(R.anim.left_side_in, R.anim.non_mobile);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Log.e(this.getResources().getString(R.string.exception), e.toString());
                    }
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * called when the search view is validated
     *
     * @param intent the intent that contains parameters
     */
    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(this.getString(R.string.exception), "query: " + query);

            this.resetApiFields();

            this.apiSearch = query;

            this.initSorts();
            this.initSpeciesList();
        }
    }

    @Override
    public void onNoResultsFound() {
        this.listSpecies.setAdapter(null);
        Toast.makeText(this, this.getString(R.string.message_no_result_found), Toast.LENGTH_LONG).show();
        this.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResultsFound() {
        this.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoadFacets(ApiAggregations aggregations) {
        Log.i(this.getString(R.string.exception), "facets loaded");

        this.facets = new ArrayList<>();
        this.facetsTraits = new ArrayList<>();

        if (aggregations.getFamiliesFacets() != null && aggregations.getFamiliesFacets().size() > 0) {
            Log.i(this.getString(R.string.exception), "families facets loaded");
            Log.i(this.getString(R.string.exception), "families facets size: " + aggregations.getFamiliesFacets().size());

            this.facets.add(new FacetCategory(this.getString(R.string.facet_family), aggregations.getFamiliesFacets(), this.apiFamily));
        } else {
            Log.i(this.getString(R.string.exception), "families facets NOT loaded");
        }

        if (aggregations.getGeneraFacets() != null && aggregations.getGeneraFacets().size() > 0) {
            Log.i(this.getString(R.string.exception), "genera facets loaded");
            Log.i(this.getString(R.string.exception), "genera facets size: " + aggregations.getGeneraFacets().size());

            this.facets.add(new FacetCategory(this.getString(R.string.facet_genus), aggregations.getGeneraFacets(), this.apiGenus));
        } else {
            Log.i(this.getString(R.string.exception), "genera facets NOT loaded");
        }

        if (aggregations.getUsagesFacets() != null && aggregations.getUsagesFacets().size() > 0) {
            Log.i(this.getString(R.string.exception), "usages facets loaded");
            Log.i(this.getString(R.string.exception), "usages facets size: " + aggregations.getUsagesFacets().size());

            this.facets.add(new FacetCategory(this.getString(R.string.facet_usage_cat), aggregations.getUsagesFacets(), this.apiUsageCat));
        } else {
            Log.i(this.getString(R.string.exception), "usages facets NOT loaded");
        }

        if (aggregations.getSubUsagesFacets() != null && aggregations.getSubUsagesFacets().size() > 0) {
            Log.i(this.getString(R.string.exception), "sub-usages facets loaded");
            Log.i(this.getString(R.string.exception), "sub-usages facets size: " + aggregations.getSubUsagesFacets().size());

            this.facets.add(new FacetCategory(this.getString(R.string.facet_usage_type), aggregations.getSubUsagesFacets(), this.apiUsageType));
        } else {
            Log.i(this.getString(R.string.exception), "sub-usages facets NOT loaded");
        }

        if (aggregations.getProjectsFacets() != null && aggregations.getProjectsFacets().size() > 0) {
            Log.i(this.getString(R.string.exception), "projects facets loaded");
            Log.i(this.getString(R.string.exception), "projects facets size: " + aggregations.getProjectsFacets().size());

            this.facets.add(new FacetCategory(this.getString(R.string.facet_project), aggregations.getProjectsFacets(), this.apiProject));
        } else {
            Log.i(this.getString(R.string.exception), "projects facets NOT loaded");
        }

        if (aggregations.getTraitsFacets() != null && aggregations.getTraitsFacets().size() > 0) {
            for (FacetTrait f : aggregations.getTraitsFacets()) {
                Facet selected = null;
                if (this.apiTraits.containsKey(f.getValue())) {
                    selected = this.apiTraits.get(f.getValue());
                }
                this.facetsTraits.add(new FacetCategory(f.getValue(), f.getFacets(), selected));
            }
        }

        if (this.facets.size() == 0) {
            this.facets = null;
        }

        if (this.facetsTraits.size() == 0) {
            this.facetsTraits = null;
        }

        this.initFacets();
    }

    /**
     * initializes the list that contains the filters in the left panel
     */
    private void initFacets() {
        this.listFacetCategories.setAdapter(null);

        if (this.facets != null) {
            this.adapterFacetCategory = new FacetCategoryAdapter(this, this.facets);
            this.listFacetCategories.setAdapter(this.adapterFacetCategory);
        }

        this.listFacetTraits.setAdapter(null);

        if (this.facetsTraits != null) {
            this.adapterFacetTrait = new FacetTraitAdapter(this, this.facetsTraits);
            this.listFacetTraits.setAdapter(this.adapterFacetTrait);
        }
    }

    /**
     * displays facets for the selected category
     *
     * @param c the selected category
     */
    private void initFacetChooser(final FacetCategory c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_facets, null);
        EditText edit = (EditText) view.findViewById(R.id.edit_search_facets);
        ImageButton btn = (ImageButton) view.findViewById(R.id.btn_sort);
        final ListView list = (ListView) view.findViewById(R.id.list_facets);

        final FacetAdapter adapter = new FacetAdapter(this, c.getFacets(), btn);
        list.setAdapter(adapter);

        // text filter
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
                list.setSelectionAfterHeaderView();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.sort();
                list.setSelectionAfterHeaderView();
            }
        });

        builder.setView(view)
                .setTitle(c.getName())
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final Dialog dialog = builder.create();

        // applies a facet filter
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Facet f = adapter.getItem(position);

                if (c.getName().equals(BrowserActivity.this.getString(R.string.facet_family))) {
                    BrowserActivity.this.apiFamily = f;
                } else if (c.getName().equals(BrowserActivity.this.getString(R.string.facet_genus))) {
                    BrowserActivity.this.apiGenus = f;
                } else if (c.getName().equals(BrowserActivity.this.getString(R.string.facet_usage_cat))) {
                    BrowserActivity.this.apiUsageCat = f;
                } else if (c.getName().equals(BrowserActivity.this.getString(R.string.facet_usage_type))) {
                    BrowserActivity.this.apiUsageType = f;
                } else if (c.getName().equals(BrowserActivity.this.getString(R.string.facet_project))) {
                    BrowserActivity.this.apiProject = f;
                }

                BrowserActivity.this.initSpeciesList();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    /**
     * displays facets for the selected category
     *
     * @param c the selected category
     */
    private void initFacetTraitChooser(final FacetCategory c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_facets, null);
        EditText edit = (EditText) view.findViewById(R.id.edit_search_facets);
        ImageButton btn = (ImageButton) view.findViewById(R.id.btn_sort);
        final ListView list = (ListView) view.findViewById(R.id.list_facets);

        final FacetAdapter adapter = new FacetAdapter(this, c.getFacets(), btn);
        list.setAdapter(adapter);

        // text filter
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
                list.setSelectionAfterHeaderView();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.sort();
                list.setSelectionAfterHeaderView();
            }
        });

        builder.setView(view)
                .setTitle(c.getName())
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final Dialog dialog = builder.create();

        // applies a facet filter
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Facet f = adapter.getItem(position);

                if (BrowserActivity.this.apiTraits.containsKey(c.getName())) {
                    BrowserActivity.this.apiTraits.remove(c.getName());
                }
                BrowserActivity.this.apiTraits.put(c.getName(), f);

                BrowserActivity.this.initSpeciesList();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (this.layoutDrawer.isDrawerOpen(Gravity.END)) {
            this.layoutDrawer.closeDrawer(Gravity.END);
        } else if (this.layoutDrawer.isDrawerOpen(Gravity.START)) {
            this.layoutDrawer.closeDrawer(Gravity.START);
        } else {
            this.closeActivity();
            //super.onBackPressed();
        }
    }

    /**
     * returns the current "sort by" option
     *
     * @return the string to apply in the API parameter
     */
    private String getSortBy() {
        if (this.sorts != null) {
            for (SortBy sb : this.sorts) {
                if (sb.getIsSelected()) {
                    return sb.getValue();
                }
            }
        }
        return "";
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(this.getString(R.string.exception), "Connected");
        this.location = LocationServices.FusedLocationApi.getLastLocation(this.googleApiClient);
        if (this.location != null) {
            if (this.adapterSpecies != null) {
                this.adapterSpecies.setLocation(this.location);
            }
            this.cbGeography.setEnabled(true);
            Log.i(this.getString(R.string.exception), "lat: " + this.location.getLatitude());
            Log.i(this.getString(R.string.exception), "long: " + this.location.getLongitude());
        } else {
            this.cbGeography.setEnabled(false);
            LocationServices.FusedLocationApi.requestLocationUpdates(this.googleApiClient, this.locationRequest, this);
            Log.i(this.getString(R.string.exception), "null location");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(this.getString(R.string.exception), "Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(this.getString(R.string.exception), "Failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(this.getString(R.string.exception), "location changed");
        this.location = location;
        Log.i(this.getString(R.string.exception), "lat: " + this.location.getLatitude());
        Log.i(this.getString(R.string.exception), "long: " + this.location.getLongitude());
        if (this.location != null) {
            if (this.adapterSpecies != null) {
                this.adapterSpecies.setLocation(this.location);
            }
            this.cbGeography.setEnabled(true);
        } else {
            this.cbGeography.setEnabled(false);
        }
    }

    @Override
    public void onLocalitySelected(Location location, String locality) {
        if (location != null && locality != null && locality.length() > 0) {
            Log.i(this.getString(R.string.exception), "Selected locality: " + locality);
            Log.i(this.getString(R.string.exception), "Selected location lat: " + location.getLatitude());
            Log.i(this.getString(R.string.exception), "Selected location long: " + location.getLongitude());

            this.cbGeography.setChecked(false);

            this.cbCustomGeography.setText(locality);
            this.apiLocation = location;

            this.initSpeciesList();
        } else {
            this.cbCustomGeography.setChecked(false);
            this.cbCustomGeography.setText(R.string.search_locality);
        }
    }

    private void closeActivity() {
        this.finish();
        this.overridePendingTransition(R.anim.non_mobile, R.anim.left_side_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.unbindDrawables(this.findViewById(R.id.layout_drawer));
    }
}
