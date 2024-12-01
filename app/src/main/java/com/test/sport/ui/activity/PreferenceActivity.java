package com.test.sport.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.test.nba.R;
import com.test.nba.databinding.ActivityPreferenceBinding;
import com.test.sport.base.BaseActivity;
import com.test.sport.utils.Tools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PreferenceActivity extends BaseActivity<ActivityPreferenceBinding> {
    private static final String PREFS_NAME = "SettingsPrefs";
    private static final String KEY_FAVORITE_TEAMS = "favorite_teams";
    private static final String KEY_FAVORITE_LEAGUES = "favorite_leagues";
    private static final String KEY_PREFERRED_TIMES = "preferred_times";

    // 预定义的时间段
    private static final String[] TIME_PERIODS = {
            "Morning (6:00-12:00)",
            "Afternoon (12:00-18:00)",
            "Evening (18:00-24:00)",
            "Dawn (0:00-6:00)"
    };

    @Override
    protected int initLayout() {
        return R.layout.activity_preference;
    }

    @Override
    protected ActivityPreferenceBinding onCreateViewBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityPreferenceBinding.inflate(layoutInflater);
    }

    @Override
    protected void initData() {
        Log.d("PreferenceActivity", "onCreate");
        super.initData();
        Tools.setStatusBarColor(this);  // 设置状态栏颜色
        getBinding().titleBar.setTitle("Preference");
        loadPreferences();
    }

    @Override
    protected void initClick() {
        super.initClick();
        // 使用公共方法设置返回按钮点击事件
        getBinding().titleBar.setLeftIconOnClickListener(v -> finish());

        //getBinding().teamsLayout.setOnClickListener(v -> showSportSelectionDialog());

        // 设置时间段选择
        getBinding().timePeriodsLayout.setOnClickListener(v -> showTimePeriodsDialog());

        // 保存按钮
        getBinding().btnSave.setOnClickListener(v -> savePreferences());
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        //Set<String> savedTeams = prefs.getStringSet(KEY_FAVORITE_TEAMS, new HashSet<>());
        //Set<String> savedLeagues = prefs.getStringSet(KEY_FAVORITE_LEAGUES, new HashSet<>());
        Set<String> savedTimes = prefs.getStringSet(KEY_PREFERRED_TIMES, new HashSet<>());


        //getBinding().etLeagues.setText(String.join(", ", savedLeagues));
        updateTimePeriodsDisplay(savedTimes);
    }

    private void showTimePeriodsDialog() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> savedTimes = prefs.getStringSet(KEY_PREFERRED_TIMES, new HashSet<>());

        boolean[] checkedTimes = new boolean[TIME_PERIODS.length];
        for (int i = 0; i < TIME_PERIODS.length; i++) {
            checkedTimes[i] = savedTimes.contains(TIME_PERIODS[i]);
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Watching Time")   // 设置对话框标题
                // 设置多选项
                .setMultiChoiceItems(
                        TIME_PERIODS,      // 字符串数组
                        checkedTimes,      // 布尔数组， 记录选项是否被选中
                        (dialog, which, isChecked) -> {   // 点击事件监听
                            checkedTimes[which] = isChecked;
                        })
                .setPositiveButton("Confirm", (dialog, which) -> {
                    Set<String> selectedTimes = new HashSet<>();
                    for (int i = 0; i < TIME_PERIODS.length; i++) {
                        if (checkedTimes[i]) {
                            selectedTimes.add(TIME_PERIODS[i]);
                        }
                    }
                    updateTimePeriodsDisplay(selectedTimes);
                })
                .show();
    }

    private void updateTimePeriodsDisplay(Set<String> selectedTimes) {
        if (selectedTimes.isEmpty()) {
            getBinding().tvSelectedTimes.setText("Not Selected");
        } else {
            getBinding().tvSelectedTimes.setText(String.join("\n", selectedTimes));
        }
    }


    private void savePreferences() {
        //String teamsText = getBinding().tvSelectedTeams.getText().toString();
        //String leaguesText = getBinding().etLeagues.getText().toString();
        String timesText = getBinding().tvSelectedTimes.getText().toString();


        Set<String> times = new HashSet<>();
        if (!timesText.equals("Not Selected")) {
            times.addAll(Arrays.asList(timesText.split("\n")));
        }

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();

        editor.putStringSet(KEY_PREFERRED_TIMES, times);
        editor.apply();

        Intent intent = new Intent("com.test.sport.PREFERENCE_CHANGED");
        sendBroadcast(intent);

        showToast("Save Success");
        finish();
    }


}