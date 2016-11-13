package com.pyozer.myagenda;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity {

    protected TextView about_version;
    private List<DataRecycler> dataList = new ArrayList<>();
    public RecyclerView recyclerView;
    private DataRecyclerAdapter mAdapter;
    boolean darkTheme = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_dark_theme", false)) {
            this.darkTheme = true;
            setTheme(R.style.AppThemeNight_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        about_version = (TextView) findViewById(R.id.about_version);
        String ver_name = "Version " + getString(R.string.version_app);
        about_version.setText(ver_name);
        // RECYCLER VIEW
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new DataRecyclerAdapter(dataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new DataRecyclerTouch(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                DataRecycler data = dataList.get(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getUrl()));
                startActivity(browserIntent);
            }

            @Override
            public void onLongClick(View view, int position) {}
        }));

        prepareDataList(); // Ajout des éléments dans la RecyclerView

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail(view);
            }
        });
    }

    private void prepareDataList() {
        int[] icons;
        if(this.darkTheme) {
            icons = new int[] {R.drawable.ic_github_white_24dp, R.drawable.ic_twitter_white_24dp};
        } else {
            icons = new int[] {R.drawable.ic_github_black_24dp, R.drawable.ic_twitter_blue_24dp};
        }
        DataRecycler movie = new DataRecycler(icons[0], "Projet Github", "https://github.com/Pyozer/MyAgenda");
        dataList.add(movie);

        movie = new DataRecycler(icons[1], "Mon Twitter", "https://twitter.com/Jc_Mousse");
        dataList.add(movie);

        mAdapter.notifyDataSetChanged();
    }

    protected void sendEmail(View view) {
        String[] TO = {"jeancharles.msse@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Application Android MyAgenda");

        try {
            startActivity(Intent.createChooser(emailIntent, "Envoyer via..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar snackbar = Snackbar.make(view, "Il n'y a aucun client mail installé.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
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
