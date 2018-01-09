package com.pyozer.myagenda.model;

import java.util.Date;

public class EventCustom extends Cours {

    private int color;

    public EventCustom(String uid, String titre, String description, Date dateStart, Date dateEnd, int color) {
        super(uid, titre, description, dateStart, dateEnd, false);
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isExpired() {
        return (getDateEnd().before(new Date()));
    }
}
