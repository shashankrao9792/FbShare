package com.example.shashank.firebase_1.data;

import java.util.HashMap;
import java.util.Map;

public class FbIdToUserIdMap {
    Map<String, String> mappings;

    public FbIdToUserIdMap() {
        this.mappings = new HashMap<>();
    }

    public Map<String, String> getMappings() {
        return mappings;
    }

    public void setMappings(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    public void addSingleMapping(String fbId, String userId){
        this.mappings.put(fbId, userId);
    }
}
