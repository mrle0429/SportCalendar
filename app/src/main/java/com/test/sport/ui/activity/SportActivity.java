package com.test.sport.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.lxj.xpopup.XPopup;
import com.test.nba.R;
import com.test.nba.databinding.ActivitySportBinding;
import com.test.sport.base.BaseActivity;
import com.test.sport.db.DbScheduleController;
import com.test.sport.model.Game;
import com.test.sport.db.entity.Schedule;
import com.test.sport.event.ScheduleEvent;
import com.test.sport.utils.CalendarReminderUtils;
import com.test.sport.utils.Tools;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

// TODO:体育 
public class SportActivity extends BaseActivity<ActivitySportBinding> {

    private List<String> dataList = new ArrayList<>();
    private List<Game> gameList = new ArrayList<>();
    private String remind;
    private Game game;
    private String timeZoneId; //时区

    private static final String FILE_PROVIDER_AUTHORITY = "com.test.sport.fileprovider";

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

        getBinding().titleBar.setRightIcon(R.drawable.ic_share);
        getBinding().titleBar.setRightIconOnClickListener(v -> shareGame());
    }

    private void shareGame() {
        // 1. 创建分享视图
        View shareView = LayoutInflater.from(this).inflate(R.layout.layout_share_game, null);
        
        // 2. 设置分享内容
        TextView titleTv = shareView.findViewById(R.id.tv_game_title);
        TextView timeTv = shareView.findViewById(R.id.tv_game_time);
        TextView homeTv = shareView.findViewById(R.id.tv_home_team);
        TextView awayTv = shareView.findViewById(R.id.tv_away_team);
        
        titleTv.setText(game.getCompetition_name() + " " + game.getStage_phase());
        timeTv.setText(game.getStart_time());
        
        if (game.getCompetitors() != null && game.getCompetitors().size() >= 2) {
            homeTv.setText(game.getCompetitors().get(0).getCompetitors_name());
            awayTv.setText(game.getCompetitors().get(1).getCompetitors_name());
        }
    
        // 3. 将视图转换为Bitmap
        shareView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        shareView.layout(0, 0, shareView.getMeasuredWidth(), shareView.getMeasuredHeight());
    
        Bitmap bitmap = Bitmap.createBitmap(
            shareView.getMeasuredWidth(),
            shareView.getMeasuredHeight(), 
            Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        shareView.draw(canvas);
    
        // 4. 分享图片
        try {
            String fileName = "game_share.jpg";
            File file = new File(getExternalCacheDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
    
            Uri uri = FileProvider.getUriForFile(this,
                getPackageName() + ".fileprovider", file);
    
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Game"));
    
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
        }
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
        if (status.equals("live") || status.equals("closed") || status.equals("ended") || status.equals("not_started")) {
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
        String date = game.getStart_time();//2024-06-01 00:00
        try {
            date = Tools.StringToDate(date, "yyyy-MM-dd HH:mm", "yyyy-M-d HH:mm");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String dates[] = date.split(" ");
        schedule.setDate(dates[0]);
        schedule.setTime(dates[1]);
        gameList.add(game);
        schedule.setGameList(gameList);
        DbScheduleController.getInstance(this).insert(schedule);
        showToast("Successfully added");
        CalendarReminderUtils.addCalendarEvent(this, getBinding().tvType.getText().toString(), "ss",
                Tools.strToLong(date, "yyyy-M-d HH:mm"), remind);
        EventBus.getDefault().post(new ScheduleEvent());
        finish();
    }

}