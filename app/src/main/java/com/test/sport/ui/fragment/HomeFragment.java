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
import com.test.sport.http.OkHttpUtil;
import com.test.sport.http.bean.Sport;
import com.test.sport.model.Game;
import com.test.sport.ui.activity.MainActivity;
import com.test.sport.ui.activity.SettingActivity;
import com.test.sport.ui.activity.SportActivity;
import com.test.sport.ui.adapter.GameAdapter;
import com.test.sport.ui.adapter.RecommendedGamesAdapter;
import com.test.sport.utils.Constants;
import com.test.sport.utils.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Response;

// 主页
public class HomeFragment extends BaseFragment<FragmentHomeBinding> implements View.OnClickListener,
        CalendarView.OnCalendarSelectListener {

    // 常量定义
    private static final String[] SUPPORTED_SPORTS = new String[]{"Basketball", "Soccer", "Icehockey", "Tennis"};
    private static final String PREFS_NAME = "SettingsPrefs";
    private static final String PREF_SELECTED_TIMEZONE = "SelectedTimezone";
    private static final String KEY_FAVORITE_TEAMS = "favorite_teams";
    private static final String KEY_PREFERRED_TIMES = "preferred_times";

    // 数据集合
    private List<Game> recommendedGames = new ArrayList<>();   // 推荐的比赛
    private List<Game> gameList = new ArrayList<>();          // 当前显示的比赛列表
    private List<Game> dataList = new ArrayList<>();         // 所有比赛
    private List<Game> searchList = new ArrayList<>();        // 搜索结果

    // 适配器
    private RecommendedGamesAdapter recommendedAdapter;
    private GameAdapter gameAdapter;

    //状态记录
    private int index;  //当前选择的运动索引
    private boolean search; //是否搜索

    private String address;
    private String jsonName;


    private String date; //当前选择的日期
    private String previousDate;
    private String timeZoneId; //时区


   

    // 时区变化广播接收器
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

    // 默认运动变化广播接收器
    private final BroadcastReceiver sportChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.test.sport.DEFAULT_SPORT_CHANGED".equals(intent.getAction())) {
                String newDefaultSport = intent.getStringExtra("sport");
                // 更新显示
                getBinding().tvSport.setText(newDefaultSport);
                // 更新index并重新请求数据
                for (int i = 0; i < SUPPORTED_SPORTS.length; i++) {
                    if (SUPPORTED_SPORTS[i].equals(newDefaultSport)) {
                        index = i;
                        break;
                    }
                }
                search = false;
                request(index);
            }
        }
    };

    // 喜欢的队伍变化广播接收器
    private final BroadcastReceiver favoritesChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.test.sport.FAVORITES_CHANGED".equals(intent.getAction())) {
                Log.d("FavoriteDebug", "HomeFragment收到收藏变化广播");

                // 获取最新的收藏球队列表
                SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                Set<String> newFavorites = prefs.getStringSet(KEY_FAVORITE_TEAMS, new HashSet<>());

                // 更新推荐适配器中的收藏球队集合
                if (recommendedAdapter != null) {
                    Log.d("FavoriteDebug", "更新推荐适配器的收藏球队列表: " + newFavorites);
                    recommendedAdapter.updateFavoriteTeams(newFavorites);
                    // 重新请求数据以更新推荐列表
                    request(index);
                }
            }
        }
    };

    @Override
    protected int initLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected FragmentHomeBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentHomeBinding.inflate(inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);   // initData和initCLick在这里调用
        registerReceivers();
        initDefaultSport();
        request(index);
    }

    @Override
    protected void initData() {
        super.initData();
        initTimeZone();
        initCalendarView();
        initDefaultDate();

    }

    // 初始化时区
    private void initTimeZone() {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        timeZoneId = preferences.getString(PREF_SELECTED_TIMEZONE, android.icu.util.TimeZone.getDefault().getID());
        Log.d("HomeFragment1", "加载的时区: " + timeZoneId);
    }

    // 初始化日历视图
    private void initCalendarView() {
        getBinding().calendarView.setOnCalendarSelectListener(this);
        // 隐藏月视图，显示周视图
        getBinding().calendarView.getMonthViewPager().setVisibility(View.GONE);
        getBinding().calendarView.getWeekViewPager().setVisibility(View.VISIBLE);
    }

    // 初始化日期
    private void initDefaultDate() {
        date = Tools.customFormat(new Date(), "yyyy-MM-dd");
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(parser.parse(date));
            cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
            previousDate = parser.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initClick() {
        super.initClick();
        getBinding().tvSport.setOnClickListener(this);
        getBinding().tvSearch.setOnClickListener(this);
        getBinding().ivSetting.setOnClickListener(this);

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

            case R.id.iv_setting:
                // 跳转到设置Activity
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
        }
    }


    // 注册广播接收器
    private void registerReceivers() {
        Log.d("HomeFragment1", "注册广播接收器");
        // 时区变化广播
        IntentFilter filter = new IntentFilter("com.test.sport.TIMEZONE_CHANGED");
        getActivity().registerReceiver(timezoneChangedReceiver, filter);

        // 默认运动变化广播
        IntentFilter sportFilter = new IntentFilter("com.test.sport.DEFAULT_SPORT_CHANGED");
        getActivity().registerReceiver(sportChangedReceiver, sportFilter);

        // 收藏变化广播
        IntentFilter favoritesFilter = new IntentFilter("com.test.sport.FAVORITES_CHANGED");
        requireContext().registerReceiver(favoritesChangedReceiver, favoritesFilter);
        Log.d("HomeFragment", "注册收藏变化广播接收器");
    }

    // 初始化默认运动
    private void initDefaultSport() {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String defaultSport = prefs.getString("default_sport", "Soccer");
        getBinding().tvSport.setText(defaultSport);

        // 设置对应的index
        for (int i = 0; i < SUPPORTED_SPORTS.length; i++) {
            if (SUPPORTED_SPORTS[i].equals(defaultSport)) {
                index = i;
                break;
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timezoneChangedReceiver != null) {
            getActivity().unregisterReceiver(timezoneChangedReceiver);
        }
        if (sportChangedReceiver != null) {
            getActivity().unregisterReceiver(sportChangedReceiver);
        }
        if (favoritesChangedReceiver != null) {
            requireContext().unregisterReceiver(favoritesChangedReceiver);
        }


    }

    // 搜索功能
    // 实现补完整
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
        //SharedPreferences prefs = getActivity().getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
        //String defaultSport = prefs.getString("default_sport", "Basketball");
        //Log.d("SportSync", "HomeFragment - 显示运动选择器，当前默认运动: " + defaultSport);
        // 运动选择弹窗
        new XPopup.Builder(getActivity())
                .hasShadowBg(false)// 是否有半透明的背景，默认为true
                .isViewMode(true)
                .offsetY(50) //弹窗在y方向的偏移量
                .atView(getBinding().tvSport)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(SUPPORTED_SPORTS,
                        new int[]{},
                        (position, text) -> {
                            getBinding().tvSport.setText(text);
                            index = position;
                            search = false;
                            // 通知MainActivity更新当前运动
                            ((MainActivity) getActivity()).setCurrentSport(text);
                            Log.d("SportSync", "HomeFragment - 设置新运动: " + text);
                            handler.sendEmptyMessage(0);
                        })
                .show();

        //     // 设置默认选中的运动
        //     getBinding().tvSport.setText(defaultSport);
        //     for (int i = 0; i < SUPPORTED_SPORTS.length; i++) {
        //         if (SUPPORTED_SPORTS[i].equals(defaultSport)) {
        //         index = i;
        //         break;
        //     }
        // }
        // search = false;
        // handler.sendEmptyMessage(0);
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
        gameList.clear(); // 清空比赛列表

        // 添加计数器和锁来确保两个请求都完成
        final int[] completedRequests = {0};
        final Object lock = new Object();

        try {
            Log.d("DateDebug", "今天：" + date);
            Log.d("DateDebug", "昨天：" + previousDate);
            // 请求两天的数据


            requestForDate(index, previousDate, new RequestCallback() {
                @Override
                public void onComplete() {
                    synchronized (lock) {
                        completedRequests[0]++;
                        Log.d("DateDebug", "昨天(" + previousDate + ")请求完成，当前比赛列表大小: " + gameList.size());
                        if (completedRequests[0] == 2) {
                            // 两个请求都完成后才更新UI
                            handler.sendEmptyMessage(1);
                            WaitDialog.dismiss();
                        }
                    }
                }
            });

            requestForDate(index, date, new RequestCallback() {
                @Override
                public void onComplete() {
                    synchronized (lock) {
                        completedRequests[0]++;
                        Log.d("DateDebug", "今天(" + date + ")请求完成，当前比赛列表大小: " + gameList.size());
                        if (completedRequests[0] == 2) {
                            // 两个请求都完成后才更新UI
                            handler.sendEmptyMessage(1);
                            WaitDialog.dismiss();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            getActivity().runOnUiThread(() -> {
                WaitDialog.dismiss();
                showToast("Date parsing failed");
            });
        }
    }

    // 添加回调接口
    interface RequestCallback {
        void onComplete();
    }

    private void requestForDate(int index, String requestDate, RequestCallback callback) {
        Log.d("DateDebug", "准备请求日期: " + requestDate + ", 运动索引: " + index);
        switch (index) {
            case 0:
                address = Constants.BASKET_BALL_URL + requestDate + Constants.SUFFIX + "?api_key=" + Constants.BASKET_BALL_KEY;
                break;

            case 1:
                address = Constants.SOCCER_URL + requestDate + Constants.SUFFIX + "?api_key=" + Constants.SOCCER_KEY;
                break;
            case 2:
                address = Constants.ICE_HOCKEY_URL + requestDate + Constants.SUFFIX + "?api_key=" + Constants.ICE_HOCKEY_KEY;

                break;
            case 3:
                address = Constants.TENNIS_URL + requestDate + Constants.SUFFIX + "?api_key=" + Constants.TENNIS_KEY;
                break;
        }
        Log.d("DateDebug", "请求URL: " + address);

        OkHttpUtil.sendHttpRequest(address, new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Sport sport = JSON.parseObject(responseBody, Sport.class);
                    String selectedDate = date;
                    if (sport.getSummaries() != null) {
                        synchronized (gameList) {  // 添加同步锁
                            Log.d("DateDebug", "处理 " + requestDate + " 的响应数据");
                            Log.d("DateDebug", "请求地址: " + call.request().url());
                            Log.d("DateDebug", "API返回比赛总数: " + sport.getSummaries().size());


                            for (Sport.SummariesDTO summariesDTO : sport.getSummaries()) {
                                String startTime = Tools.getTime(summariesDTO.getSportEvent().getStartTime(), timeZoneId);
                                String gameDate = startTime.split(" ")[0];

                                Log.d("DateDebug", "比赛转换日期" + gameDate);


                                if (gameDate.equals(selectedDate)) {

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
                            }
                        }
                    }
                }

                getActivity().runOnUiThread(() -> {
                    callback.onComplete();
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> {
                    Log.d("DateDebug", "失败");
                    showToast("Network request failed");
                    callback.onComplete();  // 即使失败也要调用回调
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


    private void initAdapter() {
        dataList.clear();
        if (search) {
            dataList.addAll(searchList);
        } else {
            synchronized (gameList) {  // 添加同步锁
                Collections.sort(gameList, (game1, game2) -> {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date date1 = sdf.parse(game1.getStart_time());
                        Date date2 = sdf.parse(game2.getStart_time());
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                });
                dataList.addAll(gameList);
            }
            updateRecommendedGames();
        }

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
                request(index);  //完整的数据重载流程（网络请求 -> 数据处理 -> UI更新）
                //initLocalData(index);
                break;
            case 1:      //仅执行UI更新（使用现有数据刷新显示）
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

            // 使用同样的Tools类计算前一天

            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(parser.parse(date));
            cal.add(java.util.Calendar.DAY_OF_MONTH, -1);
            previousDate = parser.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        request(index);
        //initLocalData(index);
    }

    private void updateRecommendedGames() {
        recommendedGames.clear();
        // 获取收藏的球队
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favoriteTeams = prefs.getStringSet(KEY_FAVORITE_TEAMS, new HashSet<>());
        Set<String> preferredTimes = prefs.getStringSet(KEY_PREFERRED_TIMES, new HashSet<>());

        Log.d("RecommendDebug", "总比赛数量: " + gameList.size());

        List<Game> scoredGames = new ArrayList<>();

        for (Game game : gameList) {
            int score = calculateGameScore(game, favoriteTeams, preferredTimes);
            game.setRecommendScore(score);
            if (score > 0) {
                scoredGames.add(game);
            }
        }

        // 按推荐分数排序
        Collections.shuffle(scoredGames);
        Collections.sort(scoredGames, (g1, g2) -> {
            int scoreCompare = Integer.compare(g2.getRecommendScore(), g1.getRecommendScore());
            if (scoreCompare == 0) {
                // 分数相同时保持随机顺序
                return 0;
            }
            return scoreCompare;
        });

        // 取前5场推荐比赛
        int recommendCount = Math.min(scoredGames.size(), 5);
        recommendedGames.addAll(scoredGames.subList(0, recommendCount));

        Log.d("RecommendDebug", "推荐比赛数量: " + recommendedGames.size());
        for (Game game : recommendedGames) {
            Log.d("RecommendDebug", "推荐比赛: " + game.getCompetition_name() +
                    ", 分数: " + game.getRecommendScore());
        }

        // 更新推荐赛事适配器
        if (recommendedAdapter == null) {
            Log.d("RecommendDebug", "创建新的推荐适配器");
            recommendedAdapter = new RecommendedGamesAdapter(getActivity(), recommendedGames, favoriteTeams);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL, false);
            getBinding().rvRecommendedGames.setLayoutManager(layoutManager);
            getBinding().rvRecommendedGames.setAdapter(recommendedAdapter);

            recommendedAdapter.setOnClickListener(pos -> {
                Intent intent = new Intent(getActivity(), SportActivity.class);
                intent.putExtra("game", recommendedGames.get(pos));
                startActivity(intent);
            });
        } else {
            recommendedAdapter.setGames(recommendedGames);
            Log.d("RecommendDebug", "更新现有推荐适配器");
        }
    }


    private int calculateGameScore(Game game, Set<String> favoriteTeams, Set<String> preferredTimes) {
        int score = 0;

        // 检查是否包含收藏的球队
        if (game.getCompetitors() != null) {
            for (Game.Competitors competitor : game.getCompetitors()) {
                String teamName = competitor.getCompetitors_name();
                if (favoriteTeams.contains(teamName)) {
                    score += 50;  // 包含收藏球队加50分
                    Log.d("RecommendDebug", "找到收藏球队: " + teamName);
                    break;
                }
            }
        }

        // 2. 检查比赛时间是否在偏好时间段内
        try {
            int hour = Integer.parseInt(game.getStart_time().substring(11, 13));
            Log.d("RecommendDebug", "比赛时间： " + hour);

            for (String timeRange : preferredTimes) {
                if (timeRange.contains("Morning") && hour >= 6 && hour < 12) {
                    score += 20;
                    Log.d("RecommendDebug", "符合早上时段偏好");
                } else if (timeRange.contains("Afternoon") && hour >= 12 && hour < 18) {
                    score += 20;
                    Log.d("RecommendDebug", "符合下午时段偏好");
                } else if (timeRange.contains("Evening") && hour >= 18 && hour < 24) {
                    score += 20;
                    Log.d("RecommendDebug", "符合晚上时段偏好");
                } else if (timeRange.contains("Dawn") && hour >= 0 && hour < 6) {
                    score += 20;
                    Log.d("RecommendDebug", "符合凌晨时段偏好");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("RecommendDebug", "时间检查失败", e);
        }

        // 重点赛事加分
        String competition = game.getCompetition_name();
        if (competition != null) {
            // 篮球热门赛事
            if (competition != null) {
                if (Tools.isPopularBasketballEvent(competition) ||
                        Tools.isPopularFootballEvent(competition) ||
                        Tools.isPopularHockeyEvent(competition) ||
                        Tools.isPopularTennisEvent(competition)) {
                    score += 20;
                    Log.d("RecommendDebug", "热门赛事: " + competition);
                }
            }
        }


        return score;
    }
}
