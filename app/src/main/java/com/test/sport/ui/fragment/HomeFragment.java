package com.test.sport.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.lxj.xpopup.XPopup;
import com.test.nba.R;
import com.test.nba.databinding.FragmentHomeBinding;
import com.test.sport.base.BaseFragment;
import com.test.sport.db.entity.Game;
import com.test.sport.http.OkHttpUtil;
import com.test.sport.http.bean.Sport;
import com.test.sport.ui.activity.SportActivity;
import com.test.sport.ui.adapter.GameAdapter;
import com.test.sport.utils.Constants;
import com.test.sport.utils.LogUtils;
import com.test.sport.utils.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Response;

// TODO:主页
public class HomeFragment extends BaseFragment<FragmentHomeBinding> implements View.OnClickListener,
        CalendarView.OnCalendarSelectListener {

    private String address;
    private String jsonName;
    private int index;  //当前选择的运动索引
    private boolean search; //是否搜索
    private String date; //当前选择的日期
    private String timeZoneId; //时区
    private static final String PREFS_NAME = "SettingsPrefs";
    private static final String PREF_SELECTED_TIMEZONE = "SelectedTimezone";

    @Override
    protected void initData() {
        super.initData();
        //SharedPreferences preferences = getActivity().getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
        //timeZoneId = preferences.getString("SelectedTimeZone", TimeZone.getDefault().getID());
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        timeZoneId = preferences.getString(PREF_SELECTED_TIMEZONE, android.icu.util.TimeZone.getDefault().getID());
        Log.d("HomeFragment1", "加载的时区: " + timeZoneId);
        
        getBinding().calendarView.setOnCalendarSelectListener(this);
        // 隐藏月视图，显示周视图
        getBinding().calendarView.getMonthViewPager().setVisibility(View.GONE);
        getBinding().calendarView.getWeekViewPager().setVisibility(View.VISIBLE);
        date = Tools.customFormat(new Date(), "yyyy-MM-dd");
        //initLocalData(0);//本地json数据
        request(0);//网络请求
    }

    @Override
    protected void initClick() {
        super.initClick();
        getBinding().tvSport.setOnClickListener(this);
        getBinding().tvSearch.setOnClickListener(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected FragmentHomeBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentHomeBinding.inflate(inflater);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sport:
                // 显示体育类型选择
                showSport();
                break;
            case R.id.tv_search:
                // 显示搜索
                search();
                break;
        }
    }

    @Override
public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Log.d("HomeFragment1", "注册广播接收器");
    IntentFilter filter = new IntentFilter("com.test.sport.TIMEZONE_CHANGED");
    getActivity().registerReceiver(timezoneChangedReceiver, filter);
}

private final BroadcastReceiver timezoneChangedReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.test.sport.TIMEZONE_CHANGED".equals(intent.getAction())) {
            // 重新加载时区
            SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            timeZoneId = preferences.getString(PREF_SELECTED_TIMEZONE, android.icu.util.TimeZone.getDefault().getID());
            Log.d("HomeFragment1", "时区已更改，重新加载: " + timeZoneId);

            // 重新请求数据
            request(index);
            //initLocalData(index);
        }
    }
};

@Override
public void onDestroyView() {
    super.onDestroyView();
    getActivity().unregisterReceiver(timezoneChangedReceiver);
}
    // 搜索功能
    private void search() {

        String keyword = getBinding().etName.getText().toString();
        if (keyword.isEmpty()) {
            showToast("Please enter");
            return;
        }
        if (keyword.equals("ALL")) {
            search = false;
            handler.sendEmptyMessage(1);
            return;
        }
        for (Game game : gameList) {
            if (game.getCompetition_name().equals(keyword)) {
                searchList.add(game);
            }
        }
        search = true;
        handler.sendEmptyMessage(1);
        getBinding().etName.getText().clear();
    }

    private void showSport() {
        // 运动选择弹窗
        new XPopup.Builder(getActivity())
                .hasShadowBg(false)// 是否有半透明的背景，默认为true
                .isViewMode(true)
                .offsetY(50) //弹窗在y方向的偏移量
                .atView(getBinding().tvSport)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"Basketball", "Football", "Icehockey", "Tennis", "Table Tennis", "Rugby"},
                        new int[]{},
                        (position, text) -> {
                            getBinding().tvSport.setText(text);
                            index = position;
                            search = false;
                            handler.sendEmptyMessage(0);
                        })
                .show();
    }

    private void initLocalData(int index) {
        switch (index) {
            case 0:
                jsonName = getJson("Basketball.json");
                break;
            case 1:
                jsonName = getJson("Football.json");
                break;
            case 2:
                jsonName = getJson("Icehockey.json");
                break;
            case 3:
                jsonName = getJson("Tennis.json");
                break;
        }
        new Thread(() -> {
            Sport sport = JSON.parseObject(jsonName, Sport.class);
            if (sport.getSummaries() != null) {
                List<Game.Competitors> competitorsList = new ArrayList<>();
                Game.Competitors competitor = null;
                Game.Score score = null;
                String status;
                gameList.clear();
                for (Sport.SummariesDTO summariesDTO : sport.getSummaries()) {
                    Game game = new Game();
                    game.setSport_name(summariesDTO.getSportEvent().getSportEventContext().getSport().getName());
                    game.setStart_time(summariesDTO.getSportEvent().getStartTime());
                    game.setStatus(summariesDTO.getSportEventStatus().getStatus());
                    game.setCountry_name(summariesDTO.getSportEvent().getSportEventContext().getCategory().getName());
                    game.setCompetition_name(summariesDTO.getSportEvent().getSportEventContext().getCompetition().getName());
                    game.setStage_start_date(summariesDTO.getSportEvent().getSportEventContext().getSeason().getStartDate());
                    game.setStage_end_date(summariesDTO.getSportEvent().getSportEventContext().getSeason().getEndDate());
                    game.setStage_type(summariesDTO.getSportEvent().getSportEventContext().getStage().getType());
                    game.setStage_phase(summariesDTO.getSportEvent().getSportEventContext().getStage().getPhase());

                    if (null != summariesDTO.getSportEvent().getVenue()) {
                        game.setVenue_name(summariesDTO.getSportEvent().getVenue().getName());
                        if (null != summariesDTO.getSportEvent().getVenue().getCapacity()) {
                            game.setVenue_capacity(summariesDTO.getSportEvent().getVenue().getCapacity());
                        }
                        game.setCity_name(summariesDTO.getSportEvent().getVenue().getCityName());
                    }

                    List<Sport.SummariesDTO.SportEventStatusDTO.PeriodScoresDTO> periodScores = summariesDTO.getSportEventStatus().getPeriodScores();

                    if (periodScores != null) {
                        List<Game.Score> scoreList = new ArrayList<>();
                        for (Sport.SummariesDTO.SportEventStatusDTO.PeriodScoresDTO periodScoresDTO : periodScores) {
                            score = new Game.Score();
                            score.setAway_score(periodScoresDTO.getAwayScore());
                            score.setHome_score(periodScoresDTO.getHomeScore());
                            score.setNumber(periodScoresDTO.getNumber() == null ? 0 : periodScoresDTO.getNumber());
                            scoreList.add(score);
                        }
                        game.setScore(scoreList);
                    }

                    List<Sport.SummariesDTO.SportEventDTO.CompetitorsDTO> competitors = summariesDTO.getSportEvent().getCompetitors();
                    for (Sport.SummariesDTO.SportEventDTO.CompetitorsDTO competitorsDTO : competitors) {
                        competitor = new Game.Competitors();
                        competitor.setCompetitors_name(competitorsDTO.getName());
                        competitor.setAbbreviation(competitorsDTO.getAbbreviation());
                        competitor.setQualifier(competitorsDTO.getQualifier());
                        competitor.setCountry_code(competitorsDTO.getCountryCode());
                        competitorsList.add(competitor);
                    }
                    game.setCompetitors(competitorsList);
                    status = summariesDTO.getSportEventStatus().getStatus();
                    game.setStatus(status);
                    if (status.equals("live") || status.equals("closed") || status.equals("ended") || status.equals("not_started")) {
                        game.setAway_score(summariesDTO.getSportEventStatus().getAwayScore() == null ? 0 : summariesDTO.getSportEventStatus().getAwayScore());
                        game.setHome_score(summariesDTO.getSportEventStatus().getHomeScore() == null ? 0 : summariesDTO.getSportEventStatus().getHomeScore());
                    }
                    gameList.add(game);
                }
                handler.sendEmptyMessage(1);
            }
        }).start();

    }

    private void request(int index) {
        WaitDialog.show("Please Wait!");

        switch (index) {
            case 0:
                address = Constants.BASKET_BALL_URL + date + Constants.SUFFIX + "?api_key=" + Constants.BASKET_BALL_KEY;
                break;
            case 1:
                address = Constants.FOOT_BALL_URL + date + Constants.SUFFIX + "?api_key=" + Constants.FOOT_BALL_KEY;
                break;
            case 2:
                address = Constants.ICE_HOCKEY_URL + date + Constants.SUFFIX + "?api_key=" + Constants.ICE_HOCKEY_KEY;
                break;
            case 3:
                address = Constants.TENNIS_URL + date + Constants.SUFFIX + "?api_key=" + Constants.TENNIS_KEY;
                break;
        }

        OkHttpUtil.sendHttpRequest(address, new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 解析响应请求
                    String responseBody = response.body().string();
                    Sport sport = JSON.parseObject(responseBody, Sport.class);
                    if (sport.getSummaries() != null) {
                        gameList.clear();

                        for (Sport.SummariesDTO summariesDTO : sport.getSummaries()) {
                            Game game = new Game();
                            game.setSport_name(summariesDTO.getSportEvent().getSportEventContext().getSport().getName());
                            game.setStart_time(Tools.getTime(summariesDTO.getSportEvent().getStartTime(), timeZoneId));
                            game.setStatus(summariesDTO.getSportEventStatus().getStatus());
                            game.setCountry_name(summariesDTO.getSportEvent().getSportEventContext().getCategory().getName());
                            game.setCompetition_name(summariesDTO.getSportEvent().getSportEventContext().getCompetition().getName());
                            game.setStage_start_date(Tools.getLocalDate(summariesDTO.getSportEvent().getSportEventContext().getSeason().getStartDate()));
                            game.setStage_end_date(Tools.getLocalDate(summariesDTO.getSportEvent().getSportEventContext().getSeason().getEndDate()));
                            game.setStage_type(summariesDTO.getSportEvent().getSportEventContext().getStage().getType());
                            game.setStage_phase(summariesDTO.getSportEvent().getSportEventContext().getStage().getPhase());

                            if (null != summariesDTO.getSportEvent().getVenue()) {
                                game.setVenue_name(summariesDTO.getSportEvent().getVenue().getName());
                                if (null != summariesDTO.getSportEvent().getVenue().getCapacity()) {
                                    game.setVenue_capacity(summariesDTO.getSportEvent().getVenue().getCapacity());
                                }
                                game.setCity_name(summariesDTO.getSportEvent().getVenue().getCityName());
                            }

                            List<Sport.SummariesDTO.SportEventStatusDTO.PeriodScoresDTO> periodScores = summariesDTO.getSportEventStatus().getPeriodScores();

                            if (periodScores != null) {
                                List<Game.Score> scoreList = new ArrayList<>();
                                for (Sport.SummariesDTO.SportEventStatusDTO.PeriodScoresDTO periodScoresDTO : periodScores) {
                                    Game.Score score = new Game.Score();
                                    score.setAway_score(periodScoresDTO.getAwayScore());
                                    score.setHome_score(periodScoresDTO.getHomeScore());
                                    score.setNumber(periodScoresDTO.getNumber() == null ? 0 : periodScoresDTO.getNumber());
                                    scoreList.add(score);
                                }
                                game.setScore(scoreList);
                            }

                            // 为每场比赛创建新的队伍列表
                            List<Game.Competitors> competitorsList = new ArrayList<>();
                            List<Sport.SummariesDTO.SportEventDTO.CompetitorsDTO> competitors = summariesDTO.getSportEvent().getCompetitors();

                            if (competitors != null) {
                                for (Sport.SummariesDTO.SportEventDTO.CompetitorsDTO competitorsDTO : competitors) {
                                    Game.Competitors competitor = new Game.Competitors();
                                    competitor.setCompetitors_name(competitorsDTO.getName());
                                    competitor.setAbbreviation(competitorsDTO.getAbbreviation());
                                    competitor.setQualifier(competitorsDTO.getQualifier());
                                    competitor.setCountry_code(competitorsDTO.getCountryCode());
                                    competitorsList.add(competitor);
                                }
                                game.setCompetitors(competitorsList);
                            }

                            String status = summariesDTO.getSportEventStatus().getStatus();
                            game.setStatus(status);
                            if (status.equals("live") || status.equals("closed") || status.equals("ended") || status.equals("not_started")) {
                                game.setAway_score(summariesDTO.getSportEventStatus().getAwayScore() == null ? 0 : summariesDTO.getSportEventStatus().getAwayScore());
                                game.setHome_score(summariesDTO.getSportEventStatus().getHomeScore() == null ? 0 : summariesDTO.getSportEventStatus().getHomeScore());
                            }
                            gameList.add(game);
                        }
                        handler.sendEmptyMessage(1);
                    }
                } else {
                    getActivity().runOnUiThread(() -> {
                        WaitDialog.dismiss();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> {
                    WaitDialog.dismiss();
                    showToast("Network request failed");
                });
            }
        });
    }

    public String getJson(String jsonName) {//从assets下读取user.json
        // 将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        // 获得assets资源管理器
        AssetManager assetManager = getActivity().getAssets();
        // 使用IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    assetManager.open(jsonName), "utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private List<Game> gameList = new ArrayList<>();
    private List<Game> dataList = new ArrayList<>();
    private List<Game> searchList = new ArrayList<>();
    private GameAdapter gameAdapter;

    private void initAdapter() {

        WaitDialog.dismiss();
        dataList.clear();
        if (search) {
            dataList.addAll(searchList);
        } else {
            dataList.addAll(gameList);
        }
     //   LogUtils.showLog("dataList.size():" + dataList.size());
        if (dataList.size() == 0) {
            getBinding().tvShow.setVisibility(View.VISIBLE);
        } else {
            getBinding().tvShow.setVisibility(View.GONE);
        }

        if (gameAdapter == null) {
            gameAdapter = new GameAdapter(getActivity(), dataList);
            getBinding().rvData.setLayoutManager(new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL, false));
            getBinding().rvData.setAdapter(gameAdapter);
        } else {
            gameAdapter.setList(dataList);
            gameAdapter.notifyDataSetChanged();
        }

        gameAdapter.setListener(pos -> {
            Intent intent = new Intent(getActivity(), SportActivity.class);
            intent.putExtra("game", gameList.get(pos));
            startActivity(intent);
        });
    }

    Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case 0:
                request(index);
                //initLocalData(index);
                break;
            case 1:
                initAdapter();
                break;
        }
        return false;
    });

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        int year = calendar.getYear();
        int month = calendar.getMonth();
        int day = calendar.getDay();
        date = year + "-" + month + "-" + day;
        try {
            date = Tools.StringToDate(date, "yyyy-M-d", "yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        request(index);
        //initLocalData(index);
    }
}
