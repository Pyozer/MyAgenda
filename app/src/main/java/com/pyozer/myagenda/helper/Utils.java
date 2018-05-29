package com.pyozer.myagenda.helper;

import android.util.Log;

import com.pyozer.myagenda.model.Cours;
import com.pyozer.myagenda.model.EventCustom;
import com.pyozer.myagenda.model.NoteCours;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import biweekly.ICalendar;
import biweekly.component.VEvent;

public class Utils {

    public static String createUrlToIcal(String groupeRes, String nbWeeks) {
        return "http://edt.univ-lemans.fr/jsp/custom/modules/plannings/anonymous_cal.jsp?resources=" + groupeRes + "&projectId=3&calType=ical&nbWeeks=" + nbWeeks;
    }

    public static List<Cours> icalToCoursList(ICalendar ical, Map<String, NoteCours> noteCoursList, List<EventCustom> customCours) {
        List<Cours> listCours = new ArrayList<>();

        for (VEvent event : ical.getEvents()) {
            Date eventStart = event.getDateStart().getValue();
            Date eventEnd = event.getDateEnd().getValue();

            if (eventEnd.getTime() >= new Date().getTime()) { // Si le cours est pas fini

                String title = Utils.titleEventFormat(event.getSummary().getValue().trim());

                boolean isExam = title.startsWith("Exam");

                String location = event.getLocation().getValue().trim();
                String prof = event.getDescription().getValue().trim();
                String description = Utils.descEventFormat(location + " - " + prof);

                String coursUid = event.getUid().getValue();

                listCours.add(new Cours(coursUid, title, description, eventStart, eventEnd, isExam));
            }
        }
        // On ajoute les évènements perso
        listCours.addAll(customCours);
        // On tri par ordre de date
        Collections.sort(listCours, (c1, c2) -> c1.getDateStart().compareTo(c2.getDateStart()));

        for (Cours cours : listCours)
            if (noteCoursList.containsKey(cours.getUid()))
                cours.setNote(noteCoursList.get(cours.getUid()));

        return listCours;
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String dateFormat4Header(Date date) {
        return new SimpleDateFormat("EEEE dd'/'MM", Locale.getDefault()).format(date);
    }

    public static String dateFormat4Event(Date dateStart, Date dateEnd) {
        SimpleDateFormat format4CoursStart = new SimpleDateFormat("EEEE dd MMM HH'h'mm", Locale.getDefault());
        SimpleDateFormat format4CoursEnd = new SimpleDateFormat("HH'h'mm", Locale.getDefault());

        return format4CoursStart.format(dateStart) + " - " + format4CoursEnd.format(dateEnd);
    }

    public static String dateFormat4Compare(Date date) {
        return new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date);
    }

    private static String titleEventFormat(String titleInit) {
        return titleInit.replaceAll("M[0-9]{4}[a-zA-Z]? -", "").trim();
    }

    private static String descEventFormat(String descInit) {
        return descInit
                .replaceAll("\\n", " ")
                .replaceAll("\\(Exported :(.*)\\)", "")
                .replaceAll("(DUT |Info1|Info 1|Info2|Info 2|LP|TQL|MMI1|MMI2|GB1|GB2|TC1|TC2|STAPS)", "")
                .trim();
    }

    public static String timeUntilDate(Date date, String day, String hour, String min, String sec) {
        long ecartTime = (date.getTime() - new Date().getTime()) / 1000;

        String result = "";
        if (ecartTime >= 86400) {
            int nbDay = (int) Math.floor(ecartTime / 86400);
            result += nbDay + day + " ";
            ecartTime -= nbDay * 86400;
        }
        if (ecartTime >= 3600) {
            int nbHour = (int) Math.floor(ecartTime / 3600);
            result += nbHour + hour + " ";
            ecartTime -= nbHour * 3600;
        }
        if (ecartTime >= 60) {
            int nbMin = (int) Math.floor(ecartTime / 60);
            result += nbMin + min + " ";
            ecartTime -= nbMin * 60;
        }
        if (ecartTime < 60) {
            result += ((int) ecartTime) + sec;
        }
        return result.trim() + ".";
    }

    public static String getStringBetween(String origin, String begin, String end) {
        Pattern pattern = Pattern.compile(begin + "(.+?)" + end);
        Matcher matcher = pattern.matcher(origin);
        if(matcher.find())
            return matcher.group(1);
        else
            return "XD";
    }
}