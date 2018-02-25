package com.lmiot.locationlibrary;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lmiot.tiblebarlibrary.LmiotTitleBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


/**
 * 创建日期：2018-02-24 11:58
 * 作者:Mr.Li
 * 描述:
 */

public class LocationActivity extends Activity implements LocationSource, GeocodeSearch.OnGeocodeSearchListener, AMap.OnMyLocationChangeListener{


    private OnLocationChangedListener mOnLocationChangedListener;
    private Marker mMarker;
    private GeocodeSearch mGeocoderSearch;
    private AMap mMap;
    private LmiotTitleBar mIdLmiotTitleBar;
    private EditText mIdSearchEdit;
    private ListView mIdListview;
    private TextView mIdLocation;
    private MapView mMapView;
    private LinearLayout mIdMapLayout;
    private ImageView mIdSearch;
    private double mLatitudeNow;
    private double mLongitudeNow;
    private double mLatitudeSet;
    private double mLongitudeSet;
    private SearchAdapter mSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initView();



        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);




        if (mMap == null) {
            mMap = mMapView.getMap();
        }


        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        mMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        mMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        mMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        mMap.setOnMyLocationChangeListener(this);


        //逆地理编码
        mGeocoderSearch = new GeocodeSearch(this);
        mGeocoderSearch.setOnGeocodeSearchListener(this);


        //点击标记

        mMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;

                showMark(latitude,longitude);

                // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系

                showLocationName(latitude, longitude);

            }
        });


        //搜索

        mIdSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String value = charSequence.toString();
                if (TextUtils.isEmpty(value)) {
                    mIdListview.setVisibility(View.GONE);

                } else {

                    mIdListview.setVisibility(View.VISIBLE);
                    search(value);


                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void showMark(double latitude,double longitude) {
        if (mMarker != null) {
            mMarker.destroy();
        }




        //显示标记点
        mMarker = mMap.addMarker(new MarkerOptions().position( new LatLng(latitude,longitude)));
    }

    /**
     * 搜索
     * @param value
     */
    private void search(String value) {
    String    url = "http://restapi.amap.com/v3/place/text?key=9556a92d60011d825d77988d3d691d2f&location=" + mLongitudeNow + "," + mLatitudeNow + "&keywords=" + value;
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
                        Log.d("ActionMethod", response);

                        try {
                            String replace = response.replace(":[]", ":''"); //adress有可能为空数组
                            LocationBean locationBean = new Gson().fromJson(replace, LocationBean.class);
                            List<LocationBean.PoisBean> poisBeanList = locationBean.getPois();

                            if(mSearchAdapter==null){
                                mSearchAdapter = new SearchAdapter(poisBeanList);
                               mIdListview.setAdapter(mSearchAdapter);
                            }
                            else{

                                mSearchAdapter.setData(poisBeanList);
                            }



                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });



    }


    private void initView() {
        mIdLmiotTitleBar = findViewById(R.id.id_lmiot_title_bar);
        mIdSearchEdit = findViewById(R.id.id_search_edit);
        mIdListview = findViewById(R.id.id_listview);
        mIdLocation = findViewById(R.id.id_location);
        mMapView = findViewById(R.id.map_view);
        mIdMapLayout = findViewById(R.id.id_map_layout);
        mIdSearch = findViewById(R.id.id_search);

        mIdLmiotTitleBar.setOnItemClickListener(new LmiotTitleBar.onItemClickListener() {
            @Override
            public void onBackClick(View view) {
                finish();
            }

            @Override
            public void onMenuClick(View view) {
                //发送通知
                EventBus.getDefault().post(new LocationSetBean(mIdLocation.getText().toString(),mLatitudeSet,mLongitudeSet));
                finish();
            }

            @Override
            public void onTitleClick(View view) {

            }
        });


        mIdSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String s = mIdSearchEdit.getText().toString();
                if(TextUtils.isEmpty(s)){
                    mIdListview.setVisibility(View.GONE);
                    Toast.makeText(LocationActivity.this, "内容不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    mIdListview.setVisibility(View.VISIBLE);
                    search(s);
                }
            }
        });

    }

    private void showLocationName(double latitude, double longitude) {
        LatLonPoint latLonPoint = new LatLonPoint(latitude, longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
        mGeocoderSearch.getFromLocationAsyn(query);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {


    }

    @Override
    public void deactivate() {

    }


    //逆地理编码解析后的地址
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        mIdLocation.setText(regeocodeAddress.getFormatAddress());

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }


    //定位点
    @Override
    public void onMyLocationChange(Location location) {

        if (mMarker != null) {
            mMarker.destroy();
        }

        mLatitudeNow = location.getLatitude();
        mLongitudeNow = location.getLongitude();

        mLatitudeSet=location.getLatitude();
        mLongitudeSet= location.getLongitude();
        showLocationName(mLatitudeNow, mLongitudeNow);

    }

private class SearchAdapter extends BaseAdapter{

    List<LocationBean.PoisBean> mpoisBeanList;

    public SearchAdapter(List<LocationBean.PoisBean> poisBeanList) {
        mpoisBeanList = poisBeanList;
    }

    @Override
    public int getCount() {
        return mpoisBeanList==null?0:mpoisBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v=View.inflate(LocationActivity.this,R.layout.item_search,null);
        final TextView textViewName=v.findViewById(R.id.id_name);
        final TextView textViewLocation=v.findViewById(R.id.id_location);
        final LocationBean.PoisBean poisBean = mpoisBeanList.get(i);
        textViewName.setText(poisBean.getName());
        textViewLocation.setText(poisBean.getPname()+poisBean.getCityname()+poisBean.getAdname()+poisBean.getAddress());
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = poisBean.getLocation();
                String[] split = location.split(",");
                mIdListview.setVisibility(View.GONE);
                mIdLocation.setText(textViewName.getText().toString());
                mLatitudeSet=Double.parseDouble(split[1]);
                mLongitudeSet=Double.parseDouble(split[0]);

                showMark( Double.parseDouble(split[1]),  Double.parseDouble(split[0]));


            }
        });

        return v;
    }

    public void setData(List<LocationBean.PoisBean> poisBeanList) {
        mpoisBeanList=poisBeanList;
        notifyDataSetChanged();

    }
}


}