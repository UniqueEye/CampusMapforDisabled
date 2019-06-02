package edu.skku.PRJOECT.TEAM3;

import java.util.HashMap;
import java.util.Map;

public class BuildingPost {
    public String name;
    public int low;
    public int high;

    public BuildingPost() {

    }

    public BuildingPost(String name, int low, int high) {
        this.name = name;
        this.low = low;
        this.high = high;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("low", low);
        result.put("high", high);

        return result;
    }
}