package com.pyozer.myagenda;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_dark_theme", false)) {
            setTheme(R.style.AppThemeNight_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainactivityLayout = findViewById(R.id.mainactivity_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAppCachePath(this.getCacheDir().getPath());
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(mWebView, url);
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError (WebView view, int errorCode, String description, String failingUrl) {
                showWebViewError(view, failingUrl);
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError (WebView view, WebResourceRequest request, WebResourceError error) {
                // On redirige vers l'ancienne fonction
                onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.rouge, R.color.indigo, R.color.lime);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(checkInternet()) {
                    getPage(true);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        /* On affiche la WebView */
        getPage(false);
    }

    protected void getPage(boolean clearCache) {
        boolean internet = checkInternet();
        String url = prepareURL();
        if(!internet) { // Si on veut d'abord afficher le cache
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        } else {
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            if(clearCache) {
                mWebView.clearCache(true);
            }
        }
        mWebView.loadUrl(url);
    }

    public void showWebViewError(WebView view, String failUrl) {
        if(checkInternet()) {
            String errorDisplay = getString(R.string.no_connexion_client);
            displaySnackbar(mainactivityLayout, errorDisplay);
        }
        /* Si y'a pas de cache */
        if(view.getSettings().getCacheMode() != WebSettings.LOAD_NO_CACHE && URL_TO_LOAD.equals(failUrl)) {
            view.loadData("<p style=\"text-align: center;margin-top: 50px;\">Aucun cache n'a été trouvé :/<br />Vous devez avoir internet.</p>", "text/html; charset=UTF-8", null);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    public String prepareURL() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String depart = preferences.getString("depart", "1");
        String annee = preferences.getString("annee", "1");
        String groupe = preferences.getString("groupe", "A");
        String nbWeeks = preferences.getString("nbWeeks", "1");
        boolean nightMode = preferences.getBoolean("pref_dark_theme", false);
        String theme = (nightMode) ? "dark" : "light";

        String url = "http://interminale.fr.nf/MyAgenda/get_calendar.php?depart=" + depart + "&annee=" + annee + "&grp=" + groupe + "&nbWeeks=" + nbWeeks;
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
            Intent myIntent = new Intent(MainActivity.this, AboutActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.nav_settings) {
            Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.nav_update) {
            Intent myIntent = new Intent(MainActivity.this, UpdateActivity.class);
            MainActivity.this.startActivity(myIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
