package com.test.sport.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.lxj.xpopup.XPopup;
import com.test.nba.R;
import com.test.nba.databinding.ActivitySportBinding;
import com.test.sport.base.BaseActivity;
import com.test.sport.db.DbScheduleController;
import com.test.sport.db.entity.Game;
import com.test.sport.db.entity.Schedule;
import com.test.sport.event.ScheduleEvent;
import com.test.sport.utils.CalendarReminderUtils;
import com.test.sport.utils.LogUtils;
import com.test.sport.utils.Tools;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

// TODO:体育 
public class SportActivity extends BaseActivity<ActivitySportBinding> {

    private List<String> dataList = new ArrayList<>();
    private List<Game> gameList=new ArrayList<>();
    private String remind;
    private Game game;
    private String timeZoneId; //时区

    @Override
    protected void initData() {
        super.initData();


        Tools.setStatusBarColor(this);
        initGame();
    }

    @Override
    protected void initClick() {
        super.initClick();
        getBinding().titleBar.setLeftIconOnClickListener(v -> finish());
        getBinding().tvSchedule.setOnClickListener(v -> requestRPermission());
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_sport;
    }

    @Override
    protected ActivitySportBinding onCreateViewBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivitySportBinding.inflate(layoutInflater);
    }

    private void initGame() {
        game = (Game) getIntent().getSerializableExtra("game");
        getBinding().tvStatus.setText(game.getStatus());
        getBinding().tvTime.setText(game.getStart_time().substring(5));

        String status = game.getStatus();
        if (status.equals("live") || status.equals("closed") || status.equals("ended")|| status.equals("not_started")) {
          //  LogUtils.showLog("game: " + game.getHome_score()+" " + game.getAway_score());
            if (game.getCompetitors().get(0).getQualifier().equals("home")) {
                getBinding().tvScore.setText(game.getHome_score() + " - " + game.getAway_score());
            } else {
                getBinding().tvScore.setText(game.getAway_score() + " - " + game.getHome_score());
            }
        }
        getBinding().tvHome1.setText(game.getCompetitors().get(0).getQualifier());
        getBinding().tvHome2.setText(game.getCompetitors().get(1).getQualifier());
        getBinding().tvType.setText(game.getCompetition_name() + " " + game.getStage_phase());
        getBinding().tvTeam1.setText(game.getCompetitors().get(0).getCompetitors_name());
        getBinding().tvTeam2.setText(game.getCompetitors().get(1).getCompetitors_name());

        getBinding().tvVenue.setText(game.getVenue_name());
        getBinding().tvCapacity.setText(game.getVenue_capacity() + "");
        getBinding().tvCity.setText(game.getCountry_name() + "-" + game.getCity_name());
        //getBinding().tvDate.setText(game.getStage_start_date() + " to " + game.getStage_end_date());

        if (game.getScore() != null && game.getScore().size() > 0) {
            for (Game.Score score : game.getScore()) {
                dataList.add(score.getNumber() + ":  home: " + score.getHome_score() + " -  away: " + score.getAway_score());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1, dataList);
            getBinding().lvData.setAdapter(adapter);
        }
    }

    private void showRemind() {
        new XPopup.Builder(this)
                .hasShadowBg(false)// 是否有半透明的背景，默认为true
                .isViewMode(true)
                .offsetY(50) //弹窗在y方向的偏移量
                .atView(getBinding().tvSchedule)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"No Reminder", "On Time", "10 Minutes Before", "30 Minutes Before", "1 Hour Before", "2 Hours Before"},
                        new int[]{},
                        (position, text) -> {
                            remind = text;
                            handler.sendEmptyMessage(1);
                        })
                .show();
    }


    // TODO:请求日历权限
    private void requestRPermission() {
        XXPermissions.with(this)
                .permission(Permission.Group.CALENDAR)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (allGranted) {
                            handler.sendEmptyMessage(0);
                        }
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {

                    }
                });
    }

    Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case 0:
                showRemind();
                break;
            case 1:
                schedule();
                break;
        }
        return false;
    });

    private void schedule() {
        Schedule schedule = new Schedule();
        schedule.setTitle(getBinding().tvType.getText().toString());
        schedule.setRemindTime(remind);
        schedule.setLocation(getBinding().tvVenue.getText().toString());
        String date=game.getStart_time();//2024-06-01 00:00
        try {
            date=Tools.StringToDate(date, "yyyy-MM-dd HH:mm", "yyyy-M-d HH:mm");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dates[]=date.split(" ");
        schedule.setDate(dates[0]);
        schedule.setTime(dates[1]);
        gameList.add(game);
        schedule.setGameList(gameList);
        DbScheduleController.getInstance(this).insert(schedule);
        showToast("Successfully added");
        CalendarReminderUtils.addCalendarEvent(this, getBinding().tvType.getText().toString(),"ss",
                Tools.strToLong(date, "yyyy-M-d HH:mm"), remind);
        EventBus.getDefault().post(new ScheduleEvent());
        finish();
    }

}