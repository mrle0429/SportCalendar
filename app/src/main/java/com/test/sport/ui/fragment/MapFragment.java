package com.test.sport.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.test.nba.R;
import com.test.nba.databinding.FragmentMapBinding;
import com.test.sport.base.BaseFragment;

public class MapFragment extends BaseFragment<FragmentMapBinding> {
    private static final String TAG = "MapFragment";
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private LocationClient mLocationClient = null;
    private boolean isFirstLoc = true; // 是否首次定位

    @Override
    protected int initLayout() {
        Log.d(TAG, "initLayout: 初始化布局");
        return R.layout.fragment_map;
    }

    @Override
    protected FragmentMapBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        Log.d(TAG, "onCreateViewBinding: 开始创建视图绑定");
        // 先同意隐私政策
        SDKInitializer.setAgreePrivacy(requireActivity().getApplicationContext(), true);

        // 然后初始化SDK
        SDKInitializer.initialize(requireActivity().getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        LocationClient.setAgreePrivacy(true);
        return FragmentMapBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void initData() {
        super.initData();
        Log.d(TAG, "initData: 开始初始化数据");
        // 获取地图控件引用
        mMapView = getBinding().bmapView;
        
        // 获取地图控制器
        mBaiduMap = mMapView.getMap();

        initLocation();
        // 初始化地图配置
        initMapConfig();


    }

    private void initMapConfig() {
        Log.d(TAG, "initMapConfig: 初始化地图配置");
        if (mBaiduMap != null) {
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
            
            // 设置地图缩放级别
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.zoom(18.0f);  // 设置地图缩放级别
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            
            // 设置地图类型为普通地图
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

            // 设置定位图层的配置信息
            MyLocationConfiguration config = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL,  // 定位模式
                true,  // 是否允许显示方向信息
                null   // 自定义定位图标
            );
            mBaiduMap.setMyLocationConfiguration(config);
        }
    }

    private void initLocation() {
        try {

            Log.d(TAG, "initLocation: 初始化定位");
            // 定位初始化
            mLocationClient = new LocationClient(requireActivity().getApplicationContext());
            LocationClientOption option = new LocationClientOption();
            // 设置定位模式
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            // 设置坐标类型
            option.setCoorType("bd09ll");
            // 设置是否需要地址信息
            option.setIsNeedAddress(true);
            // 设置是否需要位置描述
            option.setIsNeedLocationDescribe(true);
            // 设置扫描间隔，单位是毫秒
            option.setScanSpan(1000);
            
            // 设置定位选项
            mLocationClient.setLocOption(option);
            
            // 注册定位监听器
            mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation location) {
                    if (location == null || mBaiduMap == null) {
                        Log.e(TAG, "定位失败：location 或 map 为空");
                        return;
                    }

                    Log.d(TAG, "定位成功：" + location.getLatitude() + ", " + location.getLongitude());

                    // 构造定位数据
                    MyLocationData locData = new MyLocationData.Builder()
                            .accuracy(location.getRadius())
                            .direction(location.getDirection())
                            .latitude(location.getLatitude())
                            .longitude(location.getLongitude())
                            .build();

                    // 设置定位数据
                    mBaiduMap.setMyLocationData(locData);

                    // 是否是第一次定位
                    if (isFirstLoc) {
                        isFirstLoc = false;
                        Log.d(TAG, "首次定位，移动地图中心点");
                        // 设置地图中心点为定位点
                        MapStatus.Builder builder = new MapStatus.Builder()
                                .target(new com.baidu.mapapi.model.LatLng(location.getLatitude(), location.getLongitude()))
                                .zoom(18.0f);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    }
                }
            });

            // 开启定位
            mLocationClient.start();
            Log.d(TAG, "定位服务已启动");
        } catch (Exception e) {
            Log.e(TAG, "定位初始化失败", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        // 停止定位
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        // 关闭定位图层
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationEnabled(false);
        }
        // 销毁地图
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }
}