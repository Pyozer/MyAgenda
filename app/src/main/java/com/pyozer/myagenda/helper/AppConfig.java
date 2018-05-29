package com.pyozer.myagenda.helper;

import com.pyozer.myagenda.R;

public interface AppConfig {

    String PREF_AGENDA_CACHE = "agendaCache";
    String PREF_AGENDA_NOTES = "agendaNotes";
    String PREF_AGENDA_EVENT_CUSTOM = "agendaCustomEvent";

    String PREF_DEPART_KEY = "agenda_depart";
    String PREF_ANNEE_KEY = "agenda_annee";
    String PREF_GROUPE_KEY = "agenda_groupe";
    String PREF_GROUPERES_KEY = "agenda_grouperes";

    String PREF_DEPART_DEFAULT = "Informatique";
    String PREF_ANNEE_DEFAULT = "Info 1";
    String PREF_GROUPE_DEFAULT = "Grp 11A";
    String PREF_GROUPERES_DEFAULT = "2660";

    String PREF_CACHE_RELOAD = "pref_cacheReload";
    String PREF_NB_WEEKS = "nbWeeks";
    String PREF_NB_WEEKS_DEFAULT = "1";

    String PREF_THEME_COLOR_NOTE = "pref_theme_colorNote";
    int PREF_THEME_COLOR_NOTE_DEFAULT = R.color.md_blue_grey_500;

    String PREF_FIRST_TIME_LAUNCH = "isFirstTimeLaunch";

    int CACHE_EXPIRATION = 3600;

    String LAST_VERSION = "last_version";
    String LAST_VERSION_LINK = "last_version_link";
    String CHANGELOG_LINK = "changelog_link";

    String ENT_JSESSIONID = "entSessionId";
}
