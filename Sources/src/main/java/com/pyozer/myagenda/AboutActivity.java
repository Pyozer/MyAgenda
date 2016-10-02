package com.pyozer.myagenda;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail(view);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void sendEmail(View view) {
        String[] TO = {"jeancharles.msse@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Application Android MyAgenda");
        //emailIntent.putExtra(Intent.EXTRA_TEXT, "Saisissez votre message ici");

        try {
            startActivity(Intent.createChooser(emailIntent, "Envoyer via..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar snackbar = Snackbar.make(view, "Il n'y a aucun client mail installé.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
}
