package com.test.sport.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.test.nba.R;

import java.util.TimeZone;

public class TimezoneActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTED_TIMEZONE = "selected_timezone";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timezone);

        ListView listView = findViewById(R.id.listView);
        String[] timezones = TimeZone.getAvailableIDs();
        String[] displayTimezones = new String[timezones.length + 1];
        displayTimezones[0] = "自动选择时区 (" + TimeZone.getDefault().getID() + ")";
        System.arraycopy(timezones, 0, displayTimezones, 1, timezones.length);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayTimezones);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTimezone;
            if (position == 0) {
                selectedTimezone = TimeZone.getDefault().getID();
                Toast.makeText(TimezoneActivity.this, "自动选择时区: " + selectedTimezone, Toast.LENGTH_SHORT).show();
            } else {
                selectedTimezone = timezones[position - 1];
                Toast.makeText(TimezoneActivity.this, "选择时区: " + selectedTimezone, Toast.LENGTH_SHORT).show();
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_SELECTED_TIMEZONE, selectedTimezone);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}