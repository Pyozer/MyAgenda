package com.pyozer.myagenda;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebAppInterface {
    Context mContext;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        mContext = c;
    }

    /** Affiche un Toast **/
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
    }
    /** Affiche une boite de dialogue **/
    @JavascriptInterface
    public void showDialog(String title, String message) {
        new AlertDialog.Builder(mContext)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
            .show();
    }
    /** Affiche une boite de dialogue pouvant rediriger vers le menu Mise à jour **/
    @JavascriptInterface
    public void showAndroidDialogMaj(String title, String message, String yes, String no) {
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        redirectMaj();
                    }
                })
                .setNegativeButton(no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
    /** Affiche une boite de dialogue pouvant rediriger vers le menu Paramètres */
    @JavascriptInterface
    public void showAndroidDialogParams(String title, String message) {
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(mContext, SettingsActivity.class);
                        mContext.startActivity(myIntent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
    /** Redirige vers le menu Mise à jour **/
    @JavascriptInterface
    public void redirectMaj() {
        Intent myIntent = new Intent(mContext, UpdateActivity.class);
        mContext.startActivity(myIntent);
    }

}