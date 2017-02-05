package com.pyozer.myagenda;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import java.util.Objects;

public class UpdateActivity extends AppCompatActivity {

    protected TextView version_install;
    protected TextView version_new;
    protected TextView update_checked;
    protected Button update_download;
    protected WebView webView_ChangeLog;

    protected SwipeRefreshLayout swipeRefreshLayout;

    protected View update_layout;

    private Snackbar snackbar;
    private boolean SNACK_SHOW = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppTheme appTheme = new AppTheme(this, false);
        setTheme(appTheme.getStyle());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        setupActionBar();

        version_new = (TextView) findViewById(R.id.version_new);
        update_checked = (TextView) findViewById(R.id.update_checked);
        update_download = (Button) findViewById(R.id.update_download);
        // On affiche la version actuelle de l'application
        version_install = (TextView) findViewById(R.id.version_install);
        version_install.setText(getString(R.string.update_actual_version) + " " + getString(R.string.version_app));

        webView_ChangeLog = (WebView) findViewById(R.id.webView_ChangeLog);

        update_layout = findViewById(R.id.update_layout);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_Update);
        swipeRefreshLayout.setColorSchemeResources(R.color.rouge, R.color.md_indigo_500, R.color.md_lime_500, R.color.md_orange_500);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(checkInternet()) {
                    StartCheckUpdate();
                    // On charge le changelog
                    StartCheckChangeLog();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        StartCheckUpdate();
        // On charge le changelog
        StartCheckChangeLog();
    }

    // Permet de vérifier la connexion internet
    public boolean checkInternet() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            if(SNACK_SHOW) { // Si y'avais une snackbar on la supprime
                snackbar.dismiss();
            }
            return true;
        } else {
            displaySnackbar(update_layout, getString(R.string.no_internet));
            return false;
        }
    }

    public void displaySnackbar(View mainactivityLayout, String error) {
        snackbar = Snackbar
                .make(mainactivityLayout, error, Snackbar.LENGTH_INDEFINITE)
                .setAction("Réessayer", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UpdateActivity.this.finish();
                        startActivity(new Intent(UpdateActivity.this, UpdateActivity.class));
                    }
                });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
        SNACK_SHOW = true;
    }

    // On prépare l'url avant la requete
    protected void StartCheckUpdate() {
        if(checkInternet()) {
            String url = "https://raw.githubusercontent.com/Pyozer/MyAgenda/master/last_version.txt";
            HttpRequest HttpRequest = new HttpRequest(this, false);
            HttpRequest.new DownloadWebpageTask().execute(url, "5000", "5000");
        }
    }

    // On prépare l'url avant la requete
    protected void StartCheckChangeLog() {
        if(checkInternet()) {
            String url = "https://raw.githubusercontent.com/Pyozer/MyAgenda/master/changelog.txt";
            HttpRequest HttpRequest = new HttpRequest(this, true);
            HttpRequest.new DownloadWebpageTask().execute(url, "5000", "5000");
        }
    }

    /**
     * Affiche la CardView pour ma mise à jour
     */
    public void showResponse(String last_version) {

        final String version2download = last_version.trim();
        String actual_version = getString(R.string.version_app);

        version_new.setVisibility(View.GONE);
        update_download.setVisibility(View.GONE);

        if(actual_version.equals(version2download)) { // Si pas de nouvelle version
            update_checked.setText(getString(R.string.update_checked_noupdate));
        } else if(Objects.equals(version2download, "error")){ // Si erreur lors de la récupération via github
            update_checked.setText(getString(R.string.no_connexion_github));
        } else { // Si une nouvelle version
            update_checked.setText(getString(R.string.update_checked_newupdate));

            String lastVersionDisplay = getString(R.string.update_last_version) + " " + version2download;
            version_new.setVisibility(View.VISIBLE);
            version_new.setText(lastVersionDisplay);

            update_download.setVisibility(View.VISIBLE);
            update_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Pyozer/MyAgenda/raw/master/Versions/MyAgenda_v" + version2download + ".apk"));
                    startActivity(browserIntent);
                }
            });
        }
    }

    /**
     * Affiche la CardView du changelog
     */
    public void showResponseChangeLog(String change) {
        webView_ChangeLog.setBackgroundColor(Color.TRANSPARENT);
        String finalChangelog;
        finalChangelog = "<html><head><style>body { color: ";
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_dark_theme", false)) {
            finalChangelog += "#d0d0d0;";
        } else {
            finalChangelog += "#555555;";
        }
        finalChangelog += " }" + change;
        webView_ChangeLog.loadData(finalChangelog, "text/html; charset=UTF-8", null);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
