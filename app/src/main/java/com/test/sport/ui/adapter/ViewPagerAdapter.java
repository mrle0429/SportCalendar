package com.test.sport.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.test.sport.ui.fragment.HomeFragment;
import com.test.sport.ui.fragment.ScheduleFragment;
import com.test.sport.ui.fragment.SettingFragment;
import com.test.sport.ui.fragment.ChatFragment;

// TODO:fragment适配器
public class ViewPagerAdapter extends FragmentStateAdapter {

    HomeFragment homeFragment = new HomeFragment();
    ScheduleFragment scheduleFragment = new ScheduleFragment();
    SettingFragment settingFragment = new SettingFragment();
    ChatFragment chatFragment = new ChatFragment();

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return homeFragment;
            case 1:
                return scheduleFragment;
            case 2:
                return settingFragment;
            case 3:
                return chatFragment;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
