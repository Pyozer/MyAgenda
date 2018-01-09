package com.pyozer.myagenda.preferences;

import android.content.Context;

import com.google.gson.Gson;
import com.pyozer.myagenda.helper.AppConfig;
import com.pyozer.myagenda.model.NoteCours;

import java.util.HashMap;
import java.util.Map;

public class PrefManagerNote extends PrefManager {

    public PrefManagerNote(Context context) {
        super(context.getSharedPreferences(AppConfig.PREF_AGENDA_NOTES, Context.MODE_PRIVATE));
    }

    public Map<String, NoteCours> getAllNotes() {
        Map<String, NoteCours> noteCoursList = new HashMap<>();

        for (Map.Entry<String, ?> entry : pref.getAll().entrySet()) {
            Gson gson = new Gson();
            NoteCours noteCours = gson.fromJson(entry.getValue().toString(), NoteCours.class);
            if (!noteCours.isExpired())
                noteCoursList.put(entry.getKey(), noteCours);
            else {
                removeNote(entry.getKey());
            }
        }
        return noteCoursList;
    }

    public void saveNote(NoteCours noteCours) {
        Gson gson = new Gson();
        String json = gson.toJson(noteCours);
        editor.putString(noteCours.getCoursUid(), json);
        editor.apply();
    }

    public void removeNote(String coursUid) {
        editor.remove(coursUid);
        editor.apply();
    }
}