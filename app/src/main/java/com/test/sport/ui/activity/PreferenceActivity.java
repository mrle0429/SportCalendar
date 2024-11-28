package com.test.sport.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.nba.R;
import com.test.nba.databinding.ActivityPreferenceBinding;
import com.test.sport.base.BaseActivity;
import com.test.sport.model.Team;
import com.test.sport.utils.Tools;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PreferenceActivity extends BaseActivity<ActivityPreferenceBinding> {
    private static final String PREFS_NAME = "SettingsPrefs";
    private static final String KEY_FAVORITE_TEAMS = "favorite_teams";
    private static final String KEY_FAVORITE_LEAGUES = "favorite_leagues";
    private static final String KEY_PREFERRED_TIMES = "preferred_times";
    
    // 预定义的时间段
    private static final String[] TIME_PERIODS = {
        "早上 (6:00-12:00)",
        "下午 (12:00-18:00)",
        "晚上 (18:00-24:00)",
        "凌晨 (0:00-6:00)"
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
        getBinding().titleBar.setTitle("观赛偏好设置");
        loadPreferences();
    }
    
    @Override
    protected void initClick() {
        super.initClick();
        // 使用公共方法设置返回按钮点击事件
        getBinding().titleBar.setLeftIconOnClickListener(v -> finish());

        getBinding().teamsLayout.setOnClickListener(v -> showSportSelectionDialog());
        
        // 设置时间段选择
        getBinding().timePeriodsLayout.setOnClickListener(v -> showTimePeriodsDialog());
        
        // 保存按钮
        getBinding().btnSave.setOnClickListener(v -> savePreferences());
    }
    
    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> savedTeams = prefs.getStringSet(KEY_FAVORITE_TEAMS, new HashSet<>());
        Set<String> savedLeagues = prefs.getStringSet(KEY_FAVORITE_LEAGUES, new HashSet<>());
        Set<String> savedTimes = prefs.getStringSet(KEY_PREFERRED_TIMES, new HashSet<>());
        

        if (savedTeams.isEmpty()) {
            getBinding().tvSelectedTeams.setText("未选择");
        } else {
            getBinding().tvSelectedTeams.setText(String.join(", ", savedTeams));
        }
        
        getBinding().etLeagues.setText(String.join(", ", savedLeagues));
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
            .setTitle("选择观赛时间")   // 设置对话框标题
            // 设置多选项
            .setMultiChoiceItems(
                TIME_PERIODS,      // 字符串数组
                checkedTimes,      // 布尔数组， 记录选项是否被选中
                (dialog, which, isChecked) -> {   // 点击事件监听
                checkedTimes[which] = isChecked;
            })
            .setPositiveButton("确定", (dialog, which) -> {
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
            getBinding().tvSelectedTimes.setText("未选择");
        } else {
            getBinding().tvSelectedTimes.setText(String.join("\n", selectedTimes));
        }
    }

    private void showSportSelectionDialog() {
        String[] sports = new String[]{"Basketball", "Football", "Icehockey", "Tennis", "Soccer", "Rugby"};
        
        new AlertDialog.Builder(this)
            .setTitle("选择运动")
            .setItems(sports, (dialog, which) -> {
                String selectedSport = sports[which];
                showTeamsDialog(selectedSport);
            })
            .setNegativeButton("取消", null)
            .show();
    }
    
    private void showTeamsDialog(String sport) {
        try {
            String jsonFileName;
        if ("Soccer".equals(sport)) {
            jsonFileName = "Soccer_UEFA_Champions_League_Teams_24_25.json";
        } else if ("Basketball".equals(sport)) {
            jsonFileName = "teams.json";
        } else {
            jsonFileName = sport + ".json";
        }
        
        String jsonStr = Tools.readAssetFile(getApplicationContext(), jsonFileName);
        if (jsonStr == null) {
            showToast("暂不支持该运动");
                return;
            }
    
            // 使用Fastjson解析
            JSONObject json = JSON.parseObject(jsonStr);
            List<Team> teams;
            //List<Team> teams = JSON.parseArray(json.getString("teams"), Team.class);
            // 根据不同的JSON格式解析
        if ("Soccer".equals(sport)) {
            // 解析足球队伍数据
            teams = json.getJSONArray("season_competitors").stream()
                .map(obj -> {
                    JSONObject teamObj = (JSONObject) obj;
                    Team team = new Team();
                    team.setId(teamObj.getString("id"));
                    team.setName(teamObj.getString("name"));
                    team.setAlias(teamObj.getString("short_name"));
                    team.setSportType(sport);
                    return team;
                })
                .collect(Collectors.toList());
        } else {
            // 解析其他运动队伍数据
            teams = JSON.parseArray(json.getString("teams"), Team.class);
        }
            // 获取已保存的选择
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            Set<String> savedTeams = new HashSet<>(prefs.getStringSet(KEY_FAVORITE_TEAMS, new HashSet<>()));
            Log.d("PreferenceActivity", "已保存的球队: " + savedTeams);
    
            // 过滤并准备显示的球队列表
            List<Team> filteredTeams = teams.stream()
                .filter(team -> team.getAlias() != null && !team.getAlias().isEmpty())
                .sorted((t1, t2) -> t1.getName().compareTo(t2.getName()))
                .collect(Collectors.toList());
    
            String[] teamDisplayNames = filteredTeams.stream()
                .map(team -> team.getName())
                .toArray(String[]::new);
    
            // 初始化选中状态
            boolean[] checkedTeams = new boolean[teamDisplayNames.length];
            for (int i = 0; i < teamDisplayNames.length; i++) {
                checkedTeams[i] = savedTeams.contains(teamDisplayNames[i]);
                Log.d("PreferenceActivity", "球队: " + teamDisplayNames[i] + 
                      ", 是否在已保存列表中: " + checkedTeams[i]);
            }
    
            new AlertDialog.Builder(this)
                .setTitle("选择" + sport + "球队")
                .setMultiChoiceItems(teamDisplayNames, checkedTeams, (dialog, which, isChecked) -> {
                    checkedTeams[which] = isChecked;
                    Log.d("PreferenceActivity", "选择变更 - 球队: " + teamDisplayNames[which] + 
                          ", 新状态: " + isChecked);
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    Set<String> selectedTeams = new HashSet<>(savedTeams); // 保留其他运动的选择
                    
                    // 更新当前运动的选择
                    for (int i = 0; i < teamDisplayNames.length; i++) {
                        String teamName = teamDisplayNames[i];
                        if (checkedTeams[i]) {
                            selectedTeams.add(teamName);
                        } else {
                            selectedTeams.remove(teamName);
                        }
                    }
                    
                    // 保存选择
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putStringSet(KEY_FAVORITE_TEAMS, selectedTeams);
                    editor.apply();
                    
                    // 更新显示
                    updateTeamsDisplay(selectedTeams);
                })
                .setNegativeButton("取消", null)
                .show();
        } catch (Exception e) {
            Log.e("PreferenceActivity", "加载球队数据失败: " + e.getMessage(), e);
            showToast("加载球队数据失败");
        }
    }


    private void updateTeamsDisplay(Set<String> selectedTeams) {
        if (selectedTeams.isEmpty()) {
            getBinding().tvSelectedTeams.setText("未选择");
        } else {
            getBinding().tvSelectedTeams.setText(String.join("\n", selectedTeams));
        }
    }
    
    private void savePreferences() {
        String teamsText = getBinding().tvSelectedTeams.getText().toString();
        String leaguesText = getBinding().etLeagues.getText().toString();
        String timesText = getBinding().tvSelectedTimes.getText().toString();
        
        if (teamsText.equals("未选择") && leaguesText.isEmpty() && timesText.equals("未选择")) {
            showToast("请至少设置一项偏好");
            return;
        }
        
        Set<String> teams = new HashSet<>();
        if (!teamsText.equals("未选择")) {
            teams.addAll(Arrays.asList(teamsText.split("\\s*,\\s*")));
        }
        
        Set<String> leagues = new HashSet<>(Arrays.asList(
            leaguesText.split("\\s*,\\s*")));
        Set<String> times = new HashSet<>();
        if (!timesText.equals("未选择")) {
            times.addAll(Arrays.asList(timesText.split("\n")));
        }
        
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putStringSet(KEY_FAVORITE_TEAMS, teams);
        editor.putStringSet(KEY_FAVORITE_LEAGUES, leagues);
        editor.putStringSet(KEY_PREFERRED_TIMES, times);
        editor.apply();
        
        Intent intent = new Intent("com.test.sport.PREFERENCE_CHANGED");
        sendBroadcast(intent);
        
        showToast("保存成功");
        finish();
    }



}