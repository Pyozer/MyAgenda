package com.pyozer.myagenda.preferences;

import android.content.Context;

import com.google.gson.Gson;
import com.pyozer.myagenda.helper.AppConfig;
import com.pyozer.myagenda.model.EventCustom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrefManagerEventCustom extends PrefManager {

    public PrefManagerEventCustom(Context context) {
        super(context.getSharedPreferences(AppConfig.PREF_AGENDA_EVENT_CUSTOM, Context.MODE_PRIVATE));
    }

    public List<EventCustom> getAllCustomEvents() {
        List<EventCustom> customEventList = new ArrayList<>();

        for (Map.Entry<String, ?> entry : pref.getAll().entrySet()) {
            Gson gson = new Gson();
            EventCustom eventCustom = gson.fromJson(entry.getValue().toString(), EventCustom.class);
            if (!eventCustom.isExpired())
                customEventList.add(eventCustom);
            else {
                removeCustomEvent(entry.getKey());
            }
        }
        return customEventList;
    }

    public void saveCustomEvent(EventCustom customCours) {
        Gson gson = new Gson();
        String json = gson.toJson(customCours);
        editor.putString(customCours.getUid(), json);
        editor.apply();
    }

    public void removeCustomEvent(String coursUid) {
        editor.remove(coursUid);
        editor.apply();
    }
}