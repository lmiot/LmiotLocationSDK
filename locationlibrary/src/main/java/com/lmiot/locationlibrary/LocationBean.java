package com.lmiot.locationlibrary;

import java.util.List;

/**
 * Created by Administrator on 2017/7/23 0023.
 */

public class LocationBean {

    private String status;
    private String count;
    private String info;
    private String infocode;
    private List<PoisBean> pois;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }


    public List<PoisBean> getPois() {
        return pois;
    }

    public void setPois(List<PoisBean> pois) {
        this.pois = pois;
    }



    public static class PoisBean {

              /*  "distance": "1434",
                "pname": "广东省",
                "cityname": "惠州市",
                "adname": "惠城区",*/


        private String id;
        private String name;
        private String location;
        private String distance;
        private String address;
        private String pname;
        private String cityname;
        private String adname;

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getAdname() {
            return adname;
        }

        public void setAdname(String adname) {
            this.adname = adname;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPname() {
            return pname;
        }

        public void setPname(String pname) {
            this.pname = pname;
        }

        public String getCityname() {
            return cityname;
        }

        public void setCityname(String cityname) {
            this.cityname = cityname;
        }
    }
}
