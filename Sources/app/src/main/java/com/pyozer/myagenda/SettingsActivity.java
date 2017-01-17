package com.pyozer.myagenda;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import java.util.List;
import java.util.Objects;

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

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

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
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_dark_theme", false)) {
            setTheme(R.style.AppThemeNight);
        }
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
    public static class GeneralPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.registerOnSharedPreferenceChangeListener(this);

            ListPreference depart = (ListPreference) findPreference("depart");
            ListPreference annee = (ListPreference) findPreference("annee");
            setPreferenceValues(depart.getValue(), annee.getValue(), (ListPreference) findPreference("groupe"));

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            bindPreferenceSummaryToValue(findPreference("depart"));
            bindPreferenceSummaryToValue(findPreference("annee"));
            bindPreferenceSummaryToValue(findPreference("groupe"));
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            ListPreference groupe = (ListPreference) findPreference("groupe");

            String depart = sharedPreferences.getString("depart", "1");
            String annee = sharedPreferences.getString("annee", "1");

            setPreferenceValues(depart, annee, groupe);
        }

        protected ListPreference setPreferenceValues(String depart, String annee, ListPreference group) {
            CharSequence[] titles_info1 = {"Groupe A", "Groupe B", "Groupe C", "Groupe D"};
            CharSequence[] values_info1 = {"A", "B", "C", "D"};

            CharSequence[] titles_mmi2 = {"Groupe A", "Groupe B", "Groupe C", "Groupe D", "S4 - Grp ALT", "S4 - Grp A PID", "S4 - Grp B PP", "S4 - Grp C PCAG", "S4 - Grp D PCAG"};
            CharSequence[] values_mmi2 = {"A", "B", "C", "D", "ALT", "APID", "BPP", "CPCAG", "DPCAG"};

            CharSequence[] titles_info2 = {"Groupe A", "Groupe B", "Groupe C", "Groupe D", "S4 - Grp A IPLP", "S4 - Grp B IPLP", "S4 - Grp A PEL", "S4 - Grp B PEL"};
            CharSequence[] values_info2 = {"A", "B", "C", "D", "AI", "BI", "AP", "BP"};

            CharSequence[] titles_mmi1_gb1 = {"Groupe A", "Groupe B", "Groupe C", "Groupe D", "Groupe E", "Groupe F"};
            CharSequence[] values_mmi1_gb1 = {"A", "B", "C", "D", "E", "F"};

            CharSequence[] titles_gb2 = {"Groupe A", "Groupe B", "Groupe C", "Groupe D", "Groupe E", "IPLP 1", "IPLP 2", "IPLP 3", "PEL 1", "PEL 2"};
            CharSequence[] values_gb2 = {"A", "B", "C", "D", "E", "IPLP1", "IPLP2", "IPLP3", "PEL1", "PEL2"};

            CharSequence[] titles_tc1 = {"TP 111", "TP 112", "TP 121", "TP 122", "TP 123", "TP 131", "TP 132", "TP 141", "TP 142"};
            CharSequence[] values_tc1 = {"A", "B", "C", "D", "E", "F", "G", "H", "I"};

            CharSequence[] titles_tc2 = {"TP 211", "TP 212", "TP 221", "TP 222", "TP 223", "TP 231", "TP 232", "TP 241", "TP 242", "TP 243"};
            CharSequence[] values_tc2 = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

            CharSequence[] titles_staps1 = {"TP A1", "TP A2", "TP B2", "TP B3", "TP C4", "TP C5", "TP D5", "TP D6", "TP E7", "TP E8", "TP F8", "TP F9", "TP G10", "TP G11", "TP H11", "TP H12", "TP I13", "TP I14"};
            CharSequence[] values_staps1 = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R"};

            CharSequence[] titles_staps2 = {"GP A1", "GP A2", "GP B3", "GP B4", "GP C5", "GP C6", "GP D7", "GP D8"};
            CharSequence[] values_staps2 = {"A", "B", "C", "D", "E", "F", "G", "H"};

            if(Objects.equals(depart, "1") && Objects.equals(annee, "1")) { // Si Info1
                group.setEntries(titles_info1);
                group.setEntryValues(values_info1);
            } else if(Objects.equals(depart, "2") && Objects.equals(annee, "2")) { // Si MMI2
                group.setEntries(titles_mmi2);
                group.setEntryValues(values_mmi2);
            } else if(Objects.equals(depart, "1") && Objects.equals(annee, "2")) { // Si Info2
                group.setEntries(titles_info2);
                group.setEntryValues(values_info2);
            } else if((Objects.equals(depart, "2") && Objects.equals(annee, "1")) || (Objects.equals(depart, "3") && Objects.equals(annee, "1")))  { // Si MMI1 ou GB1
                group.setEntries(titles_mmi1_gb1);
                group.setEntryValues(values_mmi1_gb1);
            } else if((Objects.equals(depart, "3") && Objects.equals(annee, "2")))  { // Si GB2
                group.setEntries(titles_gb2);
                group.setEntryValues(values_gb2);
            } else if(Objects.equals(depart, "4") && Objects.equals(annee, "1"))  { //Si TC 1
                group.setEntries(titles_tc1);
                group.setEntryValues(values_tc1);
            } else if(Objects.equals(depart, "4") && Objects.equals(annee, "2"))  { // Si tc 2
                group.setEntries(titles_tc2);
                group.setEntryValues(values_tc2);
            } else if((Objects.equals(depart, "5") && Objects.equals(annee, "1")))  { //Si staps 1
                group.setEntries(titles_staps1);
                group.setEntryValues(values_staps1);
            } else if((Objects.equals(depart, "5") && Objects.equals(annee, "2")))  { //Si staps 2
                group.setEntries(titles_staps2);
                group.setEntryValues(values_staps2);
            } else {
                group.setEntries(titles_tc2);
                group.setEntryValues(values_tc2);
            }

            if(group.getValue() == null || group.findIndexOfValue(group.getValue()) < 0) {
                group.setValueIndex(0);
            }
            group.setSummary(group.getEntries()[group.findIndexOfValue(group.getValue())].toString());
            group.setKey("groupe");

            return group;
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
