package com.pyozer.myagenda.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.pyozer.myagenda.R;
import com.pyozer.myagenda.model.EventCustom;
import com.pyozer.myagenda.preferences.PrefManagerEventCustom;
import com.thebluealliance.spectrum.SpectrumPalette;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddEventCustomActivity extends BaseActivity implements SpectrumPalette.OnColorSelectedListener, CompoundButton.OnCheckedChangeListener {

    private EditText titleEvent;
    private EditText descEvent;
    private EditText dateEvent;
    private EditText timeStartEvent;
    private EditText timeEndEvent;
    private SpectrumPalette paletteColor;

    private Calendar dateSEventPicked = Calendar.getInstance();
    private Calendar dateEEventPicked = Calendar.getInstance();

    private PrefManagerEventCustom prefManagerEventCustom;

    private int colorSelected = Color.TRANSPARENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_custom);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        prefManagerEventCustom = new PrefManagerEventCustom(this);

        titleEvent = findViewById(R.id.add_event_title);
        descEvent = findViewById(R.id.add_event_desc);
        dateEvent = findViewById(R.id.add_event_date);
        timeStartEvent = findViewById(R.id.add_event_time_start);
        timeEndEvent = findViewById(R.id.add_event_time_end);
        Switch wantCustomColor = findViewById(R.id.add_event_want_custom);
        wantCustomColor.setOnCheckedChangeListener(this);

        paletteColor = findViewById(R.id.add_event_color);
        paletteColor.setOnColorSelectedListener(this);
        paletteColor.setSelectedColor(ContextCompat.getColor(this, R.color.md_blue_grey_500));

        final int actualColor = timeEndEvent.getCurrentTextColor();

        updateDateText();
        updateTimeText(timeStartEvent, dateSEventPicked);
        dateEEventPicked.add(Calendar.HOUR_OF_DAY, 1);
        updateTimeText(timeEndEvent, dateEEventPicked);

        dateEvent.setOnClickListener(view -> {
            final DatePickerDialog datePicker = new DatePickerDialog(AddEventCustomActivity.this, (view13, year, monthOfYear, dayOfMonth) -> {
                try {
                    dateSEventPicked.set(Calendar.YEAR, year);
                    dateSEventPicked.set(Calendar.MONTH, monthOfYear);
                    dateSEventPicked.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    dateEEventPicked.set(Calendar.YEAR, year);
                    dateEEventPicked.set(Calendar.MONTH, monthOfYear);
                    dateEEventPicked.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    updateDateText();

                } catch (Exception ignored) {
                }
            }, dateSEventPicked.get(Calendar.YEAR), dateSEventPicked.get(Calendar.MONTH), dateSEventPicked.get(Calendar.DAY_OF_MONTH));
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
            datePicker.show();
        });

        timeStartEvent.setOnClickListener(view -> {
            TimePickerDialog td = new TimePickerDialog(AddEventCustomActivity.this, (view12, hourOfDay, minute) -> {
                try {
                    dateSEventPicked.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    dateSEventPicked.set(Calendar.MINUTE, minute);

                    if (dateSEventPicked.after(dateEEventPicked)) {
                        dateEEventPicked.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateEEventPicked.set(Calendar.MINUTE, minute);

                        timeEndEvent.setTextColor(actualColor);
                        updateTimeText(timeEndEvent, dateEEventPicked);
                    }

                    updateTimeText(timeStartEvent, dateSEventPicked);

                } catch (Exception ignored) {
                }
            }, dateSEventPicked.get(Calendar.HOUR_OF_DAY), dateSEventPicked.get(Calendar.MINUTE), DateFormat.is24HourFormat(AddEventCustomActivity.this));
            td.show();
        });
        timeEndEvent.setOnClickListener(view -> {
            TimePickerDialog td = new TimePickerDialog(AddEventCustomActivity.this, (view1, hourOfDay, minute) -> {
                try {
                    dateEEventPicked.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    dateEEventPicked.set(Calendar.MINUTE, minute);
                    timeEndEvent.setTextColor(actualColor);
                    if(dateEEventPicked.before(dateSEventPicked)) {
                        Toast.makeText(AddEventCustomActivity.this, getString(R.string.error_hour_end_inferior), Toast.LENGTH_LONG).show();
                        timeEndEvent.setTextColor(getResources().getColor(R.color.md_red_500));
                    }
                    updateTimeText(timeEndEvent, dateEEventPicked);

                } catch (Exception ignored) {
                }
            }, dateEEventPicked.get(Calendar.HOUR_OF_DAY), dateEEventPicked.get(Calendar.MINUTE), DateFormat.is24HourFormat(AddEventCustomActivity.this));
            td.show();
        });
    }

    private void updateDateText() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateEvent.setText(formatter.format(dateSEventPicked.getTime()));
    }

    private void updateTimeText(EditText editText, Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        editText.setText(formatter.format(calendar.getTime()));
    }

    private boolean isValideCustomEvent() {
        titleEvent.setError(null);
        descEvent.setError(null);
        dateEvent.setError(null);
        timeStartEvent.setError(null);
        timeEndEvent.setError(null);

        boolean isOk = true;

        String titre = titleEvent.getText().toString().trim();
        String desc = descEvent.getText().toString().trim();
        String date = dateEvent.getText().toString().trim();
        String startTime = timeStartEvent.getText().toString().trim();
        String endTime = timeEndEvent.getText().toString().trim();

        if (TextUtils.isEmpty(titre)) {
            titleEvent.setError(getString(R.string.error_field_required));
            isOk = false;
        } else if(titre.length() > 40){
            titleEvent.setError(getString(R.string.error_max_40_length));
            isOk = false;
        }
        if (TextUtils.isEmpty(desc)) {
            descEvent.setError(getString(R.string.error_field_required));
            isOk = false;
        } else if(desc.length() > 40){
            descEvent.setError(getString(R.string.error_max_40_length));
            isOk = false;
        }
        if (TextUtils.isEmpty(date)) {
            dateEvent.setError(getString(R.string.error_field_required));
            isOk = false;
        }
        if (TextUtils.isEmpty(startTime)) {
            timeStartEvent.setError(getString(R.string.error_field_required));
            isOk = false;
        }
        if (TextUtils.isEmpty(endTime)) {
            timeEndEvent.setError(getString(R.string.error_field_required));
            isOk = false;
        }
        if (dateSEventPicked.before(new Date())) {
            dateEvent.setError(getString(R.string.error_date_before_today));
            isOk = false;
        }
        if (dateEEventPicked.before(dateSEventPicked)) {
            timeEndEvent.setError(getString(R.string.error_hour_end_inferior));
            isOk = false;
        }

        return isOk;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                if (isValideCustomEvent()) {
                    prefManagerEventCustom.saveCustomEvent(
                            new EventCustom(
                                    UUID.randomUUID().toString(),
                                    titleEvent.getText().toString().trim(),
                                    descEvent.getText().toString().trim(),
                                    dateSEventPicked.getTime(),
                                    dateEEventPicked.getTime(),
                                    colorSelected)
                    );
                    Toast.makeText(this, getString(R.string.add_event_success), Toast.LENGTH_LONG).show();

                    finish();
                }
                break;
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

    @Override
    public void onColorSelected(int color) {
        colorSelected = color;
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            paletteColor.setVisibility(View.VISIBLE);
        } else {
            paletteColor.setVisibility(View.GONE);
            colorSelected = Color.TRANSPARENT;
        }
    }
}
