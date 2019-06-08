package edu.skku.PRJOECT.TEAM3;

import java.util.HashMap;
import java.util.Map;

public class StorePost {
    public String name;
    public String addr;
    public double lat;
    public double lon;
    public int count;
    public float door;
    public float space;
    public float toilet;

    public StorePost() {
        name = null;
        addr = null;
        lat = 0;
        lon = 0;
        count = 0;
        door = 0;
        space = 0;
        toilet = 0;

    }

    public StorePost(String name, String addr, double lat, double lon, int count, float door, float space, float toilet) {
        this.name = name;
        this.addr = addr;
        this.lat = lat;
        this.lon = lon;
        this.count = count;
        this.door = door;
        this.space = space;
        this.toilet = toilet;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("addr", addr);
        result.put("lat", lat);
        result.put("lon", lon);
        result.put("count", count);
        result.put("door", door);
        result.put("space", space);
        result.put("toilet", toilet);

        return result;
    }
}
