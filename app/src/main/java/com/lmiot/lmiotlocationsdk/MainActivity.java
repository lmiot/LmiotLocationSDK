package com.lmiot.lmiotlocationsdk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lmiot.locationlibrary.LocationActivity;
import com.lmiot.locationlibrary.LocationSDK;
import com.lmiot.locationlibrary.LocationSetBean;
import com.lmiot.locationlibrary.WeatherBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;



public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    //MapView mMapView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        mTextView = findViewById(R.id.id_weather);
     //   LocationSDK.startLocation(getApplicationContext());
        startActivity(new Intent(MainActivity.this, LocationActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        LocationSDK.destroy();
    }

    /**
     * 【5】收到定位和天气情况
     * 注:threadMode = ThreadMode.MAIN 有5种模式选择！！
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WeatherBean weatherBean) {
        mTextView.setText(new Gson().toJson(weatherBean));

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LocationSetBean locationSetBean) {
        mTextView.setText(locationSetBean.getName());

    }
}
