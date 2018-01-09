package com.pyozer.myagenda.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pyozer.myagenda.ClickListener;
import com.pyozer.myagenda.DataRecyclerTouch;
import com.pyozer.myagenda.R;
import com.pyozer.myagenda.model.DataRecycler;
import com.pyozer.myagenda.adapter.DataRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends BaseActivity {

    private List<DataRecycler> dataList = new ArrayList<>();

    public RecyclerView mRecyclerView;
    private DataRecyclerAdapter mAdapter;

    boolean darkTheme = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.noActionBar = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        TextView about_version = findViewById(R.id.about_version);
        String ver_name = "Version " + getString(R.string.version_app);
        about_version.setText(ver_name);
        // RECYCLER VIEW
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new DataRecyclerAdapter(dataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new DataRecyclerTouch(getApplicationContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                DataRecycler data = dataList.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getUrl()));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        prepareDataList(); // Ajout des éléments dans la RecyclerView

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> sendEmail());
    }

    private void prepareDataList() {
        int[] icons;
        if (this.darkTheme) {
            icons = new int[]{R.drawable.ic_github_white_24dp, R.drawable.ic_twitter_white_24dp};
        } else {
            icons = new int[]{R.drawable.ic_github_black_24dp, R.drawable.ic_twitter_blue_24dp};
        }
        DataRecycler item = new DataRecycler(icons[0], getString(R.string.github_project), "https://github.com/Pyozer/MyAgenda");
        dataList.add(item);

        item = new DataRecycler(icons[1], getString(R.string.my_twitter), "https://twitter.com/Jc_Mousse");
        dataList.add(item);

        mAdapter.notifyDataSetChanged();
    }

    protected void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "jeancharles.msse@gmail.com");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Application Android MyAgenda");

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_by)));
        } catch (android.content.ActivityNotFoundException ex) {
            displaySnackbar(getString(R.string.no_email_client));
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