package com.test.sport.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.test.nba.R;
import com.test.nba.databinding.FragmentSettingBinding;
import com.test.sport.base.BaseFragment;

import java.util.List;

// TODO:设置
public class SettingFragment extends BaseFragment<FragmentSettingBinding> implements View.OnClickListener {

    @Override
    protected void initData() {
        super.initData();
        getBinding().titleBar.setTitle("Setting");
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

                        }
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {

                    }
                });
    }
}
