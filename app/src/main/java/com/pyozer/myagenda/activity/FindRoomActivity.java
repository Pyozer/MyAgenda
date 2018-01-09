package com.pyozer.myagenda.activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pyozer.myagenda.R;
import com.pyozer.myagenda.helper.Utils;
import com.pyozer.myagenda.model.Cours;
import com.pyozer.myagenda.model.RoomAvailable;
import com.pyozer.myagenda.adapter.FindRoomAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import biweekly.Biweekly;
import biweekly.ICalendar;

public class FindRoomActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private DatabaseReference mDatabase;

    private ArrayAdapter<String> mAdapter;
    private TextView mTextHour;
    private Button mButtonSubmit;

    private FindRoomAdapter mRecyclerAdapter;

    private String buildingSelected = "";

    private Calendar mDateTimeMin = GregorianCalendar.getInstance();

    private List<String> mListBuildings;
    private Map<String, String> mListRoomRes;

    private List<RoomAvailable> mListRoomFound;

    private int numChild = 0;
    private int numRoomLoaded = 0;
    private int numRoomToLoad = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_room);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListBuildings = new ArrayList<>();
        mListRoomFound = new ArrayList<>();
        mListRoomRes = new HashMap<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Spinner mSpinnerDep = findViewById(R.id.find_room_dep);
        mTextHour = findViewById(R.id.find_room_heure);
        mButtonSubmit = findViewById(R.id.find_room_submit);
        RecyclerView mRecyclerRooms = findViewById(R.id.find_room_results);

        mRecyclerAdapter = new FindRoomAdapter(mListRoomFound);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerRooms.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerRooms.addItemDecoration(dividerItemDecoration);
        mRecyclerRooms.setLayoutManager(mLayoutManager);
        mRecyclerRooms.setAdapter(mRecyclerAdapter);

        mSpinnerDep.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        mAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, mListBuildings);

        // Specify the layout to use when the list of choices appears
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinnerDep.setAdapter(mAdapter);

        loadBuildings();

        mButtonSubmit.setOnClickListener(view -> loadRoomRes(buildingSelected));

        mDateTimeMin.add(Calendar.HOUR_OF_DAY, 1);

        updateEndTimeText();

        mTextHour.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(FindRoomActivity.this, (view1, hourOfDay, minute) -> {
                try {
                    mDateTimeMin.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    mDateTimeMin.set(Calendar.MINUTE, minute);

                    updateEndTimeText();

                } catch (Exception ignored) {
                }
            }, mDateTimeMin.get(Calendar.HOUR_OF_DAY), mDateTimeMin.get(Calendar.MINUTE), DateFormat.is24HourFormat(FindRoomActivity.this));

            timePickerDialog.show();
        });
    }

    private void updateEndTimeText() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        mTextHour.setText(formatter.format(mDateTimeMin.getTime()));
    }

    private void loadBuildings() {
        Query buildingRooms = mDatabase.child("departement").orderByKey();

        numChild = 0;
        mListBuildings.clear();
        mButtonSubmit.setEnabled(false);

        buildingRooms.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataDep : dataSnapshot.getChildren()) {
                    numChild++;

                    mListBuildings.add(dataDep.getKey());
                }

                setAllDataLoad();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FindRoomActivity.this, getString(R.string.error), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setAllDataLoad() {
        mAdapter.notifyDataSetChanged();
        mButtonSubmit.setEnabled(true);
    }

    private void loadRoomRes(String building) {
        Query buildingRooms = mDatabase.child("departement").child(building).child("Salles");

        numChild = 0;
        buildingRooms.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataRoom : dataSnapshot.getChildren()) {
                    numChild++;
                    mListRoomRes.put(dataRoom.getKey(), String.valueOf(dataRoom.getValue()));
                }

                if (numChild >= dataSnapshot.getChildrenCount()) {
                    numRoomToLoad = numChild;
                    searchRoom();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FindRoomActivity.this, getString(R.string.error), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void searchRoom() {
        if (mDateTimeMin.before(Calendar.getInstance())) {
            Toast.makeText(this, getString(R.string.error_date_before_today), Toast.LENGTH_SHORT).show();
            return;
        }

        numRoomLoaded = 0;
        mListRoomFound.clear();
        mAdapter.notifyDataSetChanged();

        if (mListRoomRes.size() == 0) {
            Toast.makeText(this, getString(R.string.no_room_found), Toast.LENGTH_SHORT).show();
        }

        for (Map.Entry<String, String> entry : mListRoomRes.entrySet()) {
            String roomName = entry.getKey();
            String roomRes = entry.getValue();

            loadIcalRoom(roomName, roomRes);
        }
    }

    private void loadIcalRoom(final String roomName, String ressource) {
        String url = Utils.createUrlToIcal(ressource, "1");

        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            numRoomLoaded++;

            checkIfRoomIsAvailable(roomName, Biweekly.parse(response).first());

            if (numRoomLoaded >= numRoomToLoad) {
                // TODO: RETIRER UNE PROGRESSBAR
                Toast.makeText(FindRoomActivity.this, getString(R.string.find_room_finish), Toast.LENGTH_SHORT).show();
                mRecyclerAdapter.notifyDataSetChanged();
            }
        }, error -> displaySnackbar(getString(R.string.no_connexion_client)));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void checkIfRoomIsAvailable(String roomName, ICalendar ical) {

        if (ical != null) {
            Date now = new Date();

            List<Cours> allCours = Utils.icalToCoursList(ical, new HashMap<>(), new ArrayList<>());

            for (Cours cours : allCours) {
                Date eventStart = cours.getDateStart();

                if (now.after(eventStart) || mDateTimeMin.getTime().after(eventStart)) {
                    return;
                }

                if (now.before(eventStart) && mDateTimeMin.getTime().before(eventStart)) {
                    mListRoomFound.add(new RoomAvailable(roomName, eventStart));
                    return;
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        buildingSelected = String.valueOf(parent.getItemAtPosition(pos));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Nothing
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
