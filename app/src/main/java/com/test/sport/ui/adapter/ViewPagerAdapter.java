package com.test.sport.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.test.sport.ui.fragment.ChatFragment;
import com.test.sport.ui.fragment.FavouriteFragment;
import com.test.sport.ui.fragment.HomeFragment;
import com.test.sport.ui.fragment.MapFragment;
import com.test.sport.ui.fragment.ScheduleFragment;

// fragment适配器
public class ViewPagerAdapter extends FragmentStateAdapter {

    HomeFragment homeFragment = new HomeFragment();
    ScheduleFragment scheduleFragment = new ScheduleFragment();
    FavouriteFragment favouriteFragment = new FavouriteFragment();
    ChatFragment chatFragment = new ChatFragment();
    MapFragment mapFragment = new MapFragment();

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
                return favouriteFragment;
            case 3:
                return chatFragment;

            case 4:
                return mapFragment;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
