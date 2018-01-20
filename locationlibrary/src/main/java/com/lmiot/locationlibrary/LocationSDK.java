package com.lmiot.locationlibrary;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Call;

/**
 * 创建日期：2018-01-20 10:41
 * 作者:Mr.Li
 * 描述:
 */
public class LocationSDK {

    private static AMapLocationListener mLocationListener ;
    private static AMapLocationClient mLocationClient;
    private static AMapLocationClientOption mMLocationOption;

    public  static  void startLocation(Context context){
        getLocation( context); //设置定位
        startLoca(); //开始定位

    }

    private static void getLocation(Context context) {

        //城市信息
//定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
        /*     //城市信息
             mCity = amapLocation.getAdCode();
             mCity_name = amapLocation.getCity();

             handler.sendEmptyMessage(0x601);*///定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
        mLocationListener  = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        Log.d("LocationSDK", "定位成功");
                        stopLocation();

                        searchWeather(amapLocation.getAdCode(),amapLocation.getCity());



                    } else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("LocationSDK", "定位失败, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());

                    }
                }


            }
        };
        mLocationClient = new AMapLocationClient(context);
        mLocationClient.setLocationListener(mLocationListener);

        mMLocationOption = new AMapLocationClientOption();

        mMLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        mMLocationOption.setOnceLocation(true);
        mMLocationOption.setOnceLocationLatest(true);
        mMLocationOption.setInterval(1000);
        mMLocationOption.setNeedAddress(true);
        mMLocationOption.setWifiActiveScan(false);
        mMLocationOption.setMockEnable(false);
        mMLocationOption.setMockEnable(false);


    }


    //开始定位
    private static void startLoca() {
        mLocationClient.setLocationOption(mMLocationOption);
        mLocationClient.startLocation();
    }

    public  static  void stopLocation(){
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        
    }
    public  static  void destroy(){
        if (mLocationClient != null) {
            mLocationClient.onDestroy();  //取消定位功能
        }

    }


    /**
     * 获取天气
     */
    private static void searchWeather(String cityCode,String cityName) {
        String url = "http://restapi.amap.com/v3/weather/weatherInfo?city=" + cityCode + "&key=bf3f26397460b1fd42ce5019a1988a0f";
        Log.d("MainActivity", url);

        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.d("MainActivity", "天气情况：" + response);

                        try {
                            WeatherBean weatherBean = new Gson().fromJson(response, WeatherBean.class);
                            if (weatherBean.getStatus().equals("1")) {
                                EventBus.getDefault().post(weatherBean);
                            } else {
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });
    }


}
