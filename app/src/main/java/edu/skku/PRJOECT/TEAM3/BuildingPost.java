package edu.skku.PRJOECT.TEAM3;

import java.util.HashMap;
import java.util.Map;

public class BuildingPost {
    public String name;
    public double lat;
    public double lon;
    public int low;
    public int high;
    public int b1;
    public int f1;
    public int f2;
    public int f3;
    public int f4;
    public int f5;
    public int f6;

    public BuildingPost() {

    }

    public BuildingPost(String name, double lat, double lon, int low, int high, int b1, int f1, int f2, int f3, int f4, int f5, int f6) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.low = low;
        this.high = high;
        this.b1 = b1;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.f4 = f4;
        this.f5 = f5;
        this.f6 = f6;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("lat", lat);
        result.put("lon", lon);
        result.put("low", low);
        result.put("high", high);
        result.put("b1", b1);
        result.put("f1", f1);
        result.put("f2", f2);
        result.put("f3", f3);
        result.put("f4", f4);
        result.put("f5", f5);
        result.put("f6", f6);

        return result;
    }
}