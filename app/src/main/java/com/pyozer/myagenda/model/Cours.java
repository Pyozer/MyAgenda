package com.pyozer.myagenda.model;

import android.text.TextUtils;

import com.pyozer.myagenda.helper.Utils;

import java.util.Date;

public class Cours {

    private String uid;
    private String titre;
    private String description;
    private NoteCours note;
    private Date dateStart;
    private Date dateEnd;
    private boolean isExam;

    public Cours(String titre) {
        this(null, titre, null, null, null, false);
    }

    public Cours(String uid, String titre, String description, Date dateStart, Date dateEnd, boolean isExam) {
        this.uid = uid;
        this.titre = titre;
        this.description = description;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.isExam = isExam;
    }

    public String getUid() {
        return uid;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public NoteCours getNote() {
        return note;
    }

    public void setNote(NoteCours note) {
        this.note = note;
    }

    public boolean hasNote() {
        return (note != null && !TextUtils.isEmpty(note.getText()));
    }

    public Date getDateStart() {
        return dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public String getDateFormat() {
        String dateFormatted = Utils.dateFormat4Event(dateStart, dateEnd);
        return Utils.capitalize(dateFormatted);
    }

    public boolean isExam() {
        return isExam;
    }

    public boolean isHeader() {
        return (TextUtils.isEmpty(description) && dateStart == null);
    }
}
