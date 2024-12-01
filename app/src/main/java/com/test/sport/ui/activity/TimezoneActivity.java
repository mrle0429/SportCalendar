package com.test.sport.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.test.nba.R;

import java.util.TimeZone;

public class TimezoneActivity extends AppCompatActivity {

    //public static final String EXTRA_SELECTED_TIMEZONE = "selected_timezone";
    private static final String PREFS_NAME = "SettingsPrefs";
    public static final String PREF_SELECTED_TIMEZONE = "SelectedTimezone";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timezone);

        ListView listView = findViewById(R.id.listView);
        String[] timezones = TimeZone.getAvailableIDs();
        String[] displayTimezones = new String[timezones.length + 1];
        displayTimezones[0] = "Default time zone (" + TimeZone.getDefault().getID() + ")";
        System.arraycopy(timezones, 0, displayTimezones, 1, timezones.length);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayTimezones);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTimezone;
            if (position == 0) {
                selectedTimezone = TimeZone.getDefault().getID();
                Toast.makeText(TimezoneActivity.this, "Default time zone: " + selectedTimezone, Toast.LENGTH_SHORT).show();
            } else {
                selectedTimezone = timezones[position - 1];
                Toast.makeText(TimezoneActivity.this, "Selected time zone: " + selectedTimezone, Toast.LENGTH_SHORT).show();
            }

            // 保存时区到 SharedPreferences
            savePreference(PREF_SELECTED_TIMEZONE, selectedTimezone);

            Intent intent = new Intent("com.test.sport.TIMEZONE_CHANGED");
            sendBroadcast(intent);

            // 返回结果给调用的Activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SelectedTimezone", selectedTimezone);
            setResult(RESULT_OK, resultIntent);
            finish();
        });


    }


    private void savePreference(String key, String value) {
        SharedPreferences preferences = getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
        Log.d("TimezoneActivity", "保存偏好设置: " + key + "=" + value);
    }
}