package com.test.sport.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;


import com.baidu.location.LocationClientOption;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.location.Poi;
import com.baidu.location.PoiRegion;
import com.baidu.mapapi.map.MapPoi;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import com.test.nba.R;
import com.test.nba.databinding.FragmentMapBinding;
import com.test.sport.base.BaseFragment;

import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.MessageDigest;
import org.json.JSONArray;
import org.json.JSONObject;

public class MapFragment extends BaseFragment<FragmentMapBinding> {
    private static final String TAG = "MapFragment";
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private LocationClient mLocationClient = null;
    private boolean isFirstLoc = true; // 是否首次定位
    private PoiSearch mPoiSearch = null;
    private static final String TRANSLATE_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";
    private static final String TRANSLATE_APP_ID = "20241220002233178";  // TODO: 替换为你的百度翻译APP ID
    private static final String TRANSLATE_SECRET = "7iqOxqArqLrzggilj9A2";   // TODO: 替换为你的百度翻译密钥

    @Override
    protected int initLayout() {
        Log.d(TAG, "initLayout: 初始化布局");
        return R.layout.fragment_map;
    }

    @Override
    protected FragmentMapBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        Log.d(TAG, "onCreateViewBinding: 开始创建视图绑定");
        // 先同意隐私政策
        //SDKInitializer.setAgreePrivacy(requireActivity().getApplicationContext(), true);

        // 然后初始化SDK
        SDKInitializer.initialize(requireActivity().getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        //LocationClient.setAgreePrivacy(true);
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

        initPoiSearch();

        initMapAndLocation();

        initPoiSearch();




    }

    private void initMapAndLocation() {
        try {
            Log.d(TAG, "initMapAndLocation: 开始初始化定位和地图");
            
            // 1. 先初始化定位
            mLocationClient = new LocationClient(requireActivity().getApplicationContext());
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            option.setCoorType("bd09ll");
            option.setIsNeedAddress(true);
            option.setIsNeedLocationDescribe(true);
            option.setScanSpan(2000);
            option.setIsNeedAltitude(true);
            
            mLocationClient.setLocOption(option);
            
            mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation location) {
                    if (location == null || mBaiduMap == null) {
                        Log.e(TAG, "定位失败：location 或 map 为空");
                        return;
                    }

                    Log.d(TAG, "定位成功：" + location.getLatitude() + ", " + location.getLongitude());
                    Log.d(TAG, "定位成功：" + location.getCity());

                    MyLocationData locData = new MyLocationData.Builder()
                            .accuracy(location.getRadius())
                            .direction(location.getDirection())
                            .latitude(location.getLatitude())
                            .longitude(location.getLongitude())
                            .build();

                    mBaiduMap.setMyLocationData(locData);

                    if (isFirstLoc) {
                        isFirstLoc = false;
                        Log.d(TAG, "首次定位，移动地图中心点");
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        MapStatus.Builder builder = new MapStatus.Builder()
                                .target(new com.baidu.mapapi.model.LatLng(location.getLatitude(), location.getLongitude()))
                                .zoom(18.0f);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                        searchNearbyPoi(latLng);
                    }
                }
            });

            // 2. 然后初始化地图配置
            if (mBaiduMap != null) {
                // 开启定位图层
                mBaiduMap.setMyLocationEnabled(true);
                
                // 设置地图缩放级别
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.zoom(18.0f);
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                
                // 设置地图类型为普通地图
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

                // 设置定位图层的配置信息
                MyLocationConfiguration config = new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.FOLLOWING,
                    true,
                    null,
                    0xAAFFFF88,
                    0xAA00FF00
                );
                mBaiduMap.setMyLocationConfiguration(config);
            }

            // 3. 最后启动定位
            mLocationClient.start();
            Log.d(TAG, "定位服务已启动");

        } catch (Exception e) {
            Log.e(TAG, "初始化失败", e);
            e.printStackTrace();
        }
    }

    private void initPoiSearch() {
        try {
            // 创建POI检索实例
            mPoiSearch = PoiSearch.newInstance();
            
            // 创建POI检索监听器
            OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
                @Override
                public void onGetPoiResult(PoiResult poiResult) {
                    if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                        Log.d(TAG, "POI检索：未找到结果");
                        return;
                    }
                    
                    if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                        List<PoiInfo> allPoi = poiResult.getAllPoi();
                        if (allPoi != null) {
                            mBaiduMap.clear(); // 清除旧的标记
                            for (PoiInfo poi : allPoi) {
                                Log.d(TAG, "POI: " + poi.name + 
                                          ", 地址: " + poi.address + 
                                          ", 位置: " + poi.location.latitude + 
                                          "," + poi.location.longitude);
                                
                                // 在地图上添加标记
                                MarkerOptions markerOptions = new MarkerOptions()
                                    .position(poi.location)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.img))
                                    .anchor(0.5f, 0.5f)
                                    .scaleX(1.5f)
                                    .scaleY(1.5f);
                                mBaiduMap.addOverlay(markerOptions);

                                Marker marker = (Marker) mBaiduMap.addOverlay(markerOptions);
                                // 将POI信息与标记关联
                                marker.setTitle(poi.name);
                                Bundle bundle = new Bundle();
                                bundle.putString("name", poi.name);
                                bundle.putString("address", poi.address);
                                bundle.putString("phone", poi.phoneNum);
                                marker.setExtraInfo(bundle);
                            }
                        }
                    }
                }

                @Override
                public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
                    // POI详情检索结果回调
                }

                @Override
                public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                    // 室内POI检索结果回调
                }
                
                @Override
                public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                    // 已废弃的方法，但仍需要实现
                }
            };
            
            // 设置检索监听器
            mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
            

            mBaiduMap.setOnMarkerClickListener(marker -> {
                Bundle bundle = marker.getExtraInfo();
                if (bundle != null) {
                    showPoiInfoWindow(marker);
                }
                return true;
            });
            

            // 设置地图点击事件监听器
            mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    // 点击地图时隐藏信息窗口
                    mBaiduMap.hideInfoWindow();
                }

                @Override
                public void onMapPoiClick(MapPoi mapPoi) {
                    // 点击地图POI点时也隐藏信息窗口
                    mBaiduMap.hideInfoWindow();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "POI搜索初始化失败", e);
        }
    }


    private void showPoiInfoWindow(Marker marker) {
        try {
            Bundle bundle = marker.getExtraInfo();
            if (bundle == null) return;
            
            String poiName = bundle.getString("name", "");
            String poiAddress = bundle.getString("address", "暂无");
            
            // 翻译POI名称
            translateText(poiName, translatedName -> {
                // 翻译地址
                translateText(poiAddress, translatedAddress -> {
                    requireActivity().runOnUiThread(() -> {
                        // 创建信息窗口视图
                        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_poi_info_window, null);
                        TextView titleTv = view.findViewById(R.id.tv_title);
                        TextView addressTv = view.findViewById(R.id.tv_address);
                        TextView phoneTv = view.findViewById(R.id.tv_phone);
                        
                        // 设置信息内容
                        titleTv.setText(translatedName);  // 使用翻译后的名称
                        addressTv.setText("Address: " + translatedAddress);  // 使用翻译后的地址
                        phoneTv.setText("Contact: " + bundle.getString("phone", "暂无"));
                        
                        // 创建InfoWindow
                        InfoWindow infoWindow = new InfoWindow(
                            view,
                            marker.getPosition(),
                            -47
                        );
                        
                        // 显示InfoWindow
                        mBaiduMap.showInfoWindow(infoWindow);
                    });
                });
            });
            
        } catch (Exception e) {
            Log.e(TAG, "显示POI信息窗口失败", e);
        }
    }

    // 添加翻译方法
    private void translateText(String text, TranslateCallback callback) {
        new Thread(() -> {
            try {
                String salt = String.valueOf(System.currentTimeMillis());
                String sign = generateMD5(TRANSLATE_APP_ID + text + salt + TRANSLATE_SECRET);
                
                String url = TRANSLATE_URL + "?q=" + URLEncoder.encode(text, "UTF-8") 
                        + "&from=zh&to=en"
                        + "&appid=" + TRANSLATE_APP_ID
                        + "&salt=" + salt
                        + "&sign=" + sign;

                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray transResult = jsonResponse.getJSONArray("trans_result");
                String translatedText = transResult.getJSONObject(0).getString("dst");
                
                callback.onTranslate(translatedText);
            } catch (Exception e) {
                Log.e(TAG, "翻译失败", e);
                callback.onTranslate(text); // 翻译失败时返回原文
            }
        }).start();
    }

    // 添加翻译回调接口
    private interface TranslateCallback {
        void onTranslate(String translatedText);
    }

    // 添加MD5加密方法
    private String generateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            Log.e(TAG, "MD5加密失败", e);
            return "";
        }
    }

    // 起周边POI检索
    private void searchNearbyPoi(LatLng location) {
        if (mPoiSearch != null) {
            Log.d(TAG, "开始POI搜索，中心点：" + location.latitude + ", " + location.longitude);

            // 发起周边检索
            mPoiSearch.searchNearby(new PoiNearbySearchOption()
                    .location(location)  // 使用传入的位置
                    .radius(10000)  // 搜索半径，单位：米
                    .keyword("运动")  // 搜索关键字
                    .pageNum(0)
                    .pageCapacity(10));
        } else {
            Log.e(TAG, "POI搜索实例为空");
        }
    }





    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (mMapView != null) {
            mMapView.onResume();
        }


        if (mLocationClient != null && !mLocationClient.isStarted()) {
            mLocationClient.start();
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

        if (mPoiSearch != null) {
            mPoiSearch.destroy();
        }
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