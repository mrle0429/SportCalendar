package com.test.sport.ui.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocation;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.test.nba.R;
import com.test.nba.databinding.ActivitySettingBinding;
import com.test.sport.base.BaseActivity;

import java.util.List;

public class SettingActivity extends BaseActivity<ActivitySettingBinding> implements View.OnClickListener {
    String location;
    private static final String CHANNEL_ID = "match_notifications";
    private AMapLocationClient mLocationClient = null;
    private static final int REQUEST_CODE_TIMEZONE = 1;
    private static final String TAG = "SettingActivity";
    private static final String PREFS_NAME = "SettingsPrefs";
    private static final String PREF_SELECTED_TIMEZONE = "SelectedTimezone";
    private static final String KEY_FAVORITE_TEAM = "favorite_team";
    private static final String KEY_FAVORITE_LEAGUES = "favorite_leagues";
    private static final String KEY_PREFRRED_TIMES = "preferred_times";
    private static final String KEY_DEFAULT_SPORT = "default_sport";
    private static final String[] SUPPORTED_SPORTS = new String[]{"Basketball", "Football", "Soccer", "Icehockey", "Tennis", "Rugby"};

    private final AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    Log.i(TAG, "onLocationChanged: 定位成功");
                    Log.d(TAG, String.format("位置信息: 经度=%f, 纬度=%f, 地址=%s",
                            aMapLocation.getLongitude(),
                            aMapLocation.getLatitude(),
                            aMapLocation.getAddress()));
                    StringBuilder locationInfo = new StringBuilder();
                    locationInfo.append("位置信息:\n")
                            .append("国家: ").append(aMapLocation.getCountry()).append("\n")
                            .append("城市: ").append(aMapLocation.getCity()).append("\n")
                            .append("地址: ").append(aMapLocation.getAddress()).append("\n")
                            .append("经度: ").append(aMapLocation.getLongitude()).append("\n")
                            .append("纬度: ").append(aMapLocation.getLatitude()).append("\n")
                            .append("精确度: ").append(aMapLocation.getAccuracy()).append("米");

                    location = locationInfo.toString();
                    runOnUiThread(() -> {
                        getBinding().tvLocationInfo.setText(location);
                    });
                } else {
                    Log.e(TAG, String.format("定位失败: code=%d, info=%s",
                            aMapLocation.getErrorCode(),
                            aMapLocation.getErrorInfo()));
                    String errText = "定位失败: " + aMapLocation.getErrorCode() +
                            "\n错误信息: " + aMapLocation.getErrorInfo();
                    runOnUiThread(() -> {
                        getBinding().tvLocationInfo.setText(errText);
                    });
                }
            } else {
                Log.e(TAG, "onLocationChanged: 定位结果为null");
            }
        }
    };

    @Override
    protected void initData() {
        super.initData();
        getBinding().titleBar.setTitle("Setting");
        // 设置返回按钮点击事件
        getBinding().titleBar.setLeftIconOnClickListener(v -> finish());
        
        initLocation();
        loadPreferences();

        // 显示当前默认运动
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String defaultSport = prefs.getString(KEY_DEFAULT_SPORT, "Basketball");
        getBinding().tvDefaultSport.setText(defaultSport);
    }

    @Override
    protected void initClick() {
        getBinding().rlPreferences.setOnClickListener(this);
        getBinding().rlTimezone.setOnClickListener(this);
        getBinding().rlLocation.setOnClickListener(this);
        getBinding().rlNotification.setOnClickListener(this);
        getBinding().rlDefaultSport.setOnClickListener(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected ActivitySettingBinding onCreateViewBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivitySettingBinding.inflate(layoutInflater);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_preferences:
                Intent intent1 = new Intent(this, PreferenceActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_timezone:
                Intent intent = new Intent(this, TimezoneActivity.class);
                startActivityForResult(intent, REQUEST_CODE_TIMEZONE);
                break;
            case R.id.rl_location:
                getBinding().ivLocation.setSelected(!getBinding().ivLocation.isSelected());
                checkLocationPermission();
                break;
            case R.id.rl_notification:
                getBinding().ivNotification.setSelected(!getBinding().ivNotification.isSelected());
                requestNotificationPermission();
                break;
            case R.id.rl_default_sport:
                showDefaultSportDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TIMEZONE && resultCode == RESULT_OK && data != null) {
            String selectedTimezone = data.getStringExtra(TimezoneActivity.PREF_SELECTED_TIMEZONE);
            if (selectedTimezone != null) {
                getBinding().tvSelectedTimezone.setText(selectedTimezone);
                Log.d(TAG, "选择的时区: " + selectedTimezone);

                Intent intent = new Intent("com.test.sport.TIMEZONE_CHANGED");
                sendBroadcast(intent);
            }
        }
    }

    private void loadPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String selectedTimezone = preferences.getString(PREF_SELECTED_TIMEZONE, TimeZone.getDefault().getID());
        getBinding().tvSelectedTimezone.setText(selectedTimezone);
        Log.d(TAG, "加载偏好设置: SelectedTimeZone=" + selectedTimezone);
    }

    private void initLocation() {
        try {
            Log.d(TAG, "initLocation: 开始初始化定位客户端");
            mLocationClient = new AMapLocationClient(getApplicationContext());
            mLocationClient.setLocationListener(mLocationListener);

            AMapLocationClientOption option = new AMapLocationClientOption();
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            option.setInterval(2000);
            option.setNeedAddress(true);
            option.setMockEnable(false);
            option.setOnceLocation(true);

            mLocationClient.setLocationOption(option);
            Log.d(TAG, "initLocation: 定位客户端配置完成");

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "initLocation: 初始化定位客户端失败", e);
        }
    }

    private void checkLocationPermission() {
        XXPermissions.with(this)
                .permission(Permission.ACCESS_FINE_LOCATION)
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            startLocation();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Toast.makeText(SettingActivity.this, "请在设置中授予定位权限", Toast.LENGTH_SHORT).show();
                            XXPermissions.startPermissionActivity(SettingActivity.this, permissions);
                        } else {
                            Toast.makeText(SettingActivity.this, "获取定位权限失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void requestNotificationPermission() {
        if (!isNotificationEnabled()) {
            Toast.makeText(this, "请在系统设置中启用通知权限", Toast.LENGTH_LONG).show();
            openNotificationSettings();
        } else {
            Log.e("notify", "a");
            showTestNotification();
        }
    }

    private boolean isNotificationEnabled() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        return notificationManagerCompat.areNotificationsEnabled();
    }

    private void showTestNotification() {
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat_ai)
                .setContentTitle("比赛通知")
                .setContentText("This is a test notification to confirm that the notification function is working properly.")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "比赛通知";
            String description = "用于通知比赛信息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void openNotificationSettings() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
        }
        startActivity(intent);
    }

    private void startLocation() {
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    private void showDefaultSportDialog() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String currentDefault = prefs.getString(KEY_DEFAULT_SPORT, "Basketball");

        int checkedItem = 0;
        for (int i = 0; i < SUPPORTED_SPORTS.length; i++) {
            if (SUPPORTED_SPORTS[i].equals(currentDefault)) {
                checkedItem = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("选择默认运动")
                .setSingleChoiceItems(SUPPORTED_SPORTS, checkedItem, (dialog, which) -> {
                    String selectedSport = SUPPORTED_SPORTS[which];

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(KEY_DEFAULT_SPORT, selectedSport);
                    editor.apply();

                    getBinding().tvDefaultSport.setText(selectedSport);

                    Intent intent = new Intent("com.test.sport.DEFAULT_SPORT_CHANGED");
                    intent.putExtra("sport", selectedSport);
                    sendBroadcast(intent);

                    dialog.dismiss();
                    Toast.makeText(this, "已设置默认运动：" + selectedSport, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}

