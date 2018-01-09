package com.pyozer.myagenda.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pyozer.myagenda.R;
import com.pyozer.myagenda.helper.AppConfig;
import com.pyozer.myagenda.helper.AppTheme;
import com.thebluealliance.spectrum.SpectrumPreference;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import static android.content.ContentValues.TAG;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {

        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        Object value = PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), "");

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppTheme appTheme = new AppTheme(this, false);
        setTheme(appTheme.getStyle());
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DisplayPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {

        private static boolean isInitialized = false;

        private DatabaseReference mDatabase;

        private TreeMap<String, TreeMap<String, TreeMap<String, String>>> listRessources = new TreeMap<>();

        private SharedPreferences prefsRes;
        private ListPreference depart;
        private ListPreference annee;
        private ListPreference groupe;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            prefsRes = PreferenceManager.getDefaultSharedPreferences(getActivity());

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.registerOnSharedPreferenceChangeListener(prefListener);

            depart = (ListPreference) findPreference(AppConfig.PREF_DEPART_KEY);
            annee = (ListPreference) findPreference(AppConfig.PREF_ANNEE_KEY);
            groupe = (ListPreference) findPreference(AppConfig.PREF_GROUPE_KEY);

            if (!isInitialized) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                isInitialized = true;
            }
            mDatabase = FirebaseDatabase.getInstance().getReference().child("departement");

            ValueEventListener dataListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot dataDep : dataSnapshot.getChildren()) {
                        TreeMap<String, TreeMap<String, String>> anneeData = new TreeMap<>();
                        for (DataSnapshot dataAnnee : dataDep.getChildren()) {
                            TreeMap<String, String> groupData = new TreeMap<>();
                            for (DataSnapshot dataGroupe : dataAnnee.getChildren()) {
                                groupData.put(dataGroupe.getKey(), String.valueOf(dataGroupe.getValue()));
                            }
                            anneeData.put(dataAnnee.getKey(), new TreeMap<>(groupData));
                        }
                        listRessources.put(dataDep.getKey(), new TreeMap<>(anneeData));
                    }

                    setPreferenceValues(depart.getValue(), annee.getValue(), groupe.getValue());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };

            mDatabase.addValueEventListener(dataListener);

            bindPreferenceSummaryToValue(depart);
            bindPreferenceSummaryToValue(annee);
            bindPreferenceSummaryToValue(groupe);
        }

        SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (key.equals(AppConfig.PREF_DEPART_KEY) ||
                        key.equals(AppConfig.PREF_ANNEE_KEY) ||
                        key.equals(AppConfig.PREF_GROUPE_KEY)) {

                    String departPref = prefs.getString(AppConfig.PREF_DEPART_KEY, AppConfig.PREF_DEPART_DEFAULT);
                    String anneePref = prefs.getString(AppConfig.PREF_ANNEE_KEY, AppConfig.PREF_ANNEE_DEFAULT);
                    String groupePref = prefs.getString(AppConfig.PREF_GROUPE_KEY, AppConfig.PREF_GROUPE_DEFAULT);

                    setPreferenceValues(departPref, anneePref, groupePref);
                }
            }
        };

        protected void setPreferenceValues(String newDep, String newAnnee, String newGroup) {

            Set<String> keysDep = listRessources.keySet();
            CharSequence[] depList = keysDep.toArray(new String[keysDep.size()]);
            depart.setEntries(depList);
            depart.setEntryValues(depList);

            if (listRessources.get(newDep) == null) { // Si le département est inconnu
                newDep = depList[0].toString();
            }

            Set<String> keysAnnee = listRessources.get(newDep).keySet();
            CharSequence[] anneeList = keysAnnee.toArray(new String[keysAnnee.size()]);
            annee.setEntries(anneeList);
            annee.setEntryValues(anneeList);

            if (listRessources.get(newDep).get(newAnnee) == null) { // Si le département/année est inconnu
                newAnnee = anneeList[0].toString();
            }

            Set<String> keysGroupe = listRessources.get(newDep).get(newAnnee).keySet();
            CharSequence[] groupeList = keysGroupe.toArray(new String[keysGroupe.size()]);
            groupe.setEntries(groupeList);
            groupe.setEntryValues(groupeList);

            if (listRessources.get(newDep).get(newAnnee).get(newGroup) == null) { // Si le département/année/groupe est inconnu
                newGroup = groupeList[0].toString();
            }

            if (depart.getValue() == null || depart.findIndexOfValue(depart.getValue()) < 0) {
                depart.setValueIndex(0);
            }
            depart.setSummary(depart.getValue());
            depart.setKey(AppConfig.PREF_DEPART_KEY);

            if (annee.getValue() == null || annee.findIndexOfValue(annee.getValue()) < 0) {
                annee.setValueIndex(0);
            }
            annee.setSummary(annee.getValue());
            annee.setKey(AppConfig.PREF_ANNEE_KEY);

            if (groupe.getValue() == null || groupe.findIndexOfValue(groupe.getValue()) < 0) {
                groupe.setValueIndex(0);
            }
            groupe.setSummary(groupe.getEntries()[groupe.findIndexOfValue(groupe.getValue())].toString());
            groupe.setKey(AppConfig.PREF_GROUPE_KEY);

            SharedPreferences.Editor editor = prefsRes.edit();
            editor.putString(AppConfig.PREF_GROUPERES_KEY, listRessources.get(newDep).get(newAnnee).get(newGroup));
            editor.apply();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Fragment pour afficher les paramètres d'affichage
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DisplayPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_affichage);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            bindPreferenceSummaryToValue(findPreference("nbWeeks"));
            bindPreferenceSummaryToValue(findPreference("pref_theme"));

            final ListPreference pref_theme = (ListPreference) findPreference("pref_theme");
            final SpectrumPreference pref_theme_colorPrimary = (SpectrumPreference) findPreference("pref_theme_colorPrimary");
            final SwitchPreference pref_dark_theme = (SwitchPreference) findPreference("pref_dark_theme");
            final SwitchPreference pref_theme_header = (SwitchPreference) findPreference("pref_theme_header");

            if (!Objects.equals(pref_theme.getValue(), "custom")) {
                pref_theme_colorPrimary.setEnabled(false);
                pref_theme_colorPrimary.setSummary(getString(R.string.pref_theme_colorPrimary_summaryOFF));
                pref_dark_theme.setEnabled(false);
                pref_theme_header.setEnabled(false);
            }

            pref_theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, newValue);
                    getActivity().recreate();
                    final String val = newValue.toString();
                    if (Objects.equals(val, "custom")) {
                        pref_theme_colorPrimary.setEnabled(true);
                        pref_theme_colorPrimary.setSummary(getString(R.string.pref_theme_colorPrimary_summary));
                        pref_dark_theme.setEnabled(true);
                        pref_theme_header.setEnabled(true);
                    } else {
                        pref_theme_colorPrimary.setEnabled(false);
                        pref_theme_colorPrimary.setSummary(getString(R.string.pref_theme_colorPrimary_summaryOFF));
                        pref_dark_theme.setEnabled(false);
                        pref_theme_header.setEnabled(false);
                    }
                    return true;
                }
            });
            pref_theme_colorPrimary.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, newValue);
                    getActivity().recreate();
                    return true;
                }
            });
            pref_dark_theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, newValue);
                    getActivity().recreate();
                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
