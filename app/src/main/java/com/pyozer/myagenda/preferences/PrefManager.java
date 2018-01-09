package com.pyozer.myagenda.preferences;

import android.content.SharedPreferences;

public abstract class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public PrefManager(SharedPreferences pref) {
        this.pref = pref;
        this.editor = pref.edit();
    }
}
