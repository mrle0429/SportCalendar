package com.test.sport.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.test.nba.R;
import com.test.nba.databinding.ActivityMainBinding;
import com.test.sport.base.BaseActivity;
import com.test.sport.ui.adapter.ViewPagerAdapter;
import com.test.sport.ui.fragment.FavouriteFragment;

// 主页：按时间显示赛事
public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private String currentSport;
    private FavouriteFragment favouriteFragment;

    @Override
    protected void initData() {
        super.initData();
        initNavigation();
        initAdapter();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected ActivityMainBinding onCreateViewBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityMainBinding.inflate(layoutInflater);
    }

    private void initNavigation() {
        // 底部导航栏设置
        getBinding().navigation.setItemIconTintList(null);
        getBinding().navigation.setItemIconSize(90);//控制底部导航栏图标大小
        getBinding().navigation.setSelectedItemId(getBinding().navigation.getMenu().getItem(0).getItemId());

        // 底部导航栏点击事件
        getBinding().navigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getBinding().viewPager.setCurrentItem(0);
                    break;
                case R.id.navigation_schedule:
                    getBinding().viewPager.setCurrentItem(1);
                    break;
                case R.id.navigation_favourite:
                    getBinding().viewPager.setCurrentItem(2);
                    break;
                case R.id.navigation_chat:
                    getBinding().viewPager.setCurrentItem(3);
                    break;
            }
            return false;
        });
    }

    private void initAdapter() {
        // 设置适配器
        ViewPagerAdapter fragmentAdapter = new ViewPagerAdapter(this);
        getBinding().viewPager.setAdapter(fragmentAdapter);
        getBinding().viewPager.setUserInputEnabled(false);
        // 页面切换事件
        getBinding().viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                getBinding().navigation.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

    }

    public void setCurrentSport(String sport) {
        this.currentSport = sport;
        if (favouriteFragment != null) {
            favouriteFragment.onSportChanged(sport);
        }
    }
    
    public String getCurrentSport() {
        if (currentSport == null) {
            // 从 SharedPreferences 获取默认运动
            SharedPreferences prefs = getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE);
            currentSport = prefs.getString("default_sport", "Basketball");
            Log.d("SportSync", "MainActivity - 从SharedPreferences获取默认运动: " + currentSport);
        }
        return currentSport;
    }
    
    public void setFavouriteFragment(FavouriteFragment fragment) {
        this.favouriteFragment = fragment;
    }
}