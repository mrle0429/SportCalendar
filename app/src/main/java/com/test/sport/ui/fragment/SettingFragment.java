package com.test.sport.ui.fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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



}
