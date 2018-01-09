package com.pyozer.myagenda.model;

public class DataRecycler {

    private String title, url;
    private int icon;

    public DataRecycler(int icon, String title, String url) {
        this.icon = icon;
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}