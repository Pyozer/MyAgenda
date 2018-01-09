package com.pyozer.myagenda.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.pyozer.myagenda.R;
import com.pyozer.myagenda.helper.AppTheme;

public abstract class BaseActivity extends AppCompatActivity {

    FirebaseRemoteConfig mFirebaseRemoteConfig;
    AppTheme appTheme;

    private static boolean isInitialized = false;
    boolean noActionBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appTheme = new AppTheme(getApplicationContext(), noActionBar);
        setTheme(appTheme.getStyle());
        super.onCreate(savedInstanceState);

        if (!isInitialized) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            isInitialized = true;
        }

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(false)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
    }

    // Permet de vérifier la connexion internet
    public boolean checkInternet() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            displaySnackbar(getString(R.string.no_internet));
            return false;
        }
        return true;
    }

    /**
     * Affiche une snackbar
     *
     * @param message
     */
    public void displaySnackbar(String message) {
        Snackbar
                .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .show();
    }

    public void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

}