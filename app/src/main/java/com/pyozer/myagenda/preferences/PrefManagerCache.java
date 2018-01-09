package com.pyozer.myagenda.preferences;

import android.content.Context;

import com.pyozer.myagenda.helper.AppConfig;

public class PrefManagerCache extends PrefManager {

    public PrefManagerCache(Context context) {
        super(context.getSharedPreferences(AppConfig.PREF_AGENDA_CACHE, Context.MODE_PRIVATE));
    }

    public String getAgendaCache(String groupRes) {
        return pref.getString(groupRes, "");
    }

    public void saveAgendaCache(String groupRes, String data) {
        // On supprime le cache actuel
        editor.clear();
        editor.apply();
        // On sauvegarde le nouveau cache
        editor.putString(groupRes, data);
        editor.apply();
    }
}
