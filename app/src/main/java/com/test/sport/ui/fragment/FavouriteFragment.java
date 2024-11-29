package com.test.sport.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.nba.R;
import com.test.nba.databinding.FragmentFavouriteBinding;
import com.test.sport.base.BaseFragment;
import com.test.sport.model.Team;
import com.test.sport.ui.activity.MainActivity;
import com.test.sport.ui.adapter.TeamAdapter;
import com.test.sport.utils.Tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FavouriteFragment extends BaseFragment<FragmentFavouriteBinding> 
        implements TeamAdapter.OnTeamFavoriteClickListener {
    
    private static final String PREFS_NAME = "SettingsPrefs";
    private static final String KEY_FAVORITE_TEAMS = "favorite_teams";
    private TeamAdapter adapter;
    private List<Team> teamList = new ArrayList<>();
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        
        super.onViewCreated(view, savedInstanceState);
        // 注册到 MainActivity
        ((MainActivity) getActivity()).setFavouriteFragment(this);

        getBinding().titleBar.setTitle("收藏球队");
        initRecyclerView();
        String currentSport = ((MainActivity) getActivity()).getCurrentSport();
        Log.d("SportSync", "FavouriteFragment - 当前运动: " + currentSport);
        updateCurrentSport(currentSport);
        loadTeams(currentSport);
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_favourite;
    }

    @Override
    protected FragmentFavouriteBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentFavouriteBinding.inflate(inflater, parent, false);
    }

    private void initRecyclerView() {
        adapter = new TeamAdapter(teamList, this);
        getBinding().rvTeams.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().rvTeams.setAdapter(adapter);
    }

    private void loadTeams(String sport) {
        try {
            String jsonFileName =
                "Soccer_UEFA_Champions_League_Teams_24_25.json" ;
            
            String jsonStr = Tools.readAssetFile(getContext(), jsonFileName);
            if (jsonStr == null) {
                showToast("暂不支持该运动");
                return;
            }

            // 获取已收藏的球队
            SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            Set<String> savedTeams = prefs.getStringSet(KEY_FAVORITE_TEAMS, new HashSet<>());

            // 解析JSON数据
            JSONObject json = JSON.parseObject(jsonStr);
            List<Team> teams;
            if ("Soccer".equals(sport)) {
                teams = parseSoccerTeams(json, savedTeams);
            } else {
                teams = parseOtherTeams(json, savedTeams);
            }

            teamList.clear();
            teamList.addAll(teams);
            adapter.updateData(teamList);

        } catch (Exception e) {
            Log.e("FavouriteFragment", "加载球队数据失败: " + e.getMessage());

            showToast("加载球队数据失败");

            //showToast("加载球队数据失败");
        }
    }

    private List<Team> parseSoccerTeams(JSONObject json, Set<String> savedTeams) {
        return json.getJSONArray("season_competitors").stream()
            .map(obj -> {
                JSONObject teamObj = (JSONObject) obj;
                Team team = new Team();
                team.setId(teamObj.getString("id"));
                team.setName(teamObj.getString("name"));
                team.setAlias(teamObj.getString("short_name"));
                team.setFavorite(savedTeams.contains(team.getName()));
                return team;
            })
            .sorted((t1, t2) -> t1.getName().compareTo(t2.getName()))
            .collect(Collectors.toList());
    }

    private List<Team> parseOtherTeams(JSONObject json, Set<String> savedTeams) {
        List<Team> teams = JSON.parseArray(json.getString("teams"), Team.class);
        return teams.stream()
            .filter(team -> team.getAlias() != null && !team.getAlias().isEmpty())
            .peek(team -> team.setFavorite(savedTeams.contains(team.getName())))
            .sorted((t1, t2) -> t1.getName().compareTo(t2.getName()))
            .collect(Collectors.toList());
    }

    @Override
    public void onFavoriteClick(Team team, boolean newState) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = new HashSet<>(prefs.getStringSet(KEY_FAVORITE_TEAMS, new HashSet<>()));
        
        if (newState) {
            favorites.add(team.getName());
        } else {
            favorites.remove(team.getName());
        }
        
        prefs.edit().putStringSet(KEY_FAVORITE_TEAMS, favorites).apply();
    }

    public void onSportChanged(String newSport) {
        Log.d("SportSync", "FavouriteFragment - 运动改变为: " + newSport);
        updateCurrentSport(newSport);
        loadTeams(newSport);
    }
    private void updateCurrentSport(String sport) {
        getBinding().tvCurrentSport.setText(sport);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 取消注册
        ((MainActivity) getActivity()).setFavouriteFragment(null);
    }
}