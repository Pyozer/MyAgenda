package com.pyozer.myagenda.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pyozer.myagenda.ClickListener;
import com.pyozer.myagenda.DataRecyclerTouch;
import com.pyozer.myagenda.R;
import com.pyozer.myagenda.adapter.CoursAdapter;
import com.pyozer.myagenda.helper.AppConfig;
import com.pyozer.myagenda.helper.Utils;
import com.pyozer.myagenda.model.Cours;
import com.pyozer.myagenda.model.EventCustom;
import com.pyozer.myagenda.model.NoteCours;
import com.pyozer.myagenda.preferences.PrefManagerCache;
import com.pyozer.myagenda.preferences.PrefManagerConfig;
import com.pyozer.myagenda.preferences.PrefManagerEventCustom;
import com.pyozer.myagenda.preferences.PrefManagerNote;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import biweekly.Biweekly;
import biweekly.ICalendar;

import static com.pyozer.myagenda.helper.AppConfig.LAST_VERSION;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAnalytics mFirebaseAnalytics;

    private CardView mAgendaEmpty;
    private CardView mAppUpdate;
    private TextView mAgendaTitle;
    private FloatingActionButton mBtnAddCustomEvent;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private CoursAdapter mAdapter;
    private List<Cours> mCoursList = new ArrayList<>();

    private PrefManagerConfig mPreferences;
    private PrefManagerNote mPreferencesNote;
    private PrefManagerCache mPreferencesCache;
    private PrefManagerEventCustom mPreferencesCustomCours;

    private boolean mRefreshOnStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.noActionBar = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPreferences = new PrefManagerConfig(this);

        if(mPreferences.getSessionId().equals("")) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mPreferencesNote = new PrefManagerNote(this);
        mPreferencesCache = new PrefManagerCache(this);
        mPreferencesCustomCours = new PrefManagerEventCustom(this);

        mBtnAddCustomEvent = findViewById(R.id.add_custom_event);
        mBtnAddCustomEvent.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AddEventCustomActivity.class)));

        mAgendaEmpty = findViewById(R.id.agenda_empty);
        mAppUpdate = findViewById(R.id.app_new_update);
        mAgendaTitle = findViewById(R.id.agenda_title);
        mAgendaTitle.setOnClickListener(view -> showDialogChangeAgenda(mAgendaTitle.getText().toString(), getString(R.string.want_change_agenda)));

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView.getHeaderView(0).findViewById(R.id.drawer).setBackgroundResource(appTheme.getGradient());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.rouge, R.color.md_indigo_500, R.color.md_lime_500, R.color.md_orange_500);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPage(false));

        mRecyclerView = findViewById(R.id.coursRecyclerView);
        mAdapter = new CoursAdapter(mCoursList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < -5 && !mBtnAddCustomEvent.isShown())
                    mBtnAddCustomEvent.show();
                else if (dy > 5 && mBtnAddCustomEvent.isShown())
                    mBtnAddCustomEvent.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        mRecyclerView.addOnItemTouchListener(new DataRecyclerTouch(getApplicationContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                showAlertDialogCours(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                dialogNote(position);
            }
        }));

        fetchConfig();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRefreshOnStart = true;
        getPage(true);
    }

    private void getPage(boolean load_cache) {
        boolean internet = checkInternet();

        String[] urlData = prepareURL();

        if (!internet || load_cache) { // Si y'a pas internet ou load le cache
            mSwipeRefreshLayout.setRefreshing(false);
            loadCache(urlData);
        } else {
            loadData(urlData);
        }
    }

    private void loadCache(String[] urlData) {
        String cacheGet = mPreferencesCache.getAgendaCache(urlData[1]);

        if (!cacheGet.equals("")) {
            hideEmptyMessage();
            iCalToRecycler(Biweekly.parse(cacheGet).first());
        } else {
            showEmptyMessage(false);
        }
        // Après ouverture de l'appli, après avoir chargé le cache, on refresh.
        boolean reloadCache = mPreferences.isWantReloadCache();
        if (mRefreshOnStart && reloadCache) {
            mRefreshOnStart = false;
            getPage(false);
        }
    }

    private void loadData(final String[] urlData) {
        mSwipeRefreshLayout.setRefreshing(true);

        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlData[0], response -> {
            mSwipeRefreshLayout.setRefreshing(false);
            hideEmptyMessage();

            mPreferencesCache.saveAgendaCache(urlData[1], response);

            iCalToRecycler(Biweekly.parse(response).first());
        }, error -> {
            mSwipeRefreshLayout.setRefreshing(false);
            loadCache(urlData);
            displaySnackbar(getString(R.string.no_connexion_client));
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void iCalToRecycler(ICalendar ical) {
        mCoursList.clear();

        if (ical != null) {
            Date prevDay = new Date(0); // On initialise avec une date
            List<Cours> allCours = Utils.icalToCoursList(ical, mPreferencesNote.getAllNotes(), mPreferencesCustomCours.getAllCustomEvents());
            for (Cours cours : allCours) {
                Date eventStart = cours.getDateStart();

                if (!Utils.dateFormat4Compare(prevDay).equals(Utils.dateFormat4Compare(eventStart))) { // Si le jour n'est plus le même on ajoute un "header"
                    String headerTitle = Utils.dateFormat4Header(eventStart);
                    mCoursList.add(new Cours(headerTitle.toUpperCase()));
                }
                mCoursList.add(cours);
                prevDay = eventStart;
            }
        }
        if (mCoursList.size() == 0) showEmptyMessage(true);

        mAdapter.notifyDataSetChanged();

        sendAnalytics();
    }

    private void sendAnalytics() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.GROUP_ID, mPreferences.getGroupeResUser());
        bundle.putString(FirebaseAnalytics.Param.LEVEL, mPreferences.getAnneeUser());
        bundle.putString(FirebaseAnalytics.Param.LOCATION, mPreferences.getDepartUser());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public String[] prepareURL() {
        String annee = mPreferences.getAnneeUser();
        String groupe = mPreferences.getGroupeUser();
        String groupeRes = mPreferences.getGroupeResUser();
        String nbWeeks = mPreferences.getNbWeeksUser();

        String agendaTitleStr = annee + " - " + groupe;
        mAgendaTitle.setText(agendaTitleStr);

        return new String[]{
                Utils.createUrlToIcal(groupeRes, nbWeeks),
                groupeRes
        };
    }

    /**
     * Affiche une boite de dialogue pouvant rediriger vers le menu Paramètres
     */
    public void showDialogChangeAgenda(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
                    intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
                    startActivity(intent);
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.cancel())
                .show();
    }

    private void showAlertDialogCours(int position) {
        final Cours cours = mCoursList.get(position);

        if (cours.isHeader()) {
            return;
        }

        String timeText;
        if (cours.getDateStart().getTime() >= new Date().getTime()) {
            timeText = getString(R.string.start_in) + " " + Utils.timeUntilDate(cours.getDateStart(), getString(R.string.day), getString(R.string.hour), getString(R.string.min), getString(R.string.sec));
        } else {
            timeText = getString(R.string.event_in_progress) + "\n"
                    + getString(R.string.end_in) + " " + Utils.timeUntilDate(cours.getDateEnd(), getString(R.string.day), getString(R.string.hour), getString(R.string.min), getString(R.string.sec));
        }

        String message = cours.getTitre() + "\n" + cours.getDescription() + "\n\n" + timeText;
        if (cours.hasNote()) {
            message += "\n\n" + getString(R.string.event_note) + "\n" + cours.getNote().getText();
        }
        showDialog(cours.getDateFormat(), message);
    }

    private void dialogNote(int position) {
        final Cours cours = mCoursList.get(position);

        if (cours.isHeader()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialoglayout = getLayoutInflater().inflate(R.layout.note_dialog_layout, null);

        final EditText noteCours = dialoglayout.findViewById(R.id.note_dialog_input);
        if (cours.getNote() != null) {
            noteCours.setText(cours.getNote().getText());
        }

        builder.setTitle(getString(R.string.dialog_note_title));
        builder.setCancelable(true);
        builder.setView(dialoglayout);
        // Add action buttons
        builder.setPositiveButton(getString(R.string.dialog_save_note), (dialog, id) -> {
            String noteCoursVal = noteCours.getText().toString().trim();

            NoteCours note = null;
            if (!TextUtils.isEmpty(noteCoursVal)) {
                note = new NoteCours(cours.getUid(), noteCoursVal, cours.getDateEnd());
                mPreferencesNote.saveNote(note);
            } else {
                mPreferencesNote.removeNote(cours.getUid());
            }
            cours.setNote(note);
            mAdapter.notifyItemChanged(position);
        });
        if (cours instanceof EventCustom) {
            builder.setNegativeButton(getString(R.string.dialog_remove_event), (dialog, id) -> {
                mCoursList.remove(cours);
                mPreferencesCustomCours.removeCustomEvent(cours.getUid());
                mRecyclerView.getAdapter().notifyItemRemoved(position);
            });
        }

        builder.create();
        builder.show();
    }

    private void showEmptyMessage(boolean no_event) {
        mAgendaEmpty.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        TextView descEmpty = findViewById(R.id.agenda_empty_text);
        if (no_event)
            descEmpty.setText(getString(R.string.agenda_no_event_text));
        else
            descEmpty.setText(getString(R.string.agenda_empty_text));
    }

    private void hideEmptyMessage() {
        mAgendaEmpty.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void fetchConfig() {
        mFirebaseRemoteConfig.fetch(AppConfig.CACHE_EXPIRATION).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                mFirebaseRemoteConfig.activateFetched();
            }
            String version2download = mFirebaseRemoteConfig.getString(LAST_VERSION);
            String actual_version = getString(R.string.version_app);
            if (!version2download.equals(actual_version)) {
                mAppUpdate.setVisibility(View.VISIBLE);
                mAppUpdate.setOnClickListener(view -> {
                    Intent myIntent = new Intent(MainActivity.this, UpdateActivity.class);
                    startActivity(myIntent);
                });
            } else {
                mAppUpdate.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_update) {
            startActivity(new Intent(MainActivity.this, UpdateActivity.class));
        } else if (id == R.id.nav_intro) {
            startActivity(new Intent(MainActivity.this, IntroActivity.class));
        } else if (id == R.id.nav_find_room) {
            startActivity(new Intent(MainActivity.this, FindRoomActivity.class));
        } else if (id == R.id.nav_logout) {
            mPreferences.setSessionId("");
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}