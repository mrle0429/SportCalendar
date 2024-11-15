package com.test.sport.ui.activity;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.test.nba.R;
import com.test.nba.databinding.ActivityMainBinding;
import com.test.sport.base.BaseActivity;
import com.test.sport.ui.adapter.ViewPagerAdapter;

// TODO:主页
public class MainActivity extends BaseActivity<ActivityMainBinding> {

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

        getBinding().navigation.setItemIconTintList(null);
        getBinding().navigation.setItemIconSize(90);//控制底部导航栏图标大小
        getBinding().navigation.setSelectedItemId(getBinding().navigation.getMenu().getItem(0).getItemId());

        getBinding().navigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getBinding().viewPager.setCurrentItem(0);
                    break;
                case R.id.navigation_schedule:
                    getBinding().viewPager.setCurrentItem(1);
                    break;
                case R.id.navigation_setting:
                    getBinding().viewPager.setCurrentItem(2);
                    break;
            }
            return false;
        });
    }

    private void initAdapter() {
        ViewPagerAdapter fragmentAdapter = new ViewPagerAdapter(this);
        getBinding().viewPager.setAdapter(fragmentAdapter);
        getBinding().viewPager.setUserInputEnabled(false);
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

}