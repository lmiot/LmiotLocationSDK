package com.lmiot.locationlibrary;

import java.util.List;

/**
 * Created by ming on 2016/11/1.
 */
public class LocationSetBean {

    private String name;
    private Double mLatitude;
    private Double mLongitude;

    public LocationSetBean(String name, Double latitude, Double longitude) {
        this.name = name;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Double longitude) {
        mLongitude = longitude;
    }
}
