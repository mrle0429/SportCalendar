package com.test.sport.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.test.nba.R;
import com.test.nba.databinding.FragmentSettingBinding;
import com.test.sport.base.BaseFragment;
import com.test.sport.ui.activity.PreferenceActivity;
import com.test.sport.ui.activity.TimezoneActivity;

import java.util.List;

// TODO:设置
public class SettingFragment extends BaseFragment<FragmentSettingBinding> implements View.OnClickListener {
    String location;
    private static final String CHANNEL_ID = "match_notifications";
    private AMapLocationClient mLocationClient = null;
    private static final int REQUEST_CODE_TIMEZONE = 1;
    private static final String TAG = "SettingFragment";
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
                    // 定位成功
                    StringBuilder locationInfo = new StringBuilder();
                    locationInfo.append("位置信息:\n")
                            .append("国家: ").append(aMapLocation.getCountry()).append("\n")
                            .append("城市: ").append(aMapLocation.getCity()).append("\n")
                            .append("地址: ").append(aMapLocation.getAddress()).append("\n")
                            .append("经度: ").append(aMapLocation.getLongitude()).append("\n")
                            .append("纬度: ").append(aMapLocation.getLatitude()).append("\n")
                            .append("精确度: ").append(aMapLocation.getAccuracy()).append("米");

                    location = locationInfo.toString();
                    getActivity().runOnUiThread(() -> {
                        getBinding().tvLocationInfo.setText(location);
                    });
                } else {
                    Log.e(TAG, String.format("定位失败: code=%d, info=%s",
                            aMapLocation.getErrorCode(),
                            aMapLocation.getErrorInfo()));
                    // 定位失败
                    String errText = "定位失败: " + aMapLocation.getErrorCode() +
                            "\n错误信息: " + aMapLocation.getErrorInfo();
                    getActivity().runOnUiThread(() -> {
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
        initLocation();
        loadPreferences();

        // 显示当前默认运动
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String defaultSport = prefs.getString(KEY_DEFAULT_SPORT, "Basketball");
        getBinding().tvDefaultSport.setText(defaultSport);

    }

    @Override
    protected void initClick() {
        super.initClick();
        getBinding().rlPreferences.setOnClickListener(this);
        getBinding().rlTimezone.setOnClickListener(this);
        getBinding().rlLocation.setOnClickListener(this);
        getBinding().rlNotification.setOnClickListener(this);
        getBinding().rlDefaultSport.setOnClickListener(this);
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_setting;
    }

    @Override
    protected FragmentSettingBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentSettingBinding.inflate(inflater);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_preferences:
                Intent intent1 = new Intent(getActivity(), PreferenceActivity.class);

                startActivity(intent1);
                break;
            case R.id.rl_timezone:
                Intent intent = new Intent(getActivity(), TimezoneActivity.class);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TIMEZONE && resultCode == RESULT_OK && data != null) {
            String selectedTimezone = data.getStringExtra(TimezoneActivity.PREF_SELECTED_TIMEZONE);
            if (selectedTimezone != null) {
                getBinding().tvSelectedTimezone.setText(selectedTimezone);
                Log.d(TAG, "选择的时区: " + selectedTimezone);

                Intent intent = new Intent("com.test.sport.TIMEZONE_CHANGED");
                getActivity().sendBroadcast(intent);
            }
        }
    }


    private void loadPreferences() {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String selectedTimezone = preferences.getString(PREF_SELECTED_TIMEZONE, TimeZone.getDefault().getID());
        getBinding().tvSelectedTimezone.setText(selectedTimezone);
        Log.d(TAG, "加载偏好设置: SelectedTimeZone=" + selectedTimezone);
    }


    // 初始化定位
    private void initLocation() {
        try {
            Log.d(TAG, "initLocation: 开始初始化定位客户端");
            mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
            mLocationClient.setLocationListener(mLocationListener);

            // 初始化定位参数
            AMapLocationClientOption option = new AMapLocationClientOption();
            // 设置定位模式为高精度模式
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 设置定位间隔,单位毫秒,默认为2000ms
            option.setInterval(2000);
            // 设置是否返回地址信息
            option.setNeedAddress(true);
            // 设置是否允许模拟位置,默认为false
            option.setMockEnable(false);
            // 单次定位
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
                            // 用户选择了永久拒绝
                            Toast.makeText(getContext(), "请在设置中授予定位权限", Toast.LENGTH_SHORT).show();
                            XXPermissions.startPermissionActivity(getContext(), permissions);
                        } else {
                            Toast.makeText(getContext(), "获取定位权限失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    // 请求通知权限
    private void requestNotificationPermission() {
        if (!isNotificationEnabled()) {
            // 提示用户在系统设置中启用通知
            Toast.makeText(getActivity(), "请在系统设置中启用通知权限", Toast.LENGTH_LONG).show();
            openNotificationSettings();
        } else {
            // 显示测试通知
            Log.e("notify", "a");
            showTestNotification();
        }
    }

    // 检查通知权限是否已启用
    private boolean isNotificationEnabled() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getActivity());
        return notificationManagerCompat.areNotificationsEnabled();
    }

    // 显示测试通知
    private void showTestNotification() {
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat_ai)
                .setContentTitle("比赛通知")
                .setContentText("This is a test notification to confirm that the notification function is working properly.")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
        notificationManager.notify(1, builder.build());
    }

    // 创建通知渠道（仅在 Android 8.0 及更高版本需要）
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "比赛通知";
            String description = "用于通知比赛信息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    // 打开通知设置界面
    private void openNotificationSettings() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
        }
        startActivity(intent);
    }


    private void startLocation() {
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    private void showDefaultSportDialog() {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String currentDefault = prefs.getString(KEY_DEFAULT_SPORT, "Basketball");

        // 找到当前选中的运动索引
        int checkedItem = 0;
        for (int i = 0; i < SUPPORTED_SPORTS.length; i++) {
            if (SUPPORTED_SPORTS[i].equals(currentDefault)) {
                checkedItem = i;
                break;
            }
        }

        new AlertDialog.Builder(getActivity())
                .setTitle("选择默认运动")
                .setSingleChoiceItems(SUPPORTED_SPORTS, checkedItem, (dialog, which) -> {
                    String selectedSport = SUPPORTED_SPORTS[which];

                    // 保存选择
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(KEY_DEFAULT_SPORT, selectedSport);
                    editor.apply();

                    // 更新显示
                    getBinding().tvDefaultSport.setText(selectedSport);

                    // 发送广播通知其他组件
                    Intent intent = new Intent("com.test.sport.DEFAULT_SPORT_CHANGED");
                    intent.putExtra("sport", selectedSport);
                    getActivity().sendBroadcast(intent);

                    dialog.dismiss();
                    Toast.makeText(getActivity(), "已设置默认运动：" + selectedSport, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
