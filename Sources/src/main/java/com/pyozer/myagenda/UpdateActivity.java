package com.pyozer.myagenda;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Objects;

public class UpdateActivity extends AppCompatActivity {

    protected TextView changelog;
    protected TextView changelog_version;
    protected TextView version_install;
    protected TextView update_checked;
    protected Button update_download;
    protected SwipeRefreshLayout swipeRefreshLayout;
    private String version2download;

    protected View update_layout;
    protected HttpRequest HttpRequest;

    private Snackbar snackbar;
    private boolean SNACKBARSHOW = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pref_dark_theme", false)) {
            setTheme(R.style.AppThemeNight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        HttpRequest = new HttpRequest(this);

        changelog = (TextView) findViewById(R.id.changeLog);
        changelog_version = (TextView) findViewById(R.id.changeLog_version);
        version_install = (TextView) findViewById(R.id.version_install);
        update_checked = (TextView) findViewById(R.id.update_checked);
        update_download = (Button) findViewById(R.id.update_download);
        // On affiche la version actuelle de l'application
        version_install.setText(getString(R.string.update_actual_version) + " " + getString(R.string.version_app));

        update_layout = findViewById(R.id.update_layout);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_Update);
        swipeRefreshLayout.setColorSchemeResources(R.color.rouge, R.color.indigo, R.color.lime);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(checkInternet()) {
                    StartCheckUpdate();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        // On vérifie la connexion internet
        StartCheckUpdate();
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
        SNACKBARSHOW = true;
    }

    // On prépare l'url avant la requete
    protected void StartCheckUpdate() {
        if(checkInternet()) {
            String url = "https://raw.githubusercontent.com/Pyozer/MyAgenda/master/last_version.txt";
            HttpRequest.changeLog = false;
            HttpRequest.new DownloadWebpageTask().execute(url, "4000", "4000");
        }
    }

    // On prépare l'url avant la requete
    protected void StartCheckChangeLog(String versionToGet) {
        if(checkInternet()) {
            String url = "https://raw.githubusercontent.com/Pyozer/MyAgenda/master/changelog.txt";
            HttpRequest.changeLog = true;
            HttpRequest.new DownloadWebpageTask().execute(url, "4000", "4000");
        }
    }

    /**
     * Affiche la CardView pour ma mise à jour
     */
    public void showResponse(String last_version) {

        version2download = last_version.trim();
        String actual_version = getString(R.string.version_app);

        if(actual_version.equals(version2download)) { // Si pas de nouvelle version
            update_checked.setText(getString(R.string.update_checked_noupdate));
        } else if(Objects.equals(version2download, "error")){ // Si erreur lors de la récupération via github
            update_checked.setText(getString(R.string.no_connexion_github));
        } else { // Si nouvelle version
            update_checked.setText(getString(R.string.update_checked_newupdate));

            update_download.setVisibility(View.VISIBLE);
            update_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Pyozer/MyAgenda/raw/master/Versions/MyAgenda_v" + version2download + ".apk"));
                    startActivity(browserIntent);
                }
            });
        }
        // On charge le changelog
        StartCheckChangeLog(version2download);
    }

    /**
     * Affiche la CardView du changelog
     */
    public void showResponseChangeLog(String change) {
        findViewById(R.id.card_view_changelog).setVisibility(View.VISIBLE);
        changelog_version.setText("Changelog (" + version2download + ")");
        changelog.setText(change);
    }

}
