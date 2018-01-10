package com.pyozer.myagenda.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pyozer.myagenda.R;
import com.pyozer.myagenda.helper.AppConfig;

import static com.pyozer.myagenda.helper.AppConfig.CHANGELOG_LINK;
import static com.pyozer.myagenda.helper.AppConfig.LAST_VERSION;
import static com.pyozer.myagenda.helper.AppConfig.LAST_VERSION_LINK;

public class UpdateActivity extends BaseActivity {

    protected TextView mVersionNew;
    protected TextView mUpdateChecked;
    protected Button mUpdateDownload;
    protected WebView mWebViewChangeLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        setupActionBar();

        mVersionNew = findViewById(R.id.version_new);
        mUpdateChecked = findViewById(R.id.update_checked);
        mUpdateDownload = findViewById(R.id.update_download);
        // On affiche la version actuelle de l'application
        TextView version_install = findViewById(R.id.version_install);

        String currentVersion = getString(R.string.update_actual_version) + " " + getString(R.string.version_app);
        version_install.setText(currentVersion);

        mWebViewChangeLog = findViewById(R.id.webView_ChangeLog);

        fetchConfig();
    }

    private void fetchConfig() {
        mFirebaseRemoteConfig.fetch(AppConfig.CACHE_EXPIRATION).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                mFirebaseRemoteConfig.activateFetched();
            }
            showResponseUpdate();
            startCheckChangeLog();
        });
    }

    // On prépare l'url avant la requete
    protected void startCheckChangeLog() {
        if (checkInternet()) {
            String url = mFirebaseRemoteConfig.getString(CHANGELOG_LINK);
            RequestQueue queue = Volley.newRequestQueue(this);
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> showResponseChangeLog(response), error -> displaySnackbar(getString(R.string.no_changelog_get)));
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
    }

    /**
     * Affiche la CardView pour ma mise à jour
     */
    public void showResponseUpdate() {

        final String version2download = mFirebaseRemoteConfig.getString(LAST_VERSION);
        String actual_version = getString(R.string.version_app);

        mVersionNew.setVisibility(View.GONE);
        mUpdateDownload.setVisibility(View.GONE);

        if (actual_version.equals(version2download)) { // Si pas de nouvelle version
            mUpdateChecked.setText(getString(R.string.update_checked_noupdate));
        } else { // Si une nouvelle version
            mUpdateChecked.setText(getString(R.string.update_checked_newupdate));

            String lastVersionDisplay = getString(R.string.update_last_version) + " " + version2download;
            mVersionNew.setVisibility(View.VISIBLE);
            mVersionNew.setText(lastVersionDisplay);

            mUpdateDownload.setVisibility(View.VISIBLE);
            mUpdateDownload.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mFirebaseRemoteConfig.getString(LAST_VERSION_LINK)));
                startActivity(browserIntent);
            });
        }
    }

    /**
     * Affiche la CardView du changelog
     */
    public void showResponseChangeLog(String changelog) {
        mWebViewChangeLog.setBackgroundColor(Color.TRANSPARENT);

        String color = appTheme.isDark() ? "rgb(255, 255, 255);" : "rgb(0, 0, 0);";

        changelog = changelog.replace("##BODY_COLOR##", color);

        mWebViewChangeLog.loadData(changelog, "text/html; charset=UTF-8", null);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}