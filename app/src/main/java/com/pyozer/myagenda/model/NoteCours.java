package com.pyozer.myagenda.model;

import java.util.Date;

public class NoteCours {

    private String coursUid;
    private String text = "";
    private Date dateExpiration;

    public NoteCours(String coursUid, String text, Date dateExpiration) {
        this.coursUid = coursUid;
        this.text = text;
        this.dateExpiration = dateExpiration;
    }

    public String getCoursUid() {
        return coursUid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isExpired() {
        return (dateExpiration.before(new Date()));
    }
}
