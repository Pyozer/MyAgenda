package com.pyozer.myagenda.helper;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MyCookieManager {

    private static MyCookieManager instance = new MyCookieManager();

    private static CookieManager mCookieManager = null;

    public static MyCookieManager getInstance() {
        return instance;
    }

    private MyCookieManager() {
        mCookieManager = new CookieManager();
        mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(mCookieManager);
    }

    public List<HttpCookie> getCookies() {
        return (mCookieManager == null) ? new ArrayList<>() : mCookieManager.getCookieStore().getCookies();
    }

    public String getCookieValue(String cookieName) {
        for (HttpCookie eachCookie : getCookies())
            if(eachCookie.getName().equals(cookieName))
                return eachCookie.getValue();

        return "";
    }

    public void clearCookies() {
        if (mCookieManager != null)
            mCookieManager.getCookieStore().removeAll();
    }

    public boolean isCookieManagerEmpty() {
        return mCookieManager == null || mCookieManager.getCookieStore().getCookies().isEmpty();
    }


    public String getCookiesValue() {
        String cookieValue = "";

        for (HttpCookie eachCookie : getCookies())
            cookieValue = cookieValue + String.format("%s=%s; ", eachCookie.getName(), eachCookie.getValue());

        return cookieValue;
    }

}