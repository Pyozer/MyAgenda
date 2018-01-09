package com.pyozer.myagenda.preferences;

import android.content.Context;
import android.preference.PreferenceManager;

import com.pyozer.myagenda.R;
import com.pyozer.myagenda.helper.AppConfig;

public class PrefManagerConfig extends PrefManager {

    public PrefManagerConfig(Context context) {
        super(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(AppConfig.PREF_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(AppConfig.PREF_FIRST_TIME_LAUNCH, true);
    }

    public boolean isWantReloadCache() {
        return pref.getBoolean(AppConfig.PREF_CACHE_RELOAD, true);
    }

    public String getAnneeUser() {
        return pref.getString(AppConfig.PREF_ANNEE_KEY, AppConfig.PREF_ANNEE_DEFAULT);
    }

    public String getGroupeUser() {
        return pref.getString(AppConfig.PREF_GROUPE_KEY, AppConfig.PREF_GROUPE_DEFAULT);
    }

    public String getGroupeResUser() {
        return pref.getString(AppConfig.PREF_GROUPERES_KEY, AppConfig.PREF_GROUPERES_DEFAULT);
    }

    public String getNbWeeksUser() {
        return pref.getString(AppConfig.PREF_NB_WEEKS, AppConfig.PREF_NB_WEEKS_DEFAULT);
    }

    public int getNoteColor() {
        return pref.getInt(AppConfig.PREF_THEME_COLOR_NOTE, AppConfig.PREF_THEME_COLOR_NOTE_DEFAULT);
    }

    public String getPrefTheme() {
        return pref.getString("pref_theme", "light");
    }

    public int getPrefThemeColorPrimary() {
        return pref.getInt("pref_theme_colorPrimary", R.color.md_red_500);
    }
    public boolean isPrefDarkTheme() {
        return pref.getBoolean("pref_dark_theme", false);
    }
    public boolean isPrefThemeHeader() {
        return pref.getBoolean("pref_theme_header", true);
    }

    public void removeNote(String coursUid) {
        editor.remove(coursUid);
    }

}
