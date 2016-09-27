package com.pyozer.myagenda;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected View mainactivity_layout;
    private WebView mWebView;
    public TextView textTitle;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        textTitle = (TextView) findViewById(R.id.title);
        mWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mainactivity_layout = findViewById(R.id.mainactivity_layout);

        // On vérifie la connexion internet
        if(!checkInternet()){
            // Si pas internet, on met un message
            Snackbar snackbar = Snackbar
                    .make(mainactivity_layout, getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction("Rafraichir", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        } else {
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(mWebView, url);
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Snackbar snackbar = Snackbar
                            .make(mainactivity_layout, getString(R.string.no_connexion_client), Snackbar.LENGTH_INDEFINITE)
                            .setAction("Réessayer", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                }
            });
            mWebView.loadUrl(prepareURL());
        }
    }

    public String prepareURL() {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String groupe = preferences.getString("groupe", "A");
        String annee = preferences.getString("annee", "1");
        String nbWeeks = preferences.getString("nbWeeks", "1");

        String url = "http://interminale.fr.nf/MyAgenda/get_calendar.php?annee=" + annee + "&grp=" + groupe + "&nbWeeks=" + nbWeeks;
        // On ajoute la version actuelle
        url += "&version=" + getString(R.string.version_app);

        return url;
    }

    // Permet de vérifier la connexion internet
    public boolean checkInternet() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
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
