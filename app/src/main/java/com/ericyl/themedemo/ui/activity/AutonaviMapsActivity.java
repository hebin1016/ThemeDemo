package com.ericyl.themedemo.ui.activity;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.ericyl.themedemo.R;


public class AutonaviMapsActivity extends AppCompatActivity implements AMapLocationListener {

    private MapView mapView;
    private AMap aMap;
    private UiSettings uiSettings;

    private LocationManagerProxy locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autonavi_maps);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            uiSettings = aMap.getUiSettings();
        }
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(true);
        initLocationManager();
    }

    private void initLocationManager() {
        locationManager = LocationManagerProxy.getInstance(this);
        locationManager.requestLocationData(
                LocationProviderProxy.AMapNetwork, 5000, 15, this);
//        mLocationManagerProxy.setGpsEnable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
//            this.aMapLocation = aMapLocation;// 判断超时机制
            Double geoLat = aMapLocation.getLatitude();
            Double geoLng = aMapLocation.getLongitude();
            String cityCode = "";
            String desc = "";
            Bundle locBundle = aMapLocation.getExtras();
            if (locBundle != null) {
                cityCode = locBundle.getString("citycode");
                desc = locBundle.getString("desc");
            }
            String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
                    + "\n精    度    :" + aMapLocation.getAccuracy() + "米"
                    + "\n定位方式:" + aMapLocation.getProvider() + "\n城市编码:"
                    + cityCode + "\n位置描述:" + desc + "\n省:"
                    + aMapLocation.getProvince() + "\n市:" + aMapLocation.getCity()
                    + "\n区(县):" + aMapLocation.getDistrict() + "\n区域编码:" + aMapLocation
                    .getAdCode());
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void stopLocation() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager.destory();
        }
        locationManager = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        stopLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
