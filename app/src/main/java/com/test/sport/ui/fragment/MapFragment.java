package com.test.sport.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.test.nba.R;
import com.test.nba.databinding.FragmentMapBinding;
import com.test.sport.base.BaseFragment;

public class MapFragment extends BaseFragment<FragmentMapBinding> {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;

    @Override
    protected int initLayout() {
        return R.layout.fragment_map;
    }

    @Override
    protected FragmentMapBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        // 先同意隐私政策
        SDKInitializer.setAgreePrivacy(requireActivity().getApplicationContext(), true);
        // 然后初始化SDK
        SDKInitializer.initialize(requireActivity().getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        return FragmentMapBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void initData() {
        super.initData();
        // 获取地图控件引用
        mMapView = getBinding().bmapView;
        
        // 获取地图控制器
        mBaiduMap = mMapView.getMap();
        
        // 初始化地图配置
        initMapConfig();
    }

    private void initMapConfig() {
        if (mBaiduMap != null) {
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
            
            // 设置地图缩放级别
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.zoom(18.0f);  // 设置地图缩放级别
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            
            // 设置地图类型为普通地图
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationEnabled(false);
        }
    }
}