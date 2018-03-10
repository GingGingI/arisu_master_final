package com.chkk.arisong_arisu;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class GPSInfo  extends Service implements LocationListener {

    private final Context mContext;

    boolean isGPSEnabled = false,
            isNetworkEnabled = false,
            isGetLocation = false;

    public static final int REQUEST_GPS = 0;
    private static final int MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final int MIN_TIME_BW_UPDATES = 500;

    Location location;
    double lat = 37;
    double lon = 127;
    double[] position = new double[2];

    protected LocationManager locationManager;

    public GPSInfo(Context context) {
        mContext = context;
        Log.i("sdsds","Dsdsds");
        onLocationChanged(location);
    }

    public Location getLocation() {
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        Log.d("check", "locationManager 댐");
        // GPS 정보 가져오기
        isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
        Log.d("check", "Gps값:"+isGPSEnabled);
        //현재 네트워크 상태 값 알아오기
        isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);
        Log.d("check", "Network값:"+isNetworkEnabled);

        checkPermission();
        Log.d("check","chkperission 됨");

        if (!isGPSEnabled && !isNetworkEnabled) {
            // GPS 와 네트워크사용이 가능하지 않을때 소스 구현
            Log.d("check","안됨");
        } else {
            this.isGetLocation = true;
            //네트워크 정보로 부터 위치값 가져오기
            if (isNetworkEnabled) {
                Log.d("check","네트워크 됨");
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    Log.d("check","gps 됨");
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        // 위도 경도 저장
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                    }
                }
            }

            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                    }
                }
            }
        }
        Log.i("CHK : ", lat+" "+lon);
        return location;
    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            },REQUEST_GPS);

        }
    }

    public void stopUsingGPS(){
        if (ContextCompat.checkSelfPermission(mContext,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationManager != null){
            locationManager.removeUpdates(GPSInfo.this);
        }
    }

    public double getLatitude(){
        if (location != null)
            lat = location.getLatitude();
        Log.i(lat+""," ");
        return lat;
    }

    public double getLongitude(){
        if(location != null)
            lon = location.getLongitude();
        Log.i(lon+""," ");
        return lon;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onLocationChanged(Location location){
        getLocation();
        MainActivity maps = (MainActivity) mContext;
        if (String.valueOf(getLatitude()) != null && String.valueOf(getLongitude()) != null) {
            maps.setLatLon(getLatitude(),getLongitude());
            maps.DosetPosition();
            Log.i("works", getLatitude()+" , "+getLongitude());
        }
        Log.i("tlqkf","종하~~!");
        Log.d("GPS","Location 바뀜, Lat :"+lat+", Lon : "+lon);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

}
