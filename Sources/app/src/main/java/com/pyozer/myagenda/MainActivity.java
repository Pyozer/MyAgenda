package com.pyozer.myagenda;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    protected View mainactivityLayout;
    private WebView mWebView;
    private Snackbar snackbar;
    private boolean SNACKBARSHOW = false;
    private String URL_TO_LOAD;
    private boolean NEED_REFRESH = true;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_dark_theme", false)) {
            setTheme(R.style.AppThemeNight_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mainactivityLayout = findViewById(R.id.mainactivity_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.rouge, R.color.indigo, R.color.lime);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(checkInternet()) {
                    getPage(true, false);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        mWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCachePath(this.getCacheDir().getPath());
        webSettings.setAppCacheMaxSize(1024*1024*8);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap bitmap) {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(mWebView, url);
                swipeRefreshLayout.setRefreshing(false);
                boolean reloadCache = preferences.getBoolean("pref_cacheReload", true);
                if(NEED_REFRESH && reloadCache) {
                    getPage(false, false);
                    NEED_REFRESH = false;
                }
            }
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                showWebViewError(view, failingUrl);
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                // On redirige vers l'ancienne fonction
                onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
            }
        });
        /* On affiche la WebView */
        getPage(false, true);
    }

    protected void getPage(boolean clearCache, boolean load_cache) {
        boolean internet = checkInternet();
        String url = prepareURL();
        if(!internet) { // Si y'a pas internet
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        } else if(load_cache) { // Si on veut charger le cache (tout en gardant l'expiration du cache)
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        } else { // Sinon si on veut simplement charger la page
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            if(clearCache) { // Si on veut supprimer le cache avant de charger la page
                mWebView.clearCache(true);
            }
        }
        mWebView.loadUrl(url);
    }

    public void showWebViewError(WebView view, String failUrl) {
        if(checkInternet()) {
            displaySnackbar(mainactivityLayout, getString(R.string.no_connexion_client));
        }
        /* Si y'a pas de cache */
        if(view.getSettings().getCacheMode() != WebSettings.LOAD_NO_CACHE && URL_TO_LOAD.equals(failUrl)) {
            view.loadData("<p style=\"text-align: center;margin-top: 50px;\">Aucun cache n'a été trouvé :/<br />Vous devez avoir internet.</p>", "text/html; charset=UTF-8", null);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    public String prepareURL() {
        String depart = preferences.getString("depart", "1");
        String annee = preferences.getString("annee", "1");
        String groupe = preferences.getString("groupe", "A");
        String nbWeeks = preferences.getString("nbWeeks", "1");
        boolean nightMode = preferences.getBoolean("pref_dark_theme", false);
        String theme = (nightMode) ? "dark" : "light";

        String url = "http://jourmagic.fr/MyAgenda/get_calendar.php?depart=" + depart + "&annee=" + annee + "&grp=" + groupe + "&nbWeeks=" + nbWeeks;
        // On ajoute la version actuelle
        url += "&version=" + getString(R.string.version_app);
        // On ajoute le thème désiré
        url += "&theme=" + theme;
        URL_TO_LOAD = url;
        return url;
    }

    // Permet de vérifier la connexion internet
    public boolean checkInternet() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            if(SNACKBARSHOW) { // Si y'avais une snackbar on la supprime
                snackbar.dismiss();
            }
            return true;
        } else {
            displaySnackbar(mainactivityLayout, getString(R.string.no_internet));
            return false;
        }
    }

    public void displaySnackbar(View mainactivityLayout, String error) {
        snackbar = Snackbar
                .make(mainactivityLayout, error, Snackbar.LENGTH_INDEFINITE)
                .setAction("Réessayer", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.this.finish();
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                    }
                });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
        SNACKBARSHOW = true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_update) {
            startActivity(new Intent(MainActivity.this, UpdateActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
