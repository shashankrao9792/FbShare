package com.example.shashank.firebase_1.model;

import java.util.HashMap;
import java.util.Map;

public class ViewAllMsgsRecyclerClass {
//    Map<String, String> Files_Shared;

    String filename;
    String url;

    public ViewAllMsgsRecyclerClass() {
        this.filename = "";
        this.url = "";
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String key) {
        this.filename = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) {
        this.url = value;
    }

    //    public ViewAllMsgsRecyclerClass() {
//        Files_Shared = new HashMap<>();
//    }
//
//    public Map<String, String> getFilesShared() {
//        return Files_Shared;
//    }
//
//    public void setFilesShared(Map<String, String> filesShared) {
//        Files_Shared = filesShared;
//    }
}
