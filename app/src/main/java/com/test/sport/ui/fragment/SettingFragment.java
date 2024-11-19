package com.test.sport.ui.fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
// Android 相关
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocation;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.test.nba.R;
import com.test.nba.databinding.FragmentSettingBinding;
import com.test.sport.base.BaseFragment;

import java.util.List;

// TODO:设置
public class SettingFragment extends BaseFragment<FragmentSettingBinding> implements View.OnClickListener {
    String location;
    private static final String CHANNEL_ID = "match_notifications";
    private AMapLocationClient mLocationClient = null;
    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                // 定位成功，更新位置

                location = aMapLocation.getFloor();
                Log.e("location1", "ErrorCode: " + aMapLocation.getErrorCode());
                Log.e("location1", "Country: " + aMapLocation.getCountry());
                Log.e("location1", "City: " + aMapLocation.getCity());
                Log.e("location1", "Address: " + aMapLocation.getAddress());
                Log.e("location1", "floor:" +aMapLocation.getFloor());
                Log.e("location1", "floor:" +aMapLocation.getCityCode());
                Log.e("location1", "floor:" +aMapLocation.getAccuracy());
                getBinding().tvLocationInfo.setText("Location: " + location);
            } else {
                // 定位失败
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapError", errText);
            }
        }
    };

    @Override
    protected void initData() {
        super.initData();
        getBinding().titleBar.setTitle("Setting");
        getBinding().tvLocationInfo.setText("Location: " + location);
    }

    @Override
    protected void initClick() {
        super.initClick();
        getBinding().rlPreferences.setOnClickListener(this);
        getBinding().rlTime.setOnClickListener(this);
        getBinding().rlTimezone.setOnClickListener(this);
        getBinding().rlLocation.setOnClickListener(this);
        getBinding().rlNotification.setOnClickListener(this);
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
                break;
            case R.id.rl_timezone:
                break;
            case R.id.rl_time:
                break;
            case R.id.rl_location:
                getBinding().ivLocation.setSelected(!getBinding().ivLocation.isSelected());
                requestRPermission();
                break;
            case R.id.rl_notification:
                getBinding().ivNotification.setSelected(!getBinding().ivNotification.isSelected());
                requestNotificationPermission();
                break;
        }
    }

    // TODO:请求定位权限
    private void requestRPermission() {
        XXPermissions.with(this)
                .permission(Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (allGranted) {
                            Log.e("location", "onGranted: 所有申请的权限都已通过");
                            try {
                                initLocation();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }


                        }
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {

                    }
                });
    }


    // 初始化定位
    private void initLocation() throws Exception {
    Context context = getActivity().getApplicationContext();
    // 设置隐私合规接口
    AMapLocationClient.updatePrivacyShow(context,true,true);
    AMapLocationClient.updatePrivacyAgree(context,true);

    mLocationClient = new AMapLocationClient(context);
    mLocationClient.setLocationListener(mLocationListener);
    // 设置定位参数
    AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
    mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
    mLocationOption.setOnceLocation(true);
    mLocationOption.setNeedAddress(true);
    mLocationOption.setLocationCacheEnable(false); // 禁用缓存
    mLocationClient.setLocationOption(mLocationOption);
    // 启动定位
    mLocationClient.startLocation();
}




      // 请求通知权限
      private void requestNotificationPermission() {
        if (!isNotificationEnabled()) {
            // 提示用户在系统设置中启用通知
            Toast.makeText(getActivity(), "请在系统设置中启用通知权限", Toast.LENGTH_LONG).show();
            openNotificationSettings();
        } else {
            // 显示测试通知
            Log.e("notify","a");
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
}
